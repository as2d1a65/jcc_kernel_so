#include "jccdrv.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <time.h>

static long hx(const char *s) { return strtol(s, NULL, 16); }

/* ??????????????????????? ELF??? .dynsym/.dynstr */
static uintptr_t resolve_sym(uintptr_t base, const char *sym, int verbose)
{
    unsigned char eh[64];
    unsigned char ents[2048];
    uint64_t phoff, strsz = 0, syment = 24;
    unsigned short phentsize, phnum;
    uintptr_t dyn = 0, symtab = 0, strtab = 0, found = 0;
    unsigned char *strs = 0, *symbuf = 0;
    int i, n;

    if (drv_read(base, eh, 64) != 0) return 0;
    if (memcmp(eh, "\x7f" "ELF", 4) != 0) { printf("not ELF at 0x%lx\n", (unsigned long)base); return 0; }
    memcpy(&phoff, eh + 0x20, 8);
    memcpy(&phentsize, eh + 0x36, 2);
    memcpy(&phnum, eh + 0x38, 2);
    if (verbose) printf("phoff=0x%lx entsize=%u phnum=%u\n", (unsigned long)phoff, phentsize, phnum);
    if (phnum > 32 || phentsize != 56) { printf("unsupported phdr table\n"); return 0; }
    if (drv_read(base + phoff, ents, (size_t)phentsize * phnum) != 0) { printf("read phdrs failed\n"); return 0; }
    for (i = 0; i < phnum; i++) {
        uint32_t type; uint64_t vaddr;
        memcpy(&type,  ents + i * 56, 4);
        memcpy(&vaddr, ents + i * 56 + 16, 8);
        if (type == 2) { dyn = base + (uintptr_t)vaddr; break; }
    }
    if (!dyn) { printf("no PT_DYNAMIC\n"); return 0; }

    for (n = 0; n < 128; n++) {
        uint64_t tag, val;
        if (drv_read(dyn + (uintptr_t)n * 16, ents, 16) != 0) break;
        memcpy(&tag, ents, 8);
        memcpy(&val, ents + 8, 8);
        if (tag == 0) break;
        if (tag == 6)  symtab = base + (uintptr_t)val;
        if (tag == 5)  strtab = base + (uintptr_t)val;
        if (tag == 10) strsz = val;
        if (tag == 11) syment = val;
    }
    if (verbose) printf("SYMTAB=0x%lx STRTAB=0x%lx STRSZ=%lu syment=%lu\n",
                        (unsigned long)symtab, (unsigned long)strtab,
                        (unsigned long)strsz, (unsigned long)syment);
    if (!symtab || !strtab || !strsz) { printf("dynamic tags missing\n"); return 0; }
    if (strsz > (1u << 22)) strsz = 1u << 22;
    if (syment != 24) syment = 24;

    strs = malloc((size_t)strsz);
    symbuf = malloc((size_t)syment * 512);
    if (!strs || !symbuf) { free(strs); free(symbuf); return 0; }
    if (drv_read(strtab, strs, (size_t)strsz) != 0) { printf("read strtab failed\n"); free(strs); free(symbuf); return 0; }

    for (n = 0; n < 40000 && !found; n += 512) {
        int k;
        if (drv_read(symtab + (uintptr_t)n * (uintptr_t)syment, symbuf, (size_t)syment * 512) != 0) break;
        for (k = 0; k < 512; k++) {
            unsigned char *e = symbuf + (size_t)k * syment;
            uint32_t nmoff; unsigned char info; uint64_t val;
            memcpy(&nmoff, e, 4);
            memcpy(&info,  e + 4, 1);
            memcpy(&val,   e + 8, 8);
            if (!nmoff || (uint64_t)nmoff >= strsz) continue;
            if ((info & 0xf) != 2) continue;
            if (!strcmp((char *)strs + nmoff, sym)) { found = base + (uintptr_t)val; break; }
        }
    }
    free(strs); free(symbuf);
    return found;
}
static uintptr_t follow_veneer(uintptr_t a)
{
    uint32_t insn = 0;
    if (drv_read32(a, &insn) != 0) return a;
    if ((insn & 0x7c000000u) == 0x14000000u) {
        int32_t imm26 = (int32_t)(insn & 0x03ffffffu);
        if (imm26 & 0x02000000) imm26 |= (int32_t)0xfc000000u;
        return a + (intptr_t)(imm26 * 4);
    }
    return a;
}

int main(int argc, char **argv)
{
    if (argc < 2 || (argc < 3 && strcmp(argv[1], "touchtest") != 0)) {
        printf("usage:\n");
        printf("  jccd base  <pid> <module>\n");
        printf("  jccd sym   <pid> <module> <symbol> [veneer]\n");
        printf("  jccd read  <pid> <addr_hex> <len>\n");
        printf("  jccd write <pid> <addr_hex> <hex32>\n");
        printf("  jccd hwbp  <pid> <addr_hex> [sec]\n");
        printf("  jccd touch <x> <y>\n");
        return 1;
    }
    const char *cmd = argv[1];


    if (!strcmp(cmd, "touchtest")) {
        int before = 0, after = 0, k;
        char line[256];
        FILE *f;

        f = fopen("/proc/bus/input/devices", "r");
        if (f) { while (fgets(line, sizeof(line), f)) if (strstr(line, "Name=")) before++; fclose(f); }
        printf("input devices before = %d\n", before);
        printf("touch_init(1080,2400) rc=%d errno=%d\n", drv_touch_init(1080, 2400), errno);
        usleep(200000);
        f = fopen("/proc/bus/input/devices", "r");
        if (f) { while (fgets(line, sizeof(line), f)) if (strstr(line, "Name=")) after++; fclose(f); }
        printf("input devices after  = %d\n", after);
        for (k = 0; k < 1; k++) {
            printf("down rc=%d\n", drv_touch(J_TOUCH_DOWN, 540, 1200, 1));
            usleep(80000);
            printf("up   rc=%d\n", drv_touch(J_TOUCH_UP, 540, 1200, 1));
        }
        printf("touch_destroy rc=%d\n", drv_touch_destroy());
        return (after > before) ? 0 : 6;
    }

    int pid = atoi(argv[2]);
    if (drv_attach(pid) != 0) { printf("attach %d failed\n", pid); return 2; }

    if (!strcmp(cmd, "regs")) {
        uintptr_t a = (uintptr_t)hx(argv[3]);
        int sec = (argc > 4) ? atoi(argv[4]) : 2;
        J_HOOK_STATUS st;
        int i, waited = 0;

        if (drv_hook_attach(pid, a) != 0) { printf("attach failed\n"); return 3; }
        printf("set_rot(2) rc=%d\n", drv_hook_set_rot(2, NULL, 0));
        while (waited < sec * 100 && drv_hook_status(&st) == 0 && !st.captured_x0) {
            usleep(10000); waited++;
        }
        printf("status: active=%u armed=%u hits=%lu x0=0x%lx\n",
               st.active, st.armed, (unsigned long)st.hit_count, (unsigned long)st.captured_x0);
        if (st.captured_x0) {
            printf("--- ?? HOOK_READREG ??? x0..x30 ---\n");
            for (i = 0; i <= 30; i++) {
                uint64_t v = 0, hc = 0;
                if (drv_hook_readreg((uint32_t)i, &v, &hc) == 0 && v)
                    printf("  x%-2d = 0x%016lx\n", i, (unsigned long)v);
            }
        }
        drv_hook_detach();
        return st.captured_x0 ? 0 : 5;
    }
    if (!strcmp(cmd, "base")) {
        if (argc < 4) return 1;
        uintptr_t b = drv_base(argv[3]);
        printf("%s base = 0x%lx\n", argv[3], (unsigned long)b);
        return b ? 0 : 3;
    }

    if (!strcmp(cmd, "sym")) {
        if (argc < 5) return 1;
        uintptr_t b = drv_base(argv[3]);
        if (!b) { printf("module not found\n"); return 3; }
        uintptr_t s = resolve_sym(b, argv[4], 1);
        printf("sym %s = 0x%lx\n", argv[4], (unsigned long)s);
        if (s && argc > 5 && !strcmp(argv[5], "veneer")) {
            uintptr_t r = follow_veneer(s);
            printf("  veneer -> 0x%lx\n", (unsigned long)r);
        }
        return s ? 0 : 4;
    }

    if (!strcmp(cmd, "secs")) {
        if (argc < 4) return 1;
        unsigned char eh[64]; unsigned char sh[64*64]; char names[4096];
        uint64_t shoff, off, size; unsigned short shentsize, shnum, shstrndx;
        int i;
        uintptr_t b = drv_base(argv[3]);
        printf("base=0x%lx\n", (unsigned long)b);
        if (!b) return 3;
        if (drv_read(b, eh, 64) != 0) { printf("read ehdr failed\n"); return 3; }
        memcpy(&shoff, eh + 0x28, 8);
        memcpy(&shentsize, eh + 0x3a, 2);
        memcpy(&shnum, eh + 0x3c, 2);
        memcpy(&shstrndx, eh + 0x3e, 2);
        printf("shoff=0x%lx entsize=%u shnum=%u shstrndx=%u\n",
               (unsigned long)shoff, shentsize, shnum, shstrndx);
        if (drv_read(b + shoff, sh, shentsize * shnum) != 0) { printf("read shdrs failed\n"); return 3; }
        {
            uint32_t type; 
            memcpy(&type, sh, 4);
            printf("sh[0].sh_name=%u sh[0].sh_type=%u (?? 0/0)\n", type, type);
        }
        memcpy(&off,  sh + shstrndx * 64 + 24, 8);
        memcpy(&size, sh + shstrndx * 64 + 32, 8);
        printf("shstrtab off=0x%lx size=%lu\n", (unsigned long)off, (unsigned long)size);
        if (size > sizeof(names)) size = sizeof(names);
        if (drv_read(b + off, names, size) != 0) { printf("read shstrtab failed\n"); return 3; }
        printf("shstrtab[0..40] = ");
        for (i = 0; i < 40; i++) printf("%02x ", (unsigned char)names[i]);
        printf("\n");
        for (i = 0; i < shnum; i++) {
            uint32_t no, ty; uint64_t so, ss;
            memcpy(&no, sh + i*64, 4);
            memcpy(&ty, sh + i*64 + 4, 4);
            memcpy(&so, sh + i*64 + 24, 8);
            memcpy(&ss, sh + i*64 + 32, 8);
            printf("  [%2d] name=%-20.20s type=%-2u off=0x%-8lx size=0x%lx\n",
                   i, (no < size) ? names + no : "?", ty, (unsigned long)so, (unsigned long)ss);
        }
        return 0;
    }

    if (!strcmp(cmd, "read")) {
        uintptr_t a = (uintptr_t)hx(argv[3]);
        int len = atoi(argv[4]), i;
        unsigned char buf[256];
        if (len > 256) len = 256;
        if (drv_read(a, buf, len) != 0) return 3;
        for (i = 0; i < len; i++) printf("%02x%s", buf[i], ((i & 15) == 15) ? "\n" : " ");
        printf("\n");
        return 0;
    }

    if (!strcmp(cmd, "write")) {
        uintptr_t a = (uintptr_t)hx(argv[3]);
        uint32_t v = (uint32_t)hx(argv[4]);
        drv_write32(a, v);
        uint32_t back = 0;
        if (drv_read32(a, &back) != 0) { printf("readback failed\n"); return 3; }
        printf("wrote 0x%08x, readback 0x%08x %s\n", v, back, back == v ? "OK" : "MISMATCH");
        return back == v ? 0 : 4;
    }

    if (!strcmp(cmd, "hwbp")) {
        uintptr_t a = (uintptr_t)hx(argv[3]);
        int sec = (argc > 4) ? atoi(argv[4]) : 2;
        int r1, r2, k;
        static struct J_HWBP_HIT_ITEM hits[16];
        int n, i;
        printf("add hwbp @0x%lx ...\n", (unsigned long)a);
        errno = 0;
        r1 = drv_hwbp_add(a, HW_BP_TYPE_X, 4);
        printf("  add rc=%d errno=%d\n", r1, errno);
        errno = 0;
        r2 = drv_hwbp_enable(a);
        printf("  enable rc=%d errno=%d\n", r2, errno);
        errno = 0;
        r1 = drv_tracking(true, a, 0, 0, 0);
        printf("  tracking(on) rc=%d errno=%d\n", r1, errno);
        sleep(sec);
        for (k = 0; k < 3; k++) {
            int ol = k == 0 ? 16 : (k == 1 ? (int)(16 * sizeof(hits[0])) : 4);
            errno = 0;
            n = drv_hwbp_hits_ex(a, hits, ol);
            printf("  get_hits(out_len=%d) n=%d errno=%d\n", ol, n, errno);
            if (n > 0) break;
        }
        printf("hits = %d\n", n);
        for (i = 0; i < n && i < 4; i++) {
            printf("  #%d tid=%d pc=0x%lx x0=0x%lx x1=0x%lx x2=0x%lx\n",
                   i, hits[i].task_id, (unsigned long)hits[i].regs_info.pc,
                   (unsigned long)hits[i].regs_info.regs[0],
                   (unsigned long)hits[i].regs_info.regs[1],
                   (unsigned long)hits[i].regs_info.regs[2]);
        }
        drv_hwbp_clear();
        return n > 0 ? 0 : 5;
    }

    if (!strcmp(cmd, "hook")) {
        uintptr_t a = (uintptr_t)hx(argv[3]);
        int sec = (argc > 4) ? atoi(argv[4]) : 2;
        J_HOOK_STATUS st;
        uint64_t v = 0, hc = 0;
        int rc;

        errno = 0;
        rc = drv_hook_attach(pid, a);
        printf("hook_attach(0x%lx) rc=%d errno=%d\n", (unsigned long)a, rc, errno);
        sleep(sec);
        errno = 0;
        rc = drv_hook_status(&st);
        printf("hook_status rc=%d errno=%d active=%u armed=%u pid=%d vaddr=0x%lx target=0x%lx hits=%lu x0=0x%lx\n",
               rc, errno, st.active, st.armed, st.pid,
               st.vaddr, st.target_addr,
               (unsigned long)st.hit_count, (unsigned long)st.captured_x0);
        if (drv_hook_readreg(0, &v, &hc) == 0)
            printf("  x0 = 0x%lx (hits=%lu)\n", (unsigned long)v, (unsigned long)hc);
        if (drv_hook_readreg(1, &v, &hc) == 0)
            printf("  x1 = 0x%lx\n", (unsigned long)v);
        return (st.captured_x0 || st.hit_count) ? 0 : 5;
    }

    if (!strcmp(cmd, "capture")) {
        uintptr_t a = (uintptr_t)hx(argv[3]);
        int sec = (argc > 4) ? atoi(argv[4]) : 2;
        uint32_t mode = (argc > 5) ? (uint32_t)atoi(argv[5]) : 2;
        J_HOOK_STATUS st;
        uint64_t v = 0, hc = 0;
        int rc;
        rc = drv_hook_attach(pid, a);
        printf("attach rc=%d\n", rc);
        rc = drv_hook_set_rot(mode, NULL, 0);
        printf("set_rot(mode=%u) rc=%d errno=%d\n", mode, rc, errno);
        sleep(sec);
        rc = drv_hook_status(&st);
        printf("status rc=%d active=%u armed=%u hits=%lu x0=0x%lx\n",
               rc, st.active, st.armed, (unsigned long)st.hit_count, (unsigned long)st.captured_x0);
        if (drv_hook_readreg(0, &v, &hc) == 0)
            printf("  x0=0x%lx hits=%lu\n", (unsigned long)v, (unsigned long)hc);
        drv_hook_detach();
        return st.captured_x0 ? 0 : 5;
    }

    if (!strcmp(cmd, "watch")) {
        uintptr_t a = (uintptr_t)hx(argv[3]);
        int sec  = (argc > 4) ? atoi(argv[4]) : 3;
        int maxn = (argc > 5) ? atoi(argv[5]) : 12;
        uint64_t seen[64]; int nseen = 0, round = 0;
        J_HOOK_STATUS st;
        time_t t0 = time(NULL);

        while ((int)(time(NULL) - t0) < sec && nseen < maxn && round < 4096) {
            uint64_t x0 = 0; int dup = 0, i;
            round++;
            if (drv_hook_attach(pid, a) != 0) break;
            drv_hook_set_rot(2, NULL, 0);
            for (i = 0; i < 200; i++) {
                usleep(300);
                if (drv_hook_status(&st) == 0 && st.captured_x0) { x0 = st.captured_x0; break; }
            }
            drv_hook_detach();
            if (!x0) continue;
            for (i = 0; i < nseen; i++) if (seen[i] == x0) { dup = 1; break; }
            if (!dup) {
                printf("NEW x0 = 0x%lx (round %d)\n", (unsigned long)x0, round);
                if (nseen < 64) seen[nseen++] = x0;
            }
        }
        printf("distinct x0 = %d (rounds=%d, time=%ds)\n", nseen, round, sec);
        return nseen ? 0 : 5;
    }


    if (!strcmp(cmd, "dump")) {
        uintptr_t a = (uintptr_t)hx(argv[3]);
        long len = (argc > 4) ? (long)hx(argv[4]) : 64;
        unsigned char *buf;
        long i, off = 0;

        if (len <= 0 || len > (1 << 20)) { printf("len out of range\n"); return 1; }
        buf = malloc((size_t)len);
        if (!buf) return 1;
        if (drv_read(a, buf, (size_t)len) != 0) {
            /* ???????? 64 ???????????? ?? */
            memset(buf, 0, (size_t)len);
            for (off = 0; off < len; off += 64) {
                long chunk = (len - off > 64) ? 64 : (len - off);
                if (drv_read(a + (uintptr_t)off, buf + off, (size_t)chunk) != 0)
                    memset(buf + off, 0xCD, (size_t)chunk);
            }
        }
        for (off = 0; off < len; off += 16) {
            long k, n = (len - off > 16) ? 16 : (len - off);
            printf("%016lx  ", (unsigned long)(a + (uintptr_t)off));
            for (k = 0; k < 16; k++) {
                if (k < n) printf("%02x ", buf[off + k]);
                else printf("   ");
                if (k == 7) printf(" ");
            }
            printf(" |");
            for (k = 0; k < n; k++) {
                unsigned char c = buf[off + k];
                printf("%c", (c >= 0x20 && c < 0x7f) ? c : '.');
            }
            printf("|\n");
        }
        free(buf);
        return 0;
    }

    if (!strcmp(cmd, "str")) {
        uintptr_t a = (uintptr_t)hx(argv[3]);
        long maxlen = (argc > 4) ? atol(argv[4]) : 256;
        char *buf;
        long i;

        if (maxlen <= 0 || maxlen > 4096) maxlen = 256;
        buf = malloc((size_t)maxlen + 1);
        if (!buf) return 1;
        memset(buf, 0, (size_t)maxlen + 1);
        if (drv_read(a, buf, (size_t)maxlen) != 0) { printf("read failed\n"); free(buf); return 3; }
        buf[maxlen] = 0;
        printf("\"");
        for (i = 0; i < maxlen && buf[i]; i++) {
            unsigned char c = (unsigned char)buf[i];
            printf("%c", (c >= 0x20 && c < 0x7f) ? c : '.');
        }
        printf("\"  (len=%ld)\n", i);
        free(buf);
        return 0;
    }

    if (!strcmp(cmd, "ptr")) {
        uintptr_t a = (uintptr_t)hx(argv[3]);
        int depth = (argc > 4) ? atoi(argv[4]) : 1;
        int d;
        for (d = 0; d < depth; d++) {
            uint64_t v = 0;
            if (drv_read(a, &v, 8) != 0) { printf("  [%d] read failed @0x%lx\n", d, (unsigned long)a); return 3; }
            printf("  [%d] *(0x%lx) = 0x%lx\n", d, (unsigned long)a, (unsigned long)v);
            if (!v || v < 0x1000) break;
            a = (uintptr_t)v;
        }
        return 0;
    }

    if (!strcmp(cmd, "touch")) {
        int x = atoi(argv[2]), y = (argc > 3) ? atoi(argv[3]) : 0;
        drv_touch_init(1080, 2400);
        printf("down <- %d,%d\n", x, y);
        printf("  rc=%d\n", drv_touch(J_TOUCH_DOWN, x, y, 1));
        usleep(60000);
        printf("up\n");
        printf("  rc=%d\n", drv_touch(J_TOUCH_UP, x, y, 1));
        drv_touch_destroy();
        return 0;
    }

    printf("unknown cmd %s\n", cmd);
    return 1;
}