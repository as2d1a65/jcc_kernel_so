import struct

PATH = r'D:\zpc\zpc_project_move\crack\android\jcc\.work\libil2cpp.so'
f = open(PATH, 'rb')
d = f.read(4096)

def u16(o): return struct.unpack_from('<H', d, o)[0]
def u32(o): return struct.unpack_from('<I', d, o)[0]
def u64(o): return struct.unpack_from('<Q', d, o)[0]

phoff = u64(0x20)
phentsize = u16(0x36)
phnum = u16(0x38)
print('phoff=0x%x entsize=%d phnum=%d' % (phoff, phentsize, phnum))

f.seek(phoff)
phdrs = []
for i in range(phnum):
    e = f.read(phentsize)
    p_type, p_flags = struct.unpack_from('<II', e, 0)
    p_offset, p_vaddr, p_paddr, p_filesz, p_memsz = struct.unpack_from('<QQQQQ', e, 8)
    phdrs.append((p_type, p_flags, p_offset, p_vaddr, p_filesz, p_memsz))
    if p_type in (1, 2, 3, 8, 9):
        name = {1: 'LOAD', 2: 'DYNAMIC', 3: 'INTERP', 8: 'LOAD?', 9: 'FILE'}.get(p_type, str(p_type))
        print('  PT_%-8s off=0x%-9x vaddr=0x%-9x filesz=0x%-9x memsz=0x%-9x flags=%d'
              % (name, p_offset, p_vaddr, p_filesz, p_memsz, p_flags))

def va2off(va):
    for p_type, _fl, off, vaddr, filesz, _ms in phdrs:
        if p_type == 1 and vaddr <= va < vaddr + filesz:
            return off + (va - vaddr)
    return None

# PT_DYNAMIC
dyn_va = next(vaddr for p_type, _f, _o, vaddr, _fs, _ms in phdrs if p_type == 2)
dyn_off = va2off(dyn_va)
print('\nDYNAMIC va=0x%x off=0x%x' % (dyn_va, dyn_off))
f.seek(dyn_off)
tags = {}
for i in range(512):
    tag, val = struct.unpack('<QQ', f.read(16))
    if tag == 0:
        break
    tags.setdefault(tag, val)
names = {5: 'STRTAB', 6: 'SYMTAB', 10: 'STRSZ', 11: 'SYMENT', 4: 'HASH', 0x6ffffef5: 'GNU_HASH'}
for k, v in tags.items():
    if k in names:
        print('  DT_%-9s = 0x%x' % (names[k], v))

symtab_va = tags[6]
strtab_va = tags[5]
strsz = tags.get(10, 0)
strtab_off = va2off(strtab_va)
symtab_off = va2off(symtab_va)
print('\nSYMTAB off=0x%x  STRTAB off=0x%x  STRSZ=%d' % (symtab_off, strtab_off, strsz))

# 用 GNU_HASH 求符号个数
def gnu_hash_count(va):
    off = va2off(va)
    if off is None:
        return None
    f.seek(off)
    nbuckets, symoffset, bloom_size, bloom_shift = struct.unpack('<IIII', f.read(16))
    f.seek(off + 16 + bloom_size * 8)
    buckets = struct.unpack('<%dI' % nbuckets, f.read(nbuckets * 4))
    last = max(buckets)
    if last < symoffset:
        return symoffset
    f.seek(off + 16 + bloom_size * 8 + nbuckets * 4 + (last - symoffset) * 4)
    while True:
        v = struct.unpack('<I', f.read(4))[0]
        last += 1
        if v & 1:
            break
    return last

cnt = gnu_hash_count(tags.get(0x6ffffef5, 0)) if 0x6ffffef5 in tags else None
print('符号总数(GNU_HASH) =', cnt)

f.seek(strtab_off)
strs = f.read(strsz)

def sym_at(i):
    f.seek(symtab_off + i * 24)
    e = f.read(24)
    st_name, st_info, st_other, st_shndx, st_value, st_size = struct.unpack('<IBBHQQ', e)
    nm = ''
    if 0 < st_name < strsz:
        z = strs.find(b'\0', st_name)
        nm = strs[st_name:z].decode('latin1', 'replace')
    return nm, st_info, st_shndx, st_value, st_size

target = 'il2cpp_class_get_method_from_name'
n = cnt or 0
found = []
for i in range(n):
    nm, info, shndx, val, size = sym_at(i)
    if nm == target:
        found.append((i, info, shndx, val, size))

print('\n=== %s ===' % target)
for i, info, shndx, val, size in found:
    print('  idx=%d type=%d bind=%d shndx=%d value=0x%x size=%d'
          % (i, info & 0xf, info >> 4, shndx, val, size))

# 顺便看看 il2cpp_* 的 size 分布，判断是否为 thunk
sizes = []
il2 = 0
for i in range(n):
    nm, info, shndx, val, size = sym_at(i)
    if nm.startswith('il2cpp_') and (info & 0xf) == 2:
        il2 += 1
        sizes.append((size, nm, val))
sizes.sort()
print('\nil2cpp_* 函数符号 %d 个' % il2)
print('size=0 的个数: %d' % sum(1 for s, _, _ in sizes if s == 0))
print('最小的 5 个:')
for s, nm, val in sizes[:5]:
    print('   size=%-6d 0x%-9x %s' % (s, val, nm))
print('最大的 5 个:')
for s, nm, val in sizes[-5:]:
    print('   size=%-6d 0x%-9x %s' % (s, val, nm))
