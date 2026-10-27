#!/bin/bash
# 一键编译：内核模块 + 用户态工具
# 用法：
#   KDIR=/opt/src/k157 ./build.sh
#   KDIR=/opt/src/k157 OUT=out ./build.sh
set -e

KDIR="${KDIR:-/opt/src/k157}"
OUT="${OUT:-$(pwd)/out}"
CC_ANDROID="${CC_ANDROID:-clang}"

mkdir -p "$OUT"

echo "=== 内核模块 ==="
if [ ! -f "$KDIR/Module.symvers" ]; then
    echo "!! $KDIR/Module.symvers 不存在，先按 README 3.1 编译一次 vmlinux" >&2
    exit 1
fi
make -C "$KDIR" ARCH=arm64 LLVM=1 M="$(pwd)/kernel" modules -j"$(nproc)"
cp kernel/jcck.ko "$OUT/"

echo "=== 自检 ==="
SZ=$(llvm-readelf -S kernel/jcck.ko | awk '/this_module/{print $6}')
echo "  struct module size : $SZ (必须是 000440)"
grep -q '000440' <<<"$SZ" || { echo "!! struct module 布局不匹配，别上机" >&2; exit 1; }
echo -n "  vermagic           : "; strings -a kernel/jcck.ko | grep '^vermagic='
echo -n "  import_ns          : "; strings -a kernel/jcck.ko | grep '^import_ns=' || { echo "!! 缺 import_ns，KernelSU 会拒加载" >&2; exit 1; }

echo "=== 用户态 ==="
"$CC_ANDROID" --target=aarch64-linux-android28 -O2 -fuse-ld=lld -pie \
    -o "$OUT/jccd" userspace/jccd.c userspace/jccdrv.c -Iuserspace
ls -l "$OUT"/jcck.ko "$OUT"/jccd
echo "done -> $OUT"
