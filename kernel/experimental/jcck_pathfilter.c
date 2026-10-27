// jcck v6 - ??? kretprobe????? seq ?????? rodata / ??????
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/kprobes.h>
#include <linux/slab.h>
#include <linux/list.h>
#include <linux/kobject.h>
#include <linux/seq_file.h>
#include <linux/proc_fs.h>
#include <linux/uaccess.h>

MODULE_LICENSE("GPL");
MODULE_DESCRIPTION("jcck");
MODULE_VERSION("9.0");
MODULE_IMPORT_NS(VFS_internal_I_am_really_a_filesystem_and_am_NOT_a_driver);

static bool hide = 0;
module_param(hide, bool, 0644);
MODULE_PARM_DESC(hide, "hide module after init");
static bool do_hook = 1;
module_param(do_hook, bool, 0644);
MODULE_PARM_DESC(do_hook, "install hooks");

static unsigned long calls = 0;
module_param(calls, ulong, 0444);
static unsigned long hits = 0;
module_param(hits, ulong, 0444);

static bool do_path = 1;
module_param(do_path, bool, 0644);
MODULE_PARM_DESC(do_path, "hide runtime files from openat/stat");

typedef unsigned long (*kln_t)(const char *);
static kln_t kln;
static int (*p_register_kretprobe)(struct kretprobe *);
static int (*p_unregister_kretprobe)(struct kretprobe *);
static void (*p_kobject_del)(struct kobject *);
static long (*p_strncpy_from_user)(char *, const char __user *, long);
static void (*p_unregister_kprobe)(struct kprobe *);

static size_t j_strlen(const char *s) { size_t n = 0; while (s[n]) n++; return n; }
static int j_memeq(const void *a, const void *b, size_t n)
{
    const unsigned char *x = a, *y = b; size_t i;
    for (i = 0; i < n; i++) if (x[i] != y[i]) return 0;
    return 1;
}

static const char *const pats[] = {
    "xf_game_control_v2", "jcc_helper_", "pool-2-thread", "pool-3-thread",
    "libdemo", "jcc-nb", "xfjcc_ipc_v2", ".ipcb_v2_", "pkf473104974",
    "memfd:libdemo", NULL
};

static int buf_hit(const char *s, size_t n)
{
    int i;
    for (i = 0; pats[i]; i++) {
        size_t pl = j_strlen(pats[i]), k;
        if (pl > n) continue;
        for (k = 0; k + pl <= n; k++)
            if (j_memeq(s + k, pats[i], pl)) return 1;
    }
    return 0;
}

static void blank(char *p, size_t n)
{
    size_t i;
    if (!buf_hit(p, n)) return;
    for (i = 0; i < n; i++) if (p[i] != '\n') p[i] = ' ';
}

static void scrub_from(struct seq_file *m, size_t before)
{
    if (!m || !m->buf) return;
    if (m->count <= before || m->count > m->size) return;
    blank(m->buf + before, m->count - before);
}

/* m_next ? entry ?????????????????????? */
static void scrub_tail(struct seq_file *m)
{
    size_t count, start;
    if (!m || !m->buf) return;
    count = m->count;
    if (!count || count > m->size) return;
    start = count - 1;
    while (start > 0 && m->buf[start - 1] != '\n') start--;
    blank(m->buf + start, count - start);
}

/* ---- ????unix_seq_show / proc_task_name ---- */
struct sf_args { struct seq_file *m; size_t before; };
static int sf_entry(struct kretprobe_instance *ri, struct pt_regs *regs)
{
    struct sf_args *a = (struct sf_args *)ri->data;
    a->m = (struct seq_file *)regs->regs[0];
    a->before = (a->m && a->m->buf) ? a->m->count : 0;
    return 0;
}
static int sf_ret(struct kretprobe_instance *ri, struct pt_regs *regs)
{
    struct sf_args *a = (struct sf_args *)ri->data;
    scrub_from(a->m, a->before);
    return 0;
}
#define SF_RP(name_, sym_) \
static struct kretprobe name_ = { \
    .kp.symbol_name = sym_, .entry_handler = sf_entry, .handler = sf_ret, \
    .data_size = sizeof(struct sf_args), .maxactive = 256, \
}
SF_RP(rp_unix, "unix_seq_show");
SF_RP(rp_comm, "proc_task_name");

/* ---- ????m_next?maps / smaps ???? ---- */
struct sf1 { struct seq_file *m; };
static int next_entry(struct kretprobe_instance *ri, struct pt_regs *regs)
{
    struct sf1 *a = (struct sf1 *)ri->data;
    a->m = (struct seq_file *)regs->regs[0];
    if (a->m && a->m->buf) scrub_tail(a->m);
    return 1;            /* ??????? */
}
static struct kretprobe rp_next = {
    .kp.symbol_name = "m_next",
    .entry_handler = next_entry,
    .data_size = sizeof(struct sf1),
    .maxactive = 256,
};

static int jcck_resolve(void)
{
    struct kprobe kp = { .symbol_name = "kallsyms_lookup_name" };
    int r = register_kprobe(&kp);
    if (r < 0) { pr_err("jcck: bootstrap rc=%d\n", r); return r; }
    kln = (kln_t)kp.addr;
    unregister_kprobe(&kp);
    if (!kln) return -ENOENT;
    p_register_kretprobe   = (void *)kln("register_kretprobe");
    p_unregister_kretprobe = (void *)kln("unregister_kretprobe");
    p_kobject_del          = (void *)kln("kobject_del");
    p_strncpy_from_user    = (void *)kln("strncpy_from_user");
    p_unregister_kprobe    = (void *)kln("unregister_kprobe");
    pr_info("jcck: kr=%px ukr=%px\n", p_register_kretprobe, p_unregister_kretprobe);
    return (p_register_kretprobe && p_unregister_kretprobe) ? 0 : -ENOENT;
}

/* ---------- ???????openat / newfstatat ????? -ENOENT ---------- */
static const char *const badpaths[] = {
    ".xfjcc_ipc_v2_bootstrap", ".ipcb_v2_", "jcc-nb",
    "pkf473104974", "jcc_helper",
    NULL
};

static int path_hit(const char *p)
{
    int i;
    if (!p) return 0;
    for (i = 0; badpaths[i]; i++) {
        size_t pl = j_strlen(badpaths[i]), k;
        size_t n = 0;
        while (p[n] && n < 512) n++;
        if (pl > n) continue;
        for (k = 0; k + pl <= n; k++)
            if (j_memeq(p + k, badpaths[i], pl)) return 1;
    }
    return 0;
}

/* openat: x1 = filename */
static int openat_pre(struct kprobe *p, struct pt_regs *regs)
{
    char buf[256];
    long n;

    if (!p_strncpy_from_user) return 0;
    n = p_strncpy_from_user(buf, (const char __user *)regs->regs[1], sizeof(buf) - 1);
    if (n <= 0) return 0;
    buf[n] = 0;
    if (!path_hit(buf)) return 0;
    regs->regs[0] = (unsigned long)(-2);        /* -ENOENT */
    regs->pc = regs->regs[30];                  /* ????? */
    return 0;
}

/* newfstatat: x1 = filename */
static int fstatat_pre(struct kprobe *p, struct pt_regs *regs)
{
    char buf[256];
    long n;

    if (!p_strncpy_from_user) return 0;
    n = p_strncpy_from_user(buf, (const char __user *)regs->regs[1], sizeof(buf) - 1);
    if (n <= 0) return 0;
    buf[n] = 0;
    if (!path_hit(buf)) return 0;
    regs->regs[0] = (unsigned long)(-2);
    regs->pc = regs->regs[30];
    return 0;
}

static unsigned long readcalls = 0;
module_param(readcalls, ulong, 0444);
static int g_read_rc = 9999;
static int g_openat_rc = 9999, g_fstatat_rc = 9999;
static struct proc_dir_entry *g_stat;

static ssize_t stat_read(struct file *f, char __user *buf, size_t len, loff_t *pos)
{
    char tmp[256];
    int n = snprintf(tmp, sizeof(tmp),
                     "openat_rc=%d fstatat_rc=%d calls=%lu hits=%lu strncpy=%d vfsread_rc=%d readcalls=%lu\n",
                     g_openat_rc, g_fstatat_rc, calls, hits,
                     p_strncpy_from_user ? 1 : 0, g_read_rc, readcalls,
                     p_strncpy_from_user ? 1 : 0);
    return simple_read_from_buffer(buf, len, pos, tmp, n);
}
static int vfsread_pre(struct kprobe *p, struct pt_regs *regs)
{
    readcalls++;
    return 0;
}
static struct kprobe kp_vfsread = { .symbol_name = "vfs_read", .pre_handler = vfsread_pre };

static const struct proc_ops stat_fops = { .proc_read = stat_read };

static struct kprobe kp_openat  = { .symbol_name = "do_sys_openat2", .pre_handler = openat_pre };
static struct kprobe kp_fstatat = { .symbol_name = "vfs_statx",      .pre_handler = fstatat_pre };
static void conceal_self(void)
{
    /* ?? sysfs?kobject_del ??? sysfs ??????????????? */
    if (p_kobject_del)
        p_kobject_del(&__this_module.mkobj.kobj);
    list_del_init(&__this_module.list);
}

static int __init jcck_init(void)
{
    int r;
    pr_info("jcck: init do_hook=%d hide=%d\n", do_hook ? 1 : 0, hide ? 1 : 0);
    r = jcck_resolve();
    if (r) return r;

    if (do_hook) {
        r = p_register_kretprobe(&rp_unix); pr_info("jcck: unix rc=%d\n", r);
        if (r) return r;
        r = p_register_kretprobe(&rp_comm); pr_info("jcck: comm rc=%d\n", r);
        if (r) { p_unregister_kretprobe(&rp_unix); return r; }

    }
    if (do_path) {
        int r2;
        g_openat_rc  = register_kprobe(&kp_openat);
        g_fstatat_rc = register_kprobe(&kp_fstatat);
        pr_info("jcck: openat rc=%d fstatat rc=%d\n", g_openat_rc, g_fstatat_rc);
        g_read_rc = register_kprobe(&kp_vfsread);
        g_stat = proc_create("jcck_stat", 0444, NULL, &stat_fops);
    }

    if (hide) conceal_self();
    pr_info("jcck: ready\n");
    return 0;
}

static void __exit jcck_exit(void)
{
    /* kretprobe ??????????? unregister_kretprobe ?????????
       ????????? panic???????? kretprobe??????????
       ?? kprobe ????????????? do_hook=0 ???? rmmod? */
    if (do_path) {
        if (g_openat_rc  == 0 && p_unregister_kprobe) p_unregister_kprobe(&kp_openat);
        if (g_fstatat_rc == 0 && p_unregister_kprobe) p_unregister_kprobe(&kp_fstatat);
        if (g_read_rc    == 0 && p_unregister_kprobe) p_unregister_kprobe(&kp_vfsread);
        if (g_stat) { remove_proc_entry("jcck_stat", NULL); g_stat = NULL; }
    }
    pr_info("jcck: exit (kretprobes intentionally left)\n");
}
module_init(jcck_init);
module_exit(jcck_exit);