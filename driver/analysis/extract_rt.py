import sys, re, os
p = sys.argv[1]
d = open(p,'rb').read()
m = re.search(rb'exit 0\r?\n#', d)
print('exit0 at', m.start() if m else None)
# the marker line is '#XX' possibly with trailing bytes; payload begins after the next \n
i = d.find(b'\n', m.end())
print('marker bytes:', d[m.end()-1: i])
print('payload starts at', hex(i+1))
payload = d[i+1:]
print('payload size', len(payload))
out = os.path.splitext(p)[0] + '.payload.bin'
open(out,'wb').write(payload)
print('wrote', out)
# ELF positions inside payload
pos=[]; q=0
while True:
    q = payload.find(b'\x7fELF', q)
    if q<0: break
    pos.append(q); q+=1
print('ELF offsets in payload:', [hex(x) for x in pos])
for o in pos:
    if o+20 > len(payload): continue
    d2 = payload[o:]
    cls, endian = d2[4], d2[5]
    import struct
    et = struct.unpack_from('<H', d2, 16)[0]
    em = struct.unpack_from('<H', d2, 18)[0]
    print(f'  @{hex(o)} class={cls} endian={endian} e_type={et} e_machine={em}')
