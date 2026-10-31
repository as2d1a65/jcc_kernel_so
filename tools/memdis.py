import subprocess, sys, re
from capstone import Cs, CS_ARCH_ARM64, CS_MODE_ARM
from capstone.arm64 import ARM64_OP_IMM

ADB = r'D:\zpc\zpc_project_move\REA\tools\platform-tools\adb.exe'
JCCD = '/data/local/tmp/jccd'
PID = '9447'

def read_mem(addr, size):
    cmd = [ADB, 'shell', 'su -c "%s dump %s 0x%x 0x%x"' % (JCCD, PID, addr, size)]
    out = subprocess.run(cmd, capture_output=True, text=True, timeout=120).stdout
    data = bytearray()
    for line in out.splitlines():
        m = re.match(r'^([0-9a-f]{16})\s+((?:[0-9a-f]{2}\s+)+)', line)
        if not m:
            continue
        bs = re.findall(r'[0-9a-f]{2}', m.group(2))
        data.extend(int(b, 16) for b in bs)
    return bytes(data[:size])

def main():
    addr = int(sys.argv[1], 16)
    size = int(sys.argv[2], 16) if len(sys.argv) > 2 else 0x200
    labels = {}
    for a in sys.argv[3:]:
        if '=' in a:
            k, v = a.split('=', 1)
            labels[int(k, 16)] = v

    code = read_mem(addr, size)
    if len(code) < 16:
        print('read failed (%d bytes)' % len(code))
        return

    md = Cs(CS_ARCH_ARM64, CS_MODE_ARM)
    md.detail = True
    n = 0
    for ins in md.disasm(code, addr):
        note = ''
        if ins.mnemonic in ('bl', 'b', 'cbz', 'cbnz', 'tbz', 'tbnz'):
            for op in ins.operands:
                if op.type == ARM64_OP_IMM:
                    t = op.imm
                    note = '; %s' % labels.get(t, '0x%x' % t)
        print('  0x%x:  %-8s %-26s %s' % (ins.address, ins.mnemonic, ins.op_str, note))
        n += 1
        if n >= 200:
            break

main()
