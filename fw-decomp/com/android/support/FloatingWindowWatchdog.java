package com.android.support;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;

/* loaded from: classes.dex */
public class FloatingWindowWatchdog extends BroadcastReceiver {
    public static final String ACTION_TICK = "com.android.support.WATCHDOG_TICK";
    private static final long FAST_INTERVAL_MS = 2500;
    private static final long INTERVAL_MS = 45000;
    private static final int PI_REQUEST_CODE = 987297;
    private static final String TAG = "FWWatchdog";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (context == null) {
            return;
        }
        Dbg.i(TAG, "onReceive: action=" + (intent != null ? intent.getAction() : null));
        try {
            scheduleNext(context);
            boolean zWasRunning = FloatingServiceState.wasRunning(context);
            boolean zIsServiceReady = FloatingWindowService.isServiceReady();
            if (!zWasRunning) {
                Dbg.d(TAG, "service_was_running=false（用户主动退出过），跳过自动拉起");
                return;
            }
            if (zIsServiceReady) {
                Dbg.d(TAG, "service 已就绪，无需拉起");
                return;
            }
            Log.w(TAG, "★ 悬浮窗服务缺失，主动拉起（绕过 AM 的 app-idle 限制）");
            FloatingWindowService.sIntentionalStart = true;
            FloatingWindowService.sReconnectMode = true;
            context.startForegroundService(new Intent(context, (Class<?>) FloatingWindowService.class));
            Dbg.i(TAG, "★ 已发起 startForegroundService，等待服务上线");
        } catch (Throwable th) {
            Log.e(TAG, "onReceive 异常", th);
        }
    }

    public static void scheduleNext(Context context) {
        scheduleAt(context, INTERVAL_MS);
    }

    public static void scheduleFast(Context context) {
        scheduleAt(context, FAST_INTERVAL_MS);
    }

    private static void scheduleAt(Context context, long j) {
        if (context == null) {
            return;
        }
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM);
            if (alarmManager == null) {
                Log.w(TAG, "scheduleAt: AlarmManager 为空");
                return;
            }
            PendingIntent pendingIntentBuildPendingIntent = buildPendingIntent(context, true);
            if (pendingIntentBuildPendingIntent == null) {
                Log.w(TAG, "scheduleAt: 无法构建 PendingIntent");
            } else {
                alarmManager.setAndAllowWhileIdle(0, System.currentTimeMillis() + j, pendingIntentBuildPendingIntent);
                Dbg.d(TAG, "已调度看门狗触发: +" + (j / 1000) + "s");
            }
        } catch (Throwable th) {
            Log.e(TAG, "scheduleAt 异常", th);
        }
    }

    public static void cancel(Context context) {
        if (context == null) {
            return;
        }
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(NotificationCompat.CATEGORY_ALARM);
            if (alarmManager == null) {
                return;
            }
            PendingIntent pendingIntentBuildPendingIntent = buildPendingIntent(context, false);
            if (pendingIntentBuildPendingIntent == null) {
                Dbg.d(TAG, "看门狗未在调度中，无需取消");
                return;
            }
            alarmManager.cancel(pendingIntentBuildPendingIntent);
            pendingIntentBuildPendingIntent.cancel();
            Dbg.i(TAG, "看门狗已取消");
        } catch (Throwable th) {
            Log.e(TAG, "cancel 异常", th);
        }
    }

    private static PendingIntent buildPendingIntent(Context context, boolean z) {
        Intent intent = new Intent(ACTION_TICK);
        intent.setClass(context, FloatingWindowWatchdog.class);
        intent.setPackage(context.getPackageName());
        return PendingIntent.getBroadcast(context, PI_REQUEST_CODE, intent, z ? 201326592 : 201326592 | 536870912);
    }
}
