import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Ball - display-only overlay.  It never receives touches: ColorOS's gesture
 * extension crashes non-app processes on pointer dispatch, so input is read
 * from the touchpanel by jcctapd instead and communicated through
 * /data/local/tmp/jcc_ui_state ("open x y").
 */
public class Ball {

    static final String SRC   = "/data/local/tmp/jcc_dash.txt";
    static final String ATLAS = "/data/local/tmp/atlas.png";
    static final String STATE = "/data/local/tmp/jcc_ui_state";

    static final int CELL_W = 15, CELL_H = 22, COLS = 16;
    static final int BALL_D = 84;
    static final int PAD = 12;
    static final int LINE = 22;
    static final int SW = 2780, SH = 1264;

    static Bitmap atlas;
    static String[] lines = new String[0];
    static boolean open = false;
    static int bx = 40, by = 300;
    static int panelW = 700, panelH = 400;

    static void loadText() {
        ArrayList<String> out = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(SRC));
            String s;
            while ((s = br.readLine()) != null) out.add(s);
            br.close();
        } catch (Throwable ignored) {}
        lines = out.toArray(new String[0]);
        int maxc = 1;
        for (String s : lines) if (s.length() > maxc) maxc = s.length();
        if (maxc > 60) maxc = 60;
        panelW = maxc * CELL_W + PAD * 2;
        panelH = lines.length * LINE + PAD * 2;
        if (panelH > SH - 20) panelH = SH - 20;
    }

    static void loadState() {
        try {
            BufferedReader br = new BufferedReader(new FileReader(STATE));
            String s = br.readLine();
            br.close();
            String[] f = s.trim().split("\\s+");
            open = Integer.parseInt(f[0]) != 0;
            if (f.length >= 3) { bx = Integer.parseInt(f[1]); by = Integer.parseInt(f[2]); }
        } catch (Throwable ignored) {}
    }

    static void drawText(Canvas c, Paint p, String s, int x, int y) {
        if (atlas == null) return;
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            if (ch < 32 || ch > 126) ch = '?';
            int idx = ch - 32;
            int sx = (idx % COLS) * CELL_W;
            int sy = (idx / COLS) * CELL_H;
            Rect src = new Rect(sx, sy, sx + CELL_W, sy + CELL_H);
            Rect dst = new Rect(x + i * CELL_W, y, x + i * CELL_W + CELL_W, y + CELL_H);
            c.drawBitmap(atlas, src, dst, p);
        }
    }

    static class V extends View {
        final Paint bg = new Paint();
        final Paint ball = new Paint();
        final Paint ring = new Paint();
        final Paint tx = new Paint();
        final Paint ring2 = new Paint();

        V(Context ctx) {
            super(ctx);
            bg.setColor(0xE0101410);
            bg.setAntiAlias(true);
            ball.setColor(0xFF1F7A3A);
            ball.setAntiAlias(true);
            ring.setColor(0xFF78FF8C);
            ring.setStyle(Paint.Style.STROKE);
            ring.setStrokeWidth(5);
            ring.setAntiAlias(true);
            tx.setColor(0xFFBFFFD0);
            tx.setAntiAlias(true);
            ring2.setColor(0xFFFFFFFF);
            ring2.setAntiAlias(true);
        }

        @Override
        protected void onDraw(Canvas c) {
            if (!open) {
                float r = BALL_D / 2f;
                c.drawCircle(r, r, r - 4, ball);
                c.drawCircle(r, r, r - 4, ring);
                drawText(c, tx, "JCC", (int) (r - 3 * CELL_W / 2f), (int) (r - CELL_H / 2f));
                return;
            }
            RectF rf = new RectF(0, 0, getWidth(), getHeight());
            c.drawRoundRect(rf, 14, 14, bg);
            int maxLines = (getHeight() - PAD * 2) / LINE;
            int n = lines.length < maxLines ? lines.length : maxLines;
            for (int i = 0; i < n; i++)
                drawText(c, tx, lines[i], PAD, PAD + i * LINE);
            /* close button sits exactly where the ball was, so the same
               tap region collapses the panel */
            float r = BALL_D / 2f;
            c.drawCircle(r, r, r - 3, ball);
            c.drawCircle(r, r, r - 3, ring);
            drawText(c, ring2, "X", (int) (r - CELL_W / 2f), (int) (r - CELL_H / 2f));
        }
    }

    public static void main(String[] args) {
        try {
            Looper.prepareMainLooper();

            try { atlas = BitmapFactory.decodeFile(ATLAS); } catch (Throwable t) {}
            loadText();
            loadState();

            Class<?> atClass = Class.forName("android.app.ActivityThread");
            Object app = atClass.getMethod("systemMain").invoke(null);
            Context ctx = (Context) atClass.getMethod("getSystemContext").invoke(app);
            final WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);

            final V v = new V(ctx);
            final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.width = BALL_D;
            lp.height = BALL_D;
            lp.gravity = Gravity.TOP | Gravity.LEFT;
            lp.x = bx;
            lp.y = by;
            lp.format = PixelFormat.TRANSLUCENT;
            lp.type = 2038;
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

            wm.addView(v, lp);

            final Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                long lastTxt = 0;
                String lastState = "";
                public void run() {
                    try {
                        String st = "";
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(STATE));
                            st = br.readLine();
                            br.close();
                            if (st == null) st = "";
                        } catch (Throwable ignored) {}

                        if (!st.equals(lastState)) {
                            lastState = st;
                            loadState();
                            if (open) {
                                loadText();
                                lp.width = panelW;
                                lp.height = panelH;
                                int x = bx, y = by;
                                if (x + panelW > SW) x = SW - panelW;
                                if (y + panelH > SH) y = SH - panelH;
                                if (x < 0) x = 0;
                                if (y < 0) y = 0;
                                lp.x = x; lp.y = y;
                            } else {
                                lp.width = BALL_D;
                                lp.height = BALL_D;
                                lp.x = bx; lp.y = by;
                            }
                            try { wm.updateViewLayout(v, lp); } catch (Throwable ignored) {}
                            v.invalidate();
                        }

                        java.io.File f = new java.io.File(SRC);
                        if (open && f.exists() && f.lastModified() != lastTxt) {
                            lastTxt = f.lastModified();
                            loadText();
                            lp.width = panelW;
                            lp.height = panelH;
                            try { wm.updateViewLayout(v, lp); } catch (Throwable ignored) {}
                            v.invalidate();
                        }
                    } catch (Throwable ignored) {}
                    h.postDelayed(this, 300);
                }
            });

            Looper.loop();
        } catch (Throwable t) {
            System.out.println("ball fatal: " + t);
            t.printStackTrace();
        }
    }
}
