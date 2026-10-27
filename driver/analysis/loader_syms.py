import struct, sys, re
d=open('5.10.payload.bin','rb').read()

# ---- loader (base 0) ----
def sections(d, base):
    e_shoff=struct.unpack_from('<Q',d,base+0x28)[0]
    es=struct.unpack_from('<H',d,base+0x3a)[0]
    n=struct.unpack_from('<H',d,base+0x3c)[0]
    si=struct.unpack_from('<H',d,base+0x3e)[0]
    S=[]
    for i in range(n):
        p=base+e_shoff+i*es
        nm,t,fl,ad,of,sz,lk,inf,al,ee=struct.unpack_from('<IIQQQQIIQQ',d,p)
        S.append(dict(nm=nm,t=t,o=of,s=sz,lk=lk,es=ee))
    st=d[base+S[si]['o']:base+S[si]['o']+S[si]['s']]
    for s in S:
        z=st.find(b'\0',s['nm']); s['n']=st[s['nm']:z].decode('latin1')
    return S

S=sections(d,0)
dynstr=[s for s in S if s['n']=='.dynstr'][0]
ds=d[dynstr['o']:dynstr['o']+dynstr['s']]
print('=== loader imported symbols ===')
p=0
syms=[]
while p < dynstr['s']:
    z=ds.find(b'\0',p)
    if z<0: break
    nm=ds[p:z].decode('latin1')
    if nm: syms.append(nm)
    p=z+1
print('\n'.join(sorted(set(syms))))
