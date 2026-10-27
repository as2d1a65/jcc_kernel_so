# jcc_kernel_so

jcc（`com.tencent.jkchess`）内核态辅助模块 + 用户态对接层。

内核侧基于 RT-hook（xinhook）驱动之上的**独立模块** `jcck`，用户态是一套不依赖任何注入器的驱动客户端。

---

## 1. 已验证的结果（实测，非推断）

测试设备：一加 PJX110，Android 14（SDK 34），arm64-v8a，KernelSU。

### 内核模块

| 项 | 结果 |
|---|---|
| KMI 对齐 | `struct module` = 1088 字节；`module_layout` CRC = `0xea759d7f` |
| vermagic | `6.1.57-android14-11-o-g54e396734c5c SMP preempt mod_unload modversions aarch64` |
| 加载 | `init_module()` 返回 0 |
| 自隐藏 | `/proc/modules`、`/sys/module`、`lsmod`、`/proc/kallsyms` 全部查不到 |
| 卸载 | **不支持**（见「已知限制」） |

### 过滤器（都带对照组）

```
/proc/net/unix
  @xf_game_control_v2_test  真实 bind 成功 → 读取命中 0（整行抹成空格）
  @zz_benign_test                              → 读取命中 1（原样，未误伤）

/proc/<pid>/task/<tid>/comm
  线程名 pool-2-thread-2  → 读回 16 个空格
  线程名 zz_ctl_name      → 原样
```

### 驱动原语（用户态 `jccd` 实测）

| 原语 | 结果 |
|---|---|
| `base` | `libc.so` → `0x7b640d0000`，与 `/proc/<pid>/maps` 一致 |
| `sym` | 目标进程内 ELF 解析，`malloc` → `0x7b641217cc`，入口首指令 `3f 23 03 d5`（`paciasp`），地址正确 |
| `capture` | 挂 `strlen` 入口抓 x0，`captured_x0 = 0x5f7690d69d`，与探针字符串指针**逐位相同**，可复现 |
| `watch` | 循环重挂抓多次，2 秒内拿到 2 个不同 x0（正好对应靶子的两个缓冲区），re-arm 逻辑正确 |
| `touch` | `touch_init` → `/proc/bus/input/devices` 从 10 条变 11 条（虚拟输入设备注册成功）；`down`/`up` 均 rc=0；`destroy` rc=0 |

`capture` 就是拿游戏对象的那个原语：换成 `CSoGame_FrameTimeTick` 的入口，`captured_x0` 即 `CSoGame*`。

---

## 2. 关键 ABI 发现

### 2.1 `HOOK_SET_ROT.rot[2]` 是模式选择器

`HookAttach` 只是占位，模式为 0 时钩子空转、`hits` 永远 0。这是排查过程中最大的坑。

| mode | 语义 | 实测命中 |
|---|---|---|
| 0 | 空转 | 0 |
| 2 | 抓 x0（一次性，抓完自动 `active=0`） | 1 |
| 3 | 内核拷贝（`stack_rot[0]=extra_offset`、`[1]=skip_bytes`） | 349,363 / 秒 |
| 4 | float3 写入 | 425,003 / 秒 |

所以对接走 `HOOK_ATTACH`(615) + `HOOK_SET_ROT`(617) + `HOOK_STATUS`(618) + `HOOK_READREG`(619)，**放弃 `HWBP_*`(609~613)**——后者在同样地址、同样 target 上 `ADD`/`ENABLE`/`UPDATE_TRACKING` 全返回 0 但 `GET_HITS` 永远 0，需要同类 mode 配置，而 `kernel.h` 没暴露。

### 2.2 目标进程内符号解析不能走 section 头

`libc.so` 的 section 头表在文件末尾（`shoff=0x15d9f8`），**bionic 只映射 PT_LOAD 段，section 头不在内存里**，用 `drv_read` 读回来全是 0。

必须走 `PT_DYNAMIC` → `DT_SYMTAB` / `DT_STRTAB` / `DT_STRSZ` / `DT_SYMENT`。实现在 `userspace/jccd.c:resolve_sym()`。

顺带：`libil2cpp.so` 的 208 个 `il2cpp_*` 导出里，很多 `st_value` 指向 4 字节 `b` 跳板，`follow_veneer()` 会跟一次拿到真实函数体。

### 2.3 IOCTL 返回码不可信

`OP_CMD_READ`(601) 读成功时返回值是 `-1`。判断成功必须看**数据**，不看返回码。`drv_read()` 用 `0xCC` 哨兵判定：读前填 `0xCC`，读后全还是 `0xCC` 才算失败。

### 2.4 重启后 RT 驱动消失，且不能用 /proc/modules 判断

`rtload` 装完的驱动自己也会摘链表，`grep rtqdnb9 /proc/modules` 恒为 0。唯一可靠判据是 `jccd base <pid> libc.so` 是否非 0。

驱动不在时，`ioctl(601/603)` 失败，早期版本的 `drv_read` 会把它当成"读到 0"，看起来一切正常实际全是垃圾。现在的 `drv_read` 先查 `/proc/<pid>/maps`，未映射直接返回失败。

### 2.5 通道不需要设备节点

驱动 hook 的是 `sys_ioctl`，认 `cmd` 号（601~628）不认文件描述符。所以 `open("/dev/null")` 就够，**磁盘上零新增节点**，`/dev` 里干净。

### 2.5 `TouchEvent` 的 `slot_mask` 不能为 0

`touch_event.slot_mask` 校验是 `!slot_mask || (slot_mask & ~0x3ff)` → `EINVAL`。**抬手指（`TOUCH_UP`）也必须给非零掩码**（例如 `1`），传 0 会失败。这和常见触摸注入 API 的习惯相反，是个容易踩的坑。

### 2.6 KernelSU 的两个门槛

1. `.modinfo` 必须带 `import_ns=VFS_internal_I_am_really_a_filesystem_and_am_NOT_a_driver`，否则加载直接 `EPERM`。
2. `su` 的 `CapEff` 是 `0`（只给 uid 0 不给 capability），`insmod` / `finit_module` 全 `EPERM`，必须用 `init_module()` syscall 把模块内容直接喂内核——见 `userspace/` 里 `kload` 的思路（`tools/` 未附，代码见 Commit 历史或自行按 ABI 实现）。

---

## 3. 编译

### 3.1 内核模块

必须用**与设备完全一致的 KMI 头文件**，否则 `struct module` 布局不一致会导致加载即 panic。以 6.1.57 / `android14-6.1` 为例：

```bash
# 1) 取对应 tag 的源码（SUBLEVEL 必须是 57）
git clone --depth=1 --branch ASB-2024-01-05_14-6.1 \
    https://android.googlesource.com/kernel/common /opt/src/k157

# 2) 用设备自己的 config，不能自己套 gki_defconfig
adb shell su -c 'cp /proc/config.gz /data/local/tmp/'
adb pull /data/local/tmp/kconfig.gz .
gunzip -c kconfig.gz > /opt/src/k157/.config

cd /opt/src/k157
./scripts/config --file .config \
  --set-str LOCALVERSION "-android14-11-o-g54e396734c5c" \
  --disable LOCALVERSION_AUTO --disable WERROR --disable TRIM_UNUSED_KSYMS \
  --set-str UNUSED_KSYMS_WHITELIST ""
make ARCH=arm64 LLVM=1 olddefconfig
make ARCH=arm64 LLVM=1 vmlinux -j$(nproc)      # 产出 vmlinux.symvers

# 3) 临时移开 .git，否则 setlocalversion 会往 vermagic 尾部加一个 '+'
mv .git .git_hold
rm -f include/generated/utsrelease.h include/config/kernel.release
make ARCH=arm64 LLVM=1 modules_prepare -j$(nproc)

# 4) 编模块
cp vmlinux.symvers Module.symvers
make ARCH=arm64 LLVM=1 M=$PWD/kernel modules -j$(nproc)
```

**三个必须核对的自检点**（任一不符就别上机）：

```bash
llvm-readelf -S kernel/jcck.ko | grep this_module    # size 必须是 0x440 = 1088
strings -a kernel/jcck.ko | grep '^vermagic='        # 必须与设备 uname -r 逐字相同
strings -a kernel/jcck.ko | grep '^import_ns='       # 必须有 VFS_internal_...
```

`CONFIG_DEBUG_INFO_BTF_MODULES=y` 决定 `struct module` 里有没有 `btf_data_size`/`btf_data` 两个字段——少了它布局就差 16 字节。这是最容易忽略的一条。

`kernel/Module.symvers` 是本设备推导出的一组 CRC，仅供比对参考，**换设备必须重新生成**。

### 3.2 用户态

```bash
clang --target=aarch64-linux-android28 -O2 -fuse-ld=lld -pie \
      -o jccd userspace/jccd.c userspace/jccdrv.c -Iuserspace
```

或用 `tools/build.sh`。

---

## 4. 用法

### 4.0 上机前必查（踩过两次的坑）

**每次重启后 RT 驱动都消失了，必须先重载**，否则所有 `jccd` 命令都"成功"但读到全零：

```bash
su -c "/data/local/tmp/rtload"        # 打印「驱动加载成功」
```

注意：RT 驱动自己也会隐藏，所以 `grep rtqdnb9 /proc/modules` 永远是 0，**不能用它判断驱动在不在**。判据只有一个：`jccd base <pid> libc.so` 返回非 0。

第二坑：`jccd` 的读现在会先查 `/proc/<pid>/maps`，地址没映射直接判失败；读到全零不再是"成功"。见 2.3。

### 4.1 命令

前提：RT 驱动（`rtload`）已加载，`jcck.ko` 已加载且 `hide=1`。

```bash
# 加载（hide=1 之后 rmmod 不可用，重启清空）
kload /data/local/tmp/jcck_v8.ko hide=1

# 模块基址
jccd base <pid> libil2cpp.so

# 目标进程内符号解析
jccd sym <pid> libc.so malloc veneer

# 抓函数入口的 x0（拿游戏对象的核心原语）
jccd capture <pid> <func_addr> <秒> 2

# 循环抓多次，只打印新出现的 x0
jccd watch   <pid> <func_addr> <秒> <最多记录数>

# 触摸注入自检（会注册一个虚拟输入设备，并在 (540,1200) 点一下）
jccd touchtest

# 内存读写与对象图
jccd read  <pid> <addr> <len>          十六进制
jccd dump  <pid> <addr> <len>          hexdump + ASCII（最长 1 MB）
jccd str   <pid> <addr> [maxlen]       读 C 字符串
jccd ptr   <pid> <addr> [depth]        顺着指针链跟
jccd write <pid> <addr> <hex32>
```

---

## 5. 已知限制

1. **`rmmod` 会触发内核 panic → 重启。** 根因在 `unregister_kretprobe`：此内核在模块内存释放后仍会访问 kprobe 实例池。所以 `__exit` 里**故意不注销** kretprobe，模块设计成「装一次、跑到重启」。这是目前唯一的硬限制，也是刻意接受的取舍。
2. **maps 过滤没做。** `show_map_vma` 被编译器内联，kretprobe 不触发；改挂 `m_next` / `show_map` 均导致加载后 panic；改 `proc_pid_maps_op.show` 指针有启动循环风险，主动放弃。替代方案是 payload 走匿名段投递——`/proc/pid/maps` 里就是一条普通 `anon` 行，本来无需隐藏。
3. **`HWBP_*` 通道未打通**，见 2.1。用 `HOOK_ATTACH` + `HOOK_SET_ROT` 替代。
4. `prebuilt/` 里的二进制只对应上述那台设备的内核，换机器必须重编。

---

## 6. 目录

```
kernel/      jcck_main.c  模块源码（seq_file 过滤 + 自隐藏）
             Makefile
             Module.symvers 本设备推导的 CRC 参考
userspace/   jccdrv.h/.c   驱动客户端封装
             jccd.c       命令行：base/sym/secs/read/write/capture/hook/hwbp/touch
             kernel.h     RT 驱动原始 ABI 头（参考）
tools/       hwtest.c      带 volatile 函数指针的热循环靶子
             runtest.sh
prebuilt/    jcck_v8.ko    已验证可加载的模块
             jccd          arm64 用户态工具
```

`tools/hwtest.c` 里那个 `volatile fn_t` 是必需的：不加的话 clang 会把 `getpid()` 之类内联成 `svc #0`，压根不经过被钩的地址，抓不到任何命中。
