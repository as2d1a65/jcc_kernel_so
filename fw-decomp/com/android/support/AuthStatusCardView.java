package com.android.support;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
final class AuthStatusCardView extends FrameLayout {
    private final View accentRail;
    private final int fixedHeight;
    private final int fixedWidth;
    private final NoticeGlyphView glyph;
    private final Listener listener;
    private final View progressView;
    private final TextView subtitleView;
    private final TextView titleView;

    interface Listener {
        void onCardClick();

        void onSettingsClick();
    }

    AuthStatusCardView(Context context, final Listener listener) {
        super(context);
        this.listener = listener;
        int iCardWidth = XfOverlayScale.cardWidth(context);
        this.fixedWidth = iCardWidth;
        int iNoticeHeight = XfOverlayScale.noticeHeight(context, true);
        this.fixedHeight = iNoticeHeight;
        setMinimumWidth(iCardWidth);
        setMinimumHeight(iNoticeHeight);
        setElevation(dp(12.0f));
        setBackground(DsTokens.withRipple(context, NoticeCardStyle.glassBackground(context, XfOverlayScale.radius(context)), (NoticeCardStyle.accentSolid(1) & ViewCompat.MEASURED_SIZE_MASK) | 637534208, Math.round(XfOverlayScale.of(context) * 16.0f)));
        OverlayUiMotion.enableRoundedClip(this);
        setClickable(true);
        setFocusable(true);
        DsTokens.pressable(this);
        if (listener != null) {
            setOnClickListener(new View.OnClickListener() { // from class: com.android.support.AuthStatusCardView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    listener.onCardClick();
                }
            });
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setPadding(dp(11.0f), dp(6.0f), dp(8.0f), dp(6.0f));
        addView(linearLayout, new FrameLayout.LayoutParams(-1, -1));
        NoticeGlyphView noticeGlyphView = new NoticeGlyphView(context);
        this.glyph = noticeGlyphView;
        noticeGlyphView.setKind(1);
        int iBadgeSize = XfOverlayScale.badgeSize(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(iBadgeSize, iBadgeSize);
        layoutParams.rightMargin = dp(9.0f);
        linearLayout.addView(noticeGlyphView, layoutParams);
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        linearLayout2.setGravity(16);
        linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextColor(-591106);
        textView.setTextSize(XfOverlayScale.sp(context, 12.0f));
        textView.setTypeface(Typeface.create("sans-serif-medium", 0));
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setShadowLayer(dp(3.0f), 0.0f, dp(1.0f), -1291845632);
        linearLayout2.addView(textView, new LinearLayout.LayoutParams(-1, -2));
        TextView textView2 = new TextView(context);
        this.subtitleView = textView2;
        textView2.setTextColor(-4734001);
        textView2.setTextSize(XfOverlayScale.sp(context, 9.5f));
        textView2.setTypeface(Typeface.create("sans-serif", 0));
        textView2.setSingleLine(true);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setShadowLayer(dp(3.0f), 0.0f, dp(1.0f), -1291845632);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
        layoutParams2.topMargin = dp(1.0f);
        linearLayout2.addView(textView2, layoutParams2);
        TextView textView3 = new TextView(context);
        textView3.setText("⚙");
        textView3.setContentDescription("设置");
        textView3.setTextColor(-6573890);
        textView3.setTextSize(XfOverlayScale.sp(context, 13.0f));
        textView3.setGravity(17);
        textView3.setPadding(dp(3.0f), dp(2.0f), 0, dp(2.0f));
        DsTokens.pressable(textView3);
        textView3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.AuthStatusCardView$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AuthStatusCardView.lambda$new$1(listener, view);
            }
        });
        linearLayout.addView(textView3, new LinearLayout.LayoutParams(dp(20.0f), dp(20.0f)));
        View view = new View(context);
        this.accentRail = view;
        addView(view, new FrameLayout.LayoutParams(dp(3.0f), -1, GravityCompat.START));
        View view2 = new View(context);
        this.progressView = view2;
        ViewGroup.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(-1, dp(2.0f), 80);
        view2.setAlpha(0.9f);
        addView(view2, layoutParams3);
        view2.setPivotX(0.0f);
        applyKind(1);
        setState(true, "");
    }

    static /* synthetic */ void lambda$new$1(Listener listener, View view) {
        if (listener != null) {
            listener.onSettingsClick();
        }
    }

    void setState(boolean z, String str) {
        String str2;
        if (!z) {
            setVisibility(8);
            return;
        }
        setVisibility(0);
        this.titleView.setText("登录成功");
        TextView textView = this.subtitleView;
        if (str == null || str.isEmpty()) {
            str2 = "已完成安全验证";
        } else {
            str2 = "有效期至  " + str;
        }
        textView.setText(str2);
        applyKind(1);
    }

    void setTransient(String str, String str2, int i) {
        setVisibility(0);
        TextView textView = this.titleView;
        if (str == null) {
            str = "";
        }
        textView.setText(str);
        TextView textView2 = this.subtitleView;
        if (str2 == null) {
            str2 = "";
        }
        textView2.setText(str2);
        applyKind(XfNoticeCardView.resolveKind(i));
    }

    void startProgress(long j) {
        this.progressView.animate().cancel();
        this.progressView.setScaleX(1.0f);
        this.progressView.animate().scaleX(0.0f).setDuration(j).setInterpolator(new LinearInterpolator()).start();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(this.fixedWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(this.fixedHeight, 1073741824));
        setMeasuredDimension(this.fixedWidth, this.fixedHeight);
    }

    private void applyKind(int i) {
        this.glyph.setKind(i);
        this.accentRail.setBackground(NoticeCardStyle.accentRail(getContext(), i));
        this.progressView.setBackground(NoticeCardStyle.progressFill(getContext(), i));
    }

    private int dp(float f) {
        return XfOverlayScale.dp(getContext(), f);
    }
}
