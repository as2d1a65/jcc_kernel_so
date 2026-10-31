// jcck_hook - instruction-level hook for a userspace process, entirely in
// the kernel, without patching any code page.
//
// Mechanism: register_user_hw_breakpoint() arms an ARM64 hardware execution
// breakpoint on an arbitrary userspace address.  When it fires we get the
// process's pt_regs in the overflow handler and may rewrite them, which gives
// two primitives:
//
//   MODE_RET   force the function to return immediately with x0 = value
//              (regs->regs[0] = value; regs->pc = regs->regs[30])
//   MODE_LOG   record the call and its x0..x3, change nothing
//
// Why not an inline patch: libil2cpp pages are file-backed and 4 KB pages hold
// hundreds of hot functions, so a page-granular remap is useless there.  A
// hardware breakpoint is per-address and leaves the code untouched.
//
// Interface: /dev/jcckhook, ioctl only.
//   HOOK_ADD    {pid, addr, mode, value}  -> id
//   HOOK_DEL    {id}
//   HOOK_CLEAR
//   HOOK_STAT   {id} -> hits, last_x0
//
// Symbols that are not exported are resolved through kallsyms_lookup_name,
// bootstrapped with a throwaway kprobe (same trick as jcck_main.c).

#include <linux/module.h>
#include <linux/kernel.h>
#include <linux/kprobes.h>
#include <linux/slab.h>
#include <linux/list.h>
#include <linux/fs.h>
#include <linux/miscdevice.h>
#include <linux/uaccess.h>
#include <linux/sched.h>
#include <linux/sched/task.h>
#include <linux/pid.h>
#include <linux/hw_breakpoint.h>
#include <linux/perf_event.h>
#include <linux/spinlock.h>

MODULE_LICENSE("GPL");
MODULE_DESCRIPTION("jcck_hook");
MODULE_VERSION("1.0");

#define HOOK_MAX 4          /* ARM64 exposes 4-6 breakpoint registers */

enum { MODE_LOG = 0, MODE_RET = 1 };

struct hook_req {
    int      id;
    int      pid;
    unsigned long addr;
    int      mode;
    unsigned long long value;
    unsigned long long hits;
    unsigned long long last_x0;
};

#define HOOK_ADD   _IOWR('J', 1, struct hook_req)
#define HOOK_DEL   _IOW('J', 2, struct hook_req)
#define HOOK_CLEAR _IO('J', 3)
#define HOOK_STAT  _IOWR('J', 4, struct hook_req)

struct hook_slot {
    int used;
    int id;
    int mode;
    unsigned long long value;
    unsigned long long hits;
    unsigned long long last_x0;
    struct task_struct *task;
    struct perf_event *bp;
    unsigned long addr;
};

static struct hook_slot slots[HOOK_MAX];
static DEFINE_SPINLOCK(slot_lock);
static int next_id = 1;

/* ---- resolved kernel symbols ---- */
typedef unsigned long (*kln_t)(const char *);
static kln_t kln;

typedef struct perf_event *(*ruhb_t)(struct perf_event_attr *attr,
                                    void (*triggered)(struct perf_event *,
                                                      struct perf_sample_data *,
                                                      struct pt_regs *),
                                    void *context, struct task_struct *task);
static ruhb_t p_register_user_hw_breakpoint;
static void (*p_unregister_hw_breakpoint)(struct perf_event *);
static struct task_struct *(*p_find_get_task_by_vpid)(pid_t);

static int resolve_syms(void)
{
    struct kprobe kp = { .symbol_name = "kallsyms_lookup_name" };
    struct kprobe kp2 = { .symbol_name = "register_user_hw_breakpoint" };
    unsigned long a = 0;
    int r;

    r = register_kprobe(&kp);
    if (r < 0) { pr_err("jcck_hook: kprobe bootstrap rc=%d\n", r); return r; }
    kln = (kln_t)kp.addr;
    unregister_kprobe(&kp);
    if (!kln) return -ENOENT;

    p_register_user_hw_breakpoint = (ruhb_t)kln("register_user_hw_breakpoint");
    p_unregister_hw_breakpoint    = (void *)kln("unregister_hw_breakpoint");
    p_find_get_task_by_vpid       = (void *)kln("find_get_task_by_vpid");

    if (!p_register_user_hw_breakpoint) {
        /* not exported at all: use the probe address as a fallback */
        r = register_kprobe(&kp2);
        if (r == 0) { a = (unsigned long)kp2.addr; unregister_kprobe(&kp2); }
        if (!a) {
            pr_err("jcck_hook: register_user_hw_breakpoint unavailable\n");
            return -ENOENT;
        }
        p_register_user_hw_breakpoint = (ruhb_t)a;
    }
    if (!p_unregister_hw_breakpoint)
        p_unregister_hw_breakpoint = (void *)kln("unregister_hw_breakpoint");
    if (!p_find_get_task_by_vpid)
        return -ENOENT;

    pr_info("jcck_hook: ruhb=%px unhb=%px findtask=%px\n",
            p_register_user_hw_breakpoint, p_unregister_hw_breakpoint,
            p_find_get_task_by_vpid);
    return 0;
}

/* ---- the breakpoint handler ---- */
static void hook_triggered(struct perf_event *bp, struct perf_sample_data *data,
                           struct pt_regs *regs)
{
    int i;
    unsigned long flags;

    if (!regs) return;

    spin_lock_irqsave(&slot_lock, flags);
    for (i = 0; i < HOOK_MAX; i++) {
        if (!slots[i].used || slots[i].bp != bp) continue;
        slots[i].hits++;
        slots[i].last_x0 = regs->regs[0];
        if (slots[i].mode == MODE_RET) {
            /* force the function to return right now with a chosen x0 */
            regs->regs[0] = (unsigned long)slots[i].value;
            regs->pc      = regs->regs[30];   /* x30 = link register */
        }
        break;
    }
    spin_unlock_irqrestore(&slot_lock, flags);
}

static int slot_add(struct hook_req *rq)
{
    struct perf_event_attr attr;
    struct task_struct *task;
    int i, id = -1;

    if (!p_register_user_hw_breakpoint) return -ENOSYS;

    task = p_find_get_task_by_vpid(rq->pid);
    if (!task) return -ESRCH;

    memset(&attr, 0, sizeof(attr));
    attr.type  = PERF_TYPE_BREAKPOINT;
    attr.size  = sizeof(attr);
    attr.bp_type = HW_BREAKPOINT_X;
    attr.bp_addr = rq->addr;
    attr.bp_len  = HW_BREAKPOINT_LEN_4;
    attr.disabled = 0;

    spin_lock(&slot_lock);
    for (i = 0; i < HOOK_MAX; i++)
        if (!slots[i].used) { id = i; break; }
    spin_unlock(&slot_lock);
    if (id < 0) { put_task_struct(task); return -ENOSPC; }

    {
        struct perf_event *bp = p_register_user_hw_breakpoint(
            &attr, hook_triggered, NULL, task);
        if (IS_ERR(bp)) {
            int e = PTR_ERR(bp);
            put_task_struct(task);
            return e;
        }
        spin_lock(&slot_lock);
        slots[id].used = 1;
        slots[id].id = next_id++;
        slots[id].mode = rq->mode;
        slots[id].value = rq->value;
        slots[id].hits = 0;
        slots[id].last_x0 = 0;
        slots[id].task = task;
        slots[id].bp = bp;
        slots[id].addr = rq->addr;
        rq->id = slots[id].id;
        spin_unlock(&slot_lock);
    }
    pr_info("jcck_hook: armed id=%d pid=%d addr=0x%lx mode=%d val=0x%llx\n",
            rq->id, rq->pid, rq->addr, rq->mode, rq->value);
    return 0;
}

static int slot_del(int id)
{
    int i, found = -1;
    spin_lock(&slot_lock);
    for (i = 0; i < HOOK_MAX; i++)
        if (slots[i].used && slots[i].id == id) { found = i; break; }
    if (found >= 0) slots[found].used = 0;
    spin_unlock(&slot_lock);
    if (found < 0) return -ENOENT;
    if (slots[found].bp && p_unregister_hw_breakpoint)
        p_unregister_hw_breakpoint(slots[found].bp);
    if (slots[found].task) put_task_struct(slots[found].task);
    slots[found].bp = NULL;
    slots[found].task = NULL;
    return 0;
}

static void slot_clear(void)
{
    int i;
    for (i = 0; i < HOOK_MAX; i++)
        if (slots[i].used) slot_del(slots[i].id);
}

static long hook_ioctl(struct file *f, unsigned int cmd, unsigned long arg)
{
    struct hook_req rq;
    int i, r = 0;

    if (cmd == HOOK_CLEAR) { slot_clear(); return 0; }
    if (copy_from_user(&rq, (void __user *)arg, sizeof(rq))) return -EFAULT;

    switch (cmd) {
    case HOOK_ADD:
        r = slot_add(&rq);
        if (r) return r;
        if (copy_to_user((void __user *)arg, &rq, sizeof(rq))) return -EFAULT;
        return 0;
    case HOOK_DEL:
        return slot_del(rq.id);
    case HOOK_STAT:
        spin_lock(&slot_lock);
        for (i = 0; i < HOOK_MAX; i++) {
            if (slots[i].used && slots[i].id == rq.id) {
                rq.hits = slots[i].hits;
                rq.last_x0 = slots[i].last_x0;
                rq.addr = slots[i].addr;
                rq.mode = slots[i].mode;
                break;
            }
        }
        spin_unlock(&slot_lock);
        if (i == HOOK_MAX) return -ENOENT;
        if (copy_to_user((void __user *)arg, &rq, sizeof(rq))) return -EFAULT;
        return 0;
    }
    return -EINVAL;
}

static const struct file_operations hook_fops = {
    .owner = THIS_MODULE,
    .unlocked_ioctl = hook_ioctl,
    .compat_ioctl = hook_ioctl,
};

static struct miscdevice hook_dev = {
    .minor = MISC_DYNAMIC_MINOR,
    .name = "jcckhook",
    .fops = &hook_fops,
    .mode = 0600,
};

static int __init hook_init(void)
{
    int r = resolve_syms();
    if (r) return r;
    r = misc_register(&hook_dev);
    if (r) { pr_err("jcck_hook: misc rc=%d\n", r); return r; }
    pr_info("jcck_hook: ready\n");
    return 0;
}

static void __exit hook_exit(void)
{
    slot_clear();
    misc_deregister(&hook_dev);
    pr_info("jcck_hook: exit\n");
}

module_init(hook_init);
module_exit(hook_exit);
