package com.android.support;

import android.content.Context;
import android.graphics.Point;
import android.util.TypedValue;
import android.view.WindowManager;

/* loaded from: classes.dex */
public class ScreenUtils {
    public static int dp2px(Context context, float f) {
        return (int) TypedValue.applyDimension(1, f, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float f) {
        return (int) TypedValue.applyDimension(2, f, context.getResources().getDisplayMetrics());
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static int scaleByWidth(Context context, int i, int i2) {
        return (int) (i2 * (getScreenWidth(context) / i));
    }

    public static int adaptSize(Context context, float f) {
        float f2 = context.getResources().getDisplayMetrics().density;
        return dp2px(context, f * (f2 >= 3.0f ? 0.9f : f2 >= 2.0f ? 1.0f : 1.1f));
    }

    public static int getRealScreenWidth(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        if (windowManager != null) {
            Point point = new Point();
            windowManager.getDefaultDisplay().getRealSize(point);
            return point.x;
        }
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getRealScreenHeight(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService("window");
        if (windowManager != null) {
            Point point = new Point();
            windowManager.getDefaultDisplay().getRealSize(point);
            return point.y;
        }
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static boolean isLandscape(Context context) {
        return getRealScreenWidth(context) > getRealScreenHeight(context);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout & 15) >= 3;
    }
}
