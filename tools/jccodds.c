/*
 * jccodds - exact shop probability for target heroes.
 *
 * Chain (verified on device):
 *   CPlayer + 0x390             level (int32)
 *   CPlayer + 0x38              _heroPool -> Z_PVE.CTAC_HeroPool
 *   heroPool + 0x18             m_stHeroKuGroup  -> CTAC_HeroKuGroup
 *   heroPool + 0x20             m_mapLevelWeigth -> map<level, map<cost, weightInt>>
 *   kuGroup  + 0x18             m_mapQualityHeroKu -> map<cost, map<heroConfID, node*>>
 *   Z_PVE.CTAC_HeroKuNode:  +0x10 heroConfID  +0x14 quality
 *                           +0x18 weight      +0x1c count   +0x20 initCount
 *
 * LinkedHashMap layout: +0x10 entries, +0x40 internalCount
 *   int  value: stride 0x18, key@+0x10 value@+0x14
 *   ptr  value: stride 0x20, value@+0x10 key@+0x18
 *
 * Shop roll = pick cost by level weight, then hero by node weight.
 *   p_slot = sum_c ( lw[c]/100 * targetWeight[c]/allWeight[c] )
 *   p_shop = 1 - (1 - p_slot)^5
 *
 * usage: jccodds <pid> <cplayer> <baseHeroId> [...]
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

#define MAXT 32
#define MAXN 512

static int i32(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }

static int normalize(int h)
{
    if (h >= 31400) return h - 20000;
    if (h >= 21400) return h - 10000;
    return h;
}

struct node { int hid, quality, weight, count, init; };

int main(int argc, char **argv)
{
    int pid, level, i, k, ntarget, nn = 0;
    unsigned long cp, heroPool, kuGroup, levelW, qualityKu;
    int targets[MAXT];
    struct node nodes[MAXN];
    int lw[6] = {0};      /* level odds per cost, percent      */
    int wAll[6] = {0};    /* sum of node weight per cost        */
    int wTgt[6] = {0};    /* sum of node weight for targets     */
    int cAll[6] = {0};    /* sum of remaining copies per cost   */
    int cTgt[6] = {0};
    double pslot = 0.0, pshop;

    if (argc < 4) {
        printf("usage: jccodds <pid> <cplayer> <baseHeroId> [...]\n");
        return 1;
    }
    pid = atoi(argv[1]);
    cp = strtoul(argv[2], NULL, 16);
    ntarget = 0;
    for (i = 3; i < argc && ntarget < MAXT; i++) targets[ntarget++] = normalize(atoi(argv[i]));
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    level = i32((uintptr_t)cp + 0x390);
    heroPool = u64v((uintptr_t)cp + 0x38);
    if (!heroPool) { printf("hero pool null (not in match?)\n"); return 3; }
    kuGroup   = u64v((uintptr_t)heroPool + 0x18);
    levelW    = u64v((uintptr_t)heroPool + 0x20);
    qualityKu = u64v((uintptr_t)kuGroup + 0x18);
    printf("level=%d  heroPool=0x%lx  levelW=0x%lx  qualityKu=0x%lx\n",
           level, heroPool, levelW, qualityKu);

    /* ---- level -> cost odds ---- */
    {
        unsigned long ents = u64v((uintptr_t)levelW + 0x10);
        int cnt = i32((uintptr_t)levelW + 0x40);
        unsigned long inner = 0;
        for (i = 0; i < cnt && ents; i++) {
            unsigned long e = ents + 0x20 + (unsigned long)i * 0x20;
            if (i32((uintptr_t)e + 0x18) == level) { inner = u64v((uintptr_t)e + 0x10); break; }
        }
        if (!inner) { printf("no weight entry for level %d\n", level); return 3; }
        {
            unsigned long e2 = u64v((uintptr_t)inner + 0x10);
            int n2 = i32((uintptr_t)inner + 0x40);
            printf("shop odds by cost:");
            for (i = 0; i < n2; i++) {
                unsigned long e = e2 + 0x20 + (unsigned long)i * 0x18;
                int c = i32((uintptr_t)e + 0x10);
                int v = i32((uintptr_t)e + 0x14);
                if (c >= 1 && c <= 5) { lw[c] = v; printf("  c%d=%d%%", c, v); }
            }
            printf("\n");
        }
    }

    /* ---- pool nodes ---- */
    {
        unsigned long ents = u64v((uintptr_t)qualityKu + 0x10);
        int cnt = i32((uintptr_t)qualityKu + 0x40);
        for (i = 0; i < cnt; i++) {
            unsigned long e = ents + 0x20 + (unsigned long)i * 0x20;
            unsigned long sub = u64v((uintptr_t)e + 0x10);
            unsigned long se;
            int sn, j;
            if (!sub) continue;
            se = u64v((uintptr_t)sub + 0x10);
            sn = i32((uintptr_t)sub + 0x40);
            for (j = 0; j < sn && nn < MAXN; j++) {
                unsigned long ee = se + 0x20 + (unsigned long)j * 0x20;
                unsigned long node = u64v((uintptr_t)ee + 0x10);
                if (!node) continue;
                nodes[nn].hid     = i32((uintptr_t)node + 0x10);
                nodes[nn].quality = i32((uintptr_t)node + 0x14);
                nodes[nn].weight  = i32((uintptr_t)node + 0x18);
                nodes[nn].count   = i32((uintptr_t)node + 0x1c);
                nodes[nn].init    = i32((uintptr_t)node + 0x20);
                nn++;
            }
        }
        printf("pool nodes: %d\n", nn);
    }

    for (i = 0; i < nn; i++) {
        int c = nodes[i].quality;
        if (c < 1 || c > 5) continue;
        wAll[c] += nodes[i].weight;
        cAll[c] += nodes[i].count;
        for (k = 0; k < ntarget; k++) {
            if (nodes[i].hid == targets[k]) {
                wTgt[c] += nodes[i].weight;
                cTgt[c] += nodes[i].count;
            }
        }
    }

    printf("\ncost  odds   allWeight  targetWeight  weightShare\n");
    for (i = 1; i <= 5; i++) {
        if (lw[i] <= 0) continue;
        printf(" %d    %3d%%   %-9d  %-12d  %.2f%%\n", i, lw[i], wAll[i], wTgt[i],
               wAll[i] > 0 ? 100.0 * wTgt[i] / wAll[i] : 0.0);
        if (wAll[i] > 0) pslot += (lw[i] / 100.0) * ((double)wTgt[i] / (double)wAll[i]);
    }

    printf("\ntargets:");
    for (k = 0; k < ntarget; k++) {
        int f = -1;
        for (i = 0; i < nn; i++) if (nodes[i].hid == targets[k]) f = i;
        if (f >= 0)
            printf("  %d(cost%d %d/%d w=%d)", targets[k], nodes[f].quality,
                   nodes[f].count, nodes[f].init, nodes[f].weight);
        else
            printf("  %d(NOT IN POOL)", targets[k]);
    }
    printf("\n");

    pshop = 1.0 - pow(1.0 - pslot, 5.0);
    printf("\nper-slot chance : %.4f%%\n", pslot * 100.0);
    printf("per-shop chance : %.4f%%  (5 slots)\n", pshop * 100.0);
    if (pshop > 0.0) {
        printf("expected refresh: %.1f 次\n", 1.0 / pshop);
        printf("expected gold   : %.0f  (2g per refresh)\n", 2.0 / pshop);
    }
    return 0;
}
