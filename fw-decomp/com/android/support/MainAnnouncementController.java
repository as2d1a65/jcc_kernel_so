package com.android.support;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.support.AnnouncementRepository;
import com.android.support.MainAnnouncementState;

/* loaded from: classes.dex */
final class MainAnnouncementController {
    private static final String KEY_NOTICE_SEEN = "notice_seen_id";
    private static final String PREFS_NAME = "main_announcement";
    private final AppCompatActivity activity;
    private final View announcementBar;
    private boolean automatic;
    private AnnouncementRepository.Snapshot current;
    private Dialog dialog;
    private final Runnable openOfficialChannel;
    private final SharedPreferences preferences;
    private boolean released;
    private final AnnouncementRepository repository;
    private final View.OnLayoutChangeListener resize = new View.OnLayoutChangeListener() { // from class: com.android.support.MainAnnouncementController$$ExternalSyntheticLambda3
        @Override // android.view.View.OnLayoutChangeListener
        public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            this.f$0.m1368lambda$new$0$comandroidsupportMainAnnouncementController(view, i, i2, i3, i4, i5, i6, i7, i8);
        }
    };
    private final MainAnnouncementState state = new MainAnnouncementState(new MainAnnouncementState.SeenStore() { // from class: com.android.support.MainAnnouncementController.1
        @Override // com.android.support.MainAnnouncementState.SeenStore
        public boolean isSeen() {
            return MainAnnouncementController.this.current == null || MainAnnouncementController.this.current.content == null || MainAnnouncementController.this.current.content.id.equals(MainAnnouncementController.this.preferences.getString(MainAnnouncementController.KEY_NOTICE_SEEN, ""));
        }

        @Override // com.android.support.MainAnnouncementState.SeenStore
        public void markSeen() {
            MainAnnouncementController.this.markCurrentSeen();
        }
    });
    private AnnouncementRepository.Subscription subscription;

    /* renamed from: lambda$new$0$com-android-support-MainAnnouncementController, reason: not valid java name */
    /* synthetic */ void m1368lambda$new$0$comandroidsupportMainAnnouncementController(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        if (i3 - i == i7 - i5 && i4 - i2 == i8 - i6) {
            return;
        }
        fitDialog();
    }

    MainAnnouncementController(AppCompatActivity appCompatActivity, View view, Runnable runnable) {
        this.activity = appCompatActivity;
        this.announcementBar = view;
        this.openOfficialChannel = runnable;
        this.preferences = appCompatActivity.getSharedPreferences(PREFS_NAME, 0);
        this.repository = AnnouncementRepository.get(appCompatActivity);
    }

    void bind() {
        this.announcementBar.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainAnnouncementController$$ExternalSyntheticLambda5
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1366lambda$bind$1$comandroidsupportMainAnnouncementController(view);
            }
        });
        this.activity.getWindow().getDecorView().addOnLayoutChangeListener(this.resize);
        this.subscription = this.repository.observe(new AnnouncementRepository.Listener() { // from class: com.android.support.MainAnnouncementController$$ExternalSyntheticLambda6
            @Override // com.android.support.AnnouncementRepository.Listener
            public final void onNotice(AnnouncementRepository.Snapshot snapshot) {
                this.f$0.m1367lambda$bind$2$comandroidsupportMainAnnouncementController(snapshot);
            }
        });
    }

    /* renamed from: lambda$bind$1$com-android-support-MainAnnouncementController, reason: not valid java name */
    /* synthetic */ void m1366lambda$bind$1$comandroidsupportMainAnnouncementController(View view) {
        show(MainAnnouncementState.Origin.MANUAL);
        this.repository.refresh();
    }

    /* renamed from: lambda$bind$2$com-android-support-MainAnnouncementController, reason: not valid java name */
    /* synthetic */ void m1367lambda$bind$2$comandroidsupportMainAnnouncementController(AnnouncementRepository.Snapshot snapshot) {
        if (this.released) {
            return;
        }
        this.current = snapshot;
        TextView textView = (TextView) this.announcementBar.findViewById(R.id.mainAnnouncementPreview);
        String strStatus = (snapshot.content == null || snapshot.content.text.isEmpty()) ? snapshot.status() : snapshot.content.text.replace('\n', ' ');
        textView.setText(strStatus);
        this.announcementBar.setContentDescription("官方公告，" + strStatus + "，点击查看全文");
        renderDialog();
        showAutomaticallyIfNeeded();
    }

    void refresh() {
        this.repository.refresh();
    }

    void showAutomaticallyIfNeeded() {
        this.automatic = true;
        AnnouncementRepository.Snapshot snapshot = this.current;
        if (snapshot == null || snapshot.loading || this.current.failed || this.current.content == null || this.current.content.stale || this.current.content.text.isEmpty()) {
            return;
        }
        show(MainAnnouncementState.Origin.AUTOMATIC);
    }

    void release() {
        this.released = true;
        AnnouncementRepository.Subscription subscription = this.subscription;
        if (subscription != null) {
            subscription.close();
        }
        this.announcementBar.setOnClickListener(null);
        this.activity.getWindow().getDecorView().removeOnLayoutChangeListener(this.resize);
        Dialog dialog = this.dialog;
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void markCurrentSeen() {
        AnnouncementRepository.Snapshot snapshot = this.current;
        if (snapshot == null || snapshot.content == null || this.current.content.text.isEmpty()) {
            return;
        }
        this.preferences.edit().putString(KEY_NOTICE_SEEN, this.current.content.id).apply();
    }

    private void renderDialog() {
        String str;
        Dialog dialog = this.dialog;
        if (dialog == null || this.current == null) {
            return;
        }
        TextView textView = (TextView) dialog.findViewById(R.id.mainAnnouncementMessage);
        TextView textView2 = (TextView) this.dialog.findViewById(R.id.mainAnnouncementStatus);
        if (this.current.content != null && !this.current.content.text.isEmpty()) {
            str = this.current.content.text;
        } else if (this.current.failed) {
            str = "暂时无法获取公告，请稍后重试。";
        } else {
            str = this.current.loading ? "正在为你获取最新消息…" : "目前没有新的公告。后续消息会在这里显示。";
        }
        textView.setText(str);
        textView2.setText(this.current.status() + ((!this.current.loading && (this.current.content != null && !this.current.content.text.isEmpty()) && (this.current.failed || this.current.content.stale)) ? " · 重试" : ""));
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainAnnouncementController$$ExternalSyntheticLambda4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1369xec9b93fe(view);
            }
        });
        if (this.dialog.isShowing()) {
            markCurrentSeen();
        }
    }

    /* renamed from: lambda$renderDialog$3$com-android-support-MainAnnouncementController, reason: not valid java name */
    /* synthetic */ void m1369xec9b93fe(View view) {
        this.repository.refresh();
    }

    private void show(MainAnnouncementState.Origin origin) {
        boolean zCanShowManually;
        if (origin == MainAnnouncementState.Origin.AUTOMATIC) {
            zCanShowManually = this.automatic && this.state.canShowAutomatically();
        } else {
            zCanShowManually = this.state.canShowManually();
        }
        if (!zCanShowManually || this.released || this.activity.isFinishing() || this.activity.isDestroyed()) {
            return;
        }
        final Dialog dialog = new Dialog(this.activity);
        dialog.requestWindowFeature(1);
        View viewInflate = LayoutInflater.from(this.activity).inflate(R.layout.dialog_main_announcement, (ViewGroup) null, false);
        dialog.setContentView(viewInflate);
        ScrollEdgeCue.attachTree(viewInflate);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        viewInflate.findViewById(R.id.mainAnnouncementJoin).setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainAnnouncementController$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.m1370lambda$show$4$comandroidsupportMainAnnouncementController(view);
            }
        });
        viewInflate.findViewById(R.id.mainAnnouncementLater).setOnClickListener(new View.OnClickListener() { // from class: com.android.support.MainAnnouncementController$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.support.MainAnnouncementController$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                this.f$0.m1371lambda$show$6$comandroidsupportMainAnnouncementController(dialog, dialogInterface);
            }
        });
        this.dialog = dialog;
        renderDialog();
        try {
            dialog.show();
            fitDialog();
            this.state.onShown(origin);
            markCurrentSeen();
        } catch (RuntimeException unused) {
            this.dialog = null;
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    /* renamed from: lambda$show$4$com-android-support-MainAnnouncementController, reason: not valid java name */
    /* synthetic */ void m1370lambda$show$4$comandroidsupportMainAnnouncementController(View view) {
        this.openOfficialChannel.run();
    }

    /* renamed from: lambda$show$6$com-android-support-MainAnnouncementController, reason: not valid java name */
    /* synthetic */ void m1371lambda$show$6$comandroidsupportMainAnnouncementController(Dialog dialog, DialogInterface dialogInterface) {
        if (this.dialog == dialog) {
            this.dialog = null;
        }
        this.state.onDismissed();
    }

    private void fitDialog() {
        Dialog dialog = this.dialog;
        if (dialog == null || dialog.getWindow() == null) {
            return;
        }
        Window window = this.dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(0));
        Rect rect = new Rect();
        this.activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        int iWidth = rect.width() > 0 ? rect.width() : this.activity.getResources().getDisplayMetrics().widthPixels;
        int iHeight = rect.height() > 0 ? rect.height() : this.activity.getResources().getDisplayMetrics().heightPixels;
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = Math.max(1, Math.min(dp(560), iWidth - dp(32)));
        attributes.height = Math.max(1, Math.min(dp(560), iHeight - dp(32)));
        boolean z = (((float) iHeight) / this.activity.getResources().getDisplayMetrics().density) / Math.max(1.0f, this.activity.getResources().getConfiguration().fontScale) < 440.0f;
        View viewFindViewById = this.dialog.findViewById(R.id.mainAnnouncementContainer);
        int iDp = dp(z ? 12 : 20);
        viewFindViewById.setPadding(iDp, iDp, iDp, iDp);
        this.dialog.findViewById(R.id.mainAnnouncementEyebrow).setVisibility(z ? 8 : 0);
        this.dialog.findViewById(R.id.mainAnnouncementBrandWatermark).setVisibility(z ? 8 : 0);
        ((TextView) this.dialog.findViewById(R.id.mainAnnouncementTitle)).setTextSize(z ? 20.0f : 24.0f);
        attributes.dimAmount = 0.4f;
        window.addFlags(2);
        window.setAttributes(attributes);
    }

    private int dp(int i) {
        return Math.round(i * this.activity.getResources().getDisplayMetrics().density);
    }
}
