import sys, struct
d=open('5.10.sh','rb').read()
print('size', len(d))
print('head 64:', d[:64])
print('head hex:', d[:64].hex())
# try to find ELF magic
i = d.find(b'\x7fELF')
print('first ELF at', hex(i) if i>=0 else 'none')
# count ELF magics
n=0; pos=[]; p=0
while True:
    p = d.find(b'\x7fELF', p)
    if p<0: break
    pos.append(p); p+=1
print('ELF magic count:', len(pos), [hex(x) for x in pos[:20]])
print('tail 64:', d[-64:].hex())
