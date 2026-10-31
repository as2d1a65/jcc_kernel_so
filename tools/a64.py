import sys
from capstone import Cs, CS_ARCH_ARM64, CS_MODE_ARM
from capstone.arm64 import ARM64_OP_IMM

SO   = r'D:\zpc\zpc_project_move\crack\android\jcc\.work\libil2cpp.so'
BASE = 0x70c9cf0000          # runtime base of libil2cpp.so on the device

def main():
    addr = int(sys.argv[1], 16)
    size = int(sys.argv[2], 16) if len(sys.argv) > 2 else 0x200
    foff = addr - BASE

    labels = {}
    for a in sys.argv[3:]:
        if '=' in a:
            k, v = a.split('=', 1)
            labels[int(k, 16)] = v

    with open(SO, 'rb') as f:
        f.seek(foff)
        code = f.read(size)

    md = Cs(CS_ARCH_ARM64, CS_MODE_ARM)
    md.detail = True
    n = 0
    for ins in md.disasm(code, addr):
        note = ''
        if ins.mnemonic in ('bl', 'b'):
            for op in ins.operands:
                if op.type == ARM64_OP_IMM:
                    t = op.imm
                    note = '   ; -> %s' % labels.get(t, '0x%x' % t)
        print('  0x%x:  %-8s %-26s%s' % (ins.address, ins.mnemonic, ins.op_str, note))
        n += 1
        if n > 400:
            break

main()
