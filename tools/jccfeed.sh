#!/system/bin/sh
D=/data/local/tmp
CLS=0x70b36f8000
while true; do
    P=$(pidof com.tencent.jkchess 2>/dev/null)
    if [ -n "$P" ]; then
        $D/jccdash $P $CLS auto 2>&1 | head -12 > $D/jcc_dash.new
        if [ -s $D/jcc_dash.new ]; then
            rm -f $D/jcc_dash.txt
            mv $D/jcc_dash.new $D/jcc_dash.txt
            chmod 666 $D/jcc_dash.txt
        else
            rm -f $D/jcc_dash.new
        fi
    fi
    sleep 1
done