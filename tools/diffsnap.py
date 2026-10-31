import struct, sys

REC = 208  # 8 + 4 + 4 + 0xC0

def load(path):
    d = open(path, 'rb').read()
    out = {}
    order = []
    n = len(d) // REC
    for i in range(n):
        o = i * REC
        addr = struct.unpack_from('<Q', d, o)[0]
        hid = struct.unpack_from('<I', d, o + 8)[0]
        sz = struct.unpack_from('<I', d, o + 12)[0]
        body = d[o + 16:o + 16 + sz]
        out[addr] = (hid, body)
        order.append(addr)
    return out, order

a, oa = load(sys.argv[1])
b, ob = load(sys.argv[2])

print("before: %d units   after: %d units" % (len(a), len(b)))
new = [x for x in ob if x not in a]
gone = [x for x in oa if x not in b]
if new:
    print("added  :", ", ".join("0x%x hero=%d" % (x, b[x][0]) for x in new))
if gone:
    print("removed:", ", ".join("0x%x hero=%d" % (x, a[x][0]) for x in gone))

print("\n=== 同地址实体的字节差异 ===")
changed_any = False
for addr in oa:
    if addr not in b:
        continue
    hid_a, ba = a[addr]
    hid_b, bb = b[addr]
    if len(ba) != len(bb):
        print("0x%x length differs" % addr)
        continue
    diffs = []
    for off in range(0, min(len(ba), len(bb)), 4):
        va = struct.unpack_from('<I', ba, off)[0]
        vb = struct.unpack_from('<I', bb, off)[0]
        if va != vb:
            fa = struct.unpack_from('<f', ba, off)[0]
            fb = struct.unpack_from('<f', bb, off)[0]
            diffs.append((off, va, vb, fa, fb))
    if diffs:
        changed_any = True
        print("\n0x%x  hero=%d" % (addr, hid_a))
        for off, va, vb, fa, fb in diffs:
            note = ""
            if off in (0x10, 0x14):
                note = "  <- entity/hero id field"
            print("  +0x%02x  int 0x%08x -> 0x%08x (%d -> %d)   float %.6g -> %.6g%s"
                  % (off, va, vb, va, vb, fa, fb, note))
if not changed_any:
    print("（同地址实体无字节差异）")
