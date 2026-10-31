/*
 * jccodds - shop probability calculator and roll-vs-level advisor.
 *
 * Verified chain and layout: see tools/jccodds.c header / docs.
 * Verified against screen: level 3 odds 75/25/0/0/0, level 6 odds 30/40/25/5/0,
 * and 5 shop cards read correctly (IDs matched names on screen).
 *
 * usage: jccodds <pid> <cplayer> <baseHeroId> [more ids...]
 *        baseHeroId is the 1-star form; 2/3-star ids are normalized.
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

/* read level -> cost odds into out[1..5]; returns 0 on failure */
static int read_level_odds(unsigned long levelW, int level, int *out)
{
    unsigned long ents = u64v((uintptr_t)levelW + 0x10);
    int cnt = i32((uintptr_t)levelW + 0x40);
    unsigned long inner = 0;
    int i;
    for (i = 0; i < cnt && ents; i++) {
        unsigned long e = ents + 0x20 + (unsigned long)i * 0x20;
        if (i32((uintptr_t)e + 0x18) == level) { inner = u64v((uintptr_t)e + 0x10); break; }
    }
    if (!inner) return 0;
    {
        unsigned long e2 = u64v((uintptr_t)inner + 0x10);
        int n2 = i32((uintptr_t)inner + 0x40);
        for (i = 0; i < n2; i++) {
            unsigned long e = e2 + 0x20 + (unsigned long)i * 0x18;
            int c = i32((uintptr_t)e + 0x10);
            int v = i32((uintptr_t)e + 0x14);
            if (c >= 1 && c <= 5) out[c] = v;
        }
    }
    return 1;
}

static double p_slot(const int *lw, const int *wAll, const int *wTgt)
{
    double p = 0.0;
    int c;
    for (c = 1; c <= 5; c++) {
        if (lw[c] <= 0 || wAll[c] <= 0) continue;
        p += (lw[c] / 100.0) * ((double)wTgt[c] / (double)wAll[c]);
    }
    return p;
}

int main(int argc, char **argv)
{
    int pid, level, i, k, ntarget, nn = 0;
    unsigned long cp, heroPool, kuGroup, levelW, qualityKu;
    int targets[MAXT];
    struct node nodes[MAXN];
    int wAll[6] = {0}, wTgt[6] = {0}, cAll[6] = {0}, cTgt[6] = {0};
    int lwNow[6] = {0}, lwNext[6] = {0};
    double ps, pshop, psN, psN2, pshopN;

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
    if (!heroPool) { printf("hero pool null\n"); return 3; }
    kuGroup   = u64v((uintptr_t)heroPool + 0x18);
    levelW    = u64v((uintptr_t)heroPool + 0x20);
    qualityKu = u64v((uintptr_t)kuGroup + 0x18);

    printf("level = %d\n", level);
    read_level_odds(levelW, level, lwNow);
    read_level_odds(levelW, level + 1, lwNext);

    /* pool */
    {
        unsigned long ents = u64v((uintptr_t)qualityKu + 0x10);
        int cnt = i32((uintptr_t)qualityKu + 0x40);
        for (i = 0; i < cnt; i++) {
            unsigned long e = ents + 0x20 + (unsigned long)i * 0x20;
            unsigned long sub = u64v((uintptr_t)e + 0x10), se;
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
    }

    for (i = 0; i < nn; i++) {
        int c = nodes[i].quality;
        if (c < 1 || c > 5) continue;
        wAll[c] += nodes[i].weight;
        cAll[c] += nodes[i].count;
        for (k = 0; k < ntarget; k++)
            if (nodes[i].hid == targets[k]) { wTgt[c] += nodes[i].weight; cTgt[c] += nodes[i].count; }
    }

    printf("\ncost | now odds  targetWeight/allWeight  share  | +1 odds\n");
    for (i = 1; i <= 5; i++) {
        if (lwNow[i] <= 0 && lwNext[i] <= 0) continue;
        printf("  %d  |  %3d%%      %-6d / %-6d        %5.2f%%  |  %3d%%\n",
               i, lwNow[i], wTgt[i], wAll[i],
               wAll[i] > 0 ? 100.0 * wTgt[i] / wAll[i] : 0.0, lwNext[i]);
    }

    printf("\ntargets (remaining/init):");
    for (k = 0; k < ntarget; k++) {
        int f = -1;
        for (i = 0; i < nn; i++) if (nodes[i].hid == targets[k]) f = i;
        if (f >= 0) printf("  %d(c%d %d/%d)", targets[k], nodes[f].quality, nodes[f].count, nodes[f].init);
        else printf("  %d(NOT-IN-POOL)", targets[k]);
    }
    printf("\n");

    ps = p_slot(lwNow, wAll, wTgt);
    pshop = 1.0 - pow(1.0 - ps, 5.0);
    psN = p_slot(lwNext, wAll, wTgt);
    pshopN = 1.0 - pow(1.0 - psN, 5.0);

    printf("\n== now (level %d) ==\n", level);
    printf("  per-slot : %.3f%%\n", ps * 100.0);
    printf("  per-shop : %.3f%%\n", pshop * 100.0);
    printf("\n  refreshes  gold  P(at least one)\n");
    for (i = 1; i <= 10; i++) {
        double p = 1.0 - pow(1.0 - pshop, (double)i);
        printf("    %2d       %3d      %6.2f%%\n", i, i * 2, p * 100.0);
    }
    {
        double g50 = pshop > 0 ? 2.0 * log(0.5) / log(1.0 - pshop) : 0;
        double g90 = pshop > 0 ? 2.0 * log(0.1) / log(1.0 - pshop) : 0;
        printf("\n  gold for 50%% confidence : %.0f\n", ceil(g50));
        printf("  gold for 90%% confidence : %.0f\n", ceil(g90));
    }
    printf("\n== after level up (level %d) ==\n", level + 1);
    printf("  per-slot : %.3f%%\n", psN * 100.0);
    printf("  per-shop : %.3f%%\n", pshopN * 100.0);
    if (pshopN > pshop && pshop > 0)
        printf("  level-up raises hit chance by %.1f%% relative\n",
               (pshopN / pshop - 1.0) * 100.0);
    return 0;
}
