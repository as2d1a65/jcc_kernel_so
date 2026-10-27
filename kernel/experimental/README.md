# 路径过滤实验（未通过，不要用）

目标：把 `libdemo.so` 写进游戏沙箱的那几个文件从游戏自己的视角藏起来

```
/data/user/0/<pkg>/cache/jcc-nb.txt
/data/data/<pkg>/.xfjcc_ipc_v2_bootstrap
/data/data/<pkg>/.ipcb_v2_(cand|fml|test).tmp.<epoch>
pkf473104974/
```

## 试过的四条路，全部失败

| 挂点 | 结果 |
|---|---|
| `__arm64_sys_openat` | `register_kprobe` 返回 0，但 `calls` 永远是 0，从不触发 |
| `__arm64_sys_newfstatat` | 同上 |
| `do_sys_openat2` | 同上（以为绕过 syscall 表就行，结果一样） |
| `vfs_statx` | 同上 |

同时挂的 `vfs_read` 对照探针也从不触发，说明**不是符号名或参数索引写错**，而是这一整类挂点在本机根本没被走到。

## 判断

这台机器上有 **KernelSU**，它用 `fp_hook` 改写了 `sys_call_table` 的部分表项。`dmesg` 里能直接看到它的痕迹：

```
KernelSU: newfstatat su->sh!
KernelSU: faccessat su->sh!
KernelSU: sys_execve su found
```

`newfstatat` / `faccessat` / `execve` 三条被它接管了。而 `do_sys_openat2` 也不触发，说明拦截位置比预想的更低，或者 kprobe 的指令改写被它的 hook 框架覆盖了。

## 正解

不要在自己写的模块里碰 open 路径。**用 RT 驱动自带的文件隐藏功能**——它的符号表里明确有：

```
legacy_hide_path     hide_mount_tmpfs     hide_umount_path
init_hide_funcs      manage_process_visibility
```

而且它启动时会打印 `Hook: sucess Hook vfs_open` / `vfs_open hooked: opened file path = %s`，说明它自己的 `vfs_open` 钩子是能工作的。路径隐藏应该在那一层做。

## 另一个不需要内核的替代

`libdemo.so` 里写过沙箱的只有两类：

1. `.xfjcc_ipc_v2_bootstrap` / `.ipcb_v2_*` —— 由 **APK 的 Java 层**创建和改名，Java 没混淆也没加固，**可以直接改路径**
2. `jcc-nb.txt` / `pkf473104974/` —— 由 `libdemo.so` 的 native 代码写，只是 `.rodata` 里的字符串常量

所以最省事的做法是把第 1 类改成不在游戏沙箱里（改到自己的私有目录），第 2 类靠 RT 驱动的路径隐藏，完全不需要自研模块参与。