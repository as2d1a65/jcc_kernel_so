package com.android.support;

/* loaded from: classes.dex */
final class JccDistributionProfile {
    private static final Entry CANDIDATE;
    private static final Entry FORMAL;
    private static final Entry TEST;

    static {
        FORMAL = new Entry("com.android.support", BuildConfig.JCC_DIST_FORMAL_SOCKET_NAME, BuildConfig.JCC_DIST_FORMAL_TCP_PORT_START);
        CANDIDATE = new Entry(BuildConfig.JCC_DIST_CANDIDATE_APPLICATION_ID, BuildConfig.JCC_DIST_CANDIDATE_SOCKET_NAME, BuildConfig.JCC_DIST_CANDIDATE_TCP_PORT_START);
        TEST = new Entry(BuildConfig.JCC_DIST_TEST_APPLICATION_ID, BuildConfig.JCC_DIST_TEST_SOCKET_NAME, BuildConfig.JCC_DIST_TEST_TCP_PORT_START);
    }

    private JccDistributionProfile() {
    }

    static Entry current() {
        return forApplicationId("com.android.support");
    }

    static Entry forApplicationId(String str) {
        Entry entry = CANDIDATE;
        if (entry.applicationId.equals(str)) {
            return entry;
        }
        Entry entry2 = TEST;
        return entry2.applicationId.equals(str) ? entry2 : FORMAL;
    }

    static final class Entry {
        final String applicationId;
        final String socketName;
        final int tcpPortStart;

        private Entry(String str, String str2, int i) {
            this.applicationId = str;
            this.socketName = str2;
            this.tcpPortStart = i;
        }

        int tcpPortEnd() {
            return this.tcpPortStart + 4;
        }
    }
}
