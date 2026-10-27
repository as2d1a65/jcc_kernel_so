#include "jccdrv.h"
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/ioctl.h>

static int g_fd = -1;
static int g_pid = -1;

int drv_open(void)
{
    if (g_fd >= 0) return g_fd;
    /* ?? hook ?? sys_ioctl?? cmd ??????/dev/null ?????????? */
    g_fd = open("/dev/null", O_RDONLY);
    return g_fd;
}

int drv_attach(int pid) { g_pid = pid; return (drv_open() >= 0 && pid > 0) ? 0 : -1; }

uintptr_t drv_base(const char *module)
{
    J_MODULE_BASE mb;
    if (drv_open() < 0 || g_pid <= 0) return 0;
    memset(&mb, 0, sizeof(mb));
    mb.pid = g_pid; mb.name = (char *)module; mb.index = 0;
    if (ioctl(g_fd, OP_CMD_BASE, &mb) != 0) return 0;
    return mb.base;
}

int drv_read(uintptr_t addr, void *buf, size_t len)
{
    J_COPY_MEMORY c;
    if (drv_open() < 0 || g_pid <= 0) return -1;
    memset(&c, 0, sizeof(c));
    c.pid = g_pid; c.addr = addr; c.buffer = buf; c.size = len;
    ioctl(g_fd, OP_CMD_READ, &c);   /* ?????????? */
    return 0;
}

int drv_write(uintptr_t addr, const void *buf, size_t len)
{
    J_COPY_MEMORY c;
    if (drv_open() < 0 || g_pid <= 0) return -1;
    memset(&c, 0, sizeof(c));
    c.pid = g_pid; c.addr = addr; c.buffer = (void *)buf; c.size = len;
    ioctl(g_fd, OP_CMD_WRITE, &c);
    return 0;
}

int drv_read32(uintptr_t addr, uint32_t *out)
{
    uint32_t sentinel = 0xCDCDCDCDu, v = sentinel;
    drv_read(addr, &v, 4);
    if (v == sentinel) return -1;
    *out = v;
    return 0;
}

int drv_write32(uintptr_t addr, uint32_t v) { return drv_write(addr, &v, 4); }

int drv_hwbp_add(uintptr_t addr, int type, int len)
{
    J_HW_BP_INFO info;
    if (drv_open() < 0 || g_pid <= 0) return -1;
    memset(&info, 0, sizeof(info));
    info.pid = g_pid; info.addr = addr; info.type = type; info.len = len;
    return ioctl(g_fd, OP_CMD_HWBP_ADD, &info);
}

int drv_hwbp_enable(uintptr_t addr)
{
    J_HW_BP_INFO info;
    if (drv_open() < 0 || g_pid <= 0) return -1;
    memset(&info, 0, sizeof(info));
    info.pid = g_pid; info.addr = addr; info.type = HW_BP_TYPE_X; info.len = 4;
    return ioctl(g_fd, OP_CMD_HWBP_ENABLE, &info);
}

int drv_hwbp_hits_ex(uintptr_t addr, struct J_HWBP_HIT_ITEM *out, int out_len)
{
    J_HWBP_HIT_ARGS a;
    if (drv_open() < 0 || g_pid <= 0) return -1;
    memset(&a, 0, sizeof(a));
    a.pid = g_pid; a.addr = addr; a.out_buf = out; a.out_len = out_len;
    if (ioctl(g_fd, OP_CMD_HWBP_GET_HITS, &a) != 0) return -1;
    return a.real_count;
}

int drv_tracking(bool active, uintptr_t bp_addr, float x, float y, float z)
{
    J_TRACKING_DATA t;
    if (drv_open() < 0) return -1;
    memset(&t, 0, sizeof(t));
    t.is_active = active; t.bp_addr = bp_addr; t.x = x; t.y = y; t.z = z;
    return ioctl(g_fd, OP_CMD_UPDATE_TRACKING, &t);
}

int drv_hwbp_clear(void) { return (drv_open() < 0) ? -1 : ioctl(g_fd, OP_CMD_HWBP_CLEAR, 0); }

int drv_hook_attach(int pid, uintptr_t addr)
{
    J_HOOK_ATTACH cmd;
    if (drv_open() < 0) return -1;
    cmd.pid = pid; cmd.target_addr = addr;
    return ioctl(g_fd, OP_CMD_HOOK_ATTACH, &cmd);
}

int drv_hook_status(J_HOOK_STATUS *st)
{
    if (drv_open() < 0) return -1;
    memset(st, 0, sizeof(*st));
    return ioctl(g_fd, OP_CMD_HOOK_STATUS, st);
}

int drv_hook_readreg(uint32_t idx, uint64_t *val, uint64_t *hits)
{
    J_HOOK_READ_REG r;
    if (drv_open() < 0) return -1;
    memset(&r, 0, sizeof(r));
    r.reg_idx = idx;
    if (ioctl(g_fd, OP_CMD_HOOK_READREG, &r) != 0) return -1;
    *val = r.value;
    if (hits) *hits = r.hit_count;
    return 0;
}

int drv_hook_set_rot(uint32_t mode, const uint32_t *stack, uint32_t n)
{
    J_HOOK_SET_ROT cfg;
    if (drv_open() < 0) return -1;
    memset(&cfg, 0, sizeof(cfg));
    cfg.rot[2] = mode;
    if (stack && n) {
        uint32_t i;
        if (n > STACK_ROT_MAX) n = STACK_ROT_MAX;
        for (i = 0; i < n; i++) cfg.stack_rot[i] = stack[i];
        cfg.stack_count = n;
    }
    return ioctl(g_fd, 0x266 /* placeholder */, 0), ioctl(g_fd, 617, &cfg);
}

int drv_hook_detach(void) { return (drv_open() < 0) ? -1 : ioctl(g_fd, OP_CMD_HOOK_DETACH, 0); }

int drv_touch_init(int max_x, int max_y)
{
    J_TOUCH_INIT t;
    if (drv_open() < 0) return -1;
    t.max_x = max_x; t.max_y = max_y;
    return ioctl(g_fd, OP_CMD_TOUCH_INIT, &t);
}

int drv_touch(int cmd, uint32_t x, uint32_t y, uint16_t slot)
{
    J_TOUCH_EVENT e;
    if (drv_open() < 0) return -1;
    memset(&e, 0, sizeof(e));
    e.command = (uint32_t)cmd; e.x = x; e.y = y; e.slot_mask = slot;
    return ioctl(g_fd, OP_CMD_TOUCH_EVENT, &e);
}

int drv_touch_destroy(void) { return (drv_open() < 0) ? -1 : ioctl(g_fd, OP_CMD_TOUCH_DESTROY, 0); }
int drv_hide_self(void) { return (drv_open() < 0) ? -1 : ioctl(g_fd, OP_CMD_HIDE_PROCESS, 0); }