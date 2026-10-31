package com.android.support;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import java.util.Iterator;
import java.util.Set;

/* loaded from: classes.dex */
final class HeroEquipDropControlBinder {

    interface Host {
        void animateButtonClick(View view);

        Set<Integer> dropEquipIds();

        Set<Integer> dropHeroIds();

        void enhanceSinglePlayerControl(View view);

        void hideKeyboard(View view);

        int selectedDropStarLevel();

        int selectedEquipDropCount();

        void setSelectedDropStarLevel(int i);

        void setSelectedEquipDropCount(int i);

        void setupModifyEditText(EditText editText);

        SharedMemManager sharedMemManager();

        void showModifyFeedback(View view, boolean z, String str);

        void updateDropEquipCountLabel();

        void writeModifyTargets();
    }

    private HeroEquipDropControlBinder() {
    }

    static void bind(View view, final Context context, final Host host) {
        final Host host2;
        final TextView[] textViewArr = {(TextView) view.findViewById(R.id.btnStar1), (TextView) view.findViewById(R.id.btnStar2), (TextView) view.findViewById(R.id.btnStar3), (TextView) view.findViewById(R.id.btnStar4)};
        for (int i = 0; i < 4; i++) {
            TextView textView = textViewArr[i];
            if (textView != null) {
                host.enhanceSinglePlayerControl(textView);
                final int i2 = i + 1;
                textViewArr[i].setOnClickListener(new View.OnClickListener() { // from class: com.android.support.HeroEquipDropControlBinder$$ExternalSyntheticLambda2
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        HeroEquipDropControlBinder.lambda$bind$0(host, i2, textViewArr, view2);
                    }
                });
            }
        }
        View viewFindViewById = view.findViewById(R.id.btnGenerateDrop);
        if (viewFindViewById != null) {
            viewFindViewById.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.HeroEquipDropControlBinder$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    HeroEquipDropControlBinder.lambda$bind$2(host, view2);
                }
            });
        }
        final TextView[] textViewArr2 = {(TextView) view.findViewById(R.id.btnEquipCount1), (TextView) view.findViewById(R.id.btnEquipCount2), (TextView) view.findViewById(R.id.btnEquipCount3), (TextView) view.findViewById(R.id.btnEquipCount5), (TextView) view.findViewById(R.id.btnEquipCount10), (TextView) view.findViewById(R.id.btnEquipCount20), (TextView) view.findViewById(R.id.btnEquipCount50), (TextView) view.findViewById(R.id.btnEquipCount100)};
        int[] iArr = {1, 2, 3, 5, 10, 20, 50, 100};
        final EditText editText = (EditText) view.findViewById(R.id.etEquipCustomCount);
        host.setupModifyEditText(editText);
        final int i3 = 0;
        while (i3 < 8) {
            TextView textView2 = textViewArr2[i3];
            if (textView2 == null) {
                host2 = host;
            } else {
                host.enhanceSinglePlayerControl(textView2);
                final int i4 = iArr[i3];
                host2 = host;
                textViewArr2[i3].setOnClickListener(new View.OnClickListener() { // from class: com.android.support.HeroEquipDropControlBinder$$ExternalSyntheticLambda4
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        HeroEquipDropControlBinder.lambda$bind$3(host2, i4, textViewArr2, i3, editText, view2);
                    }
                });
            }
            i3++;
            host = host2;
        }
        final Host host3 = host;
        if (editText != null) {
            editText.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: com.android.support.HeroEquipDropControlBinder$$ExternalSyntheticLambda5
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView3, int i5, KeyEvent keyEvent) {
                    return HeroEquipDropControlBinder.lambda$bind$4(host3, textViewArr2, context, editText, textView3, i5, keyEvent);
                }
            });
        }
        View viewFindViewById2 = view.findViewById(R.id.btnGenerateEquipDrop);
        if (viewFindViewById2 != null) {
            host3.enhanceSinglePlayerControl(viewFindViewById2);
            viewFindViewById2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.HeroEquipDropControlBinder$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) throws NumberFormatException {
                    HeroEquipDropControlBinder.lambda$bind$6(host3, editText, textViewArr2, view2);
                }
            });
        }
    }

    static /* synthetic */ void lambda$bind$0(Host host, int i, TextView[] textViewArr, View view) {
        host.setSelectedDropStarLevel(i);
        host.animateButtonClick(view);
        for (int i2 = 0; i2 < textViewArr.length; i2++) {
            if (textViewArr[i2] != null) {
                if (i2 + 1 == host.selectedDropStarLevel()) {
                    textViewArr[i2].setBackgroundResource(R.drawable.bg_equipment_card_selected);
                    textViewArr[i2].setTextColor(-1);
                } else {
                    textViewArr[i2].setBackgroundResource(R.drawable.bg_equipment_card);
                    textViewArr[i2].setTextColor(-7829368);
                }
            }
        }
    }

    static /* synthetic */ void lambda$bind$2(final Host host, final View view) {
        SharedMemManager sharedMemManager = host.sharedMemManager();
        int i = 0;
        if (sharedMemManager == null) {
            host.showModifyFeedback(view, false, "共享内存未连接");
            return;
        }
        Set<Integer> setDropHeroIds = host.dropHeroIds();
        if (setDropHeroIds.isEmpty()) {
            host.showModifyFeedback(view, false, "请先选择要生成的英雄");
            return;
        }
        final int[] iArr = new int[setDropHeroIds.size()];
        Iterator<Integer> it = setDropHeroIds.iterator();
        while (it.hasNext()) {
            iArr[i] = it.next().intValue();
            i++;
        }
        host.animateButtonClick(view);
        host.writeModifyTargets();
        final int iSelectedDropStarLevel = host.selectedDropStarLevel();
        sharedMemManager.triggerGenerateDropHero(iArr, iSelectedDropStarLevel, new Runnable() { // from class: com.android.support.HeroEquipDropControlBinder$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                host.showModifyFeedback(view, true, "已生成 " + iArr.length + " 个英雄掉落(" + iSelectedDropStarLevel + "星)");
            }
        });
    }

    static /* synthetic */ void lambda$bind$3(Host host, int i, TextView[] textViewArr, int i2, EditText editText, View view) {
        host.setSelectedEquipDropCount(i);
        host.animateButtonClick(view);
        for (int i3 = 0; i3 < textViewArr.length; i3++) {
            TextView textView = textViewArr[i3];
            if (textView != null) {
                if (i3 == i2) {
                    textView.setBackgroundResource(R.drawable.bg_equipment_card_selected);
                    textViewArr[i3].setTextColor(-1);
                } else {
                    textView.setBackgroundResource(R.drawable.bg_equipment_card);
                    textViewArr[i3].setTextColor(-7829368);
                }
            }
        }
        if (editText != null) {
            editText.setText("");
            editText.clearFocus();
        }
        host.updateDropEquipCountLabel();
    }

    static /* synthetic */ boolean lambda$bind$4(Host host, TextView[] textViewArr, Context context, EditText editText, TextView textView, int i, KeyEvent keyEvent) throws NumberFormatException {
        String strTrim = textView.getText().toString().trim();
        if (!strTrim.isEmpty()) {
            try {
                int i2 = Integer.parseInt(strTrim);
                if (i2 >= 1 && i2 <= 999) {
                    host.setSelectedEquipDropCount(i2);
                    for (TextView textView2 : textViewArr) {
                        if (textView2 != null) {
                            textView2.setBackgroundResource(R.drawable.bg_equipment_card);
                            textView2.setTextColor(-7829368);
                        }
                    }
                    host.updateDropEquipCountLabel();
                } else {
                    FloatingWindowService.safeToast(context, "数量范围: 1-999", 0);
                }
            } catch (NumberFormatException unused) {
                FloatingWindowService.safeToast(context, "请输入有效数字", 0);
            }
        }
        editText.clearFocus();
        host.hideKeyboard(editText);
        return true;
    }

    static /* synthetic */ void lambda$bind$6(final Host host, EditText editText, TextView[] textViewArr, final View view) throws NumberFormatException {
        SharedMemManager sharedMemManager = host.sharedMemManager();
        if (sharedMemManager == null) {
            host.showModifyFeedback(view, false, "共享内存未连接");
            return;
        }
        final Set<Integer> setDropEquipIds = host.dropEquipIds();
        if (setDropEquipIds.isEmpty()) {
            host.showModifyFeedback(view, false, "请先在装备生成页选择装备");
            return;
        }
        if (editText != null) {
            String strTrim = editText.getText().toString().trim();
            if (!strTrim.isEmpty()) {
                try {
                    int i = Integer.parseInt(strTrim);
                    if (i >= 1 && i <= 999) {
                        host.setSelectedEquipDropCount(i);
                        for (TextView textView : textViewArr) {
                            if (textView != null) {
                                textView.setBackgroundResource(R.drawable.bg_equipment_card);
                                textView.setTextColor(-7829368);
                            }
                        }
                    }
                } catch (NumberFormatException unused) {
                }
            }
        }
        final int iSelectedEquipDropCount = host.selectedEquipDropCount();
        final int size = setDropEquipIds.size() * iSelectedEquipDropCount;
        int[] iArr = new int[size];
        Iterator<Integer> it = setDropEquipIds.iterator();
        int i2 = 0;
        while (it.hasNext()) {
            int iIntValue = it.next().intValue();
            int i3 = 0;
            while (i3 < iSelectedEquipDropCount) {
                iArr[i2] = iIntValue;
                i3++;
                i2++;
            }
        }
        host.animateButtonClick(view);
        host.writeModifyTargets();
        sharedMemManager.triggerGenerateDropEquipment(iArr, new Runnable() { // from class: com.android.support.HeroEquipDropControlBinder$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                host.showModifyFeedback(view, true, "已生成 " + setDropEquipIds.size() + " 种装备 ×" + iSelectedEquipDropCount + " (共" + size + "个)");
            }
        });
    }
}
