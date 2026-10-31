import os, base64, json, subprocess, sys

ADB = r'D:\zpc\zpc_project_move\REA\tools\platform-tools\adb.exe'
PKG = 'com.android.support'
DIR = '/data/user/0/%s/app_ipc_v2' % PKG

token = base64.urlsafe_b64encode(os.urandom(32)).decode().rstrip('=')
assert len(token) == 43, len(token)
rec = '{"v":2,"tokenEpoch":1,"token":"%s"}' % token
print('token   :', token)
print('record  :', rec)

def sh(cmd):
    r = subprocess.run([ADB, 'shell', 'su -c "%s"' % cmd],
                       capture_output=True, text=True)
    return (r.stdout or '') + (r.stderr or '')

print(sh('mkdir -p %s' % DIR).strip())
print(sh('chmod 700 %s' % DIR).strip())

# write via a temp file then move, all as root
tmp = '/data/local/tmp/tok.json'
open('tok.json', 'w').write(rec)
subprocess.run([ADB, 'push', 'tok.json', tmp], capture_output=True, text=True)
print(sh('cp %s %s/ipc_v2_current_token.json' % (tmp, DIR)).strip())
print(sh('chmod 600 %s/ipc_v2_current_token.json' % DIR).strip())
print(sh('ls -l %s' % DIR).strip())
print(sh('cat %s/ipc_v2_current_token.json' % DIR).strip())

open('token.txt', 'w').write(token)
print('\nsaved to token.txt')
