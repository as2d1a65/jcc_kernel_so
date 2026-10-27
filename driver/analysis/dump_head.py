import sys
d=open('5.10.sh','rb').read()
head = d[:0xc9a]
try:
    print(head.decode('utf-8'))
except Exception as e:
    print('decode err', e)
    print(head.decode('latin1'))
