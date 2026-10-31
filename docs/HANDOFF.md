# jcc 内核态辅助 — 总交接文档

> 目标读者：换一台电脑接手开发的 AI 或人。读完这份文档应当能独立重建环境、复现已验证能力、并沿既定路线继续开发。

---

## 0. 一句话目标

**在不往游戏进程注入任何代码的前提下**，实现对战数据读取 + 自动操作，并让游戏进程内保持干净：

```
TracerPid == 0
匿名可执行段数量不增加
无额外线程、无额外 socket、无沙箱文件
```

这不是可选项，而是整个架构的前提。原因见第 6 节。

---

## 1. 环境基线（实测，不是假设）

| 项 | 值 |
|---|---|
| 设备 | 一加 PJX110，Android 14（SDK 34） |
| ABI | arm64-v8a |
| 内核 | `6.1.57-android14-11-o-g54e396734c5c` |
| 内核源码 tag | `ASB-2024-01-05_14-6.1`（**SUBLEVEL 必须是 57**） |
| Root | KernelSU 1.0.1（`/system/bin/su`） |
| SELinux | Enforcing |
| `su` 的 CapEff | **0**（只给 uid 0，不给 capability） |
| `kptr_restrict` | **2**，root 也改不动 |
| `/proc/kcore` | **不存在** |
| `/proc/modules` 里的地址 | 全 `0x0` |
| 游戏包名 | `com.tencent.jkchess` |

### 1.1 这些事实的直接后果

| 事实 | 后果 |
|---|---|
| `CapEff = 0` | `insmod` / `finit_module` 全部 `EPERM`，只能用 `init_module()` syscall 喂内存 |
| `kptr_restrict = 2` | 拿不到任何内核符号地址 → **不能挂内核函数做插桩** |
| `/proc/kcore` 不存在 | 没有内核内存读取通道 |
| SELinux Enforcing | `dmesg` 时常 `Permission denied`；`su -c` 里执行**脚本**会掉到 `u:r:shell:s0`，直接命令则不会 |
| KernelSU | 改写了 `sys_call_table` 的部分表项（`newfstatat`/`faccessat`/`execve`），见 4.7 |

---

## 2. 系统由三部分组成

```
┌─────────────────────────────────────────────────────────────┐
│ 1. 内核模块 jcck.ko                                          │
│    只做两件事：隐藏自己 + 过滤 /proc 输出                      │
│    已验证：unix socket 名、线程 comm 名、模块自身             │
├─────────────────────────────────────────────────────────────┤
│ 2. 用户态 jccd                                              │
│    驱动客户端：读 / 写 / 抓指针 / 注入触摸                     │
│    全部能力均已实测通过                                       │
├─────────────────────────────────────────────────────────────┤
│ 3. 静态资料（libdemo.so 的名字表）                            │
│    回答「挂哪个方法、读哪个字段、英雄叫什么」                  │
│    全部明文，无需运行、无需注入                                │
└─────────────────────────────────────────────────────────────┘
        ↑ 依赖下面的第三方内核驱动
┌─────────────────────────────────────────────────────────────┐
│ RT-hook 驱动（第三方，用户提供，device: /data/local/tmp/rtload）│
│ 提供 ioctl 601~628：读内存、写内存、取模块基址、PTE 钩子抓寄存器、│
│ 硬件断点、触摸注入、进程隐藏                                   │
└─────────────────────────────────────────────────────────────┘
```

**关键分工**：`jcck` 和 RT 驱动是两套独立的东西。`jcck` 不提供读写能力，RT 驱动不提供过滤能力。两者互不依赖，可分别加载。

---

## 3. 已验证的能力（附实测输出）

### 3.1 驱动原语（`jccd`）

| 命令 | 验证方式 | 实测结果 |
|---|---|---|
| `base <pid> <module>` | 与 `/proc/<pid>/maps` 对比 | `libc.so` → `0x76a3e40000`，一致 |
| `sym <pid> <module> <symbol>` | 读目标进程内存里的 ELF，解析 `PT_DYNAMIC` | `malloc` → `0x7b641217cc`，首 4 字节 `3f 23 03 d5`（`paciasp`），入口正确 |
| `read` / `dump` / `str` / `ptr` | 读已知内容 | `str` → `"PROBE-A" (len=7)`；`dump` → `PROBE-A.PROBE-BB` |
| `write` | 写后读回 | 读回值一致 |
| `capture <pid> <addr> <s> 2` | 挂 `strlen` 入口抓 x0 | `captured_x0 = 0x5f7690d69d`，与探针指针**逐位相同**，可复现 |
| `watch <pid> <addr> <s> <n>` | 靶子交替用两个缓冲区 | 2 秒抓到 2 个不同 x0，去重正确 |
| `touchtest` | 看 `/proc/bus/input/devices` | 设备数 10 → 11（虚拟输入设备注册成功），`down`/`up`/`destroy` 全 `rc=0` |

### 3.2 内核模块过滤

```
/proc/net/unix
  @xf_game_control_v2_test  真实 bind 成功 → 读取命中 0（整行抹成空格）
  @zz_benign_test                            → 命中 1（原样，未误伤）

/proc/<pid>/task/<tid>/comm
  线程名 pool-2-thread-2  → 读回 16 个空格
  线程名 zz_ctl_name      → 原样
```

模块自隐藏：

```
/proc/modules  → 0
/sys/module    → 0
lsmod          → 0
```

---

## 4. 关键 ABI 结论（这些是踩过的坑，直接用，别重试）

### 4.1 `HOOK_SET_ROT.rot[2]` 是模式选择器 —— 最重要的一条

`HookAttach` 只是占位。模式为 0 时钩子空转，`hits` 永远 0。

| mode | 语义 | 实测命中 |
|---|---|---|
| 0 | 空转 | 0 |
| **2** | **抓 x0（一次性，抓完自动 `active=0`）** | 1，值精确 |
| 3 | 内核拷贝（`stack_rot[0]=extra_offset`、`[1]=skip_bytes`） | 349,363 / 秒 |
| 4 | float3 写入 | 425,003 / 秒 |

所以抓对象指针的正确序列是：

```
ioctl(615 OP_CMD_HOOK_ATTACH, {pid, target_addr})
ioctl(617 OP_CMD_HOOK_SET_ROT,  {rot[2]=2})
轮询 ioctl(618 OP_CMD_HOOK_STATUS) 直到 captured_x0 != 0
ioctl(616 OP_CMD_HOOK_DETACH)
```

**`HWBP_*`（609~613）通道是死的**：`ADD`/`ENABLE`/`UPDATE_TRACKING` 全返回 0，但 `GET_HITS` 永远 0。别在这条路上浪费时间，直接用 `HOOK_*`。

### 4.2 目标进程内符号解析不能读 section 头

`libc.so` 的 section 头表在文件末尾（`shoff=0x15d9f8`），bionic **只映射 PT_LOAD 段**，section 头不在内存里，读回来全是 0。

必须走 `PT_DYNAMIC` → `DT_SYMTAB` / `DT_STRTAB` / `DT_STRSZ` / `DT_SYMENT`。实现在 `userspace/jccd.c:resolve_sym()`。

`libil2cpp.so` 的 208 个 `il2cpp_*` 导出里，很多 `st_value` 指向 4 字节 `b` 跳板，`follow_veneer()` 跟一次拿真实函数体。

### 4.3 IOCTL 返回码不可信

`OP_CMD_READ`(601) 读成功时返回值是 `-1`。判断成败只看数据。

`drv_read()` 现在的做法：先查 `/proc/<pid>/maps` 确认地址已映射，再用 `0xCC` 哨兵判断是否真的写入。

**这条必须保留**，否则「驱动没装」会被伪装成「读到一堆 0」，排查时会走极大弯路（本次已浪费一轮）。

### 4.4 触摸的 `slot_mask` 不能为 0

`touch_event.slot_mask` 校验是 `!slot_mask || (slot_mask & ~0x3ff)` → `EINVAL`。**抬手指（`TOUCH_UP`）也必须给非零掩码**（如 `1`），传 0 必失败。这与常见触摸注入 API 的习惯相反。

### 4.5 重启后必须重新 `rtload`，且不能用 `/proc/modules` 判断

RT 驱动自己也会摘链表，所以 `grep rtqdnb9 /proc/modules` **恒为 0**。唯一可靠判据：

```
jccd base <pid> libc.so    → 非 0 才算驱动在
```

### 4.5.1 `HOOK_READREG` 只支持 x0~x3（已验证）

`reg_idx` 传 0~3 得到真实值，传 4 以上**静默返回 x0 的值**（不报错，极易误判）。

用 8 参数函数 + `0x1111…`~`0x8888…` 八个可识别常数实测：x0~x3 正确，x4~x30 全部等于 x0。

好消息是 il2cpp 常用 API 的参数都落在 x0~x2，所以 P1 一次抓取即可同时拿到 `Il2CppClass*` 和方法名指针。工具：`jccd regs <pid> <addr> <秒>`。

### 4.5.2 `HOOK_ATTACH` 是页粒度的，热代码页会灌水（已验证）

`HOOK_STATUS.vaddr` 返回的是**页基址**而不是函数地址，说明 PTE 钩子装在页上。实测：

| 挂的地址 | 结果 |
|---|---|
| `il2cpp_class_get_method_from_name` 导出跳板 | `hits=5001  x0=0x0` |
| 该跳板指向的包装 | `hits=5001  x0=0x0` |
| 包装跳转到的真实函数体 | `hits=5001  x0=0x0` |
| 游戏从不调用的冷函数（同页） | `hits=5001  x0=0x0` |

四个完全不同的地址返回**一模一样的 5001**，说明在 `libil2cpp.so` 这种被高频访问的大代码页上，这个计数不是逐指令命中数，`x0` 也没有意义。

对照：`hwtest` 那种只有一处调用的冷页，`hits` 和 `x0` 都完全正确（`x0` 与探针指针逐位相同）。

**结论：`HOOK_ATTACH` 只适合挂在"少有人碰的页"上的函数。** 想抓游戏对象指针，要找战斗期才被调用、且代码页不热的函数（例如 `HeroRoot_Update`），而不是 il2cpp 的公共 API。

### 4.5.3 读成败判据只用 errno，不读 /proc

`OP_CMD_READ`(601) 返回值恒为 `-1`，区分在 errno：

```
有效地址          ret=-1  errno=25 (ENOTTY)  + 数据有效
无效/未驻留地址   ret=-1  errno=1  (EPERM)   + 缓冲区原封不动
```

所以 `drv_read` 只判 `errno==25||0`，**不再读 `/proc/<pid>/maps`**，读路径零用户态痕迹。

### 4.5.4 驱动读不了 `r--p` 只读映射段

| 段 | 可读 |
|---|---|
| `r-xp`（代码） | ✅ |
| `rw-p` / `rwxp`（数据） | ✅ |
| `r--p` 只读文件映射 | ❌ 只有第一页可读 |

后果：从内存读 `DT_STRTAB` 会失败。解决：**符号表从磁盘上的 .so 文件解析**（`jccd symf`），只有基址来自驱动。游戏的真实数据在 `rw-p`/堆、代码在 `r-xp`，都不受影响。

### 4.6 本机拿不到内核符号地址

`kptr_restrict=2` 且 root 改不动，`/proc/kcore` 不存在，`/proc/modules` 地址全 0，`/sys/module/*/sections` 需要模块可见。**结论：不能在内核里挂函数做插桩。**

### 4.7 自研 kprobe 打不到 open 路径

实测四个挂点全部 `register_kprobe` 返回 0 但从不触发（`calls` 恒 0）：

```
__arm64_sys_openat
__arm64_sys_newfstatat
do_sys_openat2
vfs_statx
```

连对照的 `vfs_read` 探针也是 0。判断是 KernelSU 的 syscall 拦截（`dmesg` 里能看到它自己的 `newfstatat su->sh!` / `faccessat` / `execve` 日志）。

**结论：路径过滤不要在自研模块里做。** 详见 `kernel/experimental/README.md`。

---

## 5. 内核模块编译（全书最危险的一节）

⚠️ **这一节出错会导致手机 kernel panic 重启。已发生过两次。别跳步。**

### 5.1 为什么危险

`struct module` 的布局由一大批 `CONFIG_*` 决定。布局不匹配时，内核会按错误尺寸分配/写入 `struct module`，直接内存破坏 → panic。

**千万不要伪造 CRC 去骗过版本检查**：CRC 匹配但布局不匹配时，检查会通过，然后内存被写坏。CRC 不匹配至少是干净的 `-ENOEXEC` 拒绝。

### 5.2 正确步骤

```bash
# 1) 取对应 tag 的源码（SUBLEVEL 必须是 57）
git clone --depth=1 --branch ASB-2024-01-05_14-6.1 \
    https://android.googlesource.com/kernel/common /opt/src/k157

# 2) 用设备自己的 config —— 不能自己套 gki_defconfig
adb shell su -c 'cp /proc/config.gz /data/local/tmp/'
adb pull /data/local/tmp/kconfig.gz .
gunzip -c kconfig.gz > /opt/src/k157/.config

cd /opt/src/k157
./scripts/config --file .config \
  --set-str LOCALVERSION "-android14-11-o-g54e396734c5c" \
  --disable LOCALVERSION_AUTO \
  --disable WERROR \
  --disable TRIM_UNUSED_KSYMS \
  --set-str UNUSED_KSYMS_WHITELIST ""
make ARCH=arm64 LLVM=1 olddefconfig
make ARCH=arm64 LLVM=1 vmlinux -j$(nproc)      # 产出 vmlinux.symvers
```

依赖：`clang`/`lld`、`llvm`（提供 `llvm-nm`）、`dwarves`（`pahole`）、`bpftool`。

**`CONFIG_DEBUG_INFO_BTF_MODULES` 必须为 `y`** —— 它决定 `struct module` 里有没有 `btf_data_size`/`btf_data` 两个字段，少了差 16 字节。设备 config 自带 `y`，但`pahole` 没装时 kconfig 会静默把它关掉。

构建 `resolve_btfids` 时 clang 21 会因 `-Werror` 报 `libbpf.c` 一处 const 转换错误，补一次：

```bash
sed -i "s/next_path = strchr(s, ':')/next_path = (char *)strchr(s, ':')/" tools/lib/bpf/libbpf.c
git commit -am "fix"
```

之后**必须把 `.git` 临时移开**，否则 `setlocalversion` 会往 vermagic 尾部加 `+`：

```bash
mv .git .git_hold
rm -f include/generated/utsrelease.h include/config/kernel.release
make ARCH=arm64 LLVM=1 modules_prepare -j$(nproc)
cat include/generated/utsrelease.h     # 必须没有末尾的 +
```

```bash
# 3) 编模块
cp vmlinux.symvers Module.symvers
make ARCH=arm64 LLVM=1 M=$(pwd)/kernel modules -j$(nproc)
```

### 5.3 三个必须核对的自检点（任一不符就别上机）

```bash
llvm-readelf -S kernel/jcck.ko | grep this_module
#   .gnu.linkonce.this_module  size 必须是 0x440 = 1088

strings -a kernel/jcck.ko | grep '^vermagic='
#   必须与设备 uname -r 逐字相同，末尾不能有 +

strings -a kernel/jcck.ko | grep '^import_ns='
#   必须有 VFS_internal_I_am_really_a_filesystem_and_am_NOT_a_driver
```

第三条是 KernelSU 的模块信任标记，缺了直接 `EPERM`。

本机推导出的参考值：`module_layout` CRC = **`0xea759d7f`**（与 RT 驱动 6.1 模块里的值一致，可作交叉验证）。

`tools/build.sh` 已把这三个检查自动化，不符直接 `exit 1`。

### 5.4 加载方式

`su` 的 `CapEff=0`，`insmod`/`finit_module` 全 `EPERM`。必须用 `init_module()` syscall：

```c
syscall(__NR_init_module, buf, len, args);
```

`args` 是模块参数串，如 `""`、`"hide=1"`、`"do_hook=0 hide=0"`。

### 5.5 卸载不可用 —— 这是硬限制

`rmmod` 实测必崩（`delete_module` 返回 0，然后 panic 重启）。根因在 `unregister_kretprobe`：此内核在模块内存释放后仍会访问 kprobe 实例池。

所以模块的设计是**「装一次、跑到重启」**，`__exit` 里故意不注销 kretprobe。操作纪律：

- 模块一次性定型，不为换版本而重启
- 需要换版本时，攒够改动重启一次，不要每个改动重启一次
- 用户态改动（`jccd`）不需要重启，随便迭代

---

## 6. 为什么必须零注入（检测面分析）

用 `tools/jccaudit.c`（`jccaudit <pid>`）量出来的干净基线（systemui，无任何外挂）：

| 指标 | 干净值 |
|---|---|
| `TracerPid` | **0** |
| **memfd 映射数** | **7** |
| **匿名可执行段数** | **1**（只有 `[vdso]`） |
| 线程数 | 171 |

### 三条结论

1. **memfd 不是检测信号。** 干净进程本来就有 7 条 `/memfd:jit-zygote-cache (deleted)`、`/memfd:jit-cache (deleted)` —— ART 自己用 memfd 做 JIT 缓存。所以内存加载 payload 用 memfd 完全混在噪声里。

2. **匿名可执行段数是可用信号。** 干净进程只有 1 条 `[vdso]`。往进程里塞一段匿名 `r-xp` 会让这个数变 2 以上。但 Unity 游戏自己也有 JIT，**必须先测游戏自己的基线**才知道噪声有多大。

3. **`TracerPid` 是唯一零误报的硬信号。** 一个字段、一行字、无需任何启发式。所以**去掉 ptrace 是封号风险优化的第一优先级，收益大于其他所有项之和**。

### 优先级表

| 优先级 | 事项 | 收益 |
|---|---|---|
| **1** | 去掉 ptrace | 消除唯一零误报信号 |
| 2 | 不往进程里放代码 | 匿名可执行段不增加；避开代码段 hook 字节校验 |
| 3 | 沙箱文件 / 路径隐藏 | 见 4.7，不在自研模块里做 |
| — | memfd 命名 | **不用管** |

---

## 7. 静态资料：libdemo.so 里的明文名字表

原 APK 里的 `libdemo.so`（5,671,856 字节）虽然用 `arm64-llvm-vmp` 保护了控制流，`__vmenc_start/__vmenc_end` 标记说明也做了字符串加密，**但 `.rodata` 0x15000~0x26000 这一大块名字表是明文**。

完整清单见 `docs/static-inventory.md`，摘要：

| 类别 | 数量 | 关键内容 |
|---|---|---|
| 游戏侧锚点方法 | **16** | `CSoGame_FrameTimeTick`、`HeroRoot_Update`、`HeroRoot.ctor`、`PlayerListItem_SetMoney`、`PlayerListItem_SetBloodValue`、`PlayerListItem_SetPlayerLevel`、`PlayerListItem_SetPercentValue`、`BaseHexCard_SetData`、`TKFrame.StageRunner` |
| 游戏侧字段 | 6 | `m_currentStage`、`m_sHeadUrl`、`m_sFaceUrl`、`m_sIconUrl`、`m_strHeadUrl`、`m_strIconUrl` |
| 对象属性名 | 19 | `heroId`、`starLevel`、`slots`、`count`、`values`、`row`、`col`、`pool`、`gold`、`level`、`heroes` |
| 外挂功能开关 | 33 | `specifiedShopReserveGold`、`autoRefreshFallbackMs`、`excludedHeroIds`、`autoPickCostMask`、`lineupAppliedStarTargets`、`specifiedShopStrict`、`specifiedShopAffordOnly`、`livePoolWeights` |
| 英雄内部名候选 | 343 | `leblanc`、`azir`、`sion`、`illaoi`、`zed`、`kayle`、`wukong`、`masteryi`、`alistar`、`hecarim`、`aphelios`、`sylas`、`taliyah` |
| 路径 | 22 | 见下 |

明文路径（这些原本以为要破解，其实直接可读）：

```
/data/user/0/com.tencent.jkchess/cache/jcc-nb.txt
/data/data/com.tencent.jkchess/.xfjcc_ipc_v2_bootstrap
/data/user/%u/%s/libdemo.so
/proc/self/maps         ← 它自己用它定位 libil2cpp.so 基址
/proc/self/mem
/proc/self/map_files/%llx-%llx
/proc/self/fd/%s
/proc/self/cmdline
/proc/self/status
/proc/cpuinfo
/system/build.prop
/system/bin/linker64
/system/bin/app_process64
```

### 7.1 静态拿不到的四类，全部有运行时替代

| 缺什么 | 为什么拿不到 | 替代 |
|---|---|---|
| **字段数值偏移** | il2cpp 运行时按 metadata 算，二进制里没常量 | `drv_read` 读 `Il2CppClass.fields[]`，`FieldInfo.offset` 直接给出 |
| **il2cpp API 名字** | `dlopen("libil2cpp.so")` + 加密的 dlsym 名 | 从 `libil2cpp.so` 的 `.dynsym` 直接读，208 个 `il2cpp_*` 全在，`jccd sym` 已验证 |
| **类/方法 → 地址** | 类名在加密的 `global-metadata.dat`（magic 被改成 `94 43 72 12`，原为 `AF 1B B1 FA`） | hook `il2cpp_class_get_method_from_name`，`x0` 即 `Il2CppClass*` |
| **VMP 字节码** | `arm64-llvm-vmp` | 不需要抄：数据常量在 `.rodata` |

**分工：静态分析回答「做什么」，运行时零注入回答「在哪里」。两者都不需要往游戏进程里放代码。**

---

## 8. 开发路线（每阶段带验收标准）

### P0 环境重建
1. `git clone` 本仓库
2. 按第 5 节重建内核模块（或直接用 `prebuilt/jcck_v8.ko`，前提是同一台设备）
3. `tools/build.sh` 编出 `jccd`
4. 推 `rtload`、`kload`、`jccd`、`jccaudit` 到 `/data/local/tmp`

**验收**：`jccd base <systemui_pid> libc.so` 返回非 0

### P1 拿 `Il2CppClass*`
游戏跑起来后：

```bash
# 1) 找 il2cpp 导出地址
jccd sym <game_pid> libil2cpp.so il2cpp_class_get_method_from_name veneer

# 2) 挂上去循环抓 x0（x0 = Il2CppClass*）
jccd watch <game_pid> <地址> 5 20
```

每个 `x0` 是一个 `Il2CppClass*`。**同时抓到的 x1 是方法名字符串指针**，用 `jccd str` 读出来就知道游戏在找哪个方法。

**验收**：能从 `x1` 读出人类可读的方法名（例如 `Update`、`FrameTimeTick`）

### P2 建立偏移表
拿到 `Il2CppClass*` 后，用 `jccd dump` / `ptr` 走 `Il2CppClass` 结构：

```
Il2CppClass 里关键成员（偏移需实测确认，不同 Unity 版本不同）：
  name           const char*        读出来应是类名，用它确认偏移找对了
  namespaze      const char*
  fields         FieldInfo*         每个 FieldInfo 含 name + offset
  methods        MethodInfo**       每个 MethodInfo 含 methodPointer
  instance_size  int32
```

**做法**：从 `Il2CppClass*` 起 dump 若干 8 字节槽，把每个槽当指针用 `jccd str` 试，指到合法 ASCII 类名的那个槽就是 `name`，其后的固定偏移就是 `namespaze`。`fields` 指针同样方法定位。

**验收**：能从某个类的 `fields[]` 里读出 `{字段名, 偏移}` 对，并与第 7 节的明文名字对上（例如 `heroId`、`starLevel`）

### P3 抓 `CSoGame*` 与对战数据
用第 7 节的锚点。思路：先 `jccd sym` 找到 `il2cpp_class_get_method_from_name` 拿不到游戏自身方法地址，所以改用：

1. 用 P1/P2 的能力，通过 `il2cpp_class_from_name` + `il2cpp_class_get_method_from_name` 的**返回值**定位类和方法
   —— 注意：`capture` 只抓 entry 的 x0，抓不到返回值。替代方案：hook `il2cpp_class_get_method_from_name`，抓 `x0`（类）和 `x1`（方法名），自行匹配我们要的 `CSoGame` + `FrameTimeTick`，拿到类指针后从 `methods[]` 里找对应 `MethodInfo.methodPointer` → **这就是锚点地址**

2. 用该地址 `jccd capture ... 2` 抓 `CSoGame*`

**验收**：连续抓两次得到同一个 `CSoGame*`（对象生命周期内地址稳定）

### P4 自动操作
```bash
jccd touchtest                    # 先验通道
```
然后用 `drv_touch(TOUCH_DOWN/MOVE/UP, x, y, slot=1)` 实现点击/拖动。注意 4.4 的 `slot_mask` 规则。

**验收**：注入一次点击能触发游戏内 UI 响应

### P5 功能对齐（可选）
把第 7 节的 33 个开关逐个对应到实现（`specifiedShopReserveGold`、`autoRefreshFallbackMs` 等）。这些原本由服务端下发，现在需要本地复现逻辑。

---

## 9. 工具清单

```
userspace/jccdrv.h/.c    驱动客户端封装
userspace/jccd.c         命令行工具
tools/jccaudit.c         暴露面审计
tools/hwtest.c           带 volatile 函数指针的热循环靶子
tools/build.sh           一键编译 + 三个自检
kernel/jcck_main.c       内核模块（过滤 + 自隐藏）
kernel/Module.symvers    本机推导的 CRC 参考
prebuilt/                已验证的二进制
docs/static-inventory.md libdemo.so 明文清单
kernel/experimental/     路径过滤的失败实验与结论
```

### `jccd` 命令全表

```bash
jccd base   <pid> <module>                      模块基址
jccd sym    <pid> <module> <symbol> [veneer]    目标进程内 ELF 符号解析
jccd secs   <pid> <module>                      段表诊断
jccd read   <pid> <addr> <len>                  十六进制
jccd dump   <pid> <addr> <len>                  hexdump + ASCII（最长 1 MB）
jccd str    <pid> <addr> [maxlen]               读 C 字符串
jccd ptr    <pid> <addr> [depth]                跟指针链
jccd write  <pid> <addr> <hex32>                写 32 位
jccd capture <pid> <addr> <秒> <mode>           抓一次 x0
jccd watch  <pid> <addr> <秒> <最多记录数>        循环重挂、去重
jccd touchtest                                  触摸注入自检
jccd touch  <x> <y>                             注入一次点击
jccd hook   <pid> <addr> <秒>                   查看钩子状态
jccd hwbp   <pid> <addr> [秒]                   硬件断点（**不可用**，见 4.1）
```

---

## 10. 未完成 / 待验证

| 项 | 状态 | 说明 |
|---|---|---|
| 游戏自己的检测基线 | 未测 | 需要游戏跑起来后 `jccaudit`，决定匿名可执行段噪声有多大 |
| `Il2CppClass` 各成员的确切偏移 | 未测 | P2 阶段实测确定 |
| `CSoGame*` 抓取 | 未验 | 需要 P1/P2 先通 |
| 字段偏移表 | 未验 | 需要 P2 先通 |
| 沙箱路径隐藏 | 未解决 | 4.7：自研 kprobe 打不到 open 路径，需 RT 驱动的 `vfs_open` 钩子或改 Java 层路径 |
| `HWBP_*` 通道 | 放弃 | 4.1 |
| 内核插桩（观察游戏读了哪些 /proc） | 不可行 | 4.6：无内核符号地址 |

---

## 11. 工作纪律（血泪总结）

1. **每次上机先 `rtload`**，判据是 `jccd base <pid> libc.so` 非 0，不是 `/proc/modules`
2. **模块一旦加载就卸不掉**，`rmmod` 必崩。所以模块一次性定型，用户态随便迭代
3. **不要伪造 CRC**。宁可 `-ENOEXEC` 被拒，也不要布局不匹配导致 panic
4. **读失败必须先怀疑驱动没装**，`drv_read` 的 maps 校验和哨兵不要去掉
5. **改动前先跑 `jccaudit` 存一份基线**，改完对比，才知道暴露面是变好还是变坏
6. **不要用 `pkill -f <名字>`**：会匹配到自己的 `adb shell` 命令行，把自己杀掉。用 `killall`
7. **`su -c` 里执行脚本会掉 SELinux 域**，`dmesg` 会 `Permission denied`；需要 dmesg 时用直接命令不用脚本
8. **adb 掉线不等于崩溃**：看 `uptime` 是否归零。不归零是掉线
9. **测试靶子用完必须立刻 `killall`**。`tools/hwtest.c` 是死循环（每 13 万次才睡 0.5 ms），一个进程吃满一个核心。曾经漏了 20 个在后台跑，直接导致手机发烫卡顿。同理 `abtest` / `nametest` 也要清
10. **怀疑模块导致发烫时，先查进程再查内核**。实测数据（一加 PJX110，8 核）：

| 状态 | cpu0 温度 | skin-msm | 电池 | 结论 |
|---|---|---|---|---|
| 无模块 | 42.2 → 40.3 ℃ | 40.0 → 38.8 ℃ | 34.6 ℃ | 基线 |
| 装 RT 驱动 | 41.4 → 40.6 ℃ | 38.8 → 39.0 ℃ | 34.5 ℃ | **无影响** |
| 装 jcck（双 kretprobe 生效） | 39.9 → 38.7 ℃ | 38.6 → 37.5 ℃ | 34.0 ℃ | **无影响** |

判定方法：`top -b -n 1` 看 `idle` 百分比（800% = 8 核满载，597% idle 就是只用掉 2 核）。Android 的 `load average` 会把 D 状态计入，14 也不代表真的忙，别拿它当依据。

---

## 12. 资源获取

| 资源 | 来源 |
|---|---|
| 代码 | 本仓库 |
| `libdemo.so` | 原 APK 的 `lib/arm64-v8a/libdemo.so` |
| `libil2cpp.so`（272 MB） | 游戏安装包的 `lib/arm64-v8a/` |
| `global-metadata.dat`（67 MB） | 游戏 `assets/bin/Data/Managed/Metadata/` |
| RT 驱动 | 用户提供的 `rtload`（`/data/local/tmp/rtload`），运行后打印「驱动加载成功」 |
| 内核源码 | `https://android.googlesource.com/kernel/common`，tag `ASB-2024-01-05_14-6.1` |

---

## 13. 如果只有一小时

按这个顺序做：

1. `git clone` 仓库，跑 `tools/build.sh`（需要先有内核源码树和 `vmlinux.symvers`）
2. 推送 `rtload`、`jccd`、`jccaudit` 到设备
3. `rtload` → 用 `jccd base <systemui> libc.so` 验驱动
4. `jccd sym <systemui> libc.so malloc veneer` 验符号解析
5. 跑 `tools/hwtest.c` 和 `jccd watch` 验钩子抓 x0
6. 读 `docs/static-inventory.md`，理解锚点和字段清单

到这里环境就通了，可以开始 P1。
