import struct, re
d=open('5.10.payload.bin','rb').read()
base=0xf941
# gather all printable strings from module .rodata/.data/.text
e_shoff=struct.unpack_from('<Q',d,base+0x28)[0]
es=struct.unpack_from('<H',d,base+0x3a)[0]
n=struct.unpack_from('<H',d,base+0x3c)[0]
si=struct.unpack_from('<H',d,base+0x3e)[0]
S=[]
for i in range(n):
    p=base+e_shoff+i*es
    nm,t,fl,ad,of,sz,lk,inf,al,ee=struct.unpack_from('<IIQQQQIIQQ',d,p)
    S.append(dict(nm=nm,t=t,o=of,s=sz))
st=d[base+S[si]['o']:base+S[si]['o']+S[si]['s']]
for s in S:
    z=st.find(b'\0',s['nm']); s['n']=st[s['nm']:z].decode('latin1')
bn={s['n']:s for s in S}
targets=['.rodata','.data','.text','.modinfo']
pat=re.compile(rb'[\x20-\x7e]{4,}')
seen=set()
for t in targets:
    s=bn.get(t)
    if not s: continue
    blob=d[base+s['o']:base+s['o']+s['s']]
    for m in pat.finditer(blob):
        v=m.group().decode('latin1')
        if re.search(r'/proc|/dev|inte|rtqdnb9|nb9|%s|​|hook|verif|report|http|token|licen|expire|@', v):
            if v in seen: continue
            seen.add(v)
for v in sorted(seen):
    if len(v)<120: print(repr(v))
