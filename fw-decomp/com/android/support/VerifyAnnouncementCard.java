package com.android.support;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.support.AnnouncementRepository;
import com.android.support.VerifyAnnouncementCard;

/* loaded from: classes.dex */
final class VerifyAnnouncementCard {
    private VerifyAnnouncementCard() {
    }

    static void bind(Context context, LinearLayout linearLayout) {
        final AnnouncementRepository announcementRepository = AnnouncementRepository.get(context);
        linearLayout.removeAllViews();
        int iDp2px = ScreenUtils.dp2px(context, 16.0f);
        linearLayout.setPadding(iDp2px, iDp2px, iDp2px, iDp2px);
        linearLayout.setBackground(DsTokens.shape(context, -1, DsTokens.LINE, 16));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setGravity(16);
        TextView textViewText = text(context, "官方公告", 16.0f, DsTokens.TEXT_1);
        textViewText.setTypeface(null, 1);
        linearLayout2.addView(textViewText, new LinearLayout.LayoutParams(0, -2, 1.0f));
        TextView textViewText2 = text(context, "刷新", 13.0f, DsTokens.BRAND);
        textViewText2.setGravity(17);
        textViewText2.setMinWidth(ScreenUtils.dp2px(context, 48.0f));
        textViewText2.setMinHeight(ScreenUtils.dp2px(context, 48.0f));
        textViewText2.setContentDescription("刷新官方公告");
        textViewText2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VerifyAnnouncementCard$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                announcementRepository.refresh();
            }
        });
        linearLayout2.addView(textViewText2);
        linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(-1, -2));
        TextView textViewText3 = text(context, "正在获取最新公告…", 12.0f, DsTokens.TEXT_3);
        linearLayout.addView(textViewText3);
        final TextView textViewText4 = text(context, "", 14.0f, DsTokens.TEXT_2);
        textViewText4.setLineSpacing(0.0f, 1.5f);
        textViewText4.setPadding(0, ScreenUtils.dp2px(context, 12.0f), 0, 0);
        linearLayout.addView(textViewText4, new LinearLayout.LayoutParams(-1, -2));
        final TextView textViewText5 = text(context, "展开全文", 13.0f, DsTokens.BRAND);
        textViewText5.setMinHeight(ScreenUtils.dp2px(context, 48.0f));
        textViewText5.setGravity(16);
        linearLayout.addView(textViewText5);
        final boolean[] zArr = {false};
        textViewText5.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.VerifyAnnouncementCard$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                VerifyAnnouncementCard.lambda$bind$1(zArr, textViewText4, textViewText5, view);
            }
        });
        linearLayout.addOnAttachStateChangeListener(new AnonymousClass1(announcementRepository, textViewText3, new String[]{""}, zArr, textViewText4, textViewText5, textViewText2));
    }

    static /* synthetic */ void lambda$bind$1(boolean[] zArr, TextView textView, TextView textView2, View view) {
        boolean z = zArr[0];
        zArr[0] = !z;
        textView.setMaxLines(!z ? Integer.MAX_VALUE : 4);
        textView.setEllipsize(zArr[0] ? null : TextUtils.TruncateAt.END);
        textView2.setText(zArr[0] ? "收起全文" : "展开全文");
    }

    /* renamed from: com.android.support.VerifyAnnouncementCard$1, reason: invalid class name */
    class AnonymousClass1 implements View.OnAttachStateChangeListener {
        private AnnouncementRepository.Subscription subscription;
        final /* synthetic */ TextView val$content;
        final /* synthetic */ String[] val$currentId;
        final /* synthetic */ TextView val$expand;
        final /* synthetic */ boolean[] val$expanded;
        final /* synthetic */ TextView val$refresh;
        final /* synthetic */ AnnouncementRepository val$repository;
        final /* synthetic */ TextView val$status;

        AnonymousClass1(AnnouncementRepository announcementRepository, TextView textView, String[] strArr, boolean[] zArr, TextView textView2, TextView textView3, TextView textView4) {
            this.val$repository = announcementRepository;
            this.val$status = textView;
            this.val$currentId = strArr;
            this.val$expanded = zArr;
            this.val$content = textView2;
            this.val$expand = textView3;
            this.val$refresh = textView4;
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
            AnnouncementRepository announcementRepository = this.val$repository;
            final TextView textView = this.val$status;
            final String[] strArr = this.val$currentId;
            final boolean[] zArr = this.val$expanded;
            final TextView textView2 = this.val$content;
            final TextView textView3 = this.val$expand;
            final TextView textView4 = this.val$refresh;
            this.subscription = announcementRepository.observe(new AnnouncementRepository.Listener() { // from class: com.android.support.VerifyAnnouncementCard$1$$ExternalSyntheticLambda0
                @Override // com.android.support.AnnouncementRepository.Listener
                public final void onNotice(AnnouncementRepository.Snapshot snapshot) {
                    VerifyAnnouncementCard.AnonymousClass1.lambda$onViewAttachedToWindow$1(textView, strArr, zArr, textView2, textView3, textView4, snapshot);
                }
            });
        }

        static /* synthetic */ void lambda$onViewAttachedToWindow$1(TextView textView, String[] strArr, final boolean[] zArr, final TextView textView2, final TextView textView3, TextView textView4, AnnouncementRepository.Snapshot snapshot) {
            String str;
            textView.setText(snapshot.status());
            String str2 = snapshot.content == null ? "" : snapshot.content.id;
            if (!str2.equals(strArr[0])) {
                zArr[0] = false;
                strArr[0] = str2;
            }
            if (snapshot.content == null || snapshot.content.text.isEmpty()) {
                if (snapshot.failed) {
                    str = "获取失败，请稍后刷新。";
                } else {
                    str = snapshot.loading ? "正在同步最新消息…" : "目前没有新的公告。";
                }
            } else {
                str = snapshot.content.text;
            }
            textView2.setText(str);
            textView2.setMaxLines(zArr[0] ? Integer.MAX_VALUE : 4);
            textView2.setEllipsize(zArr[0] ? null : TextUtils.TruncateAt.END);
            textView3.setText(zArr[0] ? "收起全文" : "展开全文");
            textView2.post(new Runnable() { // from class: com.android.support.VerifyAnnouncementCard$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    VerifyAnnouncementCard.AnonymousClass1.lambda$onViewAttachedToWindow$0(textView2, textView3, zArr);
                }
            });
            textView4.setEnabled(!snapshot.loading);
        }

        /* JADX WARN: Removed duplicated region for block: B:9:0x001a  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        static /* synthetic */ void lambda$onViewAttachedToWindow$0(android.widget.TextView r3, android.widget.TextView r4, boolean[] r5) {
            /*
                android.text.Layout r3 = r3.getLayout()
                r0 = 0
                if (r3 == 0) goto L1a
                int r1 = r3.getLineCount()
                if (r1 <= 0) goto L1a
                int r1 = r3.getLineCount()
                r2 = 1
                int r1 = r1 - r2
                int r3 = r3.getEllipsisCount(r1)
                if (r3 <= 0) goto L1a
                goto L1b
            L1a:
                r2 = r0
            L1b:
                boolean r3 = r5[r0]
                if (r3 != 0) goto L24
                if (r2 == 0) goto L22
                goto L24
            L22:
                r0 = 8
            L24:
                r4.setVisibility(r0)
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.support.VerifyAnnouncementCard.AnonymousClass1.lambda$onViewAttachedToWindow$0(android.widget.TextView, android.widget.TextView, boolean[]):void");
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            AnnouncementRepository.Subscription subscription = this.subscription;
            if (subscription != null) {
                subscription.close();
            }
        }
    }

    static void show(Context context, LinearLayout linearLayout, String str, View view, int i, int i2, int i3) {
        linearLayout.removeAllViews();
        linearLayout.setVisibility(0);
        linearLayout.setPadding(i2, i3, i2, i3);
        linearLayout.setBackground(DsTokens.shape(context, DsTokens.WARN_A16, 0, 12));
        linearLayout.addView(view);
        TextView textViewText = text(context, str, 13.0f, DsTokens.TEXT_2);
        textViewText.setLineSpacing(0.0f, 1.4f);
        linearLayout.addView(textViewText, new LinearLayout.LayoutParams(-1, -2));
    }

    private static TextView text(Context context, String str, float f, int i) {
        TextView textView = new TextView(context);
        textView.setText(str);
        textView.setTextSize(f);
        textView.setTextColor(i);
        return textView;
    }
}
