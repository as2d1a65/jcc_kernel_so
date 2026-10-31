/*
 * jccrngstate - dump the full state of one FRandom instance so the .NET
 * Knuth subtractive generator can be replayed offline.
 *
 * layout: klass+0x00  monitor+0x08  inext+0x10  inextp+0x14
 *         SeedArray+0x18 (int32[56])  count+0x20
 *
 * usage: jccrngstate <pid> <rng_addr>
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>

static int i32(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }

int main(int argc, char **argv)
{
    int pid, i;
    unsigned long rng, sa;

    if (argc < 3) { printf("usage: jccrngstate <pid> <rng_addr>\n"); return 1; }
    pid = atoi(argv[1]);
    rng = strtoul(argv[2], NULL, 16);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    sa = u64v((uintptr_t)rng + 0x18);
    printf("RNG 0x%lx\na=0x%lx\ninext=%d\ninextp=%d\ncount=%d\nseed=0x%lx\n",
           rng, (unsigned long)rng, i32((uintptr_t)rng + 0x10),
           i32((uintptr_t)rng + 0x14), i32((uintptr_t)rng + 0x20), sa);

    /* managed array: klass, monitor, bounds, max_length, then data */
    printf("maxlen=%d\n", i32((uintptr_t)sa + 0x18));
    printf("seedarray=");
    for (i = 0; i < 56; i++) {
        int v = i32((uintptr_t)sa + 0x20 + (unsigned long)i * 4);
        printf("%d%s", v, i < 55 ? "," : "\n");
    }
    return 0;
}
