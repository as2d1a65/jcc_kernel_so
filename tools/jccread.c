/*
 * jccread - 免输入定位并读出玩家数据块
 *
 * 依据实测签名（跨两局验证，页内偏移恒定）：
 *   地址 A 必须满足 (A & 0xFFF) == 0xb8c
 *   u32(A + 0x00)  血量     1..100
 *   u32(A + 0x04)  等级     1..10
 *   u32(A + 0x08)  == 0
 *   u32(A + 0x0C)  回合计数 0..64（每回合 +2）
 *   u32(A + 0x10)  金币     0..200
 *   u32(A + 0x14)  == 1
 *
 * 用法：
 *   jccread <pid>            列出所有符合签名的数据块
 *   jccread <pid> -v         同时打印地址附近 0x20 字节
 *
 * 全程只读，走驱动 ioctl，无 ptrace、无 /proc/pid/mem、无任何 hook。
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define CHUNK (1u << 20)
#define PAGEOFF 0xb8c

struct region { unsigned long lo, hi; char perms[8]; char path[256]; };

static int load_regions(int pid, struct region **out)
{
    char p[64], line[1024];
    FILE *f;
    int n = 0, cap = 2048;
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

/* 只扫可写匿名/堆区（数据块都在 rw-p 里），可显著加速 */
static int skippable(const struct region *r)
{
    if (r->perms[0] != 'r' || r->perms[1] != 'w') return 1;
    return 0;
}

static int in(int v, int lo, int hi) { return v >= lo && v <= hi; }

int main(int argc, char **argv)
{
    int pid, nreg, i, hits = 0, verbose = 0;
    struct region *regs = NULL;
    unsigned char *buf;
    unsigned long scanned = 0;

    if (argc < 2) { printf("usage: jccread <pid> [-v]\n"); return 1; }
    pid = atoi(argv[1]);
    if (argc > 2 && !strcmp(argv[2], "-v")) verbose = 1;
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    nreg = load_regions(pid, &regs);
    if (nreg <= 0) return 2;
    buf = malloc(CHUNK);
    if (!buf) return 2;

    printf("扫描可写区，按签名 (pageoff=0x%x) 找玩家数据块\n", PAGEOFF);
    printf("  %-14s %5s %6s %6s %6s\n", "地址", "血量", "等级", "金币", "回合计");

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
            /* 只检查页内偏移为 0xb8c 的位置 */
            for (k = 0; k + 0x18 <= want; k += 4096) {
                unsigned long cand = a + k + PAGEOFF;
                size_t idx;
                int hp, lv, z, rc, gl, one;
                if (((a + k) & 0xFFF) != 0) {
                    /* 让 k 对齐到页首 */
                }
                if (cand < a || cand - a + 0x18 > want) continue;
                idx = (size_t)(cand - a);
                memcpy(&hp,  buf + idx + 0x00, 4);
                memcpy(&lv,  buf + idx + 0x04, 4);
                memcpy(&z,   buf + idx + 0x08, 4);
                memcpy(&rc,  buf + idx + 0x0C, 4);
                memcpy(&gl,  buf + idx + 0x10, 4);
                memcpy(&one, buf + idx + 0x14, 4);
                if (!in(hp, 1, 100)) continue;
                if (!in(lv, 1, 10)) continue;
                if (z != 0) continue;
                if (!in(rc, 0, 64)) continue;
                if (!in(gl, 0, 200)) continue;
                if (one != 1) continue;
                printf("  0x%-12lx %5d %6d %6d %6d\n", cand, hp, lv, gl, rc);
                hits++;
                if (verbose) {
                    unsigned char tmp[0x20];
                    if (drv_read((uintptr_t)cand, tmp, 0x20) == 0) {
                        size_t b;
                        printf("      ");
                        for (b = 0; b < 0x20; b++) printf("%02x%s", tmp[b], (b % 8 == 7) ? "  " : " ");
                        printf("\n");
                    }
                }
            }
        }
    }
    printf("扫 %lu MB，命中 %d 个\n", scanned >> 20, hits);
    free(buf);
    free(regs);
    return hits ? 0 : 5;
}
