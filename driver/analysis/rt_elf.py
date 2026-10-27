import struct, sys, os, re

def elf_hdr(d, base=0):
    assert d[base:base+4]==b'\x7fELF'
    cls=d[base+4]; ee='<' if d[base+5]==1 else '>'
    e_type, e_machine = struct.unpack_from(ee+'HH', d, base+16)
    e_entry, e_phoff, e_shoff = struct.unpack_from(ee+'QQQ', d, base+24)
    e_flags = struct.unpack_from(ee+'I', d, base+48)[0]
    _,_,_,_,e_shnum,e_shstrndx = struct.unpack_from(ee+'HHHHHH', d, base+52)
    e_phentsize = struct.unpack_from(ee+'H', d, base+54)[0]
    e_phnum = struct.unpack_from(ee+'H', d, base+56)[0]
    e_shentsize = struct.unpack_from(ee+'H', d, base+58)[0]
    return dict(cls=cls,ee=ee,type=e_type,mach=e_machine,entry=e_entry,
                phoff=e_phoff,shoff=e_shoff,flags=e_flags,shnum=e_shnum,
                shstrndx=e_shstrndx,phentsize=e_phentsize,phnum=e_phnum,
                shentsize=e_shentsize)

def sections(d, base, h):
    S=[]
    for i in range(h['shnum']):
        p = base + h['shoff'] + i*h['shentsize']
        nm,t,fl,ad,of,sz,lk,inf,al,es = struct.unpack_from(h['ee']+'IIQQQQIIQQ', d, p)
        S.append(dict(nm=nm,t=t,fl=fl,a=ad,o=of,s=sz,lk=lk,inf=inf,es=es))
    st = d[base+S[h['shstrndx']]['o'] : base+S[h['shstrndx']]['o']+S[h['shstrndx']]['s']]
    for s in S:
        z=st.find(b'\0',s['nm']); s['n']=st[s['nm']:z].decode('latin1')
    return S

path = sys.argv[1]; base = int(sys.argv[2],16)
d = open(path,'rb').read()
h = elf_hdr(d, base)
print('=== header ===')
print(' type=%d mach=%d entry=0x%x phnum=%d shnum=%d' % (h['type'],h['mach'],h['entry'],h['phnum'],h['shnum']))
if h['phnum']:
    print(' --- program headers ---')
    for i in range(h['phnum']):
        p = base + h['phoff'] + i*h['phentsize']
        pt, pflags, poff, pvaddr, ppaddr, pfilesz, pmemsz, palign = struct.unpack_from(h['ee']+'IIQQQQQQ', d, p)
        print(f'   PT type=0x{pt:x} flags=0x{pflags:x} off=0x{poff:x} vaddr=0x{pvaddr:x} filesz=0x{pfilesz:x} memsz=0x{pmemsz:x}')
S = sections(d, base, h)
print(' --- sections (%d) ---' % len(S))
for s in S:
    if s['t']==8 or s['s']==0 and s['n']: continue
    print(f"   {s['n']:24s} type={s['t']:2d} off=0x{s['o']:07x} size=0x{s['s']:07x} entsize={s['es']}")
# .modinfo
mi = [s for s in S if s['n']=='.modinfo']
if mi:
    blob = d[base+mi[0]['o']: base+mi[0]['o']+mi[0]['s']]
    print(' --- .modinfo ---')
    for part in blob.split(b'\0'):
        if part: print('   ', part.decode('utf-8','replace'))
