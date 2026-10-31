/*
 * jcccallers - scan the executable mappings of a process for BL instructions
 * that target a given address, i.e. statically find all direct callers.
 *
 * Needed because the on-disk libil2cpp.so is packed: its bytes do not match
 * the decrypted code that actually runs, so file-based disassembly is useless
 * and everything has to come out of live memory.
 *
 * ARM64 BL encoding: 100101 imm26
 *   target = pc + (sign_extend(imm26) << 2)
 *
 * usage: jcccallers <pid> <target_addr_hex> [module_substr]
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define CHUNK (1u << 20)

struct seg { unsigned long lo, hi; char perms[8]; char name[256]; };

static int load_maps(int pid, struct seg **out)
{
    char p[64], line[1024];
    FILE *f;
    int n = 0, cap = 4096;
    struct seg *s = calloc((size_t)cap, sizeof(*s));
    snprintf(p, sizeof(p), "/proc/%d/maps", pid);
    f = fopen(p, "r");
    if (!f) { free(s); return 0; }
    while (fgets(line, sizeof(line), f) && n < cap) {
        unsigned long lo, hi;
        char perms[8] = {0};
        char *path = strchr(line, '/');
        if (sscanf(line, "%lx-%lx %7s", &lo, &hi, perms) < 3) continue;
        s[n].lo = lo; s[n].hi = hi;
        strncpy(s[n].perms, perms, sizeof(s[n].perms) - 1);
        if (path) {
            size_t len = strlen(path);
            while (len && (path[len-1] == '\n' || path[len-1] == ' ')) len--;
            if (len >= sizeof(s[n].name)) len = sizeof(s[n].name) - 1;
            memcpy(s[n].name, path, len);
            s[n].name[len] = 0;
        }
        n++;
    }
    fclose(f);
    *out = s;
    return n;
}

int main(int argc, char **argv)
{
    int pid, nseg, i, nfound = 0;
    unsigned long target;
    const char *filter = NULL;
    struct seg *segs = NULL;
    unsigned char *buf;

    if (argc < 3) {
        printf("usage: jcccallers <pid> <target_addr> [module_substr]\n");
        return 1;
    }
    pid = atoi(argv[1]);
    target = strtoul(argv[2], NULL, 16);
    if (argc > 3) filter = argv[3];
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    nseg = load_maps(pid, &segs);
    buf = malloc(CHUNK);
    if (!segs || !buf) return 2;

    for (i = 0; i < nseg; i++) {
        unsigned long a;
        if (segs[i].perms[0] != 'r' || segs[i].perms[2] != 'x') continue;
        if (filter && !strstr(segs[i].name, filter)) continue;

        for (a = segs[i].lo; a < segs[i].hi; a += CHUNK) {
            size_t want = (size_t)((segs[i].hi - a) < CHUNK ? (segs[i].hi - a) : CHUNK);
            size_t j;
            if (drv_read((uintptr_t)a, buf, want) != 0) continue;
            for (j = 0; j + 4 <= want; j += 4) {
                unsigned int w;
                memcpy(&w, buf + j, 4);
                if ((w & 0xFC000000u) != 0x94000000u) continue;
                {
                    int imm = (int)(w & 0x03FFFFFFu);
                    unsigned long pc = a + j;
                    unsigned long t;
                    if (imm & 0x02000000) imm -= 0x04000000;
                    t = pc + ((long)imm << 2);
                    if (t == target) {
                        printf("caller 0x%lx  (in %s)\n", pc, segs[i].name);
                        if (++nfound >= 200) goto done;
                    }
                }
            }
        }
    }
done:
    printf("\ncallers of 0x%lx: %d\n", target, nfound);
    free(buf); free(segs);
    return 0;
}
