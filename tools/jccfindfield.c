/*
 * jccfindfield - locate a field by NAME and print its owner class + offset.
 *
 * Strategy (works because metadata is decrypted into a rw-s mapping):
 *   1. find every occurrence of the field name string in memory
 *   2. find every qword that equals one of those string addresses
 *      -> a FieldInfo's `name` is at +0x00, so the slot IS the FieldInfo
 *   3. for each candidate, read parent(+0x10) and offset(+0x18);
 *      validate parent by reading its name(+0x10) as ASCII
 *
 * Also prints raw context when validation fails, so layout differences are visible.
 *
 * usage: jccfindfield <pid> <fieldname>
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define CHUNK (1u << 20)

struct region { unsigned long lo, hi; char perms[8]; char path[256]; };

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

static int scannable(const struct region *r)
{
    if (r->perms[0] != 'r') return 0;
    if (r->perms[2] == 'x') return 1;
    if (r->perms[1] == 'w') return 1;          /* rw- 与 rw-s 都要 */
    if (r->path[0] == 0) return 1;
    return 0;
}

static int read_cstr(uintptr_t a, char *out, size_t cap)
{
    size_t got = 0;
    out[0] = 0;
    while (got + 32 <= cap) {
        unsigned char buf[32];
        size_t i;
        if (drv_read(a + got, buf, sizeof(buf)) != 0) break;
        for (i = 0; i < sizeof(buf); i++) {
            if (buf[i] == 0) { out[got + i] = 0; return (int)(got + i); }
            out[got + i] = (char)buf[i];
        }
        got += sizeof(buf);
    }
    out[cap - 1] = 0;
    return (int)strlen(out);
}

static int is_ident(const char *s)
{
    if (!s || !s[0]) return 0;
    for (; *s; s++) {
        unsigned char c = (unsigned char)*s;
        if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
              (c >= '0' && c <= '9') || c == '_' || c == '<' || c == '>')) return 0;
    }
    return 1;
}

int main(int argc, char **argv)
{
    int pid, nreg, i, k;
    const char *needle;
    size_t nlen;
    struct region *regs = NULL;
    unsigned char *buf;
    unsigned long *strs = NULL;
    int nstr = 0, capstr = 4096;
    int found = 0;

    if (argc < 3) { printf("usage: jccfindfield <pid> <fieldname>\n"); return 1; }
    pid = atoi(argv[1]);
    needle = argv[2];
    nlen = strlen(needle);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    nreg = load_regions(pid, &regs);
    if (nreg <= 0) return 2;
    buf = malloc(CHUNK);
    strs = calloc((size_t)capstr, sizeof(unsigned long));
    if (!buf || !strs) return 2;

    /* 第一遍：找字段名串 */
    for (i = 0; i < nreg; i++) {
        unsigned long a;
        if (!scannable(&regs[i])) continue;
        for (a = regs[i].lo; a < regs[i].hi; a += CHUNK) {
            size_t want = (size_t)((regs[i].hi - a) < CHUNK ? (regs[i].hi - a) : CHUNK);
            size_t j;
            if (drv_read((uintptr_t)a, buf, want) != 0) {
                size_t pg;
                for (pg = 0; pg < want; pg += 4096) {
                    size_t pn = (want - pg > 4096) ? 4096 : (want - pg);
                    if (drv_read((uintptr_t)(a + pg), buf + pg, pn) != 0)
                        memset(buf + pg, 0xCD, pn);
                }
            }
            for (j = 0; j + nlen <= want; j++) {
                if (buf[j] != (unsigned char)needle[0]) continue;
                if (memcmp(buf + j, needle, nlen) != 0) continue;
                if (j + nlen < want && buf[j + nlen] != 0) continue;
                if (nstr < capstr) strs[nstr++] = a + j;
            }
        }
    }
    printf("字段名 \"%s\" 命中 %d 处\n\n", needle, nstr);

    /* 第二遍：找指向这些串的槽，按 FieldInfo 解读 */
    for (i = 0; i < nreg; i++) {
        unsigned long a;
        if (!scannable(&regs[i])) continue;
        for (a = regs[i].lo; a < regs[i].hi; a += CHUNK) {
            size_t want = (size_t)((regs[i].hi - a) < CHUNK ? (regs[i].hi - a) : CHUNK);
            size_t j;
            if (drv_read((uintptr_t)a, buf, want) != 0) {
                size_t pg;
                for (pg = 0; pg < want; pg += 4096) {
                    size_t pn = (want - pg > 4096) ? 4096 : (want - pg);
                    if (drv_read((uintptr_t)(a + pg), buf + pg, pn) != 0)
                        memset(buf + pg, 0xCD, pn);
                }
            }
            for (j = 0; j + 8 <= want; j += 8) {
                unsigned long v, parent, nmp;
                int off;
                char cname[128], cns[128];
                memcpy(&v, buf + j, 8);
                if (!v) continue;
                for (k = 0; k < nstr; k++) if (v == strs[k]) break;
                if (k == nstr) continue;

                parent = 0; off = 0;
                drv_read((uintptr_t)(a + j) + 0x10, &parent, 8);
                drv_read((uintptr_t)(a + j) + 0x18, &off, 4);

                cname[0] = cns[0] = 0;
                if (parent) {
                    nmp = 0;
                    if (drv_read((uintptr_t)parent + 0x10, &nmp, 8) == 0 && nmp)
                        read_cstr((uintptr_t)nmp, cname, sizeof(cname));
                    nmp = 0;
                    if (drv_read((uintptr_t)parent + 0x18, &nmp, 8) == 0 && nmp)
                        read_cstr((uintptr_t)nmp, cns, sizeof(cns));
                }
                printf("slot 0x%lx\n", a + j);
                printf("   +0x08 = 0x%lx\n", (unsigned long)0);
                {
                    unsigned long t1 = 0, t2 = 0;
                    drv_read((uintptr_t)(a + j) + 0x08, &t1, 8);
                    drv_read((uintptr_t)(a + j) + 0x20, &t2, 8);
                    printf("   +0x08=%#lx  +0x10=%#lx  +0x18=%d  +0x20=%#lx\n",
                           t1, parent, off, t2);
                }
                if (is_ident(cname))
                    printf("   => class %s.%s  offset 0x%x\n", cns, cname, (unsigned)off);
                else
                    printf("   => parent 不是可读类名，跳过\n");
                found++;
            }
        }
    }
    printf("\n候选槽: %d\n", found);
    free(buf); free(strs); free(regs);
    return 0;
}
