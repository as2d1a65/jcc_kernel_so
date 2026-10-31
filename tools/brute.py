import re, sys

MBIG = 2147483647

class Rand:
    __slots__ = ('s', 'i', 'j', 'n')
    def __init__(self, seed, inext, inextp):
        self.s = list(seed)
        self.i = inext
        self.j = inextp
        self.n = 0
    def clone(self):
        r = Rand(self.s, self.i, self.j)
        r.n = self.n
        return r
    def nxt(self):
        self.n += 1
        i = self.i + 1
        if i >= 56: i = 1
        j = self.j + 1
        if j >= 56: j = 1
        ret = self.s[i] - self.s[j]
        if ret == MBIG: ret -= 1
        if ret < 0: ret += MBIG
        self.s[i] = ret
        self.i = i
        self.j = j
        return ret
    def next_max(self, mx):
        return int(self.nxt() * (1.0 / MBIG) * mx)

def parse_state(path):
    inext = inextp = None
    seed = None
    for line in open(path):
        line = line.strip()
        if line.startswith('inext='): inext = int(line.split('=')[1])
        elif line.startswith('inextp='): inextp = int(line.split('=')[1])
        elif line.startswith('seedarray='):
            seed = [int(x) for x in line.split('=', 1)[1].split(',')]
    return seed, inext, inextp

def parse_pool(path):
    level = odds = None
    nodes = []
    for line in open(path):
        line = line.strip()
        if line.startswith('level='): level = int(line.split('=')[1])
        elif line.startswith('odds='): odds = [int(x) for x in line.split('=')[1].split(',')]
        elif re.match(r'^\d+ \d+ \d+ \d+ \d+$', line):
            h, q, w, c, i0 = (int(x) for x in line.split())
            nodes.append((h, q, w, c, i0))
    return level, odds, nodes

def parse_shop(path):
    """jccshop row: slot pos heroConfID star base cost entityID"""
    out = []
    for line in open(path):
        f = line.split()
        if len(f) >= 7 and f[2].isdigit():
            hid = int(f[2])
            if hid >= 31400: hid -= 20000
            elif hid >= 21400: hid -= 10000
            out.append(hid)
    return out

def roll(r, odds, nodes):
    """one shop slot -> hero id"""
    tot = sum(odds)
    x = r.next_max(tot)
    acc = 0
    cost = 1
    for k, o in enumerate(odds):
        acc += o
        if x < acc:
            cost = k + 1
            break
    cand = [n for n in nodes if n[1] == cost and n[3] > 0]
    if not cand:
        cand = [n for n in nodes if n[1] == cost]
    if not cand:
        return None
    W = sum(n[2] for n in cand)
    if W <= 0:
        return None
    y = r.next_max(W)
    acc = 0
    for n in cand:
        acc += n[2]
        if y < acc:
            return n[0]
    return cand[-1][0]

def main():
    seed, inext, inextp = parse_state(sys.argv[1])
    level, odds, nodes = parse_pool(sys.argv[2])
    obs = parse_shop(sys.argv[3])
    print('level=%d odds=%s  observed=%s  nodes=%d' % (level, odds, obs, len(nodes)))
    if not obs:
        print('no observed shop')
        return

    best = []
    for k in range(0, 6000):
        r = Rand(seed, inext, inextp)
        for _ in range(k):
            r.nxt()
        got = []
        ok = True
        for _ in range(5):
            h = roll(r, odds, nodes)
            if h is None:
                ok = False
                break
            got.append(h)
        if not ok:
            continue
        if got == obs:
            best.append(k)
            print('  MATCH at offset %d  (draws used %d)' % (k, r.n - k))
            if len(best) > 6:
                print('  ... more matches, stopping')
                break
    if not best:
        print('no offset matched the exact hero sequence')

main()
