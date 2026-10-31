/*
 * jccscan - 在目标进程内存里做只读扫描（全程走驱动 ioctl，无 ptrace、无 /proc/pid/mem）
 *
 *   jccscan str <pid> <needle...>        扫 ASCII 字符串，打印命中地址
 *   jccscan ptr <pid> <hexvalue> [align] 扫 8 字节值（用于找指向某地址的指针）
 *   jccscan u64 <pid> <lo> <hi>          扫落在 [lo,hi) 区间的 8 字节值
 *
 * 只扫描可读区域；r--p 只读文件映射段驱动读不了，自动跳过。
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <ctype.h>

#define CHUNK (1u << 20)
#define OVERLAP 4096

struct region {
    unsigned long lo, hi;
    char perms[8];
    char path[256];
};

static int load_regions(int pid, struct region **out)
{
    char p[64], line[1024];
    FILE *f;
    int n = 0, cap = 512;
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

/* r--p 文件映射段驱动读不了，跳过；纯 ---p 也跳过 */
static int skippable(const struct region *r)
{
    if (r->perms[0] != 'r') return 1;
    if (r->perms[2] == 'x') return 0;                    /* r-xp 可读，保留 */
    if (r->perms[1] != '-') return 0;                    /* rw- 保留 */
    if (r->path[0] == 0) return 0;                       /* 匿名 r-- 保留 */
    return 1;                                            /* r-- + 有路径 -> 跳过 */
}

static const char *fmt_perms(const struct region *r) { return r->perms; }

int main(int argc, char **argv)
{
    int pid, nreg, i;
    struct region *regs = NULL;
    unsigned char *buf;
    unsigned long total = 0, scanned = 0;

    if (argc < 4) {
        printf("usage:\n");
        printf("  jccscan str <pid> <needle>\n");
        printf("  jccscan ptr <pid> <hexvalue>\n");
        printf("  jccscan u64 <pid> <hexlo> <hexhi>\n");
        return 1;
    }
    const char *mode = argv[1];
    pid = atoi(argv[2]);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    nreg = load_regions(pid, &regs);
    if (nreg <= 0) { printf("no regions\n"); return 2; }

    buf = malloc(CHUNK + OVERLAP);
    if (!buf) return 2;

    unsigned long needle_val = 0, lo_val = 0, hi_val = 0;
    const char *needle = NULL;
    size_t nlen = 0;
    if (!strcmp(mode, "str")) {
        needle = argv[3];
        nlen = strlen(needle);
    } else if (!strcmp(mode, "ptr")) {
        needle_val = strtoul(argv[3], NULL, 16);
    } else if (!strcmp(mode, "u64")) {
        if (argc < 5) { printf("u64 需要 lo hi\n"); return 1; }
        lo_val = strtoul(argv[3], NULL, 16);
        hi_val = strtoul(argv[4], NULL, 16);
    } else {
        printf("unknown mode\n"); return 1;
    }

    int hits = 0;
    for (i = 0; i < nreg; i++) {
        unsigned long a;
        if (skippable(&regs[i])) continue;
        total += regs[i].hi - regs[i].lo;
    }
    printf("待扫描区域共 %lu MB（已跳过 r-- 只读文件映射）\n", total >> 20);

    for (i = 0; i < nreg; i++) {
        unsigned long a;
        if (skippable(&regs[i])) continue;
        for (a = regs[i].lo; a < regs[i].hi; a += CHUNK) {
            size_t want = (size_t)((regs[i].hi - a) < CHUNK ? (regs[i].hi - a) : CHUNK);
            size_t k;
            if (drv_read((uintptr_t)a, buf, want) != 0) continue;
            scanned += want;
            if (((scanned >> 20) & 0x3FF) == 0) {
                printf("  ... %lu MB\n", scanned >> 20);
                fflush(stdout);
            }
            if (!strcmp(mode, "str")) {
                for (k = 0; k + nlen <= want; k++) {
                    if (buf[k] != (unsigned char)needle[0]) continue;
                    if (memcmp(buf + k, needle, nlen) == 0) {
                        printf("STR  0x%lx  region=%s %s\n", a + k, fmt_perms(&regs[i]), regs[i].path);
                        if (++hits > 400) { printf("... 命中过多，截断\n"); goto done; }
                    }
                }
            } else {
                for (k = 0; k + 8 <= want; k += 8) {
                    unsigned long v;
                    memcpy(&v, buf + k, 8);
                    if (!strcmp(mode, "ptr")) {
                        if (v != needle_val) continue;
                    } else {
                        if (v < lo_val || v >= hi_val) continue;
                    }
                    printf("VAL  0x%lx  = 0x%lx  region=%s %s\n",
                           a + k, v, fmt_perms(&regs[i]), regs[i].path);
                    if (++hits > 400) { printf("... 命中过多，截断\n"); goto done; }
                }
            }
        }
    }
done:
    printf("扫描 %lu MB，命中 %d 条\n", scanned >> 20, hits);
    free(buf);
    free(regs);
    return 0;
}
