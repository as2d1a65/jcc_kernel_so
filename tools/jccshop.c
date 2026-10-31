/*
 * jccshop - dump the 5 hero shop cards.
 *
 * Verified chain:
 *   CPlayer + 0x50   StoreController (not the hero shop)
 *   CPlayer + 0x450  m_stTACBuyHero -> Z_PVE.CTAC_HeroMgr
 *        +0x20 m_mapHeroMgr  LinkedHashMap`2
 *        +0x28 m_iMaxHeroNum = 5
 *        +0x2c m_iPosType    = 3
 *        +0x30 m_lUsedPos    bitmask of occupied slots
 *   map +0x10 entries array, +0x40 internalCount
 *   entry stride 0x20: +0x00 hash, +0x04 next, +0x08 prev, +0x0C seq,
 *                      +0x10 obj (CTAC_Hero*), +0x18 slot
 *   Z_PVE.CTAC_Hero : +0x10 m_stHeroEntity (TAC_HeroEntity*), +0x1c m_iPos
 *   TAC_HeroEntity  : +0x10 _iEntityID, +0x14 _iHeroConfID
 *
 * Star / cost decoding (measured):
 *   heroConfID = base + (star-1)*10000
 *   star 1: 11400..15403    star 2: 21400..25403    star 3: 31400..35403
 *   cost tiers by base id: 11400 12400 13320 14330 15270
 *
 * usage: jccshop <pid> <cplayer_addr>
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>

static int i32(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }

static int cost_of(int base)
{
    if (base < 11400) return 0;
    if (base < 12400) return 1;
    if (base < 13320) return 2;
    if (base < 14330) return 3;
    if (base < 15270) return 4;
    return 5;
}

int main(int argc, char **argv)
{
    int pid, i, cnt;
    unsigned long cp, mgr, map, entries;
    int maxn, used;

    if (argc < 3) { printf("usage: jccshop <pid> <cplayer_addr>\n"); return 1; }
    pid = atoi(argv[1]);
    cp = strtoul(argv[2], NULL, 16);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    mgr = u64v((uintptr_t)cp + 0x450);
    if (!mgr) { printf("buy hero manager is null (not in a match?)\n"); return 3; }
    maxn = i32((uintptr_t)mgr + 0x28);
    used = i32((uintptr_t)mgr + 0x30);
    printf("CTAC_HeroMgr 0x%lx  m_iMaxHeroNum=%d  m_iPosType=%d  m_lUsedPos=0x%x\n",
           mgr, maxn, i32((uintptr_t)mgr + 0x2c), used);

    map = u64v((uintptr_t)mgr + 0x20);
    if (!map) { printf("hero map is null\n"); return 3; }
    entries = u64v((uintptr_t)map + 0x10);
    cnt = i32((uintptr_t)map + 0x40);
    printf("map 0x%lx  entries=0x%lx  internalCount=%d\n\n", map, entries, cnt);
    if (!entries || cnt <= 0 || cnt > 32) { printf("unexpected map shape\n"); return 3; }

    printf(" slot  pos  heroConfID  star  base   cost  entityID\n");
    for (i = 0; i < cnt; i++) {
        unsigned long slot_addr = entries + 0x20 + (unsigned long)i * 0x20;
        unsigned long hero = u64v((uintptr_t)slot_addr + 0x10);
        unsigned long ent;
        int pos, hid, eid, star, base;
        if (!hero) continue;
        ent = u64v((uintptr_t)hero + 0x10);
        pos = i32((uintptr_t)hero + 0x1c);
        if (!ent) { printf(" %-4d  %-4d  <empty>\n", i, pos); continue; }
        eid  = i32((uintptr_t)ent + 0x10);
        hid  = i32((uintptr_t)ent + 0x14);
        if (hid >= 31400)      { star = 3; base = hid - 20000; }
        else if (hid >= 21400) { star = 2; base = hid - 10000; }
        else                   { star = 1; base = hid; }
        printf(" %-4d  %-4d  %-10d  %-4d  %-5d  %-4d  0x%x\n",
               i, pos, hid, star, base, cost_of(base), eid);
    }
    return 0;
}
