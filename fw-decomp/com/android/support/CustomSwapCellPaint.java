package com.android.support;

import android.widget.ImageView;

/* loaded from: classes.dex */
final class CustomSwapCellPaint {
    static final int UNKNOWN_AVATAR = 17301568;

    interface Host {
        int currentSeason();

        void loadHeroAvatar(ImageView imageView, String str);
    }

    private CustomSwapCellPaint() {
    }

    static int boardHeroId(BoardPositionInfo boardPositionInfo, int i, int i2) {
        if (boardPositionInfo == null || !boardPositionInfo.isBoardOccupied(i, i2)) {
            return 0;
        }
        return boardPositionInfo.getBoardHeroId(i, i2);
    }

    static int benchHeroId(BoardPositionInfo boardPositionInfo, int i) {
        if (boardPositionInfo == null || !boardPositionInfo.isBenchOccupied(i)) {
            return 0;
        }
        return boardPositionInfo.getBenchHeroId(i);
    }

    static void applyAvatar(Host host, ImageView imageView, int i) {
        if (imageView == null) {
            return;
        }
        if (i <= 0) {
            imageView.setImageDrawable(null);
            imageView.setVisibility(8);
            return;
        }
        String englishNameById = HeroIdMapper.getEnglishNameById(i, host.currentSeason());
        if (englishNameById != null && !englishNameById.isEmpty()) {
            host.loadHeroAvatar(imageView, englishNameById);
        } else {
            imageView.setImageResource(17301568);
        }
        imageView.setVisibility(0);
    }
}
