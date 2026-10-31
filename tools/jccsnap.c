/*
 * jccsnap - dump every board entity's raw bytes into a binary snapshot file.
 *
 * Purpose: combine-before / combine-after byte diff to locate the star field.
 *
 * Record layout (little endian):
 *   u64 entity_addr
 *   u32 heroConfID
 *   u32 size                     (bytes captured, <= SNAP_SIZE)
 *   u8  bytes[size]
 *
 * usage: jccsnap <pid> <cplayer_addr> <out_file>
 * read-only, all accesses via driver ioctl.
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define SNAP_SIZE 0x20

static int i32(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }

int main(int argc, char **argv)
{
    int pid, n, i;
    unsigned long cp, list, items, size;
    FILE *out;
    unsigned long ent;

    if (argc < 4) { printf("usage: jccsnap <pid> <cplayer_addr> <out_file>\n"); return 1; }
    pid = atoi(argv[1]);
    cp = strtoul(argv[2], NULL, 16);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    out = fopen(argv[3], "wb");
    if (!out) { printf("cannot open %s\n", argv[3]); return 3; }

    list = u64v((uintptr_t)cp + 0x3c0);
    if (!list) { printf("board list null\n"); fclose(out); return 3; }
    items = u64v((uintptr_t)list + 0x10);
    size = u64v((uintptr_t)list + 0x18);
    if (!items || size > 32) { printf("unexpected list\n"); fclose(out); return 3; }
    n = (int)size;

    for (i = 0; i < n; i++) {
        unsigned long slot = items + 0x20 + (unsigned long)i * 8;
        unsigned long elem = u64v((uintptr_t)slot);
        unsigned char buf[SNAP_SIZE];
        unsigned int hid = 0, sz = SNAP_SIZE;

        if (!elem) continue;
        ent = u64v((uintptr_t)elem + 0x10);
        if (!ent) continue;
        hid = (unsigned int)i32((uintptr_t)ent + 0x14);
        memset(buf, 0xCD, sizeof(buf));
        if (drv_read((uintptr_t)ent, buf, SNAP_SIZE) != 0) continue;

        fwrite(&ent, 8, 1, out);
        fwrite(&hid, 4, 1, out);
        fwrite(&sz, 4, 1, out);
        fwrite(buf, 1, SNAP_SIZE, out);
        printf("  [%d] ent=0x%lx hero=%u\n", i, ent, hid);
    }
    fclose(out);
    printf("snapshot written: %s  (%d units)\n", argv[3], n);
    return 0;
}
