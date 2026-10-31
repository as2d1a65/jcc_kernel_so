/*
 * jcctiming - read-only probe: when do the shop hero IDs actually land in
 * memory, and how long after that can a purchase register?
 *
 * Samples the 5 heroConfIDs as fast as the driver allows and logs every
 * transition with a monotonic timestamp.  Optionally taps a slot repeatedly
 * and logs when the purchase finally takes effect, which gives the delay
 * between "data visible to us" and "UI interactive".
 *
 * Nothing is written to the game.
 *
 * usage: jcctiming <pid> <cplayer> [--secs N] [--tap-slot N] [--tap-delay MS]
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define SCREEN_H  1264
#define RAW_X_MAX 20224
#define RAW_Y_MAX 44480
#define CARD_Y    300

static const int CARD_X[5] = {573, 1059, 1545, 2031, 2517};

static int i32(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }

static void msleep(int ms)
{
    struct timespec ts;
    ts.tv_sec = ms / 1000;
    ts.tv_nsec = (long)(ms % 1000) * 1000000L;
    nanosleep(&ts, 0);
}

static double now_ms(void)
{
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return ts.tv_sec * 1000.0 + ts.tv_nsec / 1e6;
}

static void tap(int x, int y)
{
    uint32_t rx = (uint32_t)(16 * (SCREEN_H - y));
    uint32_t ry = (uint32_t)(16 * x);
    drv_touch(J_TOUCH_DOWN, rx, ry, 1);
    msleep(60);
    drv_touch(J_TOUCH_MOVE, rx, ry, 1);
    msleep(30);
    drv_touch(J_TOUCH_UP, rx, ry, 1);
}

/* read the 5 slots as a compact signature plus a printable form */
static void read5(unsigned long cp, int *h, int *n)
{
    unsigned long mgr = u64v((uintptr_t)cp + 0x450), map, ents;
    int cnt, i, k = 0;

    *n = 0;
    for (i = 0; i < 5; i++) h[i] = 0;
    if (!mgr) return;
    map = u64v((uintptr_t)mgr + 0x20);
    if (!map) return;
    ents = u64v((uintptr_t)map + 0x10);
    cnt = i32((uintptr_t)map + 0x40);
    if (!ents || cnt <= 0 || cnt > 32) return;
    for (i = 0; i < cnt && i < 5; i++) {
        unsigned long hero = u64v((uintptr_t)(ents + 0x20 + (unsigned long)i * 0x20) + 0x10);
        unsigned long ent;
        int hid, pos;
        if (!hero) continue;
        ent = u64v((uintptr_t)hero + 0x10);
        if (!ent) continue;
        hid = i32((uintptr_t)ent + 0x14);
        if (hid <= 0) continue;
        pos = i32((uintptr_t)hero + 0x1c);
        if (pos < 0 || pos > 4) pos = i;
        h[pos] = hid;
        k++;
    }
    *n = k;
}

int main(int argc, char **argv)
{
    int pid, i, secs = 120, tap_slot = -1, tap_delay = 0;
    unsigned long cp;
    int prev[5] = {0};
    double t0;

    if (argc < 3) {
        printf("usage: jcctiming <pid> <cplayer> [--secs N] [--tap-slot N] [--tap-delay MS]\n");
        return 1;
    }
    pid = atoi(argv[1]);
    cp = strtoul(argv[2], NULL, 16);
    for (i = 3; i < argc; i++) {
        if (!strcmp(argv[i], "--secs"))      { secs = atoi(argv[++i]); continue; }
        if (!strcmp(argv[i], "--tap-slot"))  { tap_slot = atoi(argv[++i]); continue; }
        if (!strcmp(argv[i], "--tap-delay")) { tap_delay = atoi(argv[++i]); continue; }
    }

    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }
    drv_touch_init(RAW_X_MAX, RAW_Y_MAX);
    msleep(1200);

    printf("sampling %d s, tap_slot=%d tap_delay=%dms\n", secs, tap_slot, tap_delay);
    printf("%10s  %-22s %-6s %s\n", "t(ms)", "slots", "gold", "event");

    t0 = now_ms();
    {
        int gold0 = i32((uintptr_t)cp + 0x39c);
        int tapped = 0;
        for (;;) {
            int cur[5], n, gold, changed = 0;
            double t = now_ms() - t0;
            if (t > secs * 1000.0) break;

            read5(cp, cur, &n);
            gold = i32((uintptr_t)cp + 0x39c);

            for (i = 0; i < 5; i++) if (cur[i] != prev[i]) changed = 1;

            if (changed) {
                printf("%10.2f  %d %d %d %d %d      %-6d shop data changed\n",
                       t, cur[0], cur[1], cur[2], cur[3], cur[4], gold);
                for (i = 0; i < 5; i++) prev[i] = cur[i];
                gold0 = gold;
                tapped = 0;
            }

            if (tap_slot >= 0 && !tapped && t > tap_delay) {
                printf("%10.2f  %-22s %-6d tapping slot %d\n", t, "", gold, tap_slot);
                tap(CARD_X[tap_slot], CARD_Y);
                tapped = 1;
                {
                    double tt = now_ms() - t0;
                    int w;
                    for (w = 0; w < 20; w++) {
                        int g2 = i32((uintptr_t)cp + 0x39c);
                        if (g2 != gold) {
                            printf("%10.2f  %-22s %-6d PURCHASE LANDED (%.0f ms after the data change)\n",
                                   now_ms() - t0, "", g2, now_ms() - t0 - t);
                            break;
                        }
                        msleep(50);
                    }
                    (void)tt;
                }
            }

            if (gold != gold0) {
                printf("%10.2f  %-22s %-6d gold changed\n", t, "", gold);
                gold0 = gold;
            }
            msleep(1);
        }
    }

    drv_touch_destroy();
    printf("done\n");
    return 0;
}
