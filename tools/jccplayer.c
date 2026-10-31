/*
 * jccplayer - list all players using the real Il2CppClass field table.
 *
 * Layout verified on-device (Unity 2021+ / il2cpp 29~31):
 *   Z_PVE.CPlayerGameStatistics  instance: qword@0 = klass
 *     +0x68  m_stOwner -> Z_PVE.CPlayer   (observed == stats_instance + 0x800)
 *
 *   Z_PVE.CPlayer:
 *     +0x248 int8   i8ChairId
 *     +0x280 bool   bTAC_IsAI
 *     +0x2dc int32  iRank
 *     +0x2e0 int32  iTAC_Level
 *     +0x2ec int32  iTAC_Exp
 *     +0x370 int32  m_iPlayerInitLife
 *     +0x374 int32  m_iPlayerLife
 *     +0x38c int32  m_iFakeLife
 *     +0x390 int32  m_iFakeLevel
 *     +0x394 int32  m_iFakeExtraPopulation
 *     +0x398 int32  m_iFakeExp
 *     +0x39c int32  m_iFakeMoney
 *     +0x3c0 void*  vecBattleHeroPos   (board)
 *     +0x3e0 void*  SurplusHeroList    (bench)
 *     +0x480 int32  m_iTAC_Money
 *
 * usage: jccplayer <pid> <statistics_class_addr>
 * read-only, all accesses via driver ioctl.
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define CHUNK (1u << 20)

struct region { unsigned long lo, hi; char perms[8]; char path[256]; };

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
        char perms[8] = {0}, path[256] = {0};
        if (sscanf(line, "%lx-%lx %7s %*s %*s %*s %255[^\n]", &lo, &hi, perms, path) < 3) continue;
        r[n].lo = lo; r[n].hi = hi;
        strncpy(r[n].perms, perms, sizeof(r[n].perms) - 1);
        strncpy(r[n].path, path, sizeof(r[n].path) - 1);
        n++;
    }
    fclose(f);
    *out = r;
    return n;
}

static int i32(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }
static int u8(uintptr_t a) { unsigned char v = 0; drv_read(a, &v, 1); return v; }

static int read_cstr(uintptr_t a, char *out, size_t cap)
{
    size_t got = 0;
    out[0] = 0;
    while (got + 32 <= cap) {
        unsigned char buf[32];
        size_t i;
        if (drv_read(a + got, buf, sizeof(buf)) != 0) break;
        for (i = 0; i < sizeof(buf); i++) {
            if (buf[i] == 0) { out[got + i] = 0; return (int)(got + i); }
            out[got + i] = (char)buf[i];
        }
        got += sizeof(buf);
    }
    out[cap - 1] = 0;
    return (int)strlen(out);
}

int main(int argc, char **argv)
{
    int pid, nreg, i, shown = 0;
    unsigned long klass;
    struct region *regs = NULL;
    unsigned char *buf;

    if (argc < 3) { printf("usage: jccplayer <pid> <statistics_class_addr>\n"); return 1; }
    pid = atoi(argv[1]);
    klass = strtoul(argv[2], NULL, 16);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    {
        char nm[128] = {0}, ns[128] = {0};
        unsigned long p = u64v((uintptr_t)klass + 0x10);
        if (p) read_cstr((uintptr_t)p, nm, sizeof(nm));
        p = u64v((uintptr_t)klass + 0x18);
        if (p) read_cstr((uintptr_t)p, ns, sizeof(ns));
        printf("class %s.%s  (0x%lx)\n\n", ns, nm, klass);
    }

    nreg = load_regions(pid, &regs);
    if (nreg <= 0) return 2;
    buf = malloc(CHUNK);
    if (!buf) return 2;

    printf(" %-5s %-3s %-5s %-4s %-3s %-5s %-5s %-14s %s\n",
           "chair", "AI", "rank", "hp", "lv", "exp", "gold", "CPlayer", "board");

    for (i = 0; i < nreg; i++) {
        unsigned long a;
        if (regs[i].perms[0] != 'r' || regs[i].perms[1] != 'w') continue;
        for (a = regs[i].lo; a < regs[i].hi; a += CHUNK) {
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
                unsigned long v;
                unsigned long owner, cp, ownklass;
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
                printf(" %-5d %-3d %-5d %-4d %-3d %-5d %-5d 0x%-12lx board=0x%lx\n",
                       ch, u8((uintptr_t)cp + 0x280), rk, hp, lv,
                       i32((uintptr_t)cp + 0x398), i32((uintptr_t)cp + 0x39c),
                       cp, u64v((uintptr_t)cp + 0x3c0));
                shown++;
            }
        }
    }
    printf("\nplayers: %d\n", shown);
    free(buf);
    free(regs);
    return shown ? 0 : 5;
}
