// jcck v6 - ??? kretprobe????? seq ?????? rodata / ??????
#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/kprobes.h>
#include <linux/slab.h>
#include <linux/list.h>
#include <linux/kobject.h>
#include <linux/seq_file.h>

MODULE_LICENSE("GPL");
MODULE_DESCRIPTION("jcck");
MODULE_VERSION("8.0");
MODULE_IMPORT_NS(VFS_internal_I_am_really_a_filesystem_and_am_NOT_a_driver);

static bool hide = 0;
module_param(hide, bool, 0644);
MODULE_PARM_DESC(hide, "hide module after init");
static bool do_hook = 1;
module_param(do_hook, bool, 0644);
MODULE_PARM_DESC(do_hook, "install hooks");

typedef unsigned long (*kln_t)(const char *);
static kln_t kln;
static int (*p_register_kretprobe)(struct kretprobe *);
static int (*p_unregister_kretprobe)(struct kretprobe *);
static void (*p_kobject_del)(struct kobject *);

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
    pr_info("jcck: kr=%px ukr=%px\n", p_register_kretprobe, p_unregister_kretprobe);
    return (p_register_kretprobe && p_unregister_kretprobe) ? 0 : -ENOENT;
}

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
    if (hide) conceal_self();
    pr_info("jcck: ready\n");
    return 0;
}

static void __exit jcck_exit(void)
{
    /* ????? kretprobe????? unregister_kretprobe ????????????
       ?????? panic??????"???????"???????? */
    pr_info("jcck: exit (no unregister by design)\n");
}

module_init(jcck_init);
module_exit(jcck_exit);