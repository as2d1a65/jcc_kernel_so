import re, sys
d=open('5.10.payload.bin','rb').read()
# search whole payload for meaningful ascii/utf16 strings, focus on loader region (0..0xf941)
lo, hi = 0, 0xf941
blob = d[lo:hi]
pat = re.compile(rb'[\x20-\x7e]{5,}')
seen=set()
for m in pat.finditer(blob):
    s=m.group().decode('latin1')
    if s in seen: continue
    seen.add(s)
for s in sorted(seen):
    if re.search(r'vermagic|uname|release|init_module|finit_module|delete_module|insmod|rmmod|\.ko|/data/|/proc/|/sys/|modprobe|kallsyms|sig|module_layout|nomodule|finfo|top_|kmi|android1[2-9]', s, re.I):
        print(s)
