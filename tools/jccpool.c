/*
 * jccpool - dump the hero pool nodes used by the shop roll.
 *
 * CPlayer + 0x38 -> CTAC_HeroPool
 *   +0x18 m_stHeroKuGroup -> +0x18 quality map -> per-cost map
 *        -> CTAC_HeroKuNode { hid@0x10 quality@0x14 weight@0x18 count@0x1c init@0x20 }
 *   +0x20 m_mapLevelWeigth: level -> { cost -> weight }
 *
 * usage: jccpool <pid> <cplayer>
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>

#define MAXN 1024

static int i32(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }

int main(int argc, char **argv)
{
    int pid, level, i, j, c;
    unsigned long cp, pool, ku, qku, lw;
    int level_odds[6] = {0};

    if (argc < 3) { printf("usage: jccpool <pid> <cplayer>\n"); return 1; }
    pid = atoi(argv[1]);
    cp = strtoul(argv[2], NULL, 16);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    level = i32((uintptr_t)cp + 0x390);
    pool = u64v((uintptr_t)cp + 0x38);
    ku   = u64v((uintptr_t)pool + 0x18);
    lw   = u64v((uintptr_t)pool + 0x20);

    printf("level=%d\n", level);

    /* level odds */
    {
        unsigned long ents = u64v((uintptr_t)lw + 0x10);
        int cnt = i32((uintptr_t)lw + 0x40);
        for (i = 0; i < cnt; i++) {
            unsigned long e = ents + 0x20 + (unsigned long)i * 0x20;
            if (i32((uintptr_t)e + 0x18) == level) {
                unsigned long inner = u64v((uintptr_t)e + 0x10);
                unsigned long e2 = u64v((uintptr_t)inner + 0x10);
                int n2 = i32((uintptr_t)inner + 0x40);
                for (j = 0; j < n2; j++) {
                    unsigned long ee = e2 + 0x20 + (unsigned long)j * 0x18;
                    int cc = i32((uintptr_t)ee + 0x10);
                    if (cc >= 1 && cc <= 5) level_odds[cc] = i32((uintptr_t)ee + 0x14);
                }
                break;
            }
        }
    }
    printf("odds=%d,%d,%d,%d,%d\n", level_odds[1], level_odds[2], level_odds[3],
           level_odds[4], level_odds[5]);

    qku = u64v((uintptr_t)ku + 0x18);
    {
        unsigned long ents = u64v((uintptr_t)qku + 0x10);
        int cnt = i32((uintptr_t)qku + 0x40);
        printf("nodes:\n");
        for (i = 0; i < cnt; i++) {
            unsigned long e = ents + 0x20 + (unsigned long)i * 0x20;
            unsigned long sub = u64v((uintptr_t)e + 0x10), se;
            int sn;
            if (!sub) continue;
            se = u64v((uintptr_t)sub + 0x10);
            sn = i32((uintptr_t)sub + 0x40);
            for (j = 0; j < sn; j++) {
                unsigned long ee = se + 0x20 + (unsigned long)j * 0x20;
                unsigned long node = u64v((uintptr_t)ee + 0x10);
                if (!node) continue;
                printf("%d %d %d %d %d\n",
                       i32((uintptr_t)node + 0x10), i32((uintptr_t)node + 0x14),
                       i32((uintptr_t)node + 0x18), i32((uintptr_t)node + 0x1c),
                       i32((uintptr_t)node + 0x20));
            }
        }
    }
    (void)c;
    return 0;
}
