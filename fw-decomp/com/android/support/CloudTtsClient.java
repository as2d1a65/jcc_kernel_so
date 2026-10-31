package com.android.support;

import android.content.Context;
import com.android.support.CloudTtsWire;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.Call;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class CloudTtsClient implements AutoCloseable {
    private static final int BUFFER_BYTES = 8192;
    private static final int MAX_ERROR_BYTES = 16384;
    private final Set<Operation> activeOperations;
    private final File cacheDirectory;
    private boolean closed;
    private final HttpUrl endpoint;
    private final ExecutorService executor;
    private long generation;
    private final OkHttpClient httpClient;
    private final Object lifecycleLock;
    public static final String DEFAULT_ENDPOINT = JccNetworkPolicy.TTS_URL;
    private static final MediaType JSON_MEDIA_TYPE = MediaType.get("application/json; charset=utf-8");
    private static final Object CACHE_FILE_LOCK = new Object();
    private static final CloudTtsWire.CachePinRegistry CACHE_PINS = new CloudTtsWire.CachePinRegistry();
    private static final ExecutorService TRANSPORT = Executors.newFixedThreadPool(3, ThreadNaming.factory(ThreadNaming.WORKER));

    public interface Callback {
        void onFailure(Failure failure);

        void onSuccess(AudioFile audioFile);
    }

    public enum FailureKind {
        NETWORK,
        SERVER,
        INVALID_AUDIO,
        CACHE_IO,
        CLOSED,
        CANCELLED
    }

    public interface RequestHandle {
        void cancel();

        boolean isDone();
    }

    private enum StoreStatus {
        SUCCESS,
        INVALID_AUDIO,
        CACHE_IO
    }

    public static final class Failure {
        private final FailureKind kind;
        private final String message;
        private final String serverCode;

        private Failure(FailureKind failureKind, String str, String str2) {
            this.kind = failureKind;
            this.serverCode = str;
            this.message = str2;
        }

        public FailureKind getKind() {
            return this.kind;
        }

        public String getServerCode() {
            return this.serverCode;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public static final class AudioFile implements AutoCloseable {
        private final File file;
        private CloudTtsWire.CachePin pin;

        private AudioFile(File file, CloudTtsWire.CachePin cachePin) {
            this.file = file;
            this.pin = cachePin;
        }

        public synchronized File getFile() {
            if (this.pin == null) {
                throw new IllegalStateException("audio file lease is closed");
            }
            return this.file;
        }

        public synchronized AudioFile retain() {
            CloudTtsWire.CachePin cachePin;
            cachePin = this.pin;
            if (cachePin == null) {
                throw new IllegalStateException("audio file lease is closed");
            }
            return new AudioFile(this.file, cachePin.retain());
        }

        @Override // java.lang.AutoCloseable
        public synchronized void close() {
            CloudTtsWire.CachePin cachePin = this.pin;
            if (cachePin == null) {
                return;
            }
            this.pin = null;
            cachePin.close();
        }
    }

    public CloudTtsClient(Context context) {
        this(context, JccNetworkPolicy.requireFixedApiUrl(DEFAULT_ENDPOINT, "/v2/api/tts"));
    }

    public CloudTtsClient(Context context, String str) {
        this.lifecycleLock = new Object();
        this.activeOperations = new HashSet();
        if (context == null) {
            throw new IllegalArgumentException("context must not be null");
        }
        this.endpoint = parseEndpoint(str);
        Context applicationContext = context.getApplicationContext();
        File cacheDir = (applicationContext != null ? applicationContext : context).getCacheDir();
        this.cacheDirectory = cacheDir == null ? null : new File(cacheDir, "tts");
        ExecutorService executorServiceNewFixedThreadPool = Executors.newFixedThreadPool(3, new CloudThreadFactory());
        this.executor = executorServiceNewFixedThreadPool;
        this.httpClient = new OkHttpClient.Builder().dispatcher(new Dispatcher(executorServiceNewFixedThreadPool)).followRedirects(false).followSslRedirects(false).build();
    }

    public RequestHandle requestTest(Callback callback) {
        return enqueue(CloudTtsWire.testRequestJson(), CloudTtsWire.cacheKeyForTest(), callback);
    }

    public RequestHandle requestRankAlert(String str, String str2, int i, Callback callback) {
        return enqueue(CloudTtsWire.rankAlertRequestJson(str, str2, i), CloudTtsWire.cacheKeyForRankAlert(str, str2, i), callback);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        synchronized (this.lifecycleLock) {
            if (this.closed) {
                return;
            }
            this.closed = true;
            this.generation++;
            ArrayList arrayList = new ArrayList(this.activeOperations);
            ArrayList arrayList2 = new ArrayList(arrayList.size());
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                Delivery deliveryClaimFailureLocked = ((Operation) it.next()).claimFailureLocked(failure(FailureKind.CLOSED, "TTS client is closed"));
                if (deliveryClaimFailureLocked != null) {
                    arrayList2.add(deliveryClaimFailureLocked);
                }
            }
            try {
                this.httpClient.dispatcher().cancelAll();
            } catch (RuntimeException unused) {
            }
            try {
                this.executor.shutdownNow();
            } catch (RuntimeException unused2) {
            }
            try {
                this.httpClient.connectionPool().evictAll();
            } catch (RuntimeException unused3) {
            }
            Iterator it2 = arrayList2.iterator();
            while (it2.hasNext()) {
                deliver((Delivery) it2.next());
            }
        }
    }

    private RequestHandle enqueue(final String str, final String str2, Callback callback) {
        final Operation operation;
        Delivery deliveryClaimFailureLocked;
        if (callback == null) {
            throw new IllegalArgumentException("callback must not be null");
        }
        synchronized (this.lifecycleLock) {
            operation = new Operation(callback, this.generation);
            if (this.closed) {
                deliveryClaimFailureLocked = operation.claimFailureLocked(failure(FailureKind.CLOSED, "TTS client is closed"));
            } else {
                this.activeOperations.add(operation);
                deliveryClaimFailureLocked = null;
            }
        }
        if (deliveryClaimFailureLocked != null) {
            deliver(deliveryClaimFailureLocked);
            return operation;
        }
        try {
            this.executor.execute(new Runnable() { // from class: com.android.support.CloudTtsClient$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() throws Throwable {
                    this.f$0.m126lambda$enqueue$0$comandroidsupportCloudTtsClient(operation, str, str2);
                }
            });
            return operation;
        } catch (RejectedExecutionException unused) {
            operation.finish(closedFailureOr(FailureKind.NETWORK, "TTS request could not start"));
            return operation;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v10 */
    /* JADX WARN: Type inference failed for: r7v12 */
    /* JADX WARN: Type inference failed for: r7v14 */
    /* renamed from: beginOperation, reason: merged with bridge method [inline-methods] */
    public void m126lambda$enqueue$0$comandroidsupportCloudTtsClient(final Operation operation, String str, String str2) throws Throwable {
        boolean zCleanupCache;
        AudioFile audioFile;
        if (!operation.isActive()) {
            return;
        }
        zCleanupCache = false;
        if (CandidateBusinessEndpointPolicy.rejectCandidateBuild(false, new Runnable() { // from class: com.android.support.CloudTtsClient$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                operation.finish(CloudTtsClient.serverFailure("CANDIDATE_FEATURE_DISABLED", "请使用正式版启用云语音。"));
            }
        })) {
            return;
        }
        String strBearerTokenNow = JccSessionState.shared().bearerTokenNow();
        if (strBearerTokenNow == null) {
            operation.finish(serverFailure("AUTH_REQUIRED", "请先完成网络验证，再使用云语音。"));
            return;
        }
        try {
            String strBearerAuthorization = CloudTtsWire.bearerAuthorization(strBearerTokenNow);
            if (!ensureCacheDirectory()) {
                operation.finish(failure(FailureKind.CACHE_IO, "TTS cache is unavailable"));
                return;
            }
            final File file = new File(this.cacheDirectory, str2 + ".wav");
            Object obj = CACHE_FILE_LOCK;
            synchronized (obj) {
                if (!operation.isActive()) {
                    return;
                }
                boolean zIsValidWav = CloudTtsWire.isValidWav(file);
                boolean z = !zIsValidWav && file.exists() && !file.delete() && file.exists();
                if (z) {
                    operation.finish(failure(FailureKind.CACHE_IO, "Invalid cached audio could not be removed"));
                    return;
                }
                if (zIsValidWav) {
                    audioFile = 0;
                    audioFile = 0;
                    audioFile = 0;
                    try {
                        synchronized (obj) {
                            try {
                                if (CloudTtsWire.isValidWav(file)) {
                                    AudioFile audioFile2 = new AudioFile(file, CACHE_PINS.pin(file.getAbsolutePath()));
                                    try {
                                        zCleanupCache = cleanupCache(file);
                                        audioFile = audioFile2;
                                    } catch (Throwable th) {
                                        th = th;
                                        audioFile = audioFile2;
                                    }
                                }
                            } catch (Throwable th2) {
                                th = th2;
                            }
                        }
                        throw th;
                    } catch (RuntimeException unused) {
                    }
                } else {
                    if (operation.isActive()) {
                        final Call callNewCall = this.httpClient.newCall(new Request.Builder().url(this.endpoint).header("Authorization", strBearerAuthorization).post(RequestBody.create(str, JSON_MEDIA_TYPE)).build());
                        if (!operation.attachCall(callNewCall)) {
                            callNewCall.cancel();
                            return;
                        } else {
                            TRANSPORT.execute(new Runnable() { // from class: com.android.support.CloudTtsClient$$ExternalSyntheticLambda1
                                @Override // java.lang.Runnable
                                public final void run() {
                                    this.f$0.m125lambda$beginOperation$2$comandroidsupportCloudTtsClient(callNewCall, operation, file);
                                }
                            });
                            return;
                        }
                    }
                    return;
                }
            }
        } catch (IllegalArgumentException unused2) {
            operation.finish(serverFailure("AUTH_REQUIRED", "登录会话无效，请重新验证后再试。"));
            return;
        }
        if (!zCleanupCache) {
            closeQuietly(audioFile);
            operation.finish(failure(FailureKind.CACHE_IO, "TTS cache cleanup failed"));
        } else {
            operation.succeed(audioFile);
        }
    }

    /* renamed from: lambda$beginOperation$2$com-android-support-CloudTtsClient, reason: not valid java name */
    /* synthetic */ void m125lambda$beginOperation$2$comandroidsupportCloudTtsClient(Call call, Operation operation, File file) {
        try {
            Response responseExecute = call.execute();
            try {
                handleResponse(operation, file, responseExecute);
                if (responseExecute != null) {
                    responseExecute.close();
                }
            } catch (Throwable th) {
                if (responseExecute != null) {
                    try {
                        responseExecute.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (IOException unused) {
            if (!call.getCanceled()) {
                operation.finish(failure(FailureKind.NETWORK, "TTS network request failed"));
            } else {
                operation.finish(operation.cancelledOrClosedFailure());
            }
        } catch (RuntimeException unused2) {
            operation.finish(failure(FailureKind.NETWORK, "TTS network request failed"));
        }
    }

    private void handleResponse(Operation operation, File file, Response response) {
        try {
            if (!operation.isActive()) {
                if (response != null) {
                    response.close();
                    return;
                }
                return;
            }
            if (!response.isSuccessful()) {
                operation.finish(readServerFailure(response.body()));
                if (response != null) {
                    response.close();
                    return;
                }
                return;
            }
            ResponseBody responseBodyBody = response.body();
            if (responseBodyBody == null) {
                operation.finish(failure(FailureKind.INVALID_AUDIO, "TTS response contained no audio"));
                if (response != null) {
                    response.close();
                    return;
                }
                return;
            }
            try {
                InputStream inputStreamByteStream = responseBodyBody.byteStream();
                try {
                    byte[] bounded = readBounded(inputStreamByteStream, 2097152);
                    if (inputStreamByteStream != null) {
                        inputStreamByteStream.close();
                    }
                    if (bounded != null && CloudTtsWire.isValidWav(bounded)) {
                        StoreResult storeResultStoreAudio = storeAudio(file, bounded);
                        if (storeResultStoreAudio.status == StoreStatus.INVALID_AUDIO) {
                            operation.finish(failure(FailureKind.INVALID_AUDIO, "Stored TTS audio failed validation"));
                        } else if (storeResultStoreAudio.status == StoreStatus.CACHE_IO) {
                            operation.finish(failure(FailureKind.CACHE_IO, "TTS audio could not be cached"));
                        } else {
                            operation.succeed(storeResultStoreAudio.audioFile);
                        }
                        if (response != null) {
                            response.close();
                            return;
                        }
                        return;
                    }
                    operation.finish(failure(FailureKind.INVALID_AUDIO, "TTS response was not valid WAV audio"));
                    if (response != null) {
                        response.close();
                    }
                } catch (Throwable th) {
                    if (inputStreamByteStream != null) {
                        try {
                            inputStreamByteStream.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (IOException unused) {
                operation.finish(failure(FailureKind.NETWORK, "TTS audio download failed"));
                if (response != null) {
                    response.close();
                }
            }
        } catch (Throwable th3) {
            if (response != null) {
                try {
                    response.close();
                } catch (Throwable th4) {
                    th3.addSuppressed(th4);
                }
            }
            throw th3;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [com.android.support.CloudTtsClient-IA] */
    /* JADX WARN: Type inference failed for: r0v4 */
    /* JADX WARN: Type inference failed for: r0v7 */
    private StoreResult storeAudio(File file, byte[] bArr) throws Throwable {
        File fileCreateTempFile;
        AudioFile audioFile = 0;
        audioFile = null;
        audioFile = 0;
        AudioFile audioFile2 = null;
        try {
            try {
                fileCreateTempFile = File.createTempFile(file.getName() + "-", ".tmp", this.cacheDirectory);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(fileCreateTempFile);
                    try {
                        fileOutputStream.write(bArr);
                        fileOutputStream.flush();
                        fileOutputStream.getFD().sync();
                        fileOutputStream.close();
                        if (!CloudTtsWire.isValidWav(fileCreateTempFile)) {
                            StoreResult storeResultFailure = StoreResult.failure(StoreStatus.INVALID_AUDIO);
                            closeQuietly(null);
                            if (fileCreateTempFile != null && fileCreateTempFile.exists()) {
                                fileCreateTempFile.delete();
                            }
                            return storeResultFailure;
                        }
                        synchronized (CACHE_FILE_LOCK) {
                            try {
                                if (!CloudTtsWire.isValidWav(file) && !moveIntoPlace(fileCreateTempFile, file)) {
                                    StoreResult storeResultFailure2 = StoreResult.failure(StoreStatus.CACHE_IO);
                                    closeQuietly(null);
                                    if (fileCreateTempFile != null && fileCreateTempFile.exists()) {
                                        fileCreateTempFile.delete();
                                    }
                                    return storeResultFailure2;
                                }
                                if (!CloudTtsWire.isValidWav(file)) {
                                    StoreResult storeResultFailure3 = StoreResult.failure(StoreStatus.INVALID_AUDIO);
                                    closeQuietly(null);
                                    if (fileCreateTempFile != null && fileCreateTempFile.exists()) {
                                        fileCreateTempFile.delete();
                                    }
                                    return storeResultFailure3;
                                }
                                AudioFile audioFile3 = new AudioFile(file, CACHE_PINS.pin(file.getAbsolutePath()));
                                try {
                                    if (fileCreateTempFile.exists() && !fileCreateTempFile.delete() && fileCreateTempFile.exists()) {
                                        StoreResult storeResultFailure4 = StoreResult.failure(StoreStatus.CACHE_IO);
                                        closeQuietly(audioFile3);
                                        if (fileCreateTempFile != null && fileCreateTempFile.exists()) {
                                            fileCreateTempFile.delete();
                                        }
                                        return storeResultFailure4;
                                    }
                                    if (!cleanupCache(file)) {
                                        StoreResult storeResultFailure5 = StoreResult.failure(StoreStatus.CACHE_IO);
                                        closeQuietly(audioFile3);
                                        if (fileCreateTempFile != null && fileCreateTempFile.exists()) {
                                            fileCreateTempFile.delete();
                                        }
                                        return storeResultFailure5;
                                    }
                                    try {
                                        StoreResult storeResultSuccess = StoreResult.success(audioFile3);
                                        closeQuietly(null);
                                        if (fileCreateTempFile != null && fileCreateTempFile.exists()) {
                                            fileCreateTempFile.delete();
                                        }
                                        return storeResultSuccess;
                                    } catch (IOException | RuntimeException unused) {
                                        audioFile2 = audioFile3;
                                        StoreResult storeResultFailure6 = StoreResult.failure(StoreStatus.CACHE_IO);
                                        closeQuietly(audioFile2);
                                        if (fileCreateTempFile != null && fileCreateTempFile.exists()) {
                                            fileCreateTempFile.delete();
                                        }
                                        return storeResultFailure6;
                                    } catch (Throwable th) {
                                        th = th;
                                        audioFile = audioFile3;
                                        closeQuietly(audioFile);
                                        if (fileCreateTempFile != null && fileCreateTempFile.exists()) {
                                            fileCreateTempFile.delete();
                                        }
                                        throw th;
                                    }
                                } catch (Throwable th2) {
                                    th = th2;
                                    throw th;
                                }
                            } catch (Throwable th3) {
                                th = th3;
                            }
                        }
                    } catch (Throwable th4) {
                        try {
                            fileOutputStream.close();
                        } catch (Throwable th5) {
                            th4.addSuppressed(th5);
                        }
                        throw th4;
                    }
                } catch (IOException | RuntimeException unused2) {
                }
            } catch (Throwable th6) {
                th = th6;
            }
        } catch (IOException | RuntimeException unused3) {
            fileCreateTempFile = null;
        } catch (Throwable th7) {
            th = th7;
            fileCreateTempFile = null;
        }
    }

    private boolean moveIntoPlace(File file, File file2) throws IOException {
        try {
            try {
                Files.move(file.toPath(), file2.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException | RuntimeException unused) {
                return CloudTtsWire.isValidWav(file2);
            }
        } catch (AtomicMoveNotSupportedException unused2) {
            Files.move(file.toPath(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException | RuntimeException unused3) {
            return CloudTtsWire.isValidWav(file2);
        }
    }

    private boolean cleanupCache(File file) {
        File[] fileArrListFiles = this.cacheDirectory.listFiles(new FilenameFilter() { // from class: com.android.support.CloudTtsClient$$ExternalSyntheticLambda3
            @Override // java.io.FilenameFilter
            public final boolean accept(File file2, String str) {
                return str.endsWith(".wav");
            }
        });
        if (fileArrListFiles == null) {
            return false;
        }
        ArrayList arrayList = new ArrayList(fileArrListFiles.length);
        HashMap map = new HashMap();
        for (File file2 : fileArrListFiles) {
            String name = file2.getName();
            arrayList.add(new CloudTtsWire.CacheEntry(name, file2.length(), file2.lastModified(), (name.equals(file.getName()) || CACHE_PINS.isPinned(file2.getAbsolutePath())) ? false : true));
            map.put(name, file2);
        }
        Iterator<CloudTtsWire.CacheEntry> it = CloudTtsWire.planCacheDeletions(arrayList).iterator();
        while (it.hasNext()) {
            File file3 = (File) map.get(it.next().getIdentifier());
            if (file3 != null && file3.exists() && !file3.delete() && file3.exists()) {
                return false;
            }
        }
        return true;
    }

    private Failure readServerFailure(ResponseBody responseBody) throws IOException {
        if (responseBody == null) {
            return serverFailure(null, null);
        }
        try {
            InputStream inputStreamByteStream = responseBody.byteStream();
            try {
                byte[] bounded = readBounded(inputStreamByteStream, 16384);
                if (inputStreamByteStream != null) {
                    inputStreamByteStream.close();
                }
                if (bounded == null) {
                    return serverFailure(null, null);
                }
                try {
                    JSONObject jSONObject = new JSONObject(new String(bounded, StandardCharsets.UTF_8));
                    JSONObject jSONObjectOptJSONObject = jSONObject.optJSONObject("error");
                    if (jSONObjectOptJSONObject != null) {
                        jSONObject = jSONObjectOptJSONObject;
                    }
                    return serverFailure(safeJsonScalar(jSONObject, "code", 64), safeJsonScalar(jSONObject, "message", 256));
                } catch (JSONException unused) {
                    return serverFailure(null, null);
                }
            } finally {
            }
        } catch (IOException unused2) {
            return serverFailure(null, null);
        }
    }

    private Failure closedFailureOr(FailureKind failureKind, String str) {
        Failure failure;
        synchronized (this.lifecycleLock) {
            if (this.closed) {
                failure = failure(FailureKind.CLOSED, "TTS client is closed");
            } else {
                failure = failure(failureKind, str);
            }
        }
        return failure;
    }

    private boolean ensureCacheDirectory() {
        return CloudTtsWire.ensureDirectory(this.cacheDirectory);
    }

    private static byte[] readBounded(InputStream inputStream, int i) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(Math.min(i, 8192));
        byte[] bArr = new byte[8192];
        int i2 = 0;
        while (true) {
            int i3 = inputStream.read(bArr);
            if (i3 == -1) {
                return byteArrayOutputStream.toByteArray();
            }
            if (i3 != 0) {
                if (i3 > i - i2) {
                    return null;
                }
                byteArrayOutputStream.write(bArr, 0, i3);
                i2 += i3;
            }
        }
    }

    private static HttpUrl parseEndpoint(String str) {
        if (str == null) {
            throw new IllegalArgumentException("endpoint must not be null");
        }
        try {
            HttpUrl httpUrl = HttpUrl.get(str);
            String strScheme = httpUrl.scheme();
            if ("http".equals(strScheme) || "https".equals(strScheme)) {
                return httpUrl;
            }
            throw new IllegalArgumentException("endpoint must use http or https");
        } catch (IllegalArgumentException unused) {
            throw new IllegalArgumentException("endpoint must be a valid HTTP URL");
        }
    }

    private static String safeJsonScalar(JSONObject jSONObject, String str, int i) {
        Object objOpt = jSONObject.opt(str);
        if (objOpt == null || objOpt == JSONObject.NULL || !((objOpt instanceof String) || (objOpt instanceof Number) || (objOpt instanceof Boolean))) {
            return null;
        }
        String strValueOf = String.valueOf(objOpt);
        StringBuilder sb = new StringBuilder(Math.min(strValueOf.length(), i));
        for (int i2 = 0; i2 < strValueOf.length() && sb.length() < i; i2++) {
            char cCharAt = strValueOf.charAt(i2);
            if (cCharAt >= ' ' && cCharAt != 127) {
                sb.append(cCharAt);
            }
        }
        if (sb.length() == 0) {
            return null;
        }
        return sb.toString();
    }

    private static Failure serverFailure(String str, String str2) {
        FailureKind failureKind = FailureKind.SERVER;
        if (str2 == null) {
            str2 = "TTS server rejected the request";
        }
        return new Failure(failureKind, str, str2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public static Failure failure(FailureKind failureKind, String str) {
        return new Failure(failureKind, null, str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void deliver(Delivery delivery) {
        if (delivery.audioFile != null) {
            try {
                delivery.callback.onSuccess(delivery.audioFile);
            } catch (RuntimeException unused) {
            } catch (Throwable th) {
                delivery.audioFile.close();
                throw th;
            }
            delivery.audioFile.close();
            return;
        }
        try {
            delivery.callback.onFailure(delivery.failure);
        } catch (RuntimeException unused2) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void closeQuietly(AudioFile audioFile) {
        if (audioFile != null) {
            audioFile.close();
        }
    }

    private static final class StoreResult {
        final AudioFile audioFile;
        final StoreStatus status;

        private StoreResult(StoreStatus storeStatus, AudioFile audioFile) {
            this.status = storeStatus;
            this.audioFile = audioFile;
        }

        static StoreResult success(AudioFile audioFile) {
            return new StoreResult(StoreStatus.SUCCESS, audioFile);
        }

        static StoreResult failure(StoreStatus storeStatus) {
            return new StoreResult(storeStatus, null);
        }
    }

    private static final class Delivery {
        final AudioFile audioFile;
        final Callback callback;
        final Failure failure;

        private Delivery(Callback callback, AudioFile audioFile, Failure failure) {
            this.callback = callback;
            this.audioFile = audioFile;
            this.failure = failure;
        }

        static Delivery success(Callback callback, AudioFile audioFile) {
            return new Delivery(callback, audioFile, null);
        }

        static Delivery failure(Callback callback, Failure failure) {
            return new Delivery(callback, null, failure);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    final class Operation implements RequestHandle {
        private Call call;
        private Callback callback;
        private boolean cancelled;
        private boolean done;
        private final long operationGeneration;

        Operation(Callback callback, long j) {
            this.callback = callback;
            this.operationGeneration = j;
        }

        @Override // com.android.support.CloudTtsClient.RequestHandle
        public void cancel() {
            synchronized (CloudTtsClient.this.lifecycleLock) {
                if (this.done) {
                    return;
                }
                this.cancelled = true;
                Call call = this.call;
                Delivery deliveryClaimFailureLocked = claimFailureLocked(CloudTtsClient.failure(FailureKind.CANCELLED, "TTS request was cancelled"));
                if (call != null) {
                    try {
                        call.cancel();
                    } catch (RuntimeException unused) {
                    }
                }
                CloudTtsClient.deliver(deliveryClaimFailureLocked);
            }
        }

        @Override // com.android.support.CloudTtsClient.RequestHandle
        public boolean isDone() {
            boolean z;
            synchronized (CloudTtsClient.this.lifecycleLock) {
                z = this.done;
            }
            return z;
        }

        boolean isActive() {
            boolean z;
            synchronized (CloudTtsClient.this.lifecycleLock) {
                z = (this.done || CloudTtsClient.this.closed || this.operationGeneration != CloudTtsClient.this.generation) ? false : true;
            }
            return z;
        }

        boolean attachCall(Call call) {
            synchronized (CloudTtsClient.this.lifecycleLock) {
                if (!this.done && !CloudTtsClient.this.closed && this.operationGeneration == CloudTtsClient.this.generation) {
                    this.call = call;
                    return true;
                }
                return false;
            }
        }

        Failure cancelledOrClosedFailure() {
            synchronized (CloudTtsClient.this.lifecycleLock) {
                if (!CloudTtsClient.this.closed && this.operationGeneration == CloudTtsClient.this.generation) {
                    if (this.cancelled) {
                        return CloudTtsClient.failure(FailureKind.CANCELLED, "TTS request was cancelled");
                    }
                    return CloudTtsClient.failure(FailureKind.NETWORK, "TTS network request failed");
                }
                return CloudTtsClient.failure(FailureKind.CLOSED, "TTS client is closed");
            }
        }

        void succeed(AudioFile audioFile) {
            Delivery deliveryClaimFailureLocked;
            synchronized (CloudTtsClient.this.lifecycleLock) {
                if (this.done) {
                    deliveryClaimFailureLocked = null;
                } else if (CloudTtsClient.this.closed || this.operationGeneration != CloudTtsClient.this.generation) {
                    deliveryClaimFailureLocked = claimFailureLocked(CloudTtsClient.failure(FailureKind.CLOSED, "TTS client is closed"));
                } else {
                    deliveryClaimFailureLocked = claimSuccessLocked(audioFile);
                }
            }
            if (deliveryClaimFailureLocked == null || deliveryClaimFailureLocked.audioFile == null) {
                CloudTtsClient.closeQuietly(audioFile);
            }
            if (deliveryClaimFailureLocked != null) {
                CloudTtsClient.deliver(deliveryClaimFailureLocked);
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void finish(Failure failure) {
            Delivery deliveryClaimFailureLocked;
            synchronized (CloudTtsClient.this.lifecycleLock) {
                deliveryClaimFailureLocked = claimFailureLocked(failure);
            }
            if (deliveryClaimFailureLocked != null) {
                CloudTtsClient.deliver(deliveryClaimFailureLocked);
            }
        }

        private Delivery claimSuccessLocked(AudioFile audioFile) {
            return claimTerminalLocked(audioFile, null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Delivery claimFailureLocked(Failure failure) {
            return claimTerminalLocked(null, failure);
        }

        private Delivery claimTerminalLocked(AudioFile audioFile, Failure failure) {
            if (this.done) {
                return null;
            }
            this.done = true;
            CloudTtsClient.this.activeOperations.remove(this);
            Callback callback = this.callback;
            this.callback = null;
            this.call = null;
            if (audioFile != null) {
                return Delivery.success(callback, audioFile);
            }
            return Delivery.failure(callback, failure);
        }
    }

    private static final class CloudThreadFactory implements ThreadFactory {
        private final AtomicInteger nextId;

        private CloudThreadFactory() {
            this.nextId = new AtomicInteger(1);
        }

        @Override // java.util.concurrent.ThreadFactory
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "net-http-" + this.nextId.getAndIncrement());
            thread.setDaemon(true);
            return thread;
        }
    }
}
