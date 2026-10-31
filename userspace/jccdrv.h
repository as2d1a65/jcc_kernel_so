/* jccdrv.h - RT ????????ABI ?? kernel.h??????? */
#ifndef JCCDRV_H
#define JCCDRV_H
#include <stdint.h>
#include <stddef.h>
#include <stdbool.h>

#define OP_CMD_READ          601
#define OP_CMD_WRITE         602
#define OP_CMD_BASE          603
#define OP_CMD_HIDE_PROCESS  605
#define OP_CMD_HWBP_ADD      609
#define OP_CMD_HWBP_GET_HITS 610
#define OP_CMD_HWBP_ENABLE   611
#define OP_CMD_HWBP_CLEAR    612
#define OP_CMD_HWBP_DISABLE  613
#define OP_CMD_UPDATE_TRACKING 614
typedef struct { bool is_active; uintptr_t bp_addr; float x, y, z; } J_TRACKING_DATA;
int drv_tracking(bool active, uintptr_t bp_addr, float x, float y, float z);
#define OP_CMD_HOOK_ATTACH   615
#define OP_CMD_HOOK_DETACH   616
#define OP_CMD_HOOK_STATUS   618
#define OP_CMD_HOOK_READREG  619
typedef struct { int pid; unsigned long target_addr; } J_HOOK_ATTACH;
typedef struct {
    unsigned char active; unsigned char armed; unsigned char pad[2]; int pid;
    unsigned long vaddr; unsigned long target_addr;
    uint32_t rot[3]; uint32_t stack_rot[3]; uint32_t stack_count; uint32_t stack_offset;
    uint64_t hit_count; uint64_t captured_x0;
} J_HOOK_STATUS;
typedef struct { uint32_t reg_idx; uint64_t value; uint64_t hit_count; } J_HOOK_READ_REG;
int drv_hook_attach(int pid, uintptr_t addr);
int drv_hook_status(J_HOOK_STATUS *st);
int drv_hook_readreg(uint32_t idx, uint64_t *val, uint64_t *hits);
#define STACK_ROT_MAX 12
typedef struct {
    uint32_t rot[3];
    uint32_t stack_rot[STACK_ROT_MAX];
    uint32_t stack_count;
    uint32_t stack_offset;
} J_HOOK_SET_ROT;
int drv_hook_set_rot(uint32_t mode, const uint32_t *stack, uint32_t n);
int drv_hook_detach(void);
int drv_unhook(void);
#define OP_CMD_TOUCH_INIT    626
#define OP_CMD_TOUCH_EVENT   627
#define OP_CMD_TOUCH_DESTROY 628

#define HW_BP_TYPE_X 4
#define MAX_MODIFY_REGS 10

typedef struct { int pid; char *name; uintptr_t base; short index; } J_MODULE_BASE;
typedef struct { int pid; uintptr_t addr; void *buffer; size_t size; } J_COPY_MEMORY;
typedef struct {
    int pid; uintptr_t addr; int type; int len;
    bool is_write_gp_regs; int gp_reg_count;
    int gp_reg_indices[MAX_MODIFY_REGS]; uint64_t gp_reg_values[MAX_MODIFY_REGS];
    bool is_write_fp_regs; int fp_reg_count;
    int fp_reg_indices[MAX_MODIFY_REGS]; uint64_t fp_reg_values[MAX_MODIFY_REGS][2];
} J_HW_BP_INFO;
struct J_REGS_INFO { uint64_t regs[31]; uint64_t sp; uint64_t pc; uint64_t pstate; };
struct J_HWBP_HIT_ITEM { int task_id; uintptr_t hit_addr; uint64_t hit_time; struct J_REGS_INFO regs_info; };
typedef struct { int pid; uintptr_t addr; void *out_buf; int out_len; int real_count; } J_HWBP_HIT_ARGS;
typedef struct { int32_t max_x; int32_t max_y; } J_TOUCH_INIT;
typedef struct { uint32_t command; uint32_t x; uint32_t y; uint16_t slot_mask; uint16_t reserved; } J_TOUCH_EVENT;
#define J_TOUCH_DOWN 1
#define J_TOUCH_MOVE 2
#define J_TOUCH_UP   3

int  drv_open(void);
int  drv_attach(int pid);
uintptr_t drv_base(const char *module);
int  drv_read(uintptr_t addr, void *buf, size_t len);
int  drv_read_raw(int pid, uintptr_t addr, void *buf, size_t len, int *out_errno);
int  drv_write(uintptr_t addr, const void *buf, size_t len);
int  drv_read32(uintptr_t addr, uint32_t *out);
int  drv_write32(uintptr_t addr, uint32_t v);
int  drv_hwbp_add(uintptr_t addr, int type, int len);
int  drv_hwbp_enable(uintptr_t addr);
int  drv_hwbp_hits(uintptr_t addr, struct J_HWBP_HIT_ITEM *out, int max);
int  drv_hwbp_hits_ex(uintptr_t addr, struct J_HWBP_HIT_ITEM *out, int out_len);
int  drv_hwbp_clear(void);
int  drv_touch_init(int max_x, int max_y);
int  drv_touch(int cmd, uint32_t x, uint32_t y, uint16_t slot);
int  drv_touch_destroy(void);
int  drv_hide_self(void);
#endif