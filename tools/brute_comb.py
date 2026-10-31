import re, sys

MBIG = 2147483647

class R:
    __slots__ = ('s', 'i', 'j')
    def __init__(self, s, i, j):
        self.s = list(s); self.i = i; self.j = j
    def nxt(self):
        i = self.i + 1
        if i >= 56: i = 1
        j = self.j + 1
        if j >= 56: j = 1
        v = self.s[i] - self.s[j]
        if v == MBIG: v -= 1
        if v < 0: v += MBIG
        self.s[i] = v; self.i = i; self.j = j
        return v

def load_instances(path):
    out = []; cur = None
    for line in open(path):
        line = line.strip()
        m = re.match(r'INST (0x[0-9a-f]+) inext=(\d+) inextp=(\d+) count=(\d+)', line)
        if m:
            cur = {'a': m.group(1), 'i': int(m.group(2)), 'j': int(m.group(3)),
                   'c': int(m.group(4)), 's': None}
            out.append(cur)
        elif line.startswith('SEED ') and cur is not None:
            cur['s'] = [int(x) for x in line[5:].split(',')]
    return [x for x in out if x['s'] and len(x['s']) == 56]

def load_pool(path):
    odds = None; nodes = []
    for line in open(path):
        line = line.strip()
        if line.startswith('odds='): odds = [int(x) for x in line.split('=')[1].split(',')]
        elif re.match(r'^\d+ \d+ \d+ \d+ \d+$', line):
            h, q, w, c, i0 = (int(x) for x in line.split())
            nodes.append((h, q, w, c))
    return odds, nodes

def load_shop(path):
    out = []
    for line in open(path):
        f = line.split()
        if len(f) >= 7 and f[2].isdigit():
            h = int(f[2])
            if h >= 31400: h -= 20000
            elif h >= 21400: h -= 10000
            out.append(h)
    return out

def main():
    insts = load_instances(sys.argv[1])
    odds, nodes = load_pool(sys.argv[2])
    obs = load_shop(sys.argv[3])
    print('inst=%d odds=%s obs=%s nodes=%d' % (len(insts), odds, obs, len(nodes)))
    if not obs:
        print('no shop'); return

    # combined weight list: weight = levelOdds[tier] * nodeWeight  (per copy available)
    comb = []
    for h, q, w, c in nodes:
        if q < 1 or q > len(odds): continue
        if odds[q-1] <= 0 or c <= 0: continue
        comb.append((h, odds[q-1] * w))
    total = sum(w for _, w in comb)
    print('combined pool: %d heroes, total weight %d' % (len(comb), total))

    def pick_combined(r, kind):
        # kind 0: scale, 1: modulo
        v = r.nxt()
        if kind == 0:
            x = int(v * (1.0 / MBIG) * total)
        else:
            x = v % total
        acc = 0
        for h, w in comb:
            acc += w
            if x < acc: return h
        return comb[-1][0]

    hits = []
    for raw in insts:
        for kind in (0, 1):
            for k in range(0, 1500):
                r = R(raw['s'], raw['i'], raw['j'])
                for _ in range(k): r.nxt()
                got = [pick_combined(r, kind) for _ in range(5)]
                if got == obs:
                    hits.append((raw['a'], raw['c'], kind, k))
    print('combined-model hits: %d' % len(hits))
    for h in hits[:10]:
        print('   ', h)
    if not hits:
        print('  -> single-draw combined model also fails')

main()
