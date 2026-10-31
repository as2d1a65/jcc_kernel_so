/*
 * jccscan - ????????????????????????????????????????????????????????? ioctl?????? ptrace?????? /proc/pid/mem???
 *
 *   jccscan str  <pid> <needle>              ASCII ?????????
 *   jccscan ptr  <pid> <hexvalue>            8 ???????????????
 *   jccscan u64  <pid> <hexlo> <hexhi>       8 ????????????
 *   jccscan u32  <pid> <decvalue> [out]      32 ????????????????????????????????? out
 *   jccscan filt <pid> <decvalue> <in> [out] ?????????????????? out ??????????????????
 *   jccscan get  <pid> <in> <count>          ??? in ?????? count ???????????? 32 ??????
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#define CHUNK (1u << 20)

struct region {
    unsigned long lo, hi;
    char perms[8];
    char path[256];
};

static int load_regions(int pid, struct region **out)
{
    char p[64], line[1024];
    FILE *f;
    int n = 0, cap = 8192;
    struct region *r = calloc((size_t)cap, sizeof(*r));

    snprintf(p, sizeof(p), "/proc/%d/maps", pid);
    f = fopen(p, "r");
    if (!f) { free(r); return 0; }
    while (fgets(line, sizeof(line), f) && n < cap) {
        unsigned long lo, hi;
        char perms[8] = {0}, path[256] = {0};
        if (sscanf(line, "%lx-%lx %7s %*s %*s %*s %255[^\n]", &lo, &hi, perms, path) < 3)
            continue;
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
    if (r->perms[1] == 'w') return 0;   /* rw- ? rw-s ??????? rw-s */
    if (r->path[0] == 0) return 0;
    return 1;
}

/* ?????????????????????????????????????????? */
static unsigned long cache_page = 0;
static unsigned char cache_buf[4096];
static int cache_ok = 0;

static int read32_cached(int pid, unsigned long addr, unsigned int *out)
{
    unsigned long pg = addr & ~0xFFFUL;
    if (pg != cache_page || !cache_ok) {
        if (drv_read((uintptr_t)pg, cache_buf, 4096) != 0) { cache_ok = 0; cache_page = 0; return -1; }
        cache_page = pg; cache_ok = 1;
    }
    memcpy(out, cache_buf + (addr & 0xFFF), 4);
    return 0;
}

int main(int argc, char **argv)
{
    int pid, nreg, i;
    struct region *regs = NULL;
    unsigned char *buf;

    if (argc < 4) {
        printf("usage:\n");
        printf("  jccscan str  <pid> <needle>\n");
        printf("  jccscan ptr  <pid> <hexvalue>\n");
        printf("  jccscan u64  <pid> <hexlo> <hexhi>\n");
        printf("  jccscan u32  <pid> <decvalue> [outfile]\n");
        printf("  jccscan filt <pid> <decvalue> <infile> [outfile]\n");
        printf("  jccscan get  <pid> <infile> <count>\n");
        return 1;
    }
    const char *mode = argv[1];
    pid = atoi(argv[2]);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    /* ---- filt????????????????????????????????? ---- */
    if (!strcmp(mode, "filt")) {
        unsigned int want = (unsigned int)strtoul(argv[3], NULL, 0);
        FILE *in, *out = NULL;
        unsigned long addr;
        int kept = 0, total = 0;
        if (argc < 5) { printf("filt ?????? infile\n"); return 1; }
        in = fopen(argv[4], "r");
        if (!in) { printf("open %s failed\n", argv[4]); return 3; }
        if (argc > 5) out = fopen(argv[5], "w");
        while (fscanf(in, "%lx", &addr) == 1) {
            unsigned int v;
            total++;
            if (read32_cached(pid, addr, &v) != 0) continue;
            if (v != want) continue;
            kept++;
            if (out) fprintf(out, "%lx\n", addr);
            else if (kept <= 30) printf("  0x%lx\n", addr);
        }
        fclose(in);
        if (out) fclose(out);
        printf("?????? %d ?????????????????? %d ??????????????? %u???\n", total, kept, want);
        return 0;
    }

    /* ---- get?????????????????????????????? ---- */
    if (!strcmp(mode, "get")) {
        FILE *in;
        unsigned long addr;
        int cnt = (argc > 4) ? atoi(argv[4]) : 40, shown = 0;
        in = fopen(argv[3], "r");
        if (!in) { printf("open %s failed\n", argv[3]); return 3; }
        while (fscanf(in, "%lx", &addr) == 1 && shown < cnt) {
            unsigned int v = 0;
            if (read32_cached(pid, addr, &v) != 0) { printf("  0x%lx  <read failed>\n", addr); continue; }
            printf("  0x%lx  = %u (0x%x)\n", addr, v, v);
            shown++;
        }
        fclose(in);
        return 0;
    }

    nreg = load_regions(pid, &regs);
    if (nreg <= 0) { printf("no regions\n"); return 2; }
    buf = malloc(CHUNK);
    if (!buf) return 2;

    unsigned long needle_val = 0, lo_val = 0, hi_val = 0;
    const char *needle = NULL;
    size_t nlen = 0;
    unsigned int want32 = 0;
    FILE *outfile = NULL;
    if (!strcmp(mode, "str")) { needle = argv[3]; nlen = strlen(needle); }
    else if (!strcmp(mode, "ptr")) { needle_val = strtoul(argv[3], NULL, 16); }
    else if (!strcmp(mode, "u64")) {
        if (argc < 5) { printf("u64 ?????? lo hi\n"); return 1; }
        lo_val = strtoul(argv[3], NULL, 16);
        hi_val = strtoul(argv[4], NULL, 16);
    } else if (!strcmp(mode, "u32")) {
        want32 = (unsigned int)strtoul(argv[3], NULL, 0);
        if (argc > 4) outfile = fopen(argv[4], "w");
    } else { printf("unknown mode\n"); return 1; }

    int hits = 0;
    unsigned long scanned = 0;
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
            if (!strcmp(mode, "str")) {
                for (k = 0; k + nlen <= want; k++) {
                    if (buf[k] != (unsigned char)needle[0]) continue;
                    if (memcmp(buf + k, needle, nlen) == 0) {
                        printf("STR  0x%lx\n", a + k);
                        if (++hits > 400) goto done;
                    }
                }
            } else if (!strcmp(mode, "u32")) {
                for (k = 0; k + 4 <= want; k += 4) {
                    unsigned int v;
                    memcpy(&v, buf + k, 4);
                    if (v != want32) continue;
                    hits++;
                    if (outfile) fprintf(outfile, "%lx\n", a + k);
                    else if (hits <= 40) printf("  U32 0x%lx\n", a + k);
                }
            } else {
                for (k = 0; k + 8 <= want; k += 8) {
                    unsigned long v;
                    memcpy(&v, buf + k, 8);
                    if (!strcmp(mode, "ptr")) { if (v != needle_val) continue; }
                    else { if (v < lo_val || v >= hi_val) continue; }
                    printf("VAL  0x%lx  = 0x%lx\n", a + k, v);
                    if (++hits > 400) goto done;
                }
            }
        }
    }
done:
    if (outfile) fclose(outfile);
    printf("?????? %lu MB????????? %d ????????? %s???\n", scanned >> 20, hits,
           !strcmp(mode, "u32") ? argv[3] : "-");
    free(buf);
    free(regs);
    return hits;
}
