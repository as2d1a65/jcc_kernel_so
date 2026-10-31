package com.android.support;

import android.widget.FrameLayout;
import android.widget.TextView;
import com.android.support.CustomSwapCellPaint;

/* loaded from: classes.dex */
final class CustomSwapBoardRefresh {
    private CustomSwapBoardRefresh() {
    }

    static void applyBattle(CustomSwapCellPaint.Host host, FrameLayout[][] frameLayoutArr, int[][] iArr, BoardPositionInfo boardPositionInfo) {
        if (frameLayoutArr == null || iArr == null) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            for (int i2 = 0; i2 < 7; i2++) {
                FrameLayout frameLayout = frameLayoutArr[i][i2];
                if (frameLayout != null) {
                    int iBoardHeroId = CustomSwapCellPaint.boardHeroId(boardPositionInfo, i, i2);
                    iArr[i][i2] = iBoardHeroId;
                    CustomSwapCellPaint.applyAvatar(host, CustomSwapCellViews.avatar(frameLayout), iBoardHeroId);
                }
            }
        }
    }

    static void applyBench(CustomSwapCellPaint.Host host, FrameLayout[] frameLayoutArr, int[] iArr, BoardPositionInfo boardPositionInfo) {
        if (frameLayoutArr == null || iArr == null) {
            return;
        }
        for (int i = 0; i < 9; i++) {
            FrameLayout frameLayout = frameLayoutArr[i];
            if (frameLayout != null) {
                int iBenchHeroId = CustomSwapCellPaint.benchHeroId(boardPositionInfo, i);
                iArr[i] = iBenchHeroId;
                CustomSwapCellPaint.applyAvatar(host, CustomSwapCellViews.avatar(frameLayout), iBenchHeroId);
            }
        }
    }

    static void applyStatusHint(TextView textView, int i, BoardPositionInfo boardPositionInfo) {
        if (textView == null) {
            return;
        }
        textView.setText(CustomSwapBoardLookup.statusHint(i, boardPositionInfo));
        textView.setTextColor(CustomSwapBoardLookup.statusHintColor(i, boardPositionInfo));
    }
}
