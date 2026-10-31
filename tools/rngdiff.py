import re, sys

def load(path):
    d = {}
    for line in open(path, 'r', errors='replace'):
        m = re.match(r'RNG (0x[0-9a-f]+)\s+inext=(\d+)\s+inextp=(\d+)\s+seed=(0x[0-9a-f]+)\s+count=(\d+)', line.strip())
        if m:
            d[m.group(1)] = (int(m.group(2)), int(m.group(3)), m.group(4), int(m.group(5)))
    return d

a = load(sys.argv[1])
b = load(sys.argv[2])
print('before: %d instances   after: %d instances' % (len(a), len(b)))
print('\n--- count changes (only advanced instances) ---')
rows = []
for k in sorted(set(a) & set(b)):
    ia, ipa, sa, ca = a[k]
    ib, ipb, sb, cb = b[k]
    if ca != cb or ia != ib or ipa != ipb:
        rows.append((cb - ca, k, ca, cb, ia, ib, ipa, ipb, sa))
rows.sort(reverse=True)
for delta, k, ca, cb, ia, ib, ipa, ipb, sa in rows:
    print('  %s  count %6d -> %6d  (+%d)   inext %d->%d  inextp %d->%d  seed=%s'
          % (k, ca, cb, delta, ia, ib, ipa, ipb, sa))
if not rows:
    print('  (none changed)')

onlyA = sorted(set(a) - set(b))
onlyB = sorted(set(b) - set(a))
if onlyA:
    print('\n--- disappeared ---')
    for k in onlyA:
        print('  %s count=%d' % (k, a[k][3]))
if onlyB:
    print('\n--- appeared ---')
    for k in onlyB:
        print('  %s count=%d' % (k, b[k][3]))
