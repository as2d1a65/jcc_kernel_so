import struct
d=open('5.10.payload.bin','rb').read()
base=0xf941
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
sym=bn['.symtab']; stro=bn['.strtab']
blob=d[base+stro['o']:base+stro['o']+stro['s']]
cnt=sym['s']//24
und=[]
for i in range(cnt):
    p=base+sym['o']+i*24
    nm,info,other,shndx,val,size=struct.unpack_from('<IBBHQQ',d,p)
    if nm==0 or shndx!=0: continue
    z=blob.find(b'\0',nm)
    und.append(blob[nm:z].decode('latin1'))
und=sorted(set(und))
print('undefined (imported) count:', len(und))
print('\n'.join(und))
