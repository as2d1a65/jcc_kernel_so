package com.android.support;

import com.android.support.CloudTtsWire;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import kotlin.UByte;

/* loaded from: classes.dex */
public final class CloudTtsWire {
    private static final char[] HEX = "0123456789abcdef".toCharArray();
    public static final long MAX_CACHE_BYTES = 20971520;
    public static final int MAX_WAV_BYTES = 2097152;
    private static final int MIN_WAV_BYTES = 44;
    private static final String TEST_AUDIO_REVISION = "xf-welcome-v1";

    private static long saturatedAdd(long j, long j2) {
        if (Long.MAX_VALUE - j < j2) {
            return Long.MAX_VALUE;
        }
        return j + j2;
    }

    private CloudTtsWire() {
    }

    public static String testRequestJson() {
        return "{\"type\":\"test\"}";
    }

    public static String bearerAuthorization(String str) {
        if (str == null || str.isEmpty() || str.length() > 256) {
            throw new IllegalArgumentException("opaque bearer token is invalid");
        }
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt <= ' ' || cCharAt >= 127) {
                throw new IllegalArgumentException("opaque bearer token is invalid");
            }
        }
        return "Bearer " + str;
    }

    public static String rankAlertRequestJson(String str, String str2, int i) {
        Objects.requireNonNull(str, "playerName");
        Objects.requireNonNull(str2, "heroName");
        return "{\"type\":\"rank_alert\",\"player_name\":\"" + escapeJson(str) + "\",\"hero_name\":\"" + escapeJson(str2) + "\",\"copies\":" + i + "}";
    }

    public static String cacheKeyForTest() {
        return sha256(testRequestJson() + "\ntest-audio-revision:xf-welcome-v1");
    }

    public static String cacheKeyForRankAlert(String str, String str2, int i) {
        return sha256(rankAlertRequestJson(str, str2, i));
    }

    public static boolean isValidWav(byte[] bArr) {
        return bArr != null && bArr.length >= 44 && bArr.length <= 2097152 && hasWavHeader(bArr);
    }

    public static boolean isValidWav(File file) throws IOException {
        if (file != null && file.isFile()) {
            long length = file.length();
            if (length >= 44 && length <= 2097152) {
                byte[] bArr = new byte[12];
                try {
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
                    try {
                        randomAccessFile.readFully(bArr);
                        boolean zHasWavHeader = hasWavHeader(bArr);
                        randomAccessFile.close();
                        return zHasWavHeader;
                    } finally {
                    }
                } catch (IOException unused) {
                }
            }
        }
        return false;
    }

    public static boolean ensureDirectory(File file) {
        if (file == null) {
            return false;
        }
        try {
            if (file.isDirectory()) {
                return true;
            }
            file.mkdirs();
            return file.isDirectory();
        } catch (SecurityException unused) {
            return false;
        }
    }

    public static List<CacheEntry> planCacheDeletions(List<CacheEntry> list) {
        long jMax;
        ArrayList<CacheEntry> arrayList = new ArrayList();
        if (list != null) {
            jMax = 0;
            for (CacheEntry cacheEntry : list) {
                if (isValidCacheEntry(cacheEntry)) {
                    arrayList.add(cacheEntry);
                    jMax = saturatedAdd(jMax, cacheEntry.sizeBytes);
                }
            }
        } else {
            jMax = 0;
        }
        ArrayList arrayList2 = new ArrayList();
        if (jMax > MAX_CACHE_BYTES) {
            arrayList.sort(Comparator.comparingLong(new ToLongFunction() { // from class: com.android.support.CloudTtsWire$$ExternalSyntheticLambda0
                @Override // java.util.function.ToLongFunction
                public final long applyAsLong(Object obj) {
                    return ((CloudTtsWire.CacheEntry) obj).getLastModified();
                }
            }).thenComparing(new Function() { // from class: com.android.support.CloudTtsWire$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((CloudTtsWire.CacheEntry) obj).getIdentifier();
                }
            }));
            for (CacheEntry cacheEntry2 : arrayList) {
                if (jMax <= MAX_CACHE_BYTES) {
                    break;
                }
                if (cacheEntry2.deletable) {
                    arrayList2.add(cacheEntry2);
                    jMax = Math.max(0L, jMax - cacheEntry2.sizeBytes);
                }
            }
        }
        return arrayList2;
    }

    public static float speedForSlider(int i) {
        return (clampSlider(i) * 0.005f) + 0.75f;
    }

    public static float pitchForSlider(int i) {
        return (clampSlider(i) * 0.004f) + 0.8f;
    }

    public static float volumeForSlider(int i) {
        return clampSlider(i) / 100.0f;
    }

    private static boolean hasWavHeader(byte[] bArr) {
        return bArr.length >= 12 && bArr[0] == 82 && bArr[1] == 73 && bArr[2] == 70 && bArr[3] == 70 && bArr[8] == 87 && bArr[9] == 65 && bArr[10] == 86 && bArr[11] == 69;
    }

    private static String escapeJson(String str) {
        StringBuilder sb = new StringBuilder(str.length() + 16);
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt == '\t') {
                sb.append("\\t");
            } else if (cCharAt == '\n') {
                sb.append("\\n");
            } else if (cCharAt == '\r') {
                sb.append("\\r");
            } else if (cCharAt == '\"') {
                sb.append("\\\"");
            } else if (cCharAt == '\\') {
                sb.append("\\\\");
            } else if (cCharAt < ' ') {
                StringBuilder sbAppend = sb.append("\\u00");
                char[] cArr = HEX;
                sbAppend.append(cArr[(cCharAt >>> 4) & 15]).append(cArr[cCharAt & 15]);
            } else {
                sb.append(cCharAt);
            }
        }
        return sb.toString();
    }

    private static String sha256(String str) {
        try {
            byte[] bArrDigest = MessageDigest.getInstance("SHA-256").digest(str.getBytes(StandardCharsets.UTF_8));
            char[] cArr = new char[bArrDigest.length * 2];
            for (int i = 0; i < bArrDigest.length; i++) {
                byte b = bArrDigest[i];
                int i2 = b & UByte.MAX_VALUE;
                int i3 = i * 2;
                char[] cArr2 = HEX;
                cArr[i3] = cArr2[i2 >>> 4];
                cArr[i3 + 1] = cArr2[b & 15];
            }
            return new String(cArr);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    private static int clampSlider(int i) {
        return Math.max(0, Math.min(100, i));
    }

    private static boolean isValidCacheEntry(CacheEntry cacheEntry) {
        return (cacheEntry == null || cacheEntry.identifier == null || cacheEntry.identifier.isEmpty() || cacheEntry.sizeBytes < 0 || cacheEntry.lastModified < 0) ? false : true;
    }

    public static final class CacheEntry {
        private final boolean deletable;
        private final String identifier;
        private final long lastModified;
        private final long sizeBytes;

        public CacheEntry(String str, long j, long j2, boolean z) {
            this.identifier = str;
            this.sizeBytes = j;
            this.lastModified = j2;
            this.deletable = z;
        }

        public String getIdentifier() {
            return this.identifier;
        }

        public long getSizeBytes() {
            return this.sizeBytes;
        }

        public long getLastModified() {
            return this.lastModified;
        }

        public boolean isDeletable() {
            return this.deletable;
        }
    }

    public static final class CachePinRegistry {
        private final Map<String, Integer> pinCounts = new HashMap();

        public synchronized CachePin pin(String str) {
            requirePinIdentifier(str);
            Integer num = this.pinCounts.get(str);
            if (num != null && num.intValue() == Integer.MAX_VALUE) {
                throw new IllegalStateException("too many cache pins");
            }
            Map<String, Integer> map = this.pinCounts;
            int iIntValue = 1;
            if (num != null) {
                iIntValue = 1 + num.intValue();
            }
            map.put(str, Integer.valueOf(iIntValue));
            return new CachePin(this, str);
        }

        public synchronized boolean isPinned(String str) {
            boolean z = false;
            if (str != null) {
                if (!str.isEmpty()) {
                    Integer num = this.pinCounts.get(str);
                    if (num != null) {
                        if (num.intValue() > 0) {
                            z = true;
                        }
                    }
                    return z;
                }
            }
            return false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized void release(String str) {
            Integer num = this.pinCounts.get(str);
            if (num == null || num.intValue() <= 1) {
                this.pinCounts.remove(str);
            } else {
                this.pinCounts.put(str, Integer.valueOf(num.intValue() - 1));
            }
        }

        private static void requirePinIdentifier(String str) {
            if (str == null || str.isEmpty()) {
                throw new IllegalArgumentException("cache pin identifier must not be empty");
            }
        }
    }

    public static final class CachePin implements AutoCloseable {
        private boolean closed;
        private final String identifier;
        private final CachePinRegistry registry;

        private CachePin(CachePinRegistry cachePinRegistry, String str) {
            this.registry = cachePinRegistry;
            this.identifier = str;
        }

        public synchronized CachePin retain() {
            if (this.closed) {
                throw new IllegalStateException("cache pin is closed");
            }
            return this.registry.pin(this.identifier);
        }

        @Override // java.lang.AutoCloseable
        public synchronized void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.registry.release(this.identifier);
        }
    }
}
