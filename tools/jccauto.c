/*
 * jccauto - shop auto-buy / auto-refresh loop with state-change waiting.
 *
 * Touch: kernel drv_touch is verified NOT to reach the app on this device
 * (virtual input device is created, all ioctls return 0, no touch lands).
 * Default path is Android's "input tap"; --drv selects the kernel path.
 *
 * Coordinates for 2780x1264 landscape (measured from screenshot crops):
 *   shop cards y=300, x = 573 1059 1545 2031 2517
 *   refresh button (2595, 830)
 *
 * usage:
 *   jccauto <pid> <cplayer> <baseHeroId> [...] [options]
 * options:
 *   --dry            plan only, one decision then exit
 *   --drv            use kernel touch instead of input tap
 *   --reserve N      keep at least N gold (default 2)
 *   --maxrolls N     at most N refreshes (default 8)
 *   --maxbuy N       at most N purchases (default 9)
 *   --tap X Y        single tap for calibration
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define MAXT 32
#define CARD_Y      300
#define REFRESH_X   2595
#define REFRESH_Y   830

static const int CARD_X[5] = {573, 1059, 1545, 2031, 2517};

static int g_use_drv = 1;
static int g_toggle = 0;

/* Screen (2780x1264 landscape) -> raw uinput coordinates.
 * Verified on device: virtual touchscreen raw range is 20224 x 44480
 * (= 1264*16, 2780*16) and the display viewport applies ROT_270, so
 *   raw_x = 16 * (1264 - screen_y)
 *   raw_y = 16 * screen_x
 * Confirmed by buying a shop card: gold -1 and the slot emptied. */
#define SCREEN_W 2780
#define SCREEN_H 1264
#define RAW_X_MAX 20224
#define RAW_Y_MAX 44480

static void tap(int x, int y);

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
    if (g_use_drv) {
        uint32_t rx = (uint32_t)(16 * (SCREEN_H - y));
        uint32_t ry = (uint32_t)(16 * x);
        drv_touch(J_TOUCH_DOWN, rx, ry, 1);
        msleep(60);
        drv_touch(J_TOUCH_MOVE, rx, ry, 1);
        msleep(30);
        drv_touch(J_TOUCH_UP, rx, ry, 1);
    } else {
        char cmd[128];
        snprintf(cmd, sizeof(cmd), "input tap %d %d", x, y);
        system(cmd);
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
    unsigned long mgr = u64v((uintptr_t)cp + 0x450);
    unsigned long map, entries;
    int cnt, i, n = 0;

    if (!mgr) return 0;
    map = u64v((uintptr_t)mgr + 0x20);
    if (!map) return 0;
    entries = u64v((uintptr_t)map + 0x10);
    cnt = i32((uintptr_t)map + 0x40);
    if (!entries || cnt <= 0 || cnt > 32) return 0;

    for (i = 0; i < cnt && i < 5; i++) {
        unsigned long slot = entries + 0x20 + (unsigned long)i * 0x20;
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
        n++;
    }
    return n;
}

/* signature of the shop contents + gold, used to detect that the UI reacted */
static long signature(unsigned long cp, int *gold)
{
    struct card c[5];
    int n = read_shop(cp, c), i;
    long s = 0;
    *gold = i32((uintptr_t)cp + 0x39c);
    for (i = 0; i < n; i++) s = s * 1000003L + c[i].hid;
    return s * 1000L + n;
}

int main(int argc, char **argv)
{
    int pid, i, k, ntarget = 0, dry = 0;
    int reserve = 2, maxrolls = 8, maxbuy = 9;
    unsigned long cp = 0;
    int targets[MAXT];
    int rolls = 0, buys = 0, fails = 0;

    if (argc < 4) {
        printf("usage: jccauto <pid> <cplayer> <heroId> [...] [--dry] [--reserve N] [--maxrolls N]\n");
        printf("       jccauto <pid> --tap X Y [rangeX rangeY]\n");
        return 1;
    }
    pid = atoi(argv[1]);

    if (!strcmp(argv[2], "--tap")) {
        int mx = (argc > 5) ? atoi(argv[5]) : 2780;
        int my = (argc > 6) ? atoi(argv[6]) : 1264;
        if (argc > 7 && !strcmp(argv[7], "drv")) g_use_drv = 1;
        if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }
        if (mx) drv_touch_init(mx, my);
        msleep(1500);              /* let InputReader finish adding the device */
        tap(atoi(argv[3]), atoi(argv[4]));
        msleep(400);
        if (mx) drv_touch_destroy();
        printf("tapped (%s,%s) drv=%d\n", argv[3], argv[4], g_use_drv);
        return 0;
    }

    cp = strtoul(argv[2], NULL, 16);
    for (i = 3; i < argc; i++) {
        if (!strcmp(argv[i], "--dry")) { dry = 1; continue; }
        if (!strcmp(argv[i], "--drv")) { g_use_drv = 1; continue; }
        if (!strcmp(argv[i], "--toggle")) { g_toggle = 1; continue; }
        if (!strcmp(argv[i], "--reserve")) { reserve = atoi(argv[++i]); continue; }
        if (!strcmp(argv[i], "--maxrolls")) { maxrolls = atoi(argv[++i]); continue; }
        if (!strcmp(argv[i], "--maxbuy")) { maxbuy = atoi(argv[++i]); continue; }
        if (ntarget < MAXT) targets[ntarget++] = normalize(atoi(argv[i]));
    }
    if (ntarget == 0) { printf("no targets\n"); return 1; }
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }
    if (g_use_drv) {
        int rc = drv_touch_init(RAW_X_MAX, RAW_Y_MAX);
        printf("touch_init(%d,%d) rc=%d\n", RAW_X_MAX, RAW_Y_MAX, rc);
        msleep(1200);
    }

    printf("targets:");
    for (k = 0; k < ntarget; k++) printf(" %d", targets[k]);
    printf("  dry=%d reserve=%d maxrolls=%d maxbuy=%d drv=%d\n\n",
           dry, reserve, maxrolls, maxbuy, g_use_drv);

    /* The shop open/closed state lives only in the Unity UI layer and is not
     * readable from il2cpp memory (verified: toggling changes no byte in
     * CSoGame[0..0x340] nor CPlayer[0..0x540]).  Probe it instead: toggle,
     * try a refresh, and if the shop did not change, toggle back and retry. */
    if (g_toggle) {
        int gold, tries, ok = 0;
        long s0, s1;
        for (tries = 0; tries < 2 && !ok; tries++) {
            printf("ensure-open: toggle%s\n", tries ? " (retry)" : "");
            tap(2595, 1130);
            msleep(500);
            s0 = signature(cp, &gold);
            tap(REFRESH_X, REFRESH_Y);
            msleep(700);
            s1 = signature(cp, &gold);
            if (s1 != s0) { ok = 1; }
        }
        printf("ensure-open: %s\n\n", ok ? "shop is open" : "could not confirm");
    }

    for (;;) {
        struct card c[5];
        int n, gold, best = -1, kk;
        long sig0, sig1;

        if (buys >= maxbuy) { printf("stop: maxbuy reached\n"); break; }

        sig0 = signature(cp, &gold);
        n = read_shop(cp, c);
        printf("gold=%2d shop:", gold);
        for (i = 0; i < n; i++) printf(" %d(c%d)", c[i].base, c[i].cost);
        printf("\n");

        for (i = 0; i < n && best < 0; i++)
            for (kk = 0; kk < ntarget; kk++)
                if (c[i].base == targets[kk] && gold >= c[i].cost) { best = i; break; }

        if (best >= 0) {
            {
                int cx = CARD_X[(c[best].pos >= 0 && c[best].pos < 5) ? c[best].pos : best];
                printf("  -> BUY pos %d (base %d cost %d) at (%d,%d)\n",
                       c[best].pos, c[best].base, c[best].cost, cx, CARD_Y);
                if (dry) break;
                tap(cx, CARD_Y);
            }
        } else {
            if (gold - 2 < reserve) { printf("  -> stop: gold %d < reserve %d + 2\n", gold, reserve); break; }
            if (rolls >= maxrolls) { printf("  -> stop: maxrolls %d reached\n", maxrolls); break; }
            printf("  -> REFRESH at (%d,%d)\n", REFRESH_X, REFRESH_Y);
            if (dry) break;
            tap(REFRESH_X, REFRESH_Y);
            rolls++;
        }

        /* wait until the game state actually changes */
        {
            int changed = 0, w;
            for (w = 0; w < 8; w++) {
                msleep(200);
                sig1 = signature(cp, &gold);
                if (sig1 != sig0) { changed = 1; break; }
            }
            if (!changed) {
                fails++;
                printf("  !! no state change (%d/3)\n", fails);
                if (fails >= 3) { printf("stop: UI not reacting\n"); break; }
            } else {
                fails = 0;
                if (best >= 0) buys++;
            }
        }
    }

    if (!dry && g_use_drv) drv_touch_destroy();
    printf("\ndone: buys=%d rolls=%d\n", buys, rolls);
    return 0;
}
