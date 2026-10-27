import struct, re
d=open('5.10.payload.bin','rb').read()
base=0xf941
# section table for the module
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
byname={s['n']:s for s in S}
sym=byname['.symtab']; stro=byname['.strtab']
blob=d[base+stro['o']:base+stro['o']+stro['s']]
cnt=sym['s']//24
names=[]
for i in range(cnt):
    p=base+sym['o']+i*24
    nm,info,other,shndx,val,size=struct.unpack_from('<IBBHQQ',d,p)
    if nm==0: continue
    z=blob.find(b'\0',nm)
    name=blob[nm:z].decode('latin1')
    typ=info&0xf; bind=info>>4
    if shndx==0: continue          # undefined
    names.append((name,typ,bind,size))
print('defined syms:', len(names))
key=re.compile(r'hook|hwbp|brk|pte|touch|gyro|ioctl|proc|hide|kall|module|break|reg|track|copy|mem|phys|pte|walk|page|signal|net|report|verify|selftest|wrap|trampoline|inline|fp_|sys_', re.I)
for name,typ,bind,size in sorted(names, key=lambda x:-x[3]):
    if key.search(name) and len(name)<80:
        print(f'  {size:8d}  {name}')
