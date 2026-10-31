package com.android.support;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.PathInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;

/* loaded from: classes.dex */
final class XfNoticeCardView extends FrameLayout {
    static final String INFO_SYMBOL = "i";
    static final int KIND_ALERT = 2;
    static final int KIND_INFO = 0;
    static final int KIND_SUCCESS = 1;
    private final int fixedHeight;
    private final int fixedWidth;
    private final NoticeGlyphView glyph;
    private final View progressView;
    private static final PathInterpolator SPRING_IN = new PathInterpolator(0.18f, 0.9f, 0.26f, 1.16f);
    private static final PathInterpolator EASE_OUT = new PathInterpolator(0.16f, 1.0f, 0.3f, 1.0f);

    static int resolveKind(int i) {
        if (i == -9899865 || i == -15305670) {
            return 1;
        }
        return (i == -15770 || i == -36205 || i == -45737 || i == -6725632 || i == -4377272 || i == -38037) ? 2 : 0;
    }

    XfNoticeCardView(Context context, String str, String str2, int i, final Runnable runnable) {
        super(context);
        boolean z = (str == null || str.isEmpty()) ? false : true;
        int iCardWidth = XfOverlayScale.cardWidth(context);
        this.fixedWidth = iCardWidth;
        int iNoticeHeight = XfOverlayScale.noticeHeight(context, z);
        this.fixedHeight = iNoticeHeight;
        setMinimumWidth(iCardWidth);
        setMinimumHeight(iNoticeHeight);
        setElevation(dp(12.0f));
        int iResolveKind = resolveKind(i);
        setBackground(DsTokens.withRipple(context, NoticeCardStyle.glassBackground(context, XfOverlayScale.radius(context)), (NoticeCardStyle.accentSolid(iResolveKind) & ViewCompat.MEASURED_SIZE_MASK) | 637534208, Math.round(XfOverlayScale.of(context) * 16.0f)));
        OverlayUiMotion.enableRoundedClip(this);
        setClickable(true);
        setFocusable(true);
        DsTokens.pressable(this);
        if (runnable != null) {
            setOnClickListener(new View.OnClickListener() { // from class: com.android.support.XfNoticeCardView$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    runnable.run();
                }
            });
        }
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(16);
        linearLayout.setPadding(dp(11.0f), dp(6.0f), dp(12.0f), dp(6.0f));
        addView(linearLayout, new FrameLayout.LayoutParams(-1, -1));
        NoticeGlyphView noticeGlyphView = new NoticeGlyphView(context);
        this.glyph = noticeGlyphView;
        noticeGlyphView.setKind(iResolveKind);
        int iBadgeSize = XfOverlayScale.badgeSize(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(iBadgeSize, iBadgeSize);
        layoutParams.rightMargin = dp(9.0f);
        linearLayout.addView(noticeGlyphView, layoutParams);
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        linearLayout2.setGravity(16);
        linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(0, -2, 1.0f));
        if (z) {
            TextView textView = new TextView(context);
            textView.setText(str);
            textView.setTextColor(-591106);
            textView.setTextSize(XfOverlayScale.sp(context, 12.0f));
            textView.setTypeface(Typeface.create("sans-serif-medium", 0));
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setShadowLayer(dp(3.0f), 0.0f, dp(1.0f), -1291845632);
            linearLayout2.addView(textView);
        }
        TextView textView2 = new TextView(context);
        textView2.setText(str2 != null ? str2 : "");
        textView2.setTextColor(z ? -4734001 : -591106);
        textView2.setTextSize(XfOverlayScale.sp(context, z ? 10.5f : 12.0f));
        textView2.setTypeface(Typeface.create(z ? "sans-serif" : "sans-serif-medium", 0));
        textView2.setMaxLines(2);
        textView2.setEllipsize(TextUtils.TruncateAt.END);
        textView2.setShadowLayer(dp(3.0f), 0.0f, dp(1.0f), -1291845632);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, -2);
        if (z) {
            layoutParams2.topMargin = dp(1.0f);
        }
        linearLayout2.addView(textView2, layoutParams2);
        View view = new View(context);
        view.setBackground(NoticeCardStyle.accentRail(context, iResolveKind));
        addView(view, new FrameLayout.LayoutParams(dp(3.0f), -1, GravityCompat.START));
        View view2 = new View(context);
        this.progressView = view2;
        view2.setBackground(NoticeCardStyle.progressFill(context, iResolveKind));
        ViewGroup.LayoutParams layoutParams3 = new FrameLayout.LayoutParams(-1, dp(2.0f), 80);
        view2.setAlpha(0.9f);
        addView(view2, layoutParams3);
        view2.setPivotX(0.0f);
    }

    void playEnter() {
        if (!OverlayUiMotion.animationsEnabled(getContext())) {
            setAlpha(1.0f);
            setTranslationY(0.0f);
            setScaleX(1.0f);
            setScaleY(1.0f);
            return;
        }
        setPivotX(this.fixedWidth);
        setPivotY(0.0f);
        setAlpha(0.0f);
        setTranslationY(-dp(22.0f));
        setScaleX(0.9f);
        setScaleY(0.9f);
        animate().alpha(1.0f).translationY(0.0f).scaleX(1.0f).scaleY(1.0f).setDuration(540L).setInterpolator(SPRING_IN).setListener(null).start();
        this.glyph.playIntro();
    }

    void playExit(Runnable runnable) {
        if (OverlayUiMotion.animationsEnabled(getContext())) {
            animate().alpha(0.0f).translationY(-dp(14.0f)).scaleX(0.92f).scaleY(0.92f).setDuration(240L).setInterpolator(EASE_OUT).withEndAction(runnable).start();
        } else if (runnable != null) {
            runnable.run();
        }
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

    private int dp(float f) {
        return XfOverlayScale.dp(getContext(), f);
    }
}
