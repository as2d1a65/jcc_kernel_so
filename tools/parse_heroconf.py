import re, sys, struct
from collections import Counter

path = r'D:\zpc\zpc_project_move\crack\android\jcc\.work\hq.txt'
txt = open(path, 'r', errors='replace').read()

data = {}
for m in re.finditer(r'^([0-9a-f]{16})\s+((?:[0-9a-f]{2}\s+){1,16})', txt, re.M):
    base = int(m.group(1), 16)
    bs = re.findall(r'[0-9a-f]{2}', m.group(2))
    for i, b in enumerate(bs):
        data[base + i] = int(b, 16)

if not data:
    print('no data'); sys.exit(1)

lo, hi = min(data), max(data)
blob = bytearray(hi - lo + 1)
for a, v in data.items():
    blob[a - lo] = v
print('range 0x%x..0x%x  bytes %d' % (lo, hi, len(blob)))

rows = []
off = 0
while off + 0x18 <= len(blob):
    k = struct.unpack_from('<i', blob, off + 0x10)[0]
    v = struct.unpack_from('<i', blob, off + 0x14)[0]
    if not (10000 <= k <= 40000):
        break
    rows.append((k, v))
    off += 0x18

print('entries %d' % len(rows))
if rows:
    print('first %s  last %s' % (rows[0], rows[-1]))
    ks = [r[0] for r in rows]
    print('keys consecutive: %s  (min %d max %d)' % (ks == list(range(ks[0], ks[0] + len(ks))), min(ks), max(ks)))
    c = Counter(v for _, v in rows)
    print('quality counts: ' + ', '.join('%d:%d' % (k, c[k]) for k in sorted(c)))
    print('run lengths by quality:')
    prev = None
    run = 0
    runs = []
    for k, v in rows:
        if v == prev:
            run += 1
        else:
            if prev is not None:
                runs.append((prev, run, start))
            prev = v
            run = 1
            start = k
    runs.append((prev, run, start))
    for q, n, st in runs:
        print('  quality %d x %d  starting key %d' % (q, n, st))
