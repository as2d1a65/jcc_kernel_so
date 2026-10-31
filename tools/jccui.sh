#!/system/bin/sh
# jccui.sh - floating ball HUD for the running match (root).
#   su -c "sh /data/local/tmp/jccui.sh"        start
#   su -c "sh /data/local/tmp/jccui.sh stop"   stop
#   su -c "sh /data/local/tmp/jccui.sh status"
# Long-press the green ball to open the panel, long-press again to close.
# Drag it to move. No APK is installed.

D=/data/local/tmp
PKG=com.tencent.jkchess
CLS=0x70b36f8000
TXT=$D/jcc_dash.txt
STATE=$D/jcc_ui_state
JAR=$D/ball.jar
ATLAS=$D/atlas.png
EVDEV=${EVDEV:-/dev/input/event6}

stop_all() {
    pkill jcctapd 2>/dev/null
    pkill -f 'ball.jar' 2>/dev/null
    echo stopped
}

case "$1" in
stop)   stop_all; exit 0 ;;
status)
    echo "daemon : $(pgrep jcctapd)"
    echo "ball   : $(pgrep -f 'ball.jar')"
    echo "state  : $(cat $STATE 2>/dev/null)"
    echo "data   : $(wc -l < $TXT 2>/dev/null) lines"
    exit 0
    ;;
esac

[ -f "$JAR" ]   || { echo "missing $JAR";   exit 1; }
[ -f "$ATLAS" ] || { echo "missing $ATLAS"; exit 1; }
[ -x "$D/jcctapd" ] || { echo "missing $D/jcctapd"; exit 1; }
[ -x "$D/jccdash" ] || { echo "missing $D/jccdash"; exit 1; }

[ -f "$STATE" ] || echo "0 40 300" > $STATE

pkill jcctapd 2>/dev/null
nohup $D/jcctapd $EVDEV > $D/tapd.log 2>&1 &
sleep 1

setsid app_process -Djava.class.path=$JAR /system/bin Ball > $D/ball.log 2>&1 < /dev/null &
sleep 1

echo "ball running. long-press it to open the panel."
echo "updating $TXT every 1s  (stop with: jccui.sh stop)"

while true; do
    P=$(pidof $PKG 2>/dev/null)
    if [ -n "$P" ]; then
        $D/jccdash $P $CLS auto > $TXT.new 2>&1
        if [ -s $TXT.new ]; then mv $TXT.new $TXT; else rm -f $TXT.new; fi
    fi
    pgrep -f 'ball.jar' > /dev/null || {
        echo "ball exited, restarting"
        setsid app_process -Djava.class.path=$JAR /system/bin Ball >> $D/ball.log 2>&1 < /dev/null &
    }
    sleep 1
done