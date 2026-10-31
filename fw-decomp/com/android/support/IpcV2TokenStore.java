package com.android.support;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: classes.dex */
public final class IpcV2TokenStore {
    private static final String CURRENT_NAME = "ipc_v2_current_token.json";
    private static final int MAX_RECORD_BYTES = 256;
    private static final String PENDING_NAME = "ipc_v2_pending_token.json";
    private static final Pattern RECORD_PATTERN = Pattern.compile("\\x7B\"v\":2,\"tokenEpoch\":([1-9][0-9]*),\"token\":\"([A-Za-z0-9_-]{43})\"\\x7D");
    private final File currentFile;
    private final File directory;
    private final File pendingFile;
    private final SecureRandom random;

    public static final class TokenRecord {
        private final long epoch;
        private final byte[] token;

        private TokenRecord(long j, byte[] bArr) {
            if (j <= 0 || bArr == null || bArr.length != 32) {
                throw new IllegalArgumentException("invalid token record");
            }
            this.epoch = j;
            this.token = (byte[]) bArr.clone();
        }

        public long epoch() {
            return this.epoch;
        }

        public byte[] token() {
            return (byte[]) this.token.clone();
        }
    }

    public IpcV2TokenStore(File file, SecureRandom secureRandom) throws IOException {
        if (file == null || secureRandom == null) {
            throw new IllegalArgumentException("directory/random required");
        }
        File canonicalFile = file.getCanonicalFile();
        this.directory = canonicalFile;
        this.random = secureRandom;
        if (!canonicalFile.isDirectory() && !canonicalFile.mkdirs()) {
            throw new IOException("cannot create token directory");
        }
        this.currentFile = new File(canonicalFile, CURRENT_NAME);
        this.pendingFile = new File(canonicalFile, PENDING_NAME);
    }

    public synchronized TokenRecord beginInjection() throws IOException {
        TokenRecord tokenRecord;
        TokenRecord record = readRecord(this.currentFile);
        TokenRecord record2 = readRecord(this.pendingFile);
        long j = 0;
        long j2 = record == null ? 0L : record.epoch;
        if (record2 != null) {
            j = record2.epoch;
        }
        long jMax = Math.max(j2, j);
        long jUniqueRandomEpoch = jMax < Long.MAX_VALUE ? jMax + 1 : uniqueRandomEpoch(record, record2);
        byte[] bArr = new byte[32];
        this.random.nextBytes(bArr);
        tokenRecord = new TokenRecord(jUniqueRandomEpoch, bArr);
        writeRecordAtomically(this.pendingFile, tokenRecord);
        return tokenRecord;
    }

    public synchronized TokenRecord current() throws IOException {
        return readRecord(this.currentFile);
    }

    public synchronized TokenRecord pending() throws IOException {
        return readRecord(this.pendingFile);
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x0024  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public synchronized com.android.support.IpcV2TokenStore.TokenRecord findForAuthentication(long r4) throws java.io.IOException {
        /*
            r3 = this;
            monitor-enter(r3)
            java.io.File r0 = r3.currentFile     // Catch: java.lang.Throwable -> L27
            com.android.support.IpcV2TokenStore$TokenRecord r0 = r3.readRecord(r0)     // Catch: java.lang.Throwable -> L27
            if (r0 == 0) goto L13
            long r1 = com.android.support.IpcV2TokenStore.TokenRecord.m1218$$Nest$fgetepoch(r0)     // Catch: java.lang.Throwable -> L27
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r1 != 0) goto L13
            monitor-exit(r3)
            return r0
        L13:
            java.io.File r0 = r3.pendingFile     // Catch: java.lang.Throwable -> L27
            com.android.support.IpcV2TokenStore$TokenRecord r0 = r3.readRecord(r0)     // Catch: java.lang.Throwable -> L27
            if (r0 == 0) goto L24
            long r1 = com.android.support.IpcV2TokenStore.TokenRecord.m1218$$Nest$fgetepoch(r0)     // Catch: java.lang.Throwable -> L27
            int r4 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r4 != 0) goto L24
            goto L25
        L24:
            r0 = 0
        L25:
            monitor-exit(r3)
            return r0
        L27:
            r4 = move-exception
            monitor-exit(r3)     // Catch: java.lang.Throwable -> L27
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.IpcV2TokenStore.findForAuthentication(long):com.android.support.IpcV2TokenStore$TokenRecord");
    }

    public synchronized TokenRecord activeForBootstrap() throws IOException {
        TokenRecord record;
        record = readRecord(this.pendingFile);
        if (record == null) {
            record = readRecord(this.currentFile);
        }
        return record;
    }

    public synchronized boolean promoteAuthenticated(long j) throws IOException {
        TokenRecord record = readRecord(this.currentFile);
        if (record != null && record.epoch == j) {
            return true;
        }
        TokenRecord record2 = readRecord(this.pendingFile);
        if (record2 != null && record2.epoch == j) {
            writeRecordAtomically(this.currentFile, record2);
            deleteIfExists(this.pendingFile);
            return true;
        }
        return false;
    }

    public synchronized void abortPending(long j) throws IOException {
        TokenRecord record = readRecord(this.pendingFile);
        if (record != null && record.epoch == j) {
            deleteIfExists(this.pendingFile);
        }
    }

    private long uniqueRandomEpoch(TokenRecord tokenRecord, TokenRecord tokenRecord2) {
        long jRandomPositiveLong;
        while (true) {
            jRandomPositiveLong = IpcV2Protocol.randomPositiveLong(this.random);
            if (tokenRecord == null || tokenRecord.epoch != jRandomPositiveLong) {
                if (tokenRecord2 == null || tokenRecord2.epoch != jRandomPositiveLong) {
                    break;
                }
            }
        }
        return jRandomPositiveLong;
    }

    private TokenRecord readRecord(File file) throws IOException {
        if (!file.isFile()) {
            return null;
        }
        long length = file.length();
        if (length <= 0 || length > 256) {
            throw new IOException("invalid token record length");
        }
        Matcher matcher = RECORD_PATTERN.matcher(new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8));
        if (!matcher.matches()) {
            throw new IOException("invalid token record schema");
        }
        try {
            try {
                return new TokenRecord(Long.parseLong(matcher.group(1)), IpcV2Protocol.decodeBase64Url(matcher.group(2), 32));
            } catch (IllegalArgumentException e) {
                throw new IOException("invalid token encoding", e);
            }
        } catch (NumberFormatException e2) {
            throw new IOException("invalid token epoch", e2);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x006f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void writeRecordAtomically(java.io.File r6, com.android.support.IpcV2TokenStore.TokenRecord r7) throws java.lang.Throwable {
        /*
            r5 = this;
            java.lang.String r7 = canonicalJson(r7)
            java.nio.charset.Charset r0 = java.nio.charset.StandardCharsets.UTF_8
            byte[] r7 = r7.getBytes(r0)
            java.lang.String r0 = r6.getName()
            java.lang.String r1 = ".tmp"
            java.io.File r5 = r5.directory
            java.io.File r5 = java.io.File.createTempFile(r0, r1, r5)
            r0 = 0
            setOwnerOnly(r5)     // Catch: java.lang.Throwable -> L6c
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L6c
            r1.<init>(r5, r0)     // Catch: java.lang.Throwable -> L6c
            r1.write(r7)     // Catch: java.lang.Throwable -> L62
            r1.flush()     // Catch: java.lang.Throwable -> L62
            java.io.FileDescriptor r7 = r1.getFD()     // Catch: java.lang.Throwable -> L62
            r7.sync()     // Catch: java.lang.Throwable -> L62
            r1.close()     // Catch: java.lang.Throwable -> L6c
            setOwnerOnly(r5)     // Catch: java.lang.Throwable -> L6c
            r7 = 1
            java.nio.file.Path r1 = r5.toPath()     // Catch: java.nio.file.AtomicMoveNotSupportedException -> L4a java.lang.Throwable -> L6c
            java.nio.file.Path r2 = r6.toPath()     // Catch: java.nio.file.AtomicMoveNotSupportedException -> L4a java.lang.Throwable -> L6c
            r3 = 2
            java.nio.file.CopyOption[] r3 = new java.nio.file.CopyOption[r3]     // Catch: java.nio.file.AtomicMoveNotSupportedException -> L4a java.lang.Throwable -> L6c
            java.nio.file.StandardCopyOption r4 = java.nio.file.StandardCopyOption.ATOMIC_MOVE     // Catch: java.nio.file.AtomicMoveNotSupportedException -> L4a java.lang.Throwable -> L6c
            r3[r0] = r4     // Catch: java.nio.file.AtomicMoveNotSupportedException -> L4a java.lang.Throwable -> L6c
            java.nio.file.StandardCopyOption r4 = java.nio.file.StandardCopyOption.REPLACE_EXISTING     // Catch: java.nio.file.AtomicMoveNotSupportedException -> L4a java.lang.Throwable -> L6c
            r3[r7] = r4     // Catch: java.nio.file.AtomicMoveNotSupportedException -> L4a java.lang.Throwable -> L6c
            java.nio.file.Files.move(r1, r2, r3)     // Catch: java.nio.file.AtomicMoveNotSupportedException -> L4a java.lang.Throwable -> L6c
            goto L5b
        L4a:
            java.nio.file.Path r1 = r5.toPath()     // Catch: java.lang.Throwable -> L6c
            java.nio.file.Path r2 = r6.toPath()     // Catch: java.lang.Throwable -> L6c
            java.nio.file.CopyOption[] r3 = new java.nio.file.CopyOption[r7]     // Catch: java.lang.Throwable -> L6c
            java.nio.file.StandardCopyOption r4 = java.nio.file.StandardCopyOption.REPLACE_EXISTING     // Catch: java.lang.Throwable -> L6c
            r3[r0] = r4     // Catch: java.lang.Throwable -> L6c
            java.nio.file.Files.move(r1, r2, r3)     // Catch: java.lang.Throwable -> L6c
        L5b:
            setOwnerOnly(r6)     // Catch: java.lang.Throwable -> L5f
            return
        L5f:
            r6 = move-exception
            r0 = r7
            goto L6d
        L62:
            r6 = move-exception
            r1.close()     // Catch: java.lang.Throwable -> L67
            goto L6b
        L67:
            r7 = move-exception
            r6.addSuppressed(r7)     // Catch: java.lang.Throwable -> L6c
        L6b:
            throw r6     // Catch: java.lang.Throwable -> L6c
        L6c:
            r6 = move-exception
        L6d:
            if (r0 != 0) goto L72
            deleteIfExists(r5)
        L72:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.support.IpcV2TokenStore.writeRecordAtomically(java.io.File, com.android.support.IpcV2TokenStore$TokenRecord):void");
    }

    private static String canonicalJson(TokenRecord tokenRecord) {
        return "{\"v\":2,\"tokenEpoch\":" + tokenRecord.epoch + ",\"token\":\"" + IpcV2Protocol.base64Url(tokenRecord.token) + "\"}";
    }

    private static void setOwnerOnly(File file) throws IOException {
        file.setReadable(false, false);
        file.setWritable(false, false);
        if (!file.setReadable(true, true) || !file.setWritable(true, true)) {
            throw new IOException("cannot set owner-only token permissions");
        }
        try {
            Files.setPosixFilePermissions(file.toPath(), EnumSet.of(PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE));
        } catch (UnsupportedOperationException unused) {
        }
    }

    private static void deleteIfExists(File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException("cannot delete token record");
        }
    }
}
