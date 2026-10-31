package com.android.support;

import android.view.View;
import android.widget.Button;
import java.util.function.Consumer;

/* loaded from: classes.dex */
final class SellHeroControlBinder {

    interface SellAction {
        void triggerConfiguredSell(View view, int i, String str);
    }

    private SellHeroControlBinder() {
    }

    static void bind(View view, final SellAction sellAction, final Consumer<View> consumer, final Runnable runnable) {
        Button button = (Button) view.findViewById(R.id.btnSellBench);
        Button button2 = (Button) view.findViewById(R.id.btnSellBattle);
        if (button != null) {
            button.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SellHeroControlBinder$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    sellAction.triggerConfiguredSell(view2, 1, "开始卖备战席英雄");
                }
            });
        }
        if (button2 != null) {
            button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SellHeroControlBinder$$ExternalSyntheticLambda1
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    sellAction.triggerConfiguredSell(view2, 2, "开始卖场上英雄");
                }
            });
        }
        Button button3 = (Button) view.findViewById(R.id.btnMirrorSwap);
        if (button3 != null) {
            button3.setOnClickListener(new View.OnClickListener() { // from class: com.android.support.SellHeroControlBinder$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    SellHeroControlBinder.lambda$bind$2(consumer, runnable, view2);
                }
            });
        }
    }

    static /* synthetic */ void lambda$bind$2(Consumer consumer, Runnable runnable, View view) {
        consumer.accept(view);
        runnable.run();
    }
}
