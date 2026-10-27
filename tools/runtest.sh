#!/system/bin/sh
killall hwtest 2>/dev/null
chmod 755 /data/local/tmp/hwtest
SYM=${1:-strlen}
setsid /data/local/tmp/hwtest "$SYM" >/data/local/tmp/hw.log 2>&1 </dev/null &
sleep 1
cat /data/local/tmp/hw.log