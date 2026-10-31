/*
 * jccrng - locate live GameFramework.FRand.FRandom instances.
 *
 * FRandom is .NET System.Random (Knuth subtractive):
 *   klass      +0x00
 *   monitor    +0x08
 *   inext      +0x10 (int32, 1..55)
 *   inextp     +0x14 (int32, 1..55)
 *   SeedArray  +0x18 (int32[56])
 *   count      +0x20 (int32)
 *
 * usage: jccrng <pid> <class_addr> [scan|dump <idx>]
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define CHUNK (1u << 20)

struct region { unsigned long lo, hi; char perms[8]; };

static int load_regions(int pid, struct region **out)
{
    char p[64], line[512];
    FILE *f;
    int n = 0, cap = 8192;
    struct region *r = calloc((size_t)cap, sizeof(*r));
    snprintf(p, sizeof(p), "/proc/%d/maps", pid);
    f = fopen(p, "r");
    if (!f) { free(r); return 0; }
    while (fgets(line, sizeof(line), f) && n < cap) {
        unsigned long lo, hi;
        char perms[8] = {0};
        if (sscanf(line, "%lx-%lx %7s", &lo, &hi, perms) < 3) continue;
        r[n].lo = lo; r[n].hi = hi;
        strncpy(r[n].perms, perms, sizeof(r[n].perms) - 1);
        n++;
    }
    fclose(f);
    *out = r;
    return n;
}

static int i32(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }

int main(int argc, char **argv)
{
    int pid, nreg, i, k;
    unsigned long klass;
    struct region *regs = NULL;
    unsigned char *buf;
    unsigned long found[64];
    int nf = 0;

    if (argc < 3) { printf("usage: jccrng <pid> <class_addr>\n"); return 1; }
    pid = atoi(argv[1]);
    klass = strtoul(argv[2], NULL, 16);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    nreg = load_regions(pid, &regs);
    buf = malloc(CHUNK);
    if (!regs || !buf) return 2;

    for (i = 0; i < nreg; i++) {
        unsigned long a;
        if (regs[i].perms[0] != 'r') continue;
        if (regs[i].perms[1] != 'w') continue;
        for (a = regs[i].lo; a < regs[i].hi; a += CHUNK) {
            size_t want = (size_t)((regs[i].hi - a) < CHUNK ? (regs[i].hi - a) : CHUNK);
            size_t j;
            if (drv_read((uintptr_t)a, buf, want) != 0) continue;
            for (j = 0; j + 8 <= want; j += 8) {
                unsigned long v;
                memcpy(&v, buf + j, 8);
                if (v != klass) continue;
                {
                    unsigned long base = a + j;
                    int inx  = i32((uintptr_t)base + 0x10);
                    int inxp = i32((uintptr_t)base + 0x14);
                    unsigned long sa = u64v((uintptr_t)base + 0x18);
                    int cnt  = i32((uintptr_t)base + 0x20);
                    int ok = 0;
                    if (inx >= 1 && inx <= 55 && inxp >= 1 && inxp <= 55 &&
                        sa > 0x1000000000UL && cnt >= 0 && cnt < 100000000)
                        ok = 1;
                    if (ok && nf < 64) found[nf++] = base;
                    if (ok)
                        printf("RNG 0x%-14lx inext=%-3d inextp=%-3d seed=0x%-12lx count=%d\n",
                               base, inx, inxp, sa, cnt);
                }
            }
        }
    }
    printf("\nvalid instances: %d\n", nf);

    if (argc > 3 && !strcmp(argv[3], "dump") && argc > 4) {
        int idx = atoi(argv[4]);
        if (idx >= 0 && idx < nf) {
            unsigned long base = found[idx];
            unsigned long sa = u64v((uintptr_t)base + 0x18);
            unsigned char a[56 * 4 + 0x20];
            int i2;
            printf("\ndump #%d @0x%lx  SeedArray=0x%lx\n", idx, base, sa);
            if (drv_read((uintptr_t)sa, a, sizeof(a)) == 0) {
                printf("header: klass=%lx maxlen=%d\n",
                       *(unsigned long *)a, *(int *)(a + 0x18));
                printf("seed[");
                for (i2 = 0; i2 < 10; i2++)
                    printf("%d%s", *(int *)(a + 0x20 + i2 * 4), i2 < 9 ? "," : "");
                printf(" ...]\n");
            }
        }
    }
    free(buf); free(regs);
    return 0;
}
