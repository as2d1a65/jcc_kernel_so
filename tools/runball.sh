#!/system/bin/sh
/data/local/tmp/jcctapd /dev/input/event6 &
exec app_process -Djava.class.path=/data/local/tmp/ball.jar /system/bin Ball
