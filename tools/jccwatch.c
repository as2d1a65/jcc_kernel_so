/*
 * jccwatch - read-only shop watcher with instant purchase.
 *
 * Reads the 5 shop slots straight out of memory (no writes, no hooks, no
 * injected process) and the moment a target hero shows up it taps that slot
 * through the kernel input device.  Nothing is ever written to the game.
 *
 * Natural refreshes happen every round for free; this catches every one of
 * them and buys within milliseconds, so no refresh is ever wasted.  Paid
 * refreshes are optional (--roll) and budgeted by the live hit probability.
 *
 * Verified layout:
 *   CPlayer+0x450 CTAC_HeroMgr -> +0x20 map (entries@+0x10, count@+0x40,
 *   stride 0x20, value@+0x10) -> CTAC_Hero -> +0x10 TAC_HeroEntity
 *   (+0x14 heroConfID, +0x1c m_iPos on the CTAC_Hero)
 *   CPlayer+0x390 level, CPlayer+0x39c gold
 *   CPlayer+0x38 CTAC_HeroPool (level weights@0x20, ku group@0x18)
 *
 * usage:
 *   jccwatch <pid> <cplayer> <baseHeroId> [...] [options]
 *     --interval MS   poll period (default 200)
 *     --secs N        run for N seconds (default 300)
 *     --maxbuy N      stop after N purchases (default 3)
 *     --roll          allow paid refreshes when the odds justify it
 *     --reserve N     keep at least N gold when rolling (default 20)
 *     --dry           never tap, only report
 *     --shell         use "input tap" instead of the kernel device
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <math.h>

#define MAXT 16
#define MAXN 512
#define CARD_Y    300
#define REFRESH_X 2595
#define REFRESH_Y 830
#define TOGGLE_X  2595
#define TOGGLE_Y  1130
#define SCREEN_H  1264
#define RAW_X_MAX 20224
#define RAW_Y_MAX 44480

static const int CARD_X[5] = {573, 1059, 1545, 2031, 2517};
static int g_shell = 0;

static int i32(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }

static void msleep(int ms)
{
    struct timespec ts;
    ts.tv_sec = ms / 1000;
    ts.tv_nsec = (long)(ms % 1000) * 1000000L;
    nanosleep(&ts, 0);
}

static void tap(int x, int y)
{
    if (g_shell) {
        char c[96];
        snprintf(c, sizeof(c), "input tap %d %d", x, y);
        system(c);
    } else {
        uint32_t rx = (uint32_t)(16 * (SCREEN_H - y));
        uint32_t ry = (uint32_t)(16 * x);
        drv_touch(J_TOUCH_DOWN, rx, ry, 1);
        msleep(60);
        drv_touch(J_TOUCH_MOVE, rx, ry, 1);
        msleep(30);
        drv_touch(J_TOUCH_UP, rx, ry, 1);
    }
}

static int norm(int h)
{
    if (h >= 31400) return h - 20000;
    if (h >= 21400) return h - 10000;
    return h;
}

static int cost_of(int b)
{
    if (b < 11400) return 0;
    if (b < 12400) return 1;
    if (b < 13320) return 2;
    if (b < 14330) return 3;
    if (b < 15270) return 4;
    return 5;
}

struct card { int base, cost, pos; };

static int read_shop(unsigned long cp, struct card *o)
{
    unsigned long mgr = u64v((uintptr_t)cp + 0x450), map, ents;
    int cnt, i, n = 0;
    if (!mgr) return 0;
    map = u64v((uintptr_t)mgr + 0x20);
    if (!map) return 0;
    ents = u64v((uintptr_t)map + 0x10);
    cnt = i32((uintptr_t)map + 0x40);
    if (!ents || cnt <= 0 || cnt > 32) return 0;
    for (i = 0; i < cnt && i < 5; i++) {
        unsigned long slot = ents + 0x20 + (unsigned long)i * 0x20;
        unsigned long hero = u64v((uintptr_t)slot + 0x10), ent;
        int hid;
        if (!hero) continue;
        ent = u64v((uintptr_t)hero + 0x10);
        if (!ent) continue;
        hid = i32((uintptr_t)ent + 0x14);
        if (hid <= 0) continue;
        o[n].base = norm(hid);
        o[n].cost = cost_of(o[n].base);
        o[n].pos = i32((uintptr_t)hero + 0x1c);
        if (o[n].pos < 0 || o[n].pos > 4) o[n].pos = i;
        n++;
    }
    return n;
}

/* live hit chance for the first target, using the same weights the game uses */
static double hit_chance(unsigned long cp, int target)
{
    unsigned long pool = u64v((uintptr_t)cp + 0x38);
    unsigned long ku, lw, qku, inner = 0, ents;
    int level = i32((uintptr_t)cp + 0x390);
    int lwo[6] = {0}, wAll[6] = {0}, wTgt[6] = {0};
    int i, j, c, cnt;
    double p = 0.0;

    if (!pool) return -1.0;
    ku = u64v((uintptr_t)pool + 0x18);
    lw = u64v((uintptr_t)pool + 0x20);

    ents = u64v((uintptr_t)lw + 0x10);
    cnt = i32((uintptr_t)lw + 0x40);
    for (i = 0; i < cnt; i++) {
        unsigned long e = ents + 0x20 + (unsigned long)i * 0x20;
        if (i32((uintptr_t)e + 0x18) == level) { inner = u64v((uintptr_t)e + 0x10); break; }
    }
    if (!inner) return -1.0;
    {
        unsigned long e2 = u64v((uintptr_t)inner + 0x10);
        int n2 = i32((uintptr_t)inner + 0x40);
        for (i = 0; i < n2; i++) {
            unsigned long e = e2 + 0x20 + (unsigned long)i * 0x18;
            c = i32((uintptr_t)e + 0x10);
            if (c >= 1 && c <= 5) lwo[c] = i32((uintptr_t)e + 0x14);
        }
    }

    qku = u64v((uintptr_t)ku + 0x18);
    {
        unsigned long qe = u64v((uintptr_t)qku + 0x10);
        int qn = i32((uintptr_t)qku + 0x40);
        for (i = 0; i < qn; i++) {
            unsigned long e = qe + 0x20 + (unsigned long)i * 0x20;
            unsigned long sub = u64v((uintptr_t)e + 0x10), se;
            int sn;
            if (!sub) continue;
            se = u64v((uintptr_t)sub + 0x10);
            sn = i32((uintptr_t)sub + 0x40);
            for (j = 0; j < sn; j++) {
                unsigned long ee = se + 0x20 + (unsigned long)j * 0x20;
                unsigned long node = u64v((uintptr_t)ee + 0x10);
                int hid, q, w, n;
                if (!node) continue;
                hid = i32((uintptr_t)node + 0x10);
                q   = i32((uintptr_t)node + 0x14);
                w   = i32((uintptr_t)node + 0x18);
                n   = i32((uintptr_t)node + 0x1c);
                if (q < 1 || q > 5) continue;
                wAll[q] += w;
                if (hid == target && n > 0) wTgt[q] += w;
            }
        }
    }
    for (c = 1; c <= 5; c++) {
        if (lwo[c] <= 0 || wAll[c] <= 0) continue;
        p += (lwo[c] / 100.0) * ((double)wTgt[c] / (double)wAll[c]);
    }
    return 1.0 - pow(1.0 - p, 5.0);
}

int main(int argc, char **argv)
{
    int pid, i, k, nt = 0, interval = 200, secs = 300, maxbuy = 3;
    int roll = 0, reserve = 20, dry = 0, buys = 0, rolls = 0;
    unsigned long cp;
    int targets[MAXT];

    if (argc < 4) {
        printf("usage: jccwatch <pid> <cplayer> <heroId> [...] [--interval MS] [--secs N]\n");
        printf("                 [--maxbuy N] [--roll] [--reserve N] [--dry] [--shell]\n");
        return 1;
    }
    pid = atoi(argv[1]);
    cp = strtoul(argv[2], NULL, 16);
    for (i = 3; i < argc; i++) {
        if (!strcmp(argv[i], "--interval")) { interval = atoi(argv[++i]); continue; }
        if (!strcmp(argv[i], "--secs"))     { secs = atoi(argv[++i]); continue; }
        if (!strcmp(argv[i], "--maxbuy"))   { maxbuy = atoi(argv[++i]); continue; }
        if (!strcmp(argv[i], "--reserve"))  { reserve = atoi(argv[++i]); continue; }
        if (!strcmp(argv[i], "--roll"))     { roll = 1; continue; }
        if (!strcmp(argv[i], "--dry"))      { dry = 1; continue; }
        if (!strcmp(argv[i], "--shell"))    { g_shell = 1; continue; }
        if (nt < MAXT) targets[nt++] = norm(atoi(argv[i]));
    }
    if (!nt) { printf("no targets\n"); return 1; }

    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }
    if (!g_shell && !dry) { drv_touch_init(RAW_X_MAX, RAW_Y_MAX); msleep(1200); }

    printf("watching:");
    for (i = 0; i < nt; i++) printf(" %d", targets[i]);
    printf("   interval=%dms maxbuy=%d roll=%d dry=%d\n\n", interval, maxbuy, roll, dry);

    {
        int elapsed = 0, last_report = -1, fails = 0;
        while (elapsed < secs && buys < maxbuy) {
            struct card c[5];
            int n = read_shop(cp, c);
            int gold = i32((uintptr_t)cp + 0x39c);
            int best = -1, kk, cost = 0;

            for (i = 0; i < n && best < 0; i++)
                for (kk = 0; kk < nt; kk++)
                    if (c[i].base == targets[kk] && gold >= c[i].cost) { best = i; break; }

            if (best >= 0) {
                int cx = CARD_X[c[best].pos];
                printf("[buy] %d cost %d -> tap (%d,%d)  gold=%d\n",
                       c[best].base, c[best].cost, cx, CARD_Y, gold);
                if (!dry) {
                    tap(cx, CARD_Y);
                    msleep(400);
                    /* if it did not register, the shop may be collapsed */
                    {
                        struct card c2[5];
                        int n2 = read_shop(cp, c2), still = 0, j;
                        for (j = 0; j < n2; j++) if (c2[j].pos == c[best].pos) still = 1;
                        if (still && fails < 3) {
                            fails++;
                            printf("      (no reaction - toggling shop)\n");
                            tap(TOGGLE_X, TOGGLE_Y);
                            msleep(500);
                            tap(cx, CARD_Y);
                            msleep(400);
                        } else fails = 0;
                    }
                    buys++;
                } else {
                    buys++;
                }
                continue;
            }

            /* optional paid refresh, only when the odds are worth it */
            if (roll && rolls < 4 && gold - 2 >= reserve) {
                double p = hit_chance(cp, targets[0]);
                if (p > 0.08) {
                    printf("[roll] hit %.2f%%  gold=%d -> refresh\n", p * 100.0, gold);
                    if (!dry) { tap(REFRESH_X, REFRESH_Y); msleep(700); }
                    rolls++;
                    continue;
                }
            }

            if (elapsed / 5 != last_report) {
                last_report = elapsed / 5;
                printf("[wait] shop:");
                for (i = 0; i < n; i++) printf(" %d", c[i].base);
                {
                    double p = hit_chance(cp, targets[0]);
                    printf("   gold=%d  hit=%.2f%%  buys=%d\n", gold, p * 100.0, buys);
                }
            }
            msleep(interval);
            elapsed += interval;
        }
    }

    if (!g_shell && !dry) drv_touch_destroy();
    printf("\ndone: buys=%d rolls=%d\n", buys, rolls);
    return 0;
}
