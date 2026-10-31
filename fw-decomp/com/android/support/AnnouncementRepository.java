package com.android.support;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import com.android.support.JccGatewayClient;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* loaded from: classes.dex */
final class AnnouncementRepository {
    private static final long MAX_AGE_MS = 86400000;
    private static AnnouncementRepository shared;
    private long cachedAt;
    private AnnouncementContent content;
    private boolean failed;
    private boolean loading;
    private final SharedPreferences preferences;
    private long retryAt;
    private final Handler main = new Handler(Looper.getMainLooper());
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final List<Listener> listeners = new ArrayList();

    interface Listener {
        void onNotice(Snapshot snapshot);
    }

    interface Subscription {
        void close();
    }

    static final class Snapshot {
        final AnnouncementContent content;
        final boolean failed;
        final boolean loading;

        Snapshot(AnnouncementContent announcementContent, boolean z, boolean z2) {
            this.content = announcementContent;
            this.loading = z;
            this.failed = z2;
        }

        String status() {
            if (this.loading) {
                return "正在获取最新公告…";
            }
            AnnouncementContent announcementContent = this.content;
            return (announcementContent == null || announcementContent.text.isEmpty()) ? this.failed ? "公告暂时无法获取，点击重试" : "暂无新公告" : (this.failed || this.content.stale) ? "暂未连接 · 显示上次公告" : "官方发布";
        }
    }

    static synchronized AnnouncementRepository get(Context context) {
        if (shared == null) {
            shared = new AnnouncementRepository(context.getApplicationContext());
        }
        return shared;
    }

    private AnnouncementRepository(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("main_announcement", 0);
        this.preferences = sharedPreferences;
        this.cachedAt = sharedPreferences.getLong("notice_cached_at", 0L);
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j = this.cachedAt;
        long j2 = jCurrentTimeMillis - j;
        if (j <= 0 || j2 < 0 || j2 >= MAX_AGE_MS) {
            return;
        }
        String string = sharedPreferences.getString("notice_text", "");
        String string2 = sharedPreferences.getString("notice_id", "");
        if (string == null || string.codePointCount(0, string.length()) > 8000 || !AnnouncementContent.digest(string).equals(string2)) {
            return;
        }
        this.content = new AnnouncementContent(string2, string, true);
    }

    Subscription observe(final Listener listener) {
        this.listeners.add(listener);
        listener.onNotice(snapshot());
        refresh();
        return new Subscription() { // from class: com.android.support.AnnouncementRepository$$ExternalSyntheticLambda0
            @Override // com.android.support.AnnouncementRepository.Subscription
            public final void close() {
                this.f$0.m74lambda$observe$0$comandroidsupportAnnouncementRepository(listener);
            }
        };
    }

    /* renamed from: lambda$observe$0$com-android-support-AnnouncementRepository, reason: not valid java name */
    /* synthetic */ void m74lambda$observe$0$comandroidsupportAnnouncementRepository(Listener listener) {
        this.listeners.remove(listener);
    }

    void refresh() {
        if (this.loading || SystemClock.elapsedRealtime() < this.retryAt) {
            return;
        }
        this.loading = true;
        publish();
        this.worker.execute(new Runnable() { // from class: com.android.support.AnnouncementRepository$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m76lambda$refresh$2$comandroidsupportAnnouncementRepository();
            }
        });
    }

    /* renamed from: lambda$refresh$2$com-android-support-AnnouncementRepository, reason: not valid java name */
    /* synthetic */ void m76lambda$refresh$2$comandroidsupportAnnouncementRepository() {
        final AnnouncementContent announcementContentFetchAnnouncement;
        try {
            announcementContentFetchAnnouncement = JccGatewayClient.shared().fetchAnnouncement(new JccGatewayClient.Cancellation());
        } catch (Exception unused) {
            announcementContentFetchAnnouncement = null;
        }
        this.main.post(new Runnable() { // from class: com.android.support.AnnouncementRepository$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.m75lambda$refresh$1$comandroidsupportAnnouncementRepository(announcementContentFetchAnnouncement);
            }
        });
    }

    /* renamed from: lambda$refresh$1$com-android-support-AnnouncementRepository, reason: not valid java name */
    /* synthetic */ void m75lambda$refresh$1$comandroidsupportAnnouncementRepository(AnnouncementContent announcementContent) {
        this.loading = false;
        this.failed = announcementContent == null;
        this.retryAt = SystemClock.elapsedRealtime() + (this.failed ? 15000 : 300000);
        if (announcementContent != null) {
            this.content = announcementContent;
            if (!announcementContent.stale) {
                this.cachedAt = System.currentTimeMillis();
                this.preferences.edit().putString("notice_text", announcementContent.text).putString("notice_id", announcementContent.id).putLong("notice_cached_at", this.cachedAt).apply();
            }
        }
        publish();
    }

    private Snapshot snapshot() {
        if (this.failed && this.content != null && System.currentTimeMillis() - this.cachedAt > MAX_AGE_MS) {
            this.content = null;
        }
        return new Snapshot(this.content, this.loading, this.failed);
    }

    private void publish() {
        Snapshot snapshot = snapshot();
        Iterator it = new ArrayList(this.listeners).iterator();
        while (it.hasNext()) {
            ((Listener) it.next()).onNotice(snapshot);
        }
    }
}
