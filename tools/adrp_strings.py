import struct, re, sys

PATH = r'D:\zpc\zpc_project_move\crack\android\jcc\.work\libdemo.so'
d = open(PATH, 'rb').read()

def u16(o): return struct.unpack_from('<H', d, o)[0]
def u32(o): return struct.unpack_from('<I', d, o)[0]
def u64(o): return struct.unpack_from('<Q', d, o)[0]

e_shoff = u64(0x28); e_shentsize = u16(0x3a); e_shnum = u16(0x3c); e_shstrndx = u16(0x3e)
secs = []
for i in range(e_shnum):
    p = e_shoff + i * e_shentsize
    nm, typ, flags, addr, off, size, link, info, align, entsize = struct.unpack_from('<IIQQQQIIQQ', d, p)
    secs.append(dict(nm=nm, typ=typ, addr=addr, off=off, size=size))
st = secs[e_shstrndx]['off']
for s in secs:
    z = d.find(b'\0', st + s['nm'])
    s['name'] = d[st + s['nm']:z].decode('latin1')
S = {s['name']: s for s in secs}

ro = S['.rodata']; tx = S['.text']
RO_LO, RO_HI = ro['addr'], ro['addr'] + ro['size']
TX_LO, TX_HI = tx['addr'], tx['addr'] + tx['size']
print('rodata 0x%x..0x%x  text 0x%x..0x%x' % (RO_LO, RO_HI, TX_LO, TX_HI))

# 字符串索引（只收 ≥4 且看起来像标识符或以可打印开头的）
strmap = {}
for m in re.finditer(rb'[\x20-\x7e]{4,200}', d[ro['off']:ro['off'] + ro['size']]):
    strmap[RO_LO + m.start()] = m.group().decode('latin1')

def str_at(va):
    if va in strmap:
        return strmap[va]
    return None

def decode_adrp(insn, pc):
    if (insn & 0x9F000000) != 0x90000000:
        return None
    rd = insn & 0x1F
    immlo = (insn >> 29) & 0x3
    immhi = (insn >> 5) & 0x7FFFF
    imm = (immhi << 2) | immlo
    if imm & (1 << 20):
        imm -= (1 << 21)
    imm <<= 12
    return rd, ((pc & ~0xFFF) + imm) & 0xFFFFFFFFFFFFFFFF

def decode_add_imm(insn):
    # ADD (immediate), 64-bit: sf=1 op=0 S=0 -> 0x91000000 mask 0x7F800000
    if (insn & 0x7F800000) != 0x11000000:
        return None
    if not (insn & 0x80000000):
        return None
    rd = insn & 0x1F
    rn = (insn >> 5) & 0x1F
    imm = (insn >> 10) & 0xFFF
    sh = (insn >> 22) & 0x3
    if sh == 1:
        imm <<= 12
    elif sh != 0:
        return None
    return rd, rn, imm

# 扫 text
refs = []          # (code_pc, string)
n_insn = tx['size'] // 4
for i in range(n_insn):
    off = tx['off'] + i * 4
    pc = TX_LO + i * 4
    insn = u32(off)
    a = decode_adrp(insn, pc)
    if not a:
        continue
    ard, atgt = a
    # 往后 12 条内找 ADD Xd, Xard, #imm
    for j in range(1, 13):
        if i + j >= n_insn:
            break
        insn2 = u32(tx['off'] + (i + j) * 4)
        ad = decode_add_imm(insn2)
        if not ad:
            continue
        drd, rn, imm = ad
        if rn != ard:
            continue
        tgt = atgt + imm
        if RO_LO <= tgt < RO_HI:
            s = str_at(tgt)
            if s:
                refs.append((pc, s))
        break

print('ADRP+ADD 引用到 rodata 字符串的次数:', len(refs))

# 按代码地址排序，找代码上相邻的引用序列
refs.sort()
groups = []
cur = [refs[0]] if refs else []
for pc, s in refs[1:]:
    if pc - cur[-1][0] <= 0x600:
        cur.append((pc, s))
    else:
        if len(cur) >= 3:
            groups.append(cur)
        cur = [(pc, s)]
if len(cur) >= 3:
    groups.append(cur)

groups.sort(key=len, reverse=True)
print('代码上相邻的引用组: %d 组，最长 %d 项' % (len(groups), len(groups[0]) if groups else 0))

KEY = re.compile(r'^(C[A-Z]|Hero|Player|Chess|BaseHex|TKFrame|m_|get_|Set|Update|ctor|array|list|slots|count|values|row|col|heroId|starLevel|level|gold|hp|name|type|index|pool)')
for g in groups[:12]:
    names = [s for _pc, s in g]
    interesting = [s for s in names if KEY.match(s)]
    if len(interesting) < 2:
        continue
    print('\n=== 组 @pc 0x%x  共 %d 项，其中 %d 项像字段/类名 ===' % (g[0][0], len(g), len(interesting)))
    print('   ' + ' -> '.join(interesting[:40]))
