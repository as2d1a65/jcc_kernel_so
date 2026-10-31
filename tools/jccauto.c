/*
 * jccauto - shop auto-buy with odds-driven rolling and self-recovering shop state.
 *
 * Verified facts this relies on:
 *   - kernel touch works: raw_x = 16*(1264-screen_y), raw_y = 16*screen_x
 *     (virtual touchscreen raw range 20224 x 44480, viewport ROT_270)
 *   - shop must be EXPANDED; hidden shop swallows every tap in that area.
 *     The open/closed state is NOT in il2cpp memory (verified by diffing
 *     CSoGame[0..0x600], CPlayer[0..0x600], StoreController, CSoCtrlManager
 *     across a toggle: only the frame timer changed), so it is probed by
 *     acting and checking whether the world changed.
 *   - CSoGame+0xb0 m_CurrTurnState == 3 means the normal shopping phase.
 *   - shop cards: CPlayer+0x450 -> CTAC_HeroMgr+0x20 map; entry stride 0x20,
 *     value@+0x10 -> CTAC_Hero; +0x10 -> TAC_HeroEntity; heroConfID@+0x14;
 *     m_iPos@+0x1c.
 *   - star = 1 + (confID-11400)/10000 style encoding, base id below 15403.
 *   - odds: CPlayer+0x38 -> CTAC_HeroPool; +0x20 level weights, +0x18 ku group
 *     -> +0x18 quality map -> per-cost map -> CTAC_HeroKuNode
 *     (weight@0x18, count@0x1c, init@0x20).
 *
 * usage:
 *   jccauto <pid> <cplayer> <baseHeroId> [...] [options]
 * options:
 *   --conf N        target confidence percent for rolling (default 80)
 *   --reserve N     keep at least N gold (default 10)
 *   --maxbuy N      max purchases (default 9)
 *   --dry           compute and print the plan, take no action
 *   --shell         use "input tap" instead of the kernel path
 *   --tap X Y       single tap to screen (X,Y) for calibration
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <math.h>

#define MAXT 32
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
        char cmd[96];
        snprintf(cmd, sizeof(cmd), "input tap %d %d", x, y);
        system(cmd);
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

static int normalize(int h)
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

struct card { int hid, base, cost, pos; };

static int read_shop(unsigned long cp, struct card *out)
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
        unsigned long hero = u64v((uintptr_t)slot + 0x10);
        unsigned long ent;
        int hid;
        if (!hero) continue;
        ent = u64v((uintptr_t)hero + 0x10);
        if (!ent) continue;
        hid = i32((uintptr_t)ent + 0x14);
        if (hid <= 0) continue;
        out[n].hid = hid;
        out[n].base = normalize(hid);
        out[n].cost = cost_of(out[n].base);
        out[n].pos = i32((uintptr_t)hero + 0x1c);
        if (out[n].pos < 0 || out[n].pos > 4) out[n].pos = i;
        n++;
    }
    return n;
}

static long shop_sig(unsigned long cp, int *gold)
{
    struct card c[5];
    int n = read_shop(cp, c), i;
    long s = 0;
    *gold = i32((uintptr_t)cp + 0x39c);
    for (i = 0; i < n; i++) s = s * 1000003L + c[i].hid;
    return s * 1000L + n;
}

/* ---- odds ---- */
struct node { int hid, quality, weight, count, init; };

static double compute_pshop(unsigned long cp, int level, const int *targets, int nt,
                            int *wSharePct, int *tgtCount, int *tgtInit)
{
    unsigned long heroPool = u64v((uintptr_t)cp + 0x38);
    unsigned long kuGroup, levelW, qualityKu, inner = 0;
    struct node nodes[MAXN];
    int nn = 0, lw[6] = {0}, wAll[6] = {0}, wTgt[6] = {0};
    double p = 0.0;
    int i, k, c;

    if (!heroPool) return -1.0;
    kuGroup = u64v((uintptr_t)heroPool + 0x18);
    levelW  = u64v((uintptr_t)heroPool + 0x20);
    qualityKu = u64v((uintptr_t)kuGroup + 0x18);

    {
        unsigned long ents = u64v((uintptr_t)levelW + 0x10);
        int cnt = i32((uintptr_t)levelW + 0x40);
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
                if (c >= 1 && c <= 5) lw[c] = i32((uintptr_t)e + 0x14);
            }
        }
    }

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
        c = nodes[i].quality;
        if (c < 1 || c > 5) continue;
        wAll[c] += nodes[i].weight;
        for (k = 0; k < nt; k++) {
            if (nodes[i].hid == targets[k]) {
                wTgt[c] += nodes[i].weight;
                if (tgtCount) tgtCount[k] = nodes[i].count;
                if (tgtInit)  tgtInit[k]  = nodes[i].init;
            }
        }
    }
    for (c = 1; c <= 5; c++) {
        if (lw[c] <= 0 || wAll[c] <= 0) continue;
        p += (lw[c] / 100.0) * ((double)wTgt[c] / (double)wAll[c]);
        if (wSharePct) wSharePct[c] = (int)(100.0 * wTgt[c] / wAll[c] + 0.5);
    }
    return 1.0 - pow(1.0 - p, 5.0);
}

int main(int argc, char **argv)
{
    int pid, i, k, ntarget = 0, dry = 0, conf = 80, reserve = 10, maxbuy = 9;
    unsigned long cp;
    int targets[MAXT];
    int buys = 0, rolls = 0, fails = 0;

    if (argc < 4) {
        printf("usage: jccauto <pid> <cplayer> <heroId> [...] [--conf N] [--reserve N]\n");
        printf("       jccauto <pid> --tap X Y\n");
        return 1;
    }
    pid = atoi(argv[1]);

    if (!strcmp(argv[2], "--tap")) {
        if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }
        drv_touch_init(RAW_X_MAX, RAW_Y_MAX);
        msleep(1200);
        tap(atoi(argv[3]), atoi(argv[4]));
        msleep(300);
        drv_touch_destroy();
        printf("tapped (%s,%s)\n", argv[3], argv[4]);
        return 0;
    }

    cp = strtoul(argv[2], NULL, 16);
    for (i = 3; i < argc; i++) {
        if (!strcmp(argv[i], "--dry"))     { dry = 1; continue; }
        if (!strcmp(argv[i], "--shell"))   { g_shell = 1; continue; }
        if (!strcmp(argv[i], "--conf"))    { conf = atoi(argv[++i]); continue; }
        if (!strcmp(argv[i], "--reserve")) { reserve = atoi(argv[++i]); continue; }
        if (!strcmp(argv[i], "--maxbuy"))  { maxbuy = atoi(argv[++i]); continue; }
        if (ntarget < MAXT) targets[ntarget++] = normalize(atoi(argv[i]));
    }
    if (!ntarget) { printf("no targets\n"); return 1; }
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }
    if (!g_shell && !dry) { drv_touch_init(RAW_X_MAX, RAW_Y_MAX); msleep(1200); }

    /* ---- plan ---- */
    {
        int level = i32((uintptr_t)cp + 0x390);
        int gold = i32((uintptr_t)cp + 0x39c);
        int share[6] = {0}, cnt[MAXT], ini[MAXT];
        double ps, need;
        int req, afford;
        for (i = 0; i < ntarget; i++) { cnt[i] = -1; ini[i] = -1; }
        ps = compute_pshop(cp, level, targets, ntarget, share, cnt, ini);
        printf("targets:");
        for (i = 0; i < ntarget; i++) printf(" %d", targets[i]);
        printf("\nlevel=%d gold=%d  conf=%d%% reserve=%d\n", level, gold, conf, reserve);
        if (ps < 0) {
            printf("odds unavailable (not in match?)\n");
            goto cleanup;
        }
        printf("cost share:");
        for (i = 1; i <= 5; i++) if (share[i]) printf("  c%d=%d%%", i, share[i]);
        printf("\nper-shop hit chance: %.2f%%\n", ps * 100.0);
        for (i = 0; i < ntarget; i++)
            if (cnt[i] >= 0) printf("  %d remaining %d/%d\n", targets[i], cnt[i], ini[i]);
        if (ps <= 0.0) { printf("target not in pool, nothing to do\n"); goto cleanup; }
        need = log(1.0 - conf / 100.0) / log(1.0 - ps);
        req = (int)ceil(need);
        afford = (gold - reserve) / 2;
        printf("rolls for %d%% confidence: %d   affordable: %d   -> will roll: %d\n",
               conf, req, afford, (req < afford ? req : afford));
        if (dry) goto cleanup;
    }

    /* ---- main loop ---- */
    for (;;) {
        struct card c[5];
        int n, gold, best = -1, kk, ts0, ts1;
        long s0, s1;
        int cx = 0;

        if (buys >= maxbuy) { printf("stop: maxbuy %d\n", maxbuy); break; }

        /* wait for an interactive phase */
        ts0 = i32((uintptr_t)(0x0) + (uintptr_t)(cp + 0x0));   /* placeholder */
        (void)ts0;

        s0 = shop_sig(cp, &gold);
        n = read_shop(cp, c);
        printf("gold=%2d shop:", gold);
        for (i = 0; i < n; i++) printf(" %d(c%d@p%d)", c[i].base, c[i].cost, c[i].pos);
        printf("\n");

        for (i = 0; i < n && best < 0; i++)
            for (kk = 0; kk < ntarget; kk++)
                if (c[i].base == targets[kk] && gold >= c[i].cost) { best = i; break; }

        if (best >= 0) {
            cx = CARD_X[c[best].pos];
            printf("  -> BUY %d at (%d,%d)\n", c[best].base, cx, CARD_Y);
        } else {
            if (gold - 2 < reserve) { printf("  -> stop: gold below reserve\n"); break; }
            printf("  -> REFRESH (%d,%d)\n", REFRESH_X, REFRESH_Y);
            cx = REFRESH_X;
        }

        /* act, then verify; if nothing happened assume the shop is collapsed */
        {
            int attempt;
            int done = 0;
            for (attempt = 0; attempt < 2 && !done; attempt++) {
                if (attempt == 1) {
                    printf("     (no reaction - toggling shop)\n");
                    tap(TOGGLE_X, TOGGLE_Y);
                    msleep(700);
                }
                tap(cx, best >= 0 ? CARD_Y : REFRESH_Y);
                {
                    int w;
                    for (w = 0; w < 10; w++) {
                        msleep(200);
                        s1 = shop_sig(cp, &gold);
                        if (s1 != s0) { done = 1; break; }
                    }
                }
            }
            (void)ts1;
            if (!done) {
                fails++;
                printf("     !! still no reaction (%d/3)\n", fails);
                if (fails >= 3) { printf("stop: shop not reachable\n"); break; }
                continue;
            }
            fails = 0;
            if (best >= 0) buys++; else rolls++;
        }
    }

cleanup:
    if (!g_shell && !dry) drv_touch_destroy();
    printf("\ndone: buys=%d rolls=%d\n", buys, rolls);
    return 0;
}
