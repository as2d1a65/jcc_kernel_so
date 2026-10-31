/*
 * jccdash - live board dashboard: every player's state plus the shared hero
 * pool with per-hero hit chance at our current level.
 *
 * Read-only. Everything comes from the driver's access_process_vm path.
 *
 * Verified layout:
 *   Z_PVE.CPlayerGameStatistics  +0x68 -> CPlayer
 *   CPlayer +0x248 i8ChairId  +0x280 bTAC_IsAI  +0x2dc iRank
 *           +0x38c hp  +0x390 level  +0x39c gold  +0x3c0 vecBattleHeroPos
 *           +0x38  CTAC_HeroPool
 *   CPlayer +0x30 -> CSoGame, CSoGame +0x10c mySeatId
 *   CTAC_HeroPool +0x18 ku group -> +0x18 quality map -> per-cost map
 *                 +0x20 level weight map
 *   CTAC_HeroKuNode +0x10 confID +0x14 quality +0x18 weight
 *                   +0x1c count +0x20 initCount
 *
 * usage:
 *   jccdash <pid> <statistics_class> <my_cplayer> [--target id ...] [--loop N]
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <math.h>

#define CHUNK (1u << 20)
#define MAXN 512
#define MAXP 16

struct region { unsigned long lo, hi; char perms[8]; };

static int i32(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }
static int u8(uintptr_t a) { unsigned char v = 0; drv_read(a, &v, 1); return v; }

static int load_regions(int pid, struct region **out)
{
    char p[64], line[1024];
    FILE *f;
    int n = 0, cap = 2048;
    struct region *r = calloc((size_t)cap, sizeof(*r));
    snprintf(p, sizeof(p), "/proc/%d/maps", pid);
    f = fopen(p, "r");
    if (!f) { free(r); return 0; }
    while (fgets(line, sizeof(line), f) && n < cap) {
        unsigned long lo, hi;
        char perms[8] = {0};
        if (sscanf(line, "%lx-%lx %7s", &lo, &hi, perms) < 3) continue;
        r[n].lo = lo; r[n].hi = hi;
        strncpy(r[n].perms, perms, sizeof(r[n].perms) - 1);
        n++;
    }
    fclose(f);
    *out = r;
    return n;
}

struct pl {
    int chair, ai, rank, hp, lv, gold, board;
    unsigned long cp;
};

static int find_players(int pid, unsigned long klass, struct pl *out, int max)
{
    struct region *regs = NULL;
    unsigned char *buf;
    int nreg, i, n = 0;

    nreg = load_regions(pid, &regs);
    if (nreg <= 0) return 0;
    buf = malloc(CHUNK);
    if (!buf) { free(regs); return 0; }

    for (i = 0; i < nreg && n < max; i++) {
        unsigned long a;
        if (regs[i].perms[0] != 'r' || regs[i].perms[1] != 'w') continue;
        for (a = regs[i].lo; a < regs[i].hi && n < max; a += CHUNK) {
            size_t want = (size_t)((regs[i].hi - a) < CHUNK ? (regs[i].hi - a) : CHUNK);
            size_t k;
            if (drv_read((uintptr_t)a, buf, want) != 0) {
                size_t pg;
                for (pg = 0; pg < want; pg += 4096) {
                    size_t pn = (want - pg > 4096) ? 4096 : (want - pg);
                    if (drv_read((uintptr_t)(a + pg), buf + pg, pn) != 0)
                        memset(buf + pg, 0xCD, pn);
                }
            }
            for (k = 0; k + 8 <= want; k += 8) {
                unsigned long v, owner, cp, ownklass;
                int ch, rk, hp, lv;
                memcpy(&v, buf + k, 8);
                if (v != klass) continue;
                owner = u64v((uintptr_t)(a + k) + 0x68);
                if (!owner) continue;
                cp = owner;
                ch = u8((uintptr_t)cp + 0x248);
                rk = i32((uintptr_t)cp + 0x2dc);
                hp = i32((uintptr_t)cp + 0x38c);
                lv = i32((uintptr_t)cp + 0x390);
                ownklass = u64v((uintptr_t)cp);
                if (ch < 0 || ch > 7) continue;
                if (rk < 1 || rk > 8) continue;
                if (hp < 0 || hp > 100) continue;
                if (lv < 1 || lv > 10) continue;
                if (ownklass < 0x7000000000UL || ownklass > 0x7100000000UL) continue;
                {
                    unsigned long board = u64v((uintptr_t)cp + 0x3c0);
                    int bsz = board ? i32((uintptr_t)board + 0x18) : 0;
                    if (bsz < 0 || bsz > 12) bsz = 0;
                    out[n].chair = ch; out[n].ai = u8((uintptr_t)cp + 0x280);
                    out[n].rank = rk; out[n].hp = hp; out[n].lv = lv;
                    out[n].gold = i32((uintptr_t)cp + 0x39c);
                    out[n].board = bsz; out[n].cp = cp;
                    n++;
                }
            }
        }
    }
    free(buf); free(regs);
    return n;
}

struct node { int hid, q, w, cnt, ini; };

static int read_pool(unsigned long cp, int *odds, struct node *nodes, int max)
{
    unsigned long pool = u64v((uintptr_t)cp + 0x38), ku, lw, qku;
    int level = i32((uintptr_t)cp + 0x390), n = 0, i, j;

    if (!pool) return 0;
    ku = u64v((uintptr_t)pool + 0x18);
    lw = u64v((uintptr_t)pool + 0x20);

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
                    int c = i32((uintptr_t)ee + 0x10);
                    if (c >= 1 && c <= 5) odds[c] = i32((uintptr_t)ee + 0x14);
                }
                break;
            }
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
            for (j = 0; j < sn && n < max; j++) {
                unsigned long ee = se + 0x20 + (unsigned long)j * 0x20;
                unsigned long node = u64v((uintptr_t)ee + 0x10);
                if (!node) continue;
                nodes[n].hid = i32((uintptr_t)node + 0x10);
                nodes[n].q   = i32((uintptr_t)node + 0x14);
                nodes[n].w   = i32((uintptr_t)node + 0x18);
                nodes[n].cnt = i32((uintptr_t)node + 0x1c);
                nodes[n].ini = i32((uintptr_t)node + 0x20);
                n++;
            }
        }
    }
    return n;
}

static void msleep(int ms)
{
    struct timespec ts;
    ts.tv_sec = ms / 1000;
    ts.tv_nsec = (long)(ms % 1000) * 1000000L;
    nanosleep(&ts, 0);
}

int main(int argc, char **argv)
{
    int pid, i, k, loop = 0;
    unsigned long klass, cp;
    int targets[16], nt = 0;

    if (argc < 4) {
        printf("usage: jccdash <pid> <statistics_class> <my_cplayer> [--target id..] [--loop N]\n");
        return 1;
    }
    pid = atoi(argv[1]);
    klass = strtoul(argv[2], NULL, 16);
    cp = strtoul(argv[3], NULL, 16);
    for (i = 4; i < argc; i++) {
        if (!strcmp(argv[i], "--loop")) { loop = atoi(argv[++i]); continue; }
        if (!strcmp(argv[i], "--target")) {
            while (i + 1 < argc && argv[i+1][0] != '-') {
                if (nt < 16) targets[nt++] = atoi(argv[++i]);
            }
            continue;
        }
    }
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    if (!strcmp(argv[3], "auto")) {
        struct pl ps0[MAXP];
        int n0 = find_players(pid, klass, ps0, MAXP), j2, seat0 = -1;
        cp = 0;
        for (j2 = 0; j2 < n0 && !cp; j2++) {
            unsigned long game = u64v((uintptr_t)ps0[j2].cp + 0x30);
            if (!game) continue;
            if (seat0 < 0) seat0 = i32((uintptr_t)game + 0x10c);
            if (ps0[j2].chair == seat0) cp = ps0[j2].cp;
        }
        if (!cp) { printf("auto: no match / no seat found (players=%d)\n", n0); return 5; }
    }

    for (;;) {
        struct pl ps[MAXP];
        struct node nodes[MAXN];
        int odds[6] = {0}, wAll[6] = {0}, wTgt[6] = {0}, cntTgt[16], iniTgt[16];
        int np, nn, level, gold, seat = -1, c;
        unsigned long game;

        np = find_players(pid, klass, ps, MAXP);
        nn = read_pool(cp, odds, nodes, MAXN);
        level = i32((uintptr_t)cp + 0x390);
        gold = i32((uintptr_t)cp + 0x39c);
        game = u64v((uintptr_t)cp + 0x30);
        if (game) seat = i32((uintptr_t)game + 0x10c);

        printf("== JCC ==\n");
        printf("seat %d  L%d  G%d\n", seat, level, gold);

        /* players */
        printf("c  AI rk hp lv g pop\n");
        printf("---------------------\n");
        for (i = 0; i < np; i++) {
            int mine = (ps[i].chair == seat);
            printf("%s%-2d %-2d %-2d %-3d %-2d %-3d %d\n",
                   mine ? ">" : " ",
                   ps[i].chair, ps[i].ai, ps[i].rank, ps[i].hp,
                   ps[i].lv, ps[i].gold, ps[i].board);
        }

        /* pool */
        for (i = 0; i < nn; i++) {
            c = nodes[i].q;
            if (c < 1 || c > 5) continue;
            wAll[c] += nodes[i].w;
            for (k = 0; k < nt; k++)
                if (nodes[i].hid == targets[k] && nodes[i].cnt > 0) wTgt[c] += nodes[i].w;
        }
        for (k = 0; k < nt; k++) { cntTgt[k] = 0; iniTgt[k] = 0; }
        for (i = 0; i < nn; i++)
            for (k = 0; k < nt; k++)
                if (nodes[i].hid == targets[k]) { cntTgt[k] = nodes[i].cnt; iniTgt[k] = nodes[i].ini; }

        printf("\nODDS ");
        for (c = 1; c <= 5; c++) printf("%d:%d ", c, odds[c]);
        printf("\nH LEFT/IN TAKEN W HIT%%\n");
        printf("---------------------\n");
        for (c = 1; c <= 5; c++) {
            for (i = 0; i < nn; i++) {
                double p;
                int star = 0;
                if (nodes[i].q != c) continue;
                {
                    double ps = 0;
                    if (odds[c] > 0 && wAll[c] > 0)
                        ps = (odds[c] / 100.0) * (nodes[i].w / (double)wAll[c]);
                    p = 1.0 - pow(1.0 - ps, 5.0);
                }
                if (nodes[i].hid >= 31400) star = 3;
                else if (nodes[i].hid >= 21400) star = 2;
                printf("%d %-6d %2d/%-2d %2d %-3d %5.2f%s\n",
                       c, nodes[i].hid, nodes[i].cnt, nodes[i].ini,
                       nodes[i].ini - nodes[i].cnt, nodes[i].w, p * 100.0,
                       nodes[i].cnt == 0 ? "  (??" : "");
                (void)star;
            }
        }

        /* targets */
        if (nt) {
            double p = 0;
            for (c = 1; c <= 5; c++)
                if (odds[c] > 0 && wAll[c] > 0 && wTgt[c] > 0)
                    p += (odds[c] / 100.0) * (wTgt[c] / (double)wAll[c]);
            printf("\nTGT");
            for (k = 0; k < nt; k++) printf(" %d:%d/%d", targets[k], cntTgt[k], iniTgt[k]);
            printf(" hit %.2f%%", (1.0 - pow(1.0 - p, 5.0)) * 100.0);
            if (p > 0) {
                double need = log(0.2) / log(1.0 - (1.0 - pow(1.0 - p, 5.0)));
                printf("  80pc=%.0f rolls %.0fg", ceil(need), ceil(need) * 2);
            }
            printf("\n");
        }
        printf("=====\n");

        if (!loop) break;
        msleep(loop);
    }
    return 0;
}
