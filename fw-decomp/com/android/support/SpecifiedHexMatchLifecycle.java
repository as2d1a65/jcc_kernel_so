package com.android.support;

/* loaded from: classes.dex */
final class SpecifiedHexMatchLifecycle {
    private String matchGeneration = "";
    private int liveStageIndex = -1;

    SpecifiedHexMatchLifecycle() {
    }

    boolean observeGeneration(String str) {
        String strNormalizeGeneration = normalizeGeneration(str);
        if (strNormalizeGeneration.equals(this.matchGeneration)) {
            return false;
        }
        this.matchGeneration = strNormalizeGeneration;
        this.liveStageIndex = -1;
        return true;
    }

    boolean updateStage(String str, int i) {
        if (!normalizeGeneration(str).equals(this.matchGeneration) || i < 0 || i >= 3 || i == this.liveStageIndex) {
            return false;
        }
        this.liveStageIndex = i;
        return true;
    }

    int stageIndexFor(String str) {
        if (normalizeGeneration(str).equals(this.matchGeneration)) {
            return this.liveStageIndex;
        }
        return -1;
    }

    boolean isCurrent(String str) {
        return normalizeGeneration(str).equals(this.matchGeneration);
    }

    private static String normalizeGeneration(String str) {
        return (str == null || str.length() != 22) ? "" : str;
    }
}
