/*
 * jccroll - does the client consume RNG draws when the shop changes?
 *
 * Samples the 5 shop hero IDs and the FRandom `count` field in one tight loop
 * (~1 ms).  On every shop change it reports the count delta across that
 * single sample interval.  Background simulation costs roughly 0.5 draw per
 * millisecond, so a real shop roll (5-15 draws) shows up as a clear spike.
 *
 * Pure reads.
 *
 * usage: jccroll <pid> <cplayer> <rng_addr> [--secs N]
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

static int i32(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }

static double now_ms(void)
{
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return ts.tv_sec * 1000.0 + ts.tv_nsec / 1e6;
}

static long shop_sig(unsigned long cp, int *ids)
{
    unsigned long mgr = u64v((uintptr_t)cp + 0x450), map, ents;
    int cnt, i, n = 0;
    long s = 0;

    for (i = 0; i < 5; i++) ids[i] = 0;
    if (!mgr) return -1;
    map = u64v((uintptr_t)mgr + 0x20);
    if (!map) return -1;
    ents = u64v((uintptr_t)map + 0x10);
    cnt = i32((uintptr_t)map + 0x40);
    if (!ents || cnt <= 0 || cnt > 32) return -1;

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
        ids[pos] = hid;
        s = s * 1000003L + hid;
        n++;
    }
    return s * 100 + n;
}

int main(int argc, char **argv)
{
    int pid, i, secs = 60;
    unsigned long cp, rng;
    int prev[5] = {0};
    long psig = -2;
    int pcount = -1;
    double t0;

    if (argc < 4) {
        printf("usage: jccroll <pid> <cplayer> <rng_addr> [--secs N]\n");
        return 1;
    }
    pid = atoi(argv[1]);
    cp = strtoul(argv[2], NULL, 16);
    rng = strtoul(argv[3], NULL, 16);
    for (i = 4; i < argc; i++)
        if (!strcmp(argv[i], "--secs")) secs = atoi(argv[++i]);

    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    printf("%9s  %-26s %8s %8s %6s\n", "t(ms)", "shop", "count_prev", "count_now", "delta");
    t0 = now_ms();
    {
        int samples = 0, total_delta = 0;
        double tend = 0;

        for (;;) {
            int ids[5];
            long sig;
            int count;
            double t = now_ms() - t0;
            if (t > secs * 1000.0) break;

            sig = shop_sig(cp, ids);
            count = i32((uintptr_t)rng + 0x20);

            if (psig != -2) total_delta += (count - pcount);

            if (sig != psig && psig != -2) {
                int k, ch = 0;
                for (k = 0; k < 5; k++) if (ids[k] != prev[k]) ch = 1;
                if (ch) {
                    int d = (pcount >= 0) ? (count - pcount) : 0;
                    printf("%9.2f  %d %d %d %d %d   %8d %8d %6d %s\n",
                           t, ids[0], ids[1], ids[2], ids[3], ids[4],
                           pcount, count, d, d > 3 ? "<== RNG SPIKE" : "");
                }
            }
            for (i = 0; i < 5; i++) prev[i] = ids[i];
            psig = sig;
            pcount = count;
            samples++;
            tend = t;
        }
        printf("\nsamples=%d over %.0f ms, avg draws per sample = %.2f\n",
               samples, tend, samples > 1 ? (double)total_delta / (samples - 1) : 0.0);
        printf("a real shop roll (5 slots) would add ~5-15 draws in ONE sample\n");
    }
    return 0;
}
