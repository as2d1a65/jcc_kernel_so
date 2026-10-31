package com.android.support;

/* loaded from: classes.dex */
public final class OpponentCardingUiState {
    private final OpponentCardingConfig config;

    public OpponentCardingUiState(OpponentCardingConfig opponentCardingConfig) {
        this.config = opponentCardingConfig == null ? OpponentCardingConfig.defaults() : opponentCardingConfig;
    }

    public OpponentCardingConfig getConfig() {
        return this.config;
    }

    public OpponentCardingUiState withEnabled(boolean z) {
        return new OpponentCardingUiState(this.config.withEnabled(z));
    }

    public OpponentCardingUiState stepThreshold(int i) {
        OpponentCardingConfig opponentCardingConfig = this.config;
        return new OpponentCardingUiState(opponentCardingConfig.withThreshold(opponentCardingConfig.getThreshold() + sign(i)));
    }

    public OpponentCardingUiState toggleCost(int i) {
        int costMask;
        int iCostBit = OpponentCardingConfig.costBit(i);
        return (iCostBit == 0 || (costMask = iCostBit ^ this.config.getCostMask()) == 0) ? this : new OpponentCardingUiState(this.config.withCostMask(costMask));
    }

    public OpponentCardingUiState stepReserveGold(int i) {
        OpponentCardingConfig opponentCardingConfig = this.config;
        return new OpponentCardingUiState(opponentCardingConfig.withReserveGold(opponentCardingConfig.getReserveGold() + (sign(i) * 10)));
    }

    public OpponentCardingUiState withStrategy(int i) {
        return new OpponentCardingUiState(this.config.withStrategy(i));
    }

    public OpponentCardingUiState stepMaxOwnedCopies(int i) {
        OpponentCardingConfig opponentCardingConfig = this.config;
        return new OpponentCardingUiState(opponentCardingConfig.withMaxOwnedCopies(opponentCardingConfig.getMaxOwnedCopies() + sign(i)));
    }

    public String summaryText() {
        return this.config.toSummary();
    }

    public String statusText(OpponentCardingStatus opponentCardingStatus) {
        if (!this.config.isEnabled()) {
            return "已关闭";
        }
        if (opponentCardingStatus == null || opponentCardingStatus.getSummary().isEmpty()) {
            return "等待对局数据";
        }
        return opponentCardingStatus.getSummary();
    }

    private static int sign(int i) {
        return Integer.compare(i, 0);
    }
}
