/*
 * jccfields - 读取 Il2CppClass 的字段表（免 hook、免用户输入）
 *
 * 已实测的 Il2CppClass 布局（该游戏 libil2cpp.so，Unity 2021+ ilm2cpp 29~31）：
 *   +0x10  const char*  name
 *   +0x18  const char*  namespaze
 *   +0x78  FieldInfo*   fields        <- 本工具用到这一项
 *
 * FieldInfo（32 字节）：
 *   +0x00  const char*      name
 *   +0x08  const Il2CppType* type
 *   +0x10  Il2CppClass*     parent
 *   +0x18  int32_t          offset
 *   +0x1c  uint32_t         token
 *
 * 用法：
 *   jccfields <pid> <class_addr> [max_fields]
 *
 * 全程只读，走驱动 ioctl。
 */
#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define FIELDS_OFF 0x78
#define FI_SIZE 0x20

static int rd(int pid, uintptr_t a, void *b, size_t n)
{
    (void)pid;
    return drv_read(a, b, n);
}

static int read_cstr(uintptr_t a, char *out, size_t cap)
{
    size_t got = 0;
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
    int pid, maxf = 200, i;
    uintptr_t cls, fields;
    char name[128], ns[128];

    if (argc < 3) { printf("usage: jccfields <pid> <class_addr> [max_fields]\n"); return 1; }
    pid = atoi(argv[1]);
    cls = (uintptr_t)strtoul(argv[2], NULL, 16);
    if (argc > 3) maxf = atoi(argv[3]);
    if (drv_attach(pid) != 0) { printf("attach failed\n"); return 2; }

    name[0] = 0; ns[0] = 0;
    {
        unsigned long p = 0;
        if (rd(pid, cls + 0x10, &p, 8) == 0 && p) read_cstr((uintptr_t)p, ns, sizeof(ns)), name[0] = name[0];
        if (rd(pid, cls + 0x10, &p, 8) == 0 && p) read_cstr((uintptr_t)p, name, sizeof(name));
        if (rd(pid, cls + 0x18, &p, 8) == 0 && p) read_cstr((uintptr_t)p, ns, sizeof(ns));
    }
    printf("class 0x%lx  %s.%s\n", (unsigned long)cls, ns, name);

    if (rd(pid, cls + FIELDS_OFF, &fields, 8) != 0 || !fields) {
        printf("fields 指针读取失败（class+0x%x）\n", FIELDS_OFF);
        return 3;
    }
    printf("fields @ 0x%lx\n\n", (unsigned long)fields);
    printf("  %-34s %-8s %s\n", "字段名", "offset", "token");

    for (i = 0; i < maxf; i++) {
        uintptr_t fi = fields + (uintptr_t)i * FI_SIZE;
        unsigned long fname = 0, ftype = 0, parent = 0;
        int off = 0;
        unsigned int token = 0;
        char fn[160];

        if (rd(pid, fi, &fname, 8) != 0) break;
        if (!fname) { printf("  （共 %d 个字段）\n", i); break; }
        if (rd(pid, fi + 0x08, &ftype, 8) != 0) break;
        if (rd(pid, fi + 0x10, &parent, 8) != 0) break;
        if (rd(pid, fi + 0x18, &off, 4) != 0) break;
        if (rd(pid, fi + 0x1c, &token, 4) != 0) break;

        fn[0] = 0;
        if (read_cstr((uintptr_t)fname, fn, sizeof(fn)) <= 0) snprintf(fn, sizeof(fn), "<0x%lx>", fname);
        printf("  %-34s 0x%-6x 0x%08x\n", fn, (unsigned)off, token);
    }
    return 0;
}
