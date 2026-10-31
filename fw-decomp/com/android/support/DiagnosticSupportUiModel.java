package com.android.support;

import com.android.support.diagnostics.DiagnosticCoordinator;

/* loaded from: classes.dex */
final class DiagnosticSupportUiModel {
    final boolean copyEnabled;
    final String quota;
    final boolean showConfirm;
    final boolean showStop;
    final String status;
    final int statusColor;
    final String supportCode;

    private DiagnosticSupportUiModel(String str, String str2, String str3, int i, boolean z, boolean z2, boolean z3) {
        this.supportCode = str;
        this.status = str2;
        this.quota = str3;
        this.statusColor = i;
        this.copyEnabled = z;
        this.showConfirm = z2;
        this.showStop = z3;
    }

    /* JADX WARN: Removed duplicated region for block: B:34:0x0074  */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00bb  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00c3  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x00d3  */
    /* JADX WARN: Removed duplicated region for block: B:49:0x00e1  */
    /* JADX WARN: Removed duplicated region for block: B:50:0x00e3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static com.android.support.DiagnosticSupportUiModel create(com.android.support.diagnostics.DiagnosticCoordinator.Status r9, java.lang.String r10, boolean r11, long r12, int r14, long r15, java.lang.String r17) {
        /*
            Method dump skipped, instructions count: 254
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.DiagnosticSupportUiModel.create(com.android.support.diagnostics.DiagnosticCoordinator$Status, java.lang.String, boolean, long, int, long, java.lang.String):com.android.support.DiagnosticSupportUiModel");
    }

    /* renamed from: com.android.support.DiagnosticSupportUiModel$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$support$diagnostics$DiagnosticCoordinator$Status;

        static {
            int[] iArr = new int[DiagnosticCoordinator.Status.values().length];
            $SwitchMap$com$android$support$diagnostics$DiagnosticCoordinator$Status = iArr;
            try {
                iArr[DiagnosticCoordinator.Status.IDENTITY_PENDING.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$support$diagnostics$DiagnosticCoordinator$Status[DiagnosticCoordinator.Status.IDLE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$support$diagnostics$DiagnosticCoordinator$Status[DiagnosticCoordinator.Status.WAITING_LOCAL_CONFIRMATION.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$support$diagnostics$DiagnosticCoordinator$Status[DiagnosticCoordinator.Status.ARMED.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$support$diagnostics$DiagnosticCoordinator$Status[DiagnosticCoordinator.Status.ACTIVE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$support$diagnostics$DiagnosticCoordinator$Status[DiagnosticCoordinator.Status.UPLOADING.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$support$diagnostics$DiagnosticCoordinator$Status[DiagnosticCoordinator.Status.ENDED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$support$diagnostics$DiagnosticCoordinator$Status[DiagnosticCoordinator.Status.ERROR.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$support$diagnostics$DiagnosticCoordinator$Status[DiagnosticCoordinator.Status.OFF.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
        }
    }
}
