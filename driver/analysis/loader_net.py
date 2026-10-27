import re
d=open('5.10.payload.bin','rb').read()
blob=d[:0xf941]
pat=re.compile(rb'[\x20-\x7e]{4,}')
hits=set()
for m in pat.finditer(blob):
    s=m.group().decode('latin1')
    if re.search(r'https?://|\.cn|\.com|\.net|\.org|\.xyz|\.top|t\.me|@|vermagic|module_layout|module\.sig|nomod|init_module|finit', s, re.I):
        hits.add(s)
for s in sorted(hits)[:80]: print(repr(s))
