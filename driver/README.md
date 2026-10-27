# RT-hook 驱动

第三方内核驱动，提供本方案全部的内存读写 / 寄存器抓取 / 触摸注入能力。**不是本项目所写**，原样收录以保证可复现。

---

## 1. 文件

| 文件 | 说明 |
|---|---|
| `rtload` | **已验证可用的加载器**（本项目所用的那一份） |
| `6.1.sh` ~ `6.18.sh` | 原始自提取脚本，10 个内核版本各一份 |
| `kernel.h` | 驱动的用户态 ABI 头（ioctl 命令与结构体定义） |
| `analysis/*.py` | 当初用来逆 ABI、抠 payload、核对 KMI 的脚本 |

`rtload` 就是 `6.1.sh` 解出来的 payload 本体，与 `driver/6.1.sh` 里打包的内容**字节一致**：

```
文件大小  3,426,120
MD5       ea0182597fb70c2b0bb2daec5983ce5d
```

> 原目录里还有一个 `RT-hook无缓存全新版本v1.0以前对接依旧可用.zip`（9.6 MB）。已验证它只含上述 10 个 `.sh`，无额外内容，故不重复收录。

---

## 2. 版本矩阵

`.sh` 文件名对应目标设备的内核主版本。**必须精确匹配**，装错版本的驱动行为不可预期。

| 脚本 | 目标内核 |
|---|---|
| `5.4.sh` / `5.4b.sh` / `5.4c.sh` | 5.4（三个分支变体） |
| `5.10.sh` | 5.10 |
| `5.15.sh` | 5.15 |
| **`6.1.sh`** | **6.1 ← 本设备用这个** |
| `6.6.sh` | 6.6 |
| `6.12.23.sh` / `6.12.58.sh` | 6.12.23 / 6.12.58 |
| `6.18.sh` | 6.18 |

本设备内核 `6.1.57-android14-11-o-g54e396734c5c`，主版本 6.1。

---

## 3. 使用

```bash
adb push rtload /data/local/tmp/
adb shell su -c 'chmod 755 /data/local/tmp/rtload'
adb shell su -c '/data/local/tmp/rtload'
# 期望输出：驱动加载成功
```

### 3.1 最重要的两条判据

1. **重启后必须重新加载。** 驱动不落盘、不自启。
2. **`/proc/modules` 不能用来判断驱动在不在。** 驱动自己会摘链表，`grep rtqdnb9 /proc/modules` **恒为 0**。

唯一可靠判据：

```bash
jccd base <pid> libc.so     # 返回非 0 才算驱动在工作
```

### 3.2 通道不需要设备节点

驱动 hook 的是 `sys_ioctl`，认 `cmd` 号（601~628）不认文件描述符，所以 `open("/dev/null")` 就够，**磁盘上零新增节点**。

---

## 4. 内部结构

`rtload` 是一个自解压复合体（arm64 ELF，Rust 编写）：

| 偏移 | 内容 |
|---|---|
| `0x0` | Rust 加载器主体（ET_DYN），链接 goblin 0.10.5，负责解出内嵌 `.ko` 并调 `init_module` |
| `0xf941` | 真正的内核模块（ET_REL） |

模块本身的信息：

```
vermagic      6.1.112-00034-g226e9f92a06c-dirty SMP preempt mod_unload modversions aarch64
import_ns     VFS_internal_I_am_really_a_filesystem_and_am_NOT_a_driver
__versions    121 条
module_layout 0xea759d7f
```

> 注意 vermagic 是 `6.1.112`，与本机 `6.1.57` 不同 —— 说明它靠 `import_ns` 标记走 KernelSU 的信任通道，而不是靠 vermagic 匹配。**照抄它的 CRC 到自研模块是危险的**：CRC 匹配但 `struct module` 布局不匹配时内核会按错误尺寸写内存，直接 panic（本项目已踩过两次）。

驱动具备的能力（从其符号与字符串推断，均已被实际调用验证）：

```
kCFI 绕过                      fixmap + stop_machine 热补丁
fault_info PTE 钩子            CNTKCTL_EL1 / EL0VTEN 反调试计时绕过
input_event 触摸注入            模块自身与路径隐藏
```

---

## 5. ioctl 命令表

| cmd | 名称 | 用途 |
|---|---|---|
| 601 | `OP_CMD_READ` | 读目标进程内存（**成功时返回 -1**，见下） |
| 602 | `OP_CMD_WRITE` | 写目标进程内存 |
| 603 | `OP_CMD_BASE` | 取模块基址 |
| 604 | `OP_CMD_GETPID` | 取进程 pid |
| 605 | `OP_CMD_HIDE_PROCESS` | 进程隐藏 |
| 606 | `OP_CMD_UN_HOOK` | 拆除钩子 |
| 607 | `OP_CMD_RECOVER_PROCESS` | 恢复进程可见 |
| 608 | `OP_CMD_GYRO` | 陀螺仪配置 |
| 609~613 | `OP_CMD_HWBP_*` | 硬件断点 —— **本机不可用**，见 §6.2 |
| 615 | `OP_CMD_HOOK_ATTACH` | PTE 钩子挂载 |
| 616 | `OP_CMD_HOOK_DETACH` | 拆除 |
| 617 | `OP_CMD_HOOK_SET_ROT` | **设置钩子模式**（关键，见 §6.1） |
| 618 | `OP_CMD_HOOK_STATUS` | 读钩子状态与 `captured_x0` |
| 619 | `OP_CMD_HOOK_READREG` | 读指定寄存器 |
| 620~625 | `OP_CMD_BRK_*` | 软件断点（未验证） |
| 626~628 | `OP_CMD_TOUCH_*` | 触摸注入（INIT / EVENT / DESTROY） |

### 返回码不可信

`OP_CMD_READ`(601) **读成功时返回值是 `-1`**。判断成败只看数据，不看返回码。

正确做法（已在 `userspace/jccdrv.c` 实现）：先查 `/proc/<pid>/maps` 确认地址已映射，再用 `0xCC` 哨兵判断是否真的写入。少了这层校验，「驱动没装」会被伪装成「读到一堆 0」，排查时会走极大弯路。

---

## 6. 两个必须知道的结论

### 6.1 `HOOK_SET_ROT.rot[2]` 是模式选择器

`HookAttach` 只是占位。**模式为 0 时钩子空转，`hits` 永远 0。**

| mode | 语义 | 实测 |
|---|---|---|
| 0 | 空转 | 0 命中 |
| **2** | **抓 x0（一次性，抓完自动 `active=0`）** | 1 次，值精确 |
| 3 | 内核拷贝（`stack_rot[0]=extra_offset`、`[1]=skip_bytes`） | 349,363 / 秒 |
| 4 | float3 写入 | 425,003 / 秒 |

抓对象指针的正确序列：

```
ioctl(615, {pid, target_addr})
ioctl(617, {rot[2]=2})
轮询 ioctl(618) 直到 captured_x0 != 0
ioctl(616, 0)
```

### 6.2 `HWBP_*`（609~613）通道是死的

`ADD` / `ENABLE` / `UPDATE_TRACKING` 全部返回 0，但 `GET_HITS` 永远 0。同一地址、同一目标下 `HOOK_*` 路径能抓到 34 万次，说明不是地址问题而是该通道本身未实现或语义不同。

**别在这条路上花时间，一律用 `HOOK_*`。**

---

## 7. 关于网络行为

`rtload` 里存在这些字符串：

```
VERIFY_ADDR
send usage report
read verification response
connect verification server
verification server returned an invalid response
```

加载器确实是 Rust 写的，带 goblin 0.10.5 与 `parse elf` / `get kallsyms` 等自制逻辑。上述字符串说明作者预留了上报/验证通道。

**一处澄清**：早期日志里的

```
usage statistics unavailable: connect verification server 62.234.44.25:64545: Network is unreachable
```

来自 **`adb.exe` 自己的遥测**（新版 platform-tools 是 Rust 写的，也有同样的上报逻辑），**不是** `rtload` 发出的。两者字符串雷同容易误判。

实际观察：离线状态下 `rtload` 依然打印 `驱动加载成功` 并正常工作，未见过因网络失败而拒绝加载。

---

## 8. 换内核版本怎么办

若换到另一台设备（内核主版本不同）：

1. 先试对应版本的 `.sh` —— 它们都是自提取脚本，`sh 6.6.sh` 即可，无需手工解包
2. 若版本表里没有对应主版本，用 `analysis/extract_rt.py` 从任一 `.sh` 里抠出 Rust 加载器与内嵌 `.ko` 的布局，再参考 `analysis/kmi_table.py`、`analysis/ko_syms.py` 核对符号与 CRC
3. **不要试图用 A 版本的驱动去适配 B 版本内核** —— 驱动内部用 fixmap/stop_machine 改内核代码页，版本错配会直接 panic

`analysis/` 下脚本用途：

| 脚本 | 用途 |
|---|---|
| `extract_rt.py` | 从 `.sh` 里解出 payload |
| `dump_head.py` | 看 payload 头部 |
| `rt_elf.py` | 解析 payload 的 ELF 结构，定位内嵌 `.ko` |
| `loader_syms.py` / `loader_str.py` / `loader_net.py` | 看加载器的符号/字符串/网络逻辑 |
| `ko_syms.py` / `ko_imports.py` / `ko_strings.py` / `ko_paths.py` | 看内嵌 `.ko` 的符号、导入、字符串、路径 |
| `kmi_table.py` | 核对 `struct module` 布局与 CRC |
| `probe_rt.py` | 探测驱动是否可用的最小验证 |
