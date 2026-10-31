import re, sys

MBIG = 2147483647

class Rand:
    __slots__ = ('s', 'i', 'j', 'n')
    def __init__(self, seed, inext, inextp):
        self.s = list(seed); self.i = inext; self.j = inextp; self.n = 0
    def clone(self):
        r = Rand(self.s, self.i, self.j); r.n = self.n; return r
    def nxt(self):
        self.n += 1
        i = self.i + 1
        if i >= 56: i = 1
        j = self.j + 1
        if j >= 56: j = 1
        ret = self.s[i] - self.s[j]
        if ret == MBIG: ret -= 1
        if ret < 0: ret += MBIG
        self.s[i] = ret; self.i = i; self.j = j
        return ret

def load_state(path):
    inext = inextp = None; seed = None
    for line in open(path):
        line = line.strip()
        if line.startswith('inext='): inext = int(line.split('=')[1])
        elif line.startswith('inextp='): inextp = int(line.split('=')[1])
        elif line.startswith('seedarray='):
            seed = [int(x) for x in line.split('=', 1)[1].split(',')]
    return seed, inext, inextp

def load_pool(path):
    odds = None
    for line in open(path):
        if line.startswith('odds='):
            odds = [int(x) for x in line.split('=')[1].split(',')]
    return odds

def load_costs(path):
    out = []
    for line in open(path):
        f = line.split()
        if len(f) >= 7 and f[2].isdigit():
            out.append(int(f[5]))
    return out

def cost_from(r, odds, mode):
    tot = sum(odds)
    if mode == 0:
        x = int(r.nxt() * (1.0 / MBIG) * tot)
    else:
        x = r.nxt() % tot
    acc = 0
    for k, o in enumerate(odds):
        acc += o
        if x < acc:
            return k + 1
    return 1

def main():
    seed, inext, inextp = load_state(sys.argv[1])
    odds = load_pool(sys.argv[2])
    obs = load_costs(sys.argv[3])
    print('odds=%s observed costs=%s' % (odds, obs))
    for mode in (0, 1):
        hits = []
        for k in range(0, 8000):
            r = Rand(seed, inext, inextp)
            for _ in range(k): r.nxt()
            got = []
            for _ in range(5):
                got.append(cost_from(r, odds, mode))
                r.nxt()   # skip the hero draw
            if got == obs:
                hits.append(k)
        print('  mode=%d matches=%d  first=%s' % (mode, len(hits), hits[:8]))

main()
