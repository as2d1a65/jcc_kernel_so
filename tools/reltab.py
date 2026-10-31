import struct, sys, collections

PATH = r'D:\zpc\zpc_project_move\crack\android\jcc\.work\libdemo.so'
d = open(PATH, 'rb').read()

def u16(o): return struct.unpack_from('<H', d, o)[0]
def u32(o): return struct.unpack_from('<I', d, o)[0]
def u64(o): return struct.unpack_from('<Q', d, o)[0]

# ---- sections ----
e_shoff = u64(0x28)
e_shentsize = u16(0x3a)
e_shnum = u16(0x3c)
e_shstrndx = u16(0x3e)
secs = []
for i in range(e_shnum):
    p = e_shoff + i * e_shentsize
    nm, typ, flags, addr, off, size, link, info, align, entsize = struct.unpack_from('<IIQQQQIIQQ', d, p)
    secs.append(dict(nm=nm, typ=typ, addr=addr, off=off, size=size, entsize=entsize))
strtab_off = secs[e_shstrndx]['off']
for s in secs:
    z = d.find(b'\0', strtab_off + s['nm'])
    s['name'] = d[strtab_off + s['nm']:z].decode('latin1')

by_name = {s['name']: s for s in secs}
print('sections:', ', '.join(s['name'] for s in secs if s['size'] and s['typ'] != 8)[:400])

# ---- rodata 字符串索引 ----
ro = by_name.get('.rodata')
RO_LO, RO_HI = ro['addr'], ro['addr'] + ro['size']
print('rodata vaddr 0x%x .. 0x%x' % (RO_LO, RO_HI))

import re
strmap = {}   # vaddr -> string
for m in re.finditer(rb'[\x20-\x7e]{3,200}', d[ro['off']:ro['off'] + ro['size']]):
    va = RO_LO + m.start()
    strmap[va] = m.group().decode('latin1')
print('rodata strings indexed:', len(strmap))

def str_at(va):
    return strmap.get(va)

# ---- 重定位 ----
relocs = []   # (r_offset, addend)
for name in ('.rela.dyn', '.rela.plt'):
    s = by_name.get(name)
    if not s or not s['size']:
        continue
    n = s['size'] // 24
    for i in range(n):
        p = s['off'] + i * 24
        r_offset, r_info, r_addend = struct.unpack_from('<QQq', d, p)
        relocs.append((r_offset, r_addend, name))
print('relocations:', len(relocs))

# RELR: 压缩的相对重定位，addend == 地址本身
relr = by_name.get('.relr.dyn')
relr_offsets = []
if relr and relr['size']:
    ent = relr['size'] // 8
    where = 0
    for i in range(ent):
        v = u64(relr['off'] + i * 8)
        if (v & 1) == 0:
            relr_offsets.append(v)
            where = v + 8
        else:
            bits = v >> 1
            for b in range(63):
                if bits & (1 << b):
                    relr_offsets.append(where + b * 8)
            where += 63 * 8
print('RELR entries:', len(relr_offsets))

# 合并：把 addend 落在 rodata 的都收集起来
pairs = []       # (slot_vaddr, string)
for r_offset, r_addend, _n in relocs:
    if RO_LO <= r_addend < RO_HI:
        s = str_at(r_addend)
        if s:
            pairs.append((r_offset, s))
for r_offset in relr_offsets:
    # RELR 的 addend 就是 (slot 所在的) 目标地址，即 slot 内容 == slot 的 vaddr
    if RO_LO <= r_offset < RO_HI:
        s = str_at(r_offset)
        if s:
            # slot 位置未知（RELR 只给目标），单独记
            pairs.append((None, s))

print('指向 rodata 字符串的重定位数:', len(pairs))

# ---- 按 slot 地址排序，找 8 字节等距的连续表 ----
real = [(o, s) for o, s in pairs if o is not None]
real.sort()
clusters = []
cur = []
for o, s in real:
    if cur and o - cur[-1][0] != 8:
        if len(cur) >= 3:
            clusters.append(cur)
        cur = []
    cur.append((o, s))
if len(cur) >= 3:
    clusters.append(cur)

print('\n=== 连续 8 字节槽且都指向 rodata 字符串的表: %d 组 ===' % len(clusters))
clusters.sort(key=len, reverse=True)
for c in clusters[:8]:
    print('\n--- 表 @ 0x%x  %d 项 ---' % (c[0][0], len(c)))
    for o, s in c[:40]:
        print('   0x%06x  %s' % (o, s))

# ---- 相邻项语义：看成对出现的类名/字段名 ----
print('\n=== 2 项/组（槽差 8）的成对串，前 60 对 ===')
cnt = 0
i = 0
while i + 1 < len(real) and cnt < 60:
    o1, s1 = real[i]
    o2, s2 = real[i + 1]
    if o2 - o1 == 8:
        print('   %-28s | %s' % (s1, s2))
        cnt += 1
        i += 2
    else:
        i += 1
