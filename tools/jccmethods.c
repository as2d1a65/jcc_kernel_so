/*
 * jccmethods - list a class's methods (name + code pointer).
 *
 * Il2CppClass layout verified on-device:
 *   +0x10  name
 *   +0x18  namespaze
 *   +0x78  fields    (FieldInfo*, 32B each)
 *   +0x90  methods   -> raw MethodInfo* array DATA (no managed array header)
 *
 * MethodInfo layout verified (name of first entry read "get_iEntityID"):
 *   +0x00  methodPointer   code address, lands in libil2cpp r-xp
 *   +0x10  const char* name
 *   +0x18  Il2CppClass* klass      <- used to validate/terminate
 *   +0x40  uint32 token
 *
 * usage: jccmethods <pid> <class_addr> [max]
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static int i32v(uintptr_t a) { int v = 0; drv_read(a, &v, 4); return v; }
static unsigned long u64v(uintptr_t a) { unsigned long v = 0; drv_read(a, &v, 8); return v; }

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
    int pid, maxn = 200, i, shown = 0;
    unsigned long cls, arr;
    char nm[128] = {0}, ns[128] = {0};

    if (argc < 3) { printf("usage: jccmethods <pid> <class_addr> [max]\n"); return 1; }
    pid = atoi(argv[1]);
    cls = strtoul(argv[2], NULL, 16);
    if (argc > 3) maxn = atoi(argv[3]);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    {
        unsigned long p = u64v((uintptr_t)cls + 0x10);
        if (p) read_cstr((uintptr_t)p, nm, sizeof(nm));
        p = u64v((uintptr_t)cls + 0x18);
        if (p) read_cstr((uintptr_t)p, ns, sizeof(ns));
        printf("class %s.%s  (0x%lx)\n", ns, nm, cls);
    }

    arr = u64v((uintptr_t)cls + 0x90);
    if (!arr) { printf("methods pointer null (class+0x90)\n"); return 3; }
    printf("methods data @0x%lx\n\n", arr);
    printf("  %-8s %-34s %s\n", "token", "name", "methodPointer");

    for (i = 0; i < 4096 && shown < maxn; i++) {
        unsigned long slot = arr + (unsigned long)i * 8;
        unsigned long mi = u64v((uintptr_t)slot);
        unsigned long mp, np, kls;
        char name[160];
        unsigned int tok;

        if (!mi) break;
        kls = u64v((uintptr_t)mi + 0x18);
        if (kls != cls) break;          /* left this class's method table */

        mp = u64v((uintptr_t)mi + 0x00);
        np = u64v((uintptr_t)mi + 0x10);
        tok = (unsigned int)i32v((uintptr_t)mi + 0x40);
        name[0] = 0;
        if (np) read_cstr((uintptr_t)np, name, sizeof(name));
        if (!name[0]) snprintf(name, sizeof(name), "<0x%lx>", np);
        printf("  0x%06x %-34s 0x%lx\n", tok, name, mp);
        shown++;
    }
    printf("\nmethods: %d\n", shown);
    return 0;
}
