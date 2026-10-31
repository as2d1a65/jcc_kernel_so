import re, sys, os

def load(path):
    txt = open(path, 'r', errors='replace').read()
    data = {}
    for m in re.finditer(r'^([0-9a-f]{16})\s+((?:[0-9a-f]{2}\s+){1,16})', txt, re.M):
        base = int(m.group(1), 16)
        bs = re.findall(r'[0-9a-f]{2}', m.group(2))
        for i, b in enumerate(bs):
            data[base + i] = int(b, 16)
    return data

# usage: diffmem.py dir label
d = sys.argv[1]
label = sys.argv[2]
fa = os.path.join(d, label + '_a.txt')
fb = os.path.join(d, label + '_b.txt')
if not (os.path.exists(fa) and os.path.exists(fb)):
    sys.exit(0)
a = load(fa); b = load(fb)
common = sorted(set(a) & set(b))
if not common:
    sys.exit(0)
base = common[0]
diffs = [(x - base, a[x], b[x]) for x in common if a[x] != b[x]]
print('%-18s bytes=%4d diffs=%d' % (label, len(common), len(diffs)))
for off, va, vb in diffs[:12]:
    print('    +0x%03x  %02x -> %02x   (%d -> %d)' % (off, va, vb, va, vb))
