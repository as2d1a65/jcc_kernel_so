/*
 * jccboard - dump one player's board (hero config id + hex position).
 *
 * Verified chain:
 *   CPlayer + 0x3c0  vecBattleHeroPos  -> List<Z_PVE.TAC_BattleHeroPos>
 *   List<T> layout:  +0x10 _items (T[])   +0x18 _size
 *   T[] layout:      +0x18 max_length     elements from +0x20
 *   TAC_BattleHeroPos: +0x10 stHeroEntity  +0x18 x  +0x1c y
 *   TAC_HeroEntity:    +0x10 _iEntityID    +0x14 _iHeroConfID
 *
 * usage: jccboard <pid> <cplayer_addr>
 * read-only, all accesses via driver ioctl.
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>

static int i32(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }

int main(int argc, char **argv)
{
    int pid, n, i;
    unsigned long cp, list, items, size;

    if (argc < 3) { printf("usage: jccboard <pid> <cplayer_addr>\n"); return 1; }
    pid = atoi(argv[1]);
    cp = strtoul(argv[2], NULL, 16);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    list = u64v((uintptr_t)cp + 0x3c0);
    printf("CPlayer 0x%lx  vecBattleHeroPos=0x%lx\n", cp, list);
    if (!list) { printf("board list is null\n"); return 3; }

    items = u64v((uintptr_t)list + 0x10);
    size = u64v((uintptr_t)list + 0x18);
    printf("  _items=0x%lx  _size=%lu\n\n", items, size);
    if (!items || size > 32) { printf("unexpected list shape\n"); return 3; }
    n = (int)size;

    printf(" idx  pos      entity        heroConfID star base      entityID\n");
    for (i = 0; i < n; i++) {
        unsigned long slot = items + 0x20 + (unsigned long)i * 8;
        unsigned long elem, ent;
        int x, y, hid, eid;
        /* ?????TAC_BattleHeroPos ??????????????? elem ???????????? */
        elem = u64v((uintptr_t)slot);
        if (!elem) continue;
        ent = u64v((uintptr_t)elem + 0x10);
        x = i32((uintptr_t)elem + 0x18);
        y = i32((uintptr_t)elem + 0x1c);
        if (!ent) continue;
        hid = i32((uintptr_t)ent + 0x14);
        eid = i32((uintptr_t)ent + 0x10);
        {
            int star, base;
            if (hid >= 31400)      { star = 3; base = hid - 20000; }
            else if (hid >= 21400) { star = 2; base = hid - 10000; }
            else                   { star = 1; base = hid; }
            printf(" %-4d (%d,%d)   0x%-12lx %-6d %dstar  base=%-6d 0x%x\n",
                   i, x, y, ent, hid, star, base, eid);
        }
    }
    return 0;
}
