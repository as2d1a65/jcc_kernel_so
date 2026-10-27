import struct, sys, os, re

def find_elfs(d):
    out=[]; q=0
    while True:
        q = d.find(b'\x7fELF', q)
        if q<0: break
        if q+64 <= len(d) and d[q+4]==2 and d[q+5]==1:
            et = struct.unpack_from('<H', d, q+16)[0]
            em = struct.unpack_from('<H', d, q+18)[0]
            if em == 183:
                out.append((q, et))
        q += 1
    return out

def sections(d, base):
    e_shoff = struct.unpack_from('<Q', d, base+0x28)[0]
    e_shentsize = struct.unpack_from('<H', d, base+0x3a)[0]
    e_shnum = struct.unpack_from('<H', d, base+0x3c)[0]
    e_shstrndx = struct.unpack_from('<H', d, base+0x3e)[0]
    S=[]
    for i in range(e_shnum):
        p = base + e_shoff + i*e_shentsize
        nm,t,fl,ad,of,sz,lk,inf,al,es = struct.unpack_from('<IIQQQQIIQQ', d, p)
        S.append(dict(nm=nm,t=t,fl=fl,a=ad,o=of,s=sz,lk=lk,inf=inf,es=es))
    st = d[base+S[e_shstrndx]['o']: base+S[e_shstrndx]['o']+S[e_shstrndx]['s']]
    for s in S:
        z=st.find(b'\0',s['nm']); s['n']=st[s['nm']:z].decode('latin1')
    return S, e_shoff+e_shnum*e_shentsize+base

def modinfo(d, base, S):
    r={}
    for s in S:
        if s['n']=='.modinfo':
            blob=d[base+s['o']:base+s['o']+s['s']]
            for part in blob.split(b'\0'):
                if not part: continue
                t=part.decode('utf-8','replace')
                if '=' in t:
                    k,v=t.split('=',1); r.setdefault(k,[]).append(v)
    return r

files = ['5.4.sh','5.4b.sh','5.4c.sh','5.10.sh','5.15.sh','6.1.sh','6.6.sh','6.12.23.sh','6.12.58.sh','6.18.sh']
print(f"{'file':12s} {'ko@':>8s} {'name':12s} {'vermagic'}")
print('-'*110)
rows=[]
for f in files:
    d=open(f,'rb').read()
    m=re.search(rb'exit 0\r?\n#', d)
    i=d.find(b'\n', m.end())
    payload=d[i+1:]
    elfs=find_elfs(payload)
    rel=[o for o,t in elfs if t==1]
    dyn=[o for o,t in elfs if t==3]
    if not rel:
        print(f'{f:12s}  no ET_REL found -> {elfs}'); continue
    base=rel[0]
    try:
        S,end = sections(payload, base)
        mi = modinfo(payload, base, S)
        name = mi.get('name',['?'])[0]
        vm = mi.get('vermagic',['?'])[0]
        au = ','.join(mi.get('author',[]))
        print(f'{f:12s} {hex(base):>8s} {name:12s} {vm}')
        rows.append((f,hex(base),name,vm,au,end-base))
    except Exception as e:
        print(f'{f:12s} parse error {e}')
