/*
 * jcctapd - read the real touchpanel and turn gestures on the floating ball
 * into state changes, so the overlay window itself never needs to be
 * touchable (ColorOS crashes non-app processes that receive pointer events).
 *
 * Ball geometry lives in /data/local/tmp/jcc_ui_state:
 *     open x y
 *   open : 0 closed / 1 panel shown
 *   x y  : ball position in screen pixels (2780x1264 landscape)
 *
 * Gestures:
 *   long press >= 400 ms, movement < 40 px, inside the ball  -> toggle
 *   drag movement > 40 px starting inside the ball           -> move ball
 *
 * Touchpanel raw axes are 0..20224 x 0..44480 in portrait; the mapping that
 * matches our injection is:
 *   screen_x = raw_y / 16
 *   screen_y = 1264 - raw_x / 16
 *
 * usage: jcctapd [event_device]
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <errno.h>
#include <time.h>
#include <linux/input.h>

#define SCREEN_W 2780
#define SCREEN_H 1264
#define BALL_R   54
#define STATE    "/data/local/tmp/jcc_ui_state"
#define LONG_MS  400
#define MOVE_TOL 40

static int   g_open = 0;
static int   g_bx = 40, g_by = 300;
static int   tracking = -1;
static int   down = 0;
static int   start_x, start_y, cur_x, cur_y;
static long  down_ms;
static int   moved = 0;
static int   dragged = 0;

static long now_ms(void)
{
    struct timespec ts;
    clock_gettime(CLOCK_MONOTONIC, &ts);
    return ts.tv_sec * 1000L + ts.tv_nsec / 1000000L;
}

static void load_state(void)
{
    FILE *f = fopen(STATE, "r");
    if (!f) return;
    if (fscanf(f, "%d %d %d", &g_open, &g_bx, &g_by) != 3) {
        g_open = 0; g_bx = 40; g_by = 300;
    }
    fclose(f);
    if (g_bx < 0) g_bx = 0;
    if (g_by < 0) g_by = 0;
    if (g_bx > SCREEN_W - 60) g_bx = SCREEN_W - 60;
    if (g_by > SCREEN_H - 60) g_by = SCREEN_H - 60;
}

static void save_state(void)
{
    FILE *f = fopen(STATE ".tmp", "w");
    if (!f) return;
    fprintf(f, "%d %d %d\n", g_open, g_bx, g_by);
    fclose(f);
    rename(STATE ".tmp", STATE);
}

static int in_ball(int sx, int sy)
{
    int dx = sx - (g_bx + BALL_R);
    int dy = sy - (g_by + BALL_R);
    return dx * dx + dy * dy <= (BALL_R + 30) * (BALL_R + 30);
}

int main(int argc, char **argv)
{
    const char *dev = (argc > 1) ? argv[1] : "/dev/input/event6";
    int fd;
    struct input_event ev;
    int rx = 0, ry = 0;

    load_state();
    fd = open(dev, O_RDONLY);
    if (fd < 0) {
        fprintf(stderr, "open %s: %s\n", dev, strerror(errno));
        return 2;
    }
    fprintf(stderr, "jcctapd: %s  ball=(%d,%d) open=%d\n", dev, g_bx, g_by, g_open);

    while (read(fd, &ev, sizeof(ev)) == sizeof(ev)) {
        if (ev.type == EV_ABS) {
            if (ev.code == ABS_MT_POSITION_X) rx = ev.value;
            else if (ev.code == ABS_MT_POSITION_Y) ry = ev.value;
            else if (ev.code == ABS_MT_TRACKING_ID) {
                tracking = ev.value;
                if (tracking < 0) {
                    /* finger lifted */
                    long dur = now_ms() - down_ms;
                    int sx = ry / 16;
                    int sy = SCREEN_H - rx / 16;
                    fprintf(stderr, "up screen=(%d,%d) dur=%ldms moved=%d ball=(%d,%d) inside=%d\n",
                            sx, sy, dur, moved, g_bx, g_by, in_ball(start_x, start_y));
                    if (dragged) {
                        g_bx = sx - BALL_R;
                        g_by = sy - BALL_R;
                        if (g_bx < 0) g_bx = 0;
                        if (g_by < 0) g_by = 0;
                        if (g_bx > SCREEN_W - 60) g_bx = SCREEN_W - 60;
                        if (g_by > SCREEN_H - 60) g_by = SCREEN_H - 60;
                        save_state();
                    } else if (down && !moved && dur >= LONG_MS) {
                        g_open = !g_open;
                        save_state();
                    }
                    down = 0; moved = 0; dragged = 0;
                } else {
                    down = 1;
                    down_ms = now_ms();
                    fprintf(stderr, "down raw=(%d,%d) screen=(%d,%d)\n",
                            rx, ry, ry / 16, SCREEN_H - rx / 16);
                    moved = 0; dragged = 0;
                    start_x = ry / 16;
                    start_y = SCREEN_H - rx / 16;
                }
            }
            if (down && tracking >= 0) {
                cur_x = ry / 16;
                cur_y = SCREEN_H - rx / 16;
                {
                    int dx = cur_x - start_x, dy = cur_y - start_y;
                    if (dx < 0) dx = -dx;
                    if (dy < 0) dy = -dy;
                    if (dx > MOVE_TOL || dy > MOVE_TOL) {
                        moved = 1;
                        if (in_ball(start_x, start_y)) dragged = 1;
                    }
                }
            }
        }
    }
    return 0;
}
