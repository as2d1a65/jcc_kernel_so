/*
 * jccfind - ????????????????????? *
 * ????????????????PJX110 / 6.1.57???
 *   +0x00 int32 hp     1..100
 *   +0x04 int32 level  1..10   <- ??????????? *   +0x08 int64 (0)
 *   +0x10 int32 ??     (????????0..20)
 *   +0x14 int32 gold   0..200
 *
 * ?????ccfind <pid> <level> [hpmin] [hpmax] [goldmin] [goldmax]
 * ??????????????????????????hp ??????? *
 * ???????????? ioctl?? */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define CHUNK (1u << 20)
#define GUARD 32

struct region { unsigned long lo, hi; char perms[8]; char path[256]; };

static int load_regions(int pid, struct region **out)
{
    char p[64], line[1024];
    FILE *f;
    int n = 0, cap = 1024;
    struct region *r = calloc((size_t)cap, sizeof(*r));
    snprintf(p, sizeof(p), "/proc/%d/maps", pid);
    f = fopen(p, "r");
    if (!f) { free(r); return 0; }
    while (fgets(line, sizeof(line), f) && n < cap) {
        unsigned long lo, hi;
        char perms[8] = {0}, path[256] = {0};
        if (sscanf(line, "%lx-%lx %7s %*s %*s %*s %255[^\n]", &lo, &hi, perms, path) < 3) continue;
        r[n].lo = lo; r[n].hi = hi;
        strncpy(r[n].perms, perms, sizeof(r[n].perms) - 1);
        strncpy(r[n].path, path, sizeof(r[n].path) - 1);
        n++;
    }
    fclose(f);
    *out = r;
    return n;
}

static int skippable(const struct region *r)
{
    if (r->perms[0] != 'r') return 1;
    if (r->perms[2] == 'x') return 0;
    if (r->perms[1] != '-') return 0;
    if (r->path[0] == 0) return 0;
    return 1;
}

static int in_range(int v, int lo, int hi) { return v >= lo && v <= hi; }

int main(int argc, char **argv)
{
    int pid, nreg, i;
    struct region *regs = NULL;
    unsigned char *buf;
    int level, hpmin = 1, hpmax = 100, gmin = 0, gmax = 200, pageoff = -1;
    int hits = 0;
    unsigned long scanned = 0;

    if (argc < 3) {
        printf("usage: jccfind <pid> <level> [hpmin] [hpmax] [goldmin] [goldmax] [pageoff]\n");
        return 1;
    }
    pid = atoi(argv[1]);
    level = atoi(argv[2]);
    if (argc > 3) hpmin = atoi(argv[3]);
    if (argc > 4) hpmax = atoi(argv[4]);
    if (argc > 5) gmin = atoi(argv[5]);
    if (argc > 6) gmax = atoi(argv[6]);
    if (argc > 7) pageoff = (int)strtol(argv[7], NULL, 0);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    nreg = load_regions(pid, &regs);
    if (nreg <= 0) return 2;
    buf = malloc(CHUNK);
    if (!buf) return 2;

    printf("??level=%d hp=[%d,%d] gold=[%d,%d]\n", level, hpmin, hpmax, gmin, gmax);

    for (i = 0; i < nreg; i++) {
        unsigned long a;
        if (skippable(&regs[i])) continue;
        for (a = regs[i].lo; a < regs[i].hi; a += CHUNK) {
            size_t want = (size_t)((regs[i].hi - a) < CHUNK ? (regs[i].hi - a) : CHUNK);
            size_t k;
            if (drv_read((uintptr_t)a, buf, want) != 0) {
                size_t pg;
                for (pg = 0; pg < want; pg += 4096) {
                    size_t pn = (want - pg > 4096) ? 4096 : (want - pg);
                    if (drv_read((uintptr_t)(a + pg), buf + pg, pn) != 0)
                        memset(buf + pg, 0xCD, pn);
                }
            }
            scanned += want;
            /* 4 ????????????hp ??-4??old ??+0x10?????????????? */
            for (k = 4; k + 0x14 + 4 <= want; k += 4) {
                int lv, hp, gl, one, z4;
                memcpy(&lv, buf + k, 4);
                if (lv != level) continue;
                /* ?????????????? 0xb8c? */
                if (pageoff >= 0 && (int)((a + k - 4) & 0xFFF) != pageoff) continue;
                /* ?????hp+0x08 ? 8 ?? 0?hp+0x14 ? 1 */
                memcpy(&z4, buf + k + 4, 4);
                if (z4 != 0) continue;
                memcpy(&one, buf + k + 0x10, 4);
                if (one != 1) continue;
                memcpy(&hp, buf + k - 4, 4);
                memcpy(&gl, buf + k + 0x0C, 4);
                if (!in_range(hp, hpmin, hpmax)) continue;
                if (!in_range(gl, gmin, gmax)) continue;
                printf("HIT  hp@0x%lx  hp=%d level=%d gold=%d\n",
                       a + k - 4, hp, lv, gl);
                if (++hits >= 60) { printf("... ??????\n"); goto done; }
            }
        }
    }
done:
    printf("??? %lu MB?????%d ??n", scanned >> 20, hits);
    free(buf);
    free(regs);
    return hits ? 0 : 5;
}
