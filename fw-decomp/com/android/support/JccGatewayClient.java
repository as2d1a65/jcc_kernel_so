package com.android.support;

import android.util.Log;
import com.android.support.JccExactFields;
import com.android.support.JccSessionState;
import com.android.support.JccSyncCursor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLHandshakeException;
import okhttp3.Call;
import okhttp3.Dns;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class JccGatewayClient {
    private static final int BUFFER_BYTES = 8192;
    private static final int MAX_JSON_BYTES = 65536;
    static final String ORIGIN_FALLBACK_ADDRESS = "43.226.61.21";
    private static final long VERSION_CALL_TIMEOUT_SECONDS = 8;
    private final OkHttpClient httpClient;
    private final JccSessionState sessions = JccSessionState.shared();
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final JccGatewayClient SHARED = new JccGatewayClient();
    private static final ExecutorService PRELOGIN_REPORTER = Executors.newSingleThreadExecutor(ThreadNaming.factory(ThreadNaming.WORKER));

    public static JccGatewayClient shared() {
        return SHARED;
    }

    private JccGatewayClient() {
        OkHttpClient.Builder builderRetryOnConnectionFailure = new OkHttpClient.Builder().dispatcher(ThreadNaming.dispatcher()).dns(JccOriginDns.SHARED).connectTimeout(VERSION_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS).readTimeout(15L, TimeUnit.SECONDS).writeTimeout(15L, TimeUnit.SECONDS).callTimeout(20L, TimeUnit.SECONDS).followRedirects(false).followSslRedirects(false).retryOnConnectionFailure(false);
        builderRetryOnConnectionFailure.socketFactory(JccNetworkRoute.socketFactory());
        this.httpClient = builderRetryOnConnectionFailure.build();
    }

    static final class OriginFallbackDns implements Dns {
        private final JccOriginDns resolver;

        OriginFallbackDns(Dns dns) {
            if (dns == null) {
                throw new IllegalArgumentException("system == null");
            }
            this.resolver = new JccOriginDns(dns);
        }

        @Override // okhttp3.Dns
        public List<InetAddress> lookup(String str) throws UnknownHostException {
            return this.resolver.lookup(str);
        }
    }

    public void reportPreloginFailure(final String str, String str2, int i) {
        try {
            final Request requestPreloginReportRequest = preloginReportRequest(str, str2, i);
            PRELOGIN_REPORTER.execute(new Runnable() { // from class: com.android.support.JccGatewayClient$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.m1220x5370272(requestPreloginReportRequest, str);
                }
            });
        } catch (RuntimeException unused) {
        }
    }

    /* renamed from: lambda$reportPreloginFailure$0$com-android-support-JccGatewayClient, reason: not valid java name */
    /* synthetic */ void m1220x5370272(Request request, String str) {
        try {
            Response responseExecute = this.httpClient.newCall(request).execute();
            try {
                Log.w("JccVerify", "prelogin_report trace=" + str + " http_status=" + responseExecute.code());
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
        } catch (IOException | RuntimeException unused) {
            Log.w("JccVerify", "prelogin_report trace=" + str + " result=not_delivered");
        }
    }

    static Request preloginReportRequest(String str, String str2, int i) {
        if (str == null || !str.matches("[A-Z0-9]{8}") || !VerificationTrace.isReportableFailure(str2) || i <= 0) {
            throw new IllegalArgumentException("invalid prelogin diagnostic");
        }
        JccNetworkPolicy.requireFixedApiUrl(JccNetworkPolicy.VERSION_URL, "/v2/api/version");
        return new Request.Builder().url(JccNetworkPolicy.VERSION_URL).header("Accept", "application/json").header("X-JCC-Trace", str).header("X-JCC-Client-Stage", "PRELOGIN").header("X-JCC-Client-Code", VerificationTrace.legacyReportCode(str2)).header("X-JCC-Client-Version", Integer.toString(i)).get().build();
    }

    public AnnouncementContent fetchAnnouncement(Cancellation cancellation) throws IOException {
        return AnnouncementContent.parse(executeJson("GET", JccNetworkPolicy.ANNOUNCEMENT_URL, "/v2/api/announcement", null, null, cancellation));
    }

    public VersionPolicy fetchVersionPolicy(Cancellation cancellation) throws IOException {
        JSONObject jSONObjectExecuteJson = executeJson("GET", JccNetworkPolicy.VERSION_URL, "/v2/api/version", null, null, cancellation);
        requireExactFields(jSONObjectExecuteJson, "algorithm", "key_id", "policy", "signature");
        if (!"ES256-P1363".equals(jSONObjectExecuteJson.optString("algorithm"))) {
            throw new JccException("VERSION_ALGORITHM_INVALID", 502);
        }
        if (!JccReleaseConfig.VERSION_POLICY_KEY_ID.isEmpty() && !JccReleaseConfig.VERSION_POLICY_PUBLIC_KEY_SPKI_BASE64URL.isEmpty()) {
            if (!JccReleaseConfig.VERSION_POLICY_KEY_ID.equals(jSONObjectExecuteJson.optString("key_id"))) {
                throw new JccException("VERSION_KEY_ID_INVALID", 502);
            }
            byte[] bArrDecodeBase64Url = decodeBase64Url(jSONObjectExecuteJson.optString("policy"), 65536, "VERSION_POLICY_INVALID");
            if (!JccVersionPolicy.verify(bArrDecodeBase64Url, jSONObjectExecuteJson.optString("signature"), loadVersionPublicKey())) {
                throw new JccException("VERSION_SIGNATURE_INVALID", 502);
            }
            try {
                JSONObject jSONObject = new JSONObject(new String(bArrDecodeBase64Url, StandardCharsets.UTF_8));
                requireExactFields(jSONObject, "revision", "minimum_version", "latest_version", "download_url", "published_at", "expires_at");
                long jCorrectedEpochSeconds = ServerClockOffset.shared().correctedEpochSeconds(System.currentTimeMillis() / 1000);
                long jOptLong = jSONObject.optLong("revision", -1L);
                long jOptLong2 = jSONObject.optLong("published_at", -1L);
                long jOptLong3 = jSONObject.optLong("expires_at", -1L);
                String strOptString = jSONObject.optString("minimum_version", "");
                String strOptString2 = jSONObject.optString("latest_version", "");
                String strOptString3 = jSONObject.optString("download_url", "");
                if (jOptLong > 0 && jOptLong2 > 0 && jOptLong2 <= jCorrectedEpochSeconds && jOptLong3 > jCorrectedEpochSeconds && jOptLong3 > jOptLong2) {
                    try {
                        if (JccVersionPolicy.compare(strOptString, strOptString2) <= 0) {
                            JccNetworkPolicy.requireFixedOriginUrl(strOptString3);
                            return new VersionPolicy(jOptLong, strOptString, strOptString2, strOptString3, jOptLong2, jOptLong3);
                        }
                    } catch (IllegalArgumentException unused) {
                        throw new JccException("VERSION_POLICY_INVALID", 502);
                    }
                }
                throw new IllegalArgumentException("invalid policy values");
            } catch (JSONException unused2) {
                throw new JccException("VERSION_POLICY_INVALID", 502);
            }
        }
        throw new JccException("VERSION_KEY_UNCONFIGURED", 503);
    }

    public LoginResult login(byte[] bArr, Cancellation cancellation) throws Exception {
        return login(bArr, cancellation, null);
    }

    public LoginResult login(byte[] bArr, Cancellation cancellation, String str) throws Exception {
        return login(bArr, cancellation, str, this.sessions.snapshot());
    }

    LoginResult login(byte[] bArr, Cancellation cancellation, String str, JccSessionState.Snapshot snapshot) throws Exception {
        byte[] bytes = bArr;
        if (bytes == null || bytes.length == 0 || bytes.length > 4096) {
            throw new JccException("NATIVE_LOGIN_REQUEST_INVALID", 400);
        }
        GameRegion gameRegion = snapshot.region;
        long j = snapshot.generation;
        if (gameRegion != GameRegionState.current()) {
            throw new JccException("SESSION_REGION_CHANGED", 409);
        }
        if (gameRegion != GameRegion.CN) {
            JSONObject jSONObject = new JSONObject(new String(bytes, StandardCharsets.UTF_8));
            jSONObject.put("game_region", gameRegion.id);
            bytes = jSONObject.toString().getBytes(StandardCharsets.UTF_8);
        }
        JSONObject jSONObjectExecuteJsonBytes = executeJsonBytes("POST", JccNetworkPolicy.SESSION_LOGIN_URL, "/v2/api/session/login", bytes, null, cancellation, str);
        requireExactFields(jSONObjectExecuteJsonBytes, "access_token", "token_type", "expires_at");
        String strOptString = jSONObjectExecuteJsonBytes.optString("access_token", "");
        long jOptLong = jSONObjectExecuteJsonBytes.optLong("expires_at", -1L);
        if (!JccSessionResponsePolicy.isAcceptable(jSONObjectExecuteJsonBytes.optString("token_type"), jOptLong, ServerClockOffset.shared().correctedEpochSeconds(System.currentTimeMillis() / 1000))) {
            throw new JccException("SESSION_RESPONSE_INVALID", 502);
        }
        try {
            long jInstallIfCurrent = this.sessions.installIfCurrent(strOptString, jOptLong, gameRegion, j);
            if (jInstallIfCurrent == 0) {
                throw new JccException("SESSION_REGION_CHANGED", 409);
            }
            return new LoginResult(jOptLong, jInstallIfCurrent);
        } catch (IllegalArgumentException unused) {
            throw new JccException("SESSION_RESPONSE_INVALID", 502);
        }
    }

    public LoginResult refresh(Cancellation cancellation) throws Exception {
        return refresh(cancellation, this.sessions.snapshot());
    }

    LoginResult refresh(Cancellation cancellation, JccSessionState.Snapshot snapshot) throws Exception {
        GameRegion gameRegion = snapshot.region;
        long j = snapshot.generation;
        if (!snapshot.present() || this.sessions.generation() != j) {
            throw new JccException("SESSION_REGION_CHANGED", 409);
        }
        String str = snapshot.token;
        JSONObject jSONObjectExecuteJson = executeJson("POST", JccNetworkPolicy.SESSION_CHALLENGE_URL, "/v2/api/session/challenge", new JSONObject(), str, cancellation);
        requireExactFields(jSONObjectExecuteJson, "challenge_id", "expires_at", "message");
        long jCorrectedEpochSeconds = ServerClockOffset.shared().correctedEpochSeconds(System.currentTimeMillis() / 1000);
        long jOptLong = jSONObjectExecuteJson.optLong("expires_at", -1L);
        if (jOptLong <= jCorrectedEpochSeconds || jOptLong > jCorrectedEpochSeconds + 60) {
            throw new JccException("REFRESH_CHALLENGE_INVALID", 502);
        }
        byte[] bArrDecodeBase64Url = decodeBase64Url(jSONObjectExecuteJson.optString("message"), 4096, "REFRESH_CHALLENGE_INVALID");
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("challenge_id", jSONObjectExecuteJson.optString("challenge_id"));
            jSONObject.put("signature", JccDeviceIdentity.shared().signBase64Url(bArrDecodeBase64Url));
            JSONObject jSONObjectExecuteJson2 = executeJson("POST", JccNetworkPolicy.SESSION_REFRESH_URL, "/v2/api/session/refresh", jSONObject, str, cancellation);
            requireExactFields(jSONObjectExecuteJson2, "access_token", "token_type", "expires_at");
            long jOptLong2 = jSONObjectExecuteJson2.optLong("expires_at", -1L);
            if (!JccSessionResponsePolicy.isAcceptable(jSONObjectExecuteJson2.optString("token_type"), jOptLong2, ServerClockOffset.shared().correctedEpochSeconds(System.currentTimeMillis() / 1000))) {
                throw new JccException("SESSION_RESPONSE_INVALID", 502);
            }
            try {
                long jInstallIfCurrent = this.sessions.installIfCurrent(jSONObjectExecuteJson2.optString("access_token"), jOptLong2, gameRegion, j);
                if (jInstallIfCurrent == 0) {
                    throw new JccException("SESSION_REGION_CHANGED", 409);
                }
                return new LoginResult(jOptLong2, jInstallIfCurrent);
            } catch (IllegalArgumentException unused) {
                throw new JccException("SESSION_RESPONSE_INVALID", 502);
            }
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    public JSONObject presence(long j, long j2, boolean z, Cancellation cancellation) throws JSONException, IOException {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("generation", j);
            jSONObject.put("seq", j2);
            jSONObject.put("online", z);
            JSONObject jSONObjectExecuteJson = executeJson("POST", JccNetworkPolicy.PRESENCE_URL, "/v2/api/presence", jSONObject, requireBearer(), cancellation);
            requireExactFields(jSONObjectExecuteJson, "server_time", "accepted_seq", "online_count");
            return jSONObjectExecuteJson;
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    public JSONObject peerSync(JccSyncCursor.Snapshot snapshot, Cancellation cancellation) throws JSONException, IOException {
        if (snapshot == null) {
            throw new IllegalArgumentException("peer snapshot is required");
        }
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("operation", snapshot.online() ? "publish" : "leave");
            jSONObject.put("match_hash", snapshot.matchHash());
            if (snapshot.online()) {
                jSONObject.put("seat", snapshot.seat());
            }
            jSONObject.put("generation", snapshot.generation());
            jSONObject.put("seq", snapshot.sequence());
            JSONObject jSONObjectExecuteJson = executeJson("POST", JccNetworkPolicy.PEER_SYNC_URL, "/v2/api/peer-sync", jSONObject, requireBearer(), cancellation);
            requireExactFields(jSONObjectExecuteJson, "peers", "accepted_seq");
            return jSONObjectExecuteJson;
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    public ShareStartResult shareStart(long j, Cancellation cancellation) throws JSONException, IOException {
        if (j <= 0) {
            throw new IllegalArgumentException("generation is required");
        }
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("generation", j);
            JSONObject jSONObjectExecuteJson = executeJson("POST", JccNetworkPolicy.SHARE_START_URL, "/v2/api/share/start", jSONObject, requireBearer(), cancellation);
            requireExactFields(jSONObjectExecuteJson, "room", "share_url", "expires_at");
            String strOptString = jSONObjectExecuteJson.optString("room", "");
            String strOptString2 = jSONObjectExecuteJson.optString("share_url", "");
            long jOptLong = jSONObjectExecuteJson.optLong("expires_at", -1L);
            if (!strOptString.matches("XF[0-9]{3}") || !"https://jcc.zhuzhufaka.cn/share/".equals(strOptString2) || jOptLong <= ServerClockOffset.shared().correctedEpochSeconds(System.currentTimeMillis() / 1000)) {
                throw new JccException("SHARE_RESPONSE_INVALID", 502);
            }
            return new ShareStartResult(strOptString, strOptString2, jOptLong);
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    public long sharePublish(String str, long j, long j2, JSONObject jSONObject, Cancellation cancellation) throws JSONException, IOException {
        requireShareMutation(str, j, j2);
        if (jSONObject == null) {
            throw new IllegalArgumentException("snapshot is required");
        }
        JSONObject jSONObject2 = new JSONObject();
        try {
            jSONObject2.put("room", str);
            jSONObject2.put("generation", j);
            jSONObject2.put("seq", j2);
            jSONObject2.put("snapshot", jSONObject);
            JSONObject jSONObjectExecuteJson = executeJson("POST", JccNetworkPolicy.SHARE_PUBLISH_URL, "/v2/api/share/publish", jSONObject2, requireBearer(), cancellation);
            requireExactFields(jSONObjectExecuteJson, "accepted_seq", "expires_at");
            if (jSONObjectExecuteJson.optLong("accepted_seq", -1L) != j2 || jSONObjectExecuteJson.optLong("expires_at", -1L) <= ServerClockOffset.shared().correctedEpochSeconds(System.currentTimeMillis() / 1000)) {
                throw new JccException("SHARE_RESPONSE_INVALID", 502);
            }
            return j2;
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    public void shareStop(String str, long j, long j2, Cancellation cancellation) throws JSONException, IOException {
        requireShareMutation(str, j, j2);
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("room", str);
            jSONObject.put("generation", j);
            jSONObject.put("seq", j2);
            executeNoContent(JccNetworkPolicy.SHARE_STOP_URL, "/v2/api/share/stop", jSONObject, requireBearer(), cancellation);
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    public void logout(String str, Cancellation cancellation) throws IOException {
        executeNoContent(JccNetworkPolicy.SESSION_LOGOUT_URL, "/v2/api/session/logout", str, cancellation);
    }

    public void unbind(String str, Cancellation cancellation) throws IOException {
        executeNoContent(JccNetworkPolicy.SESSION_UNBIND_URL, "/v2/api/session/unbind", str, cancellation);
    }

    byte[] computeSpecified(byte[] bArr) throws IOException {
        return executeJsonBytes("POST", JccNetworkPolicy.apiUrl(false, JccNetworkPolicy.JCC_API_PREFIX, "/v2/api/compute/specified"), "/v2/api/compute/specified", bArr, null, null).toString().getBytes(StandardCharsets.UTF_8);
    }

    private JSONObject executeJson(String str, String str2, String str3, JSONObject jSONObject, String str4, Cancellation cancellation) throws IOException {
        return executeJsonBytes(str, str2, str3, jSONObject == null ? null : jSONObject.toString().getBytes(StandardCharsets.UTF_8), str4, cancellation);
    }

    private JSONObject executeJsonBytes(String str, String str2, String str3, byte[] bArr, String str4, Cancellation cancellation) throws IOException {
        return executeJsonBytes(str, str2, str3, bArr, str4, cancellation, null);
    }

    private JSONObject executeJsonBytes(String str, String str2, String str3, byte[] bArr, String str4, Cancellation cancellation, String str5) throws IOException {
        JccNetworkPolicy.requireFixedApiUrl(str2, str3);
        Request.Builder builderHeader = new Request.Builder().url(str2).header("Accept", "application/json");
        if (!"/v2/api/compute/specified".equals(str3)) {
            builderHeader.header("Accept-Encoding", "identity");
        }
        if (str5 != null && str5.matches("[A-Z0-9]{8}")) {
            builderHeader.header("X-JCC-Trace", str5);
        }
        if (str4 != null) {
            builderHeader.header("Authorization", "Bearer " + str4);
        }
        if ("POST".equals(str)) {
            if (bArr != null && bArr.length != 0) {
                if (bArr.length <= ("/v2/api/compute/specified".equals(str3) ? 524288 : 65536)) {
                    builderHeader.post(RequestBody.create(bArr, JSON));
                }
            }
            throw new JccException("INVALID_REQUEST", 400);
        }
        if ("GET".equals(str)) {
            builderHeader.get();
        } else {
            throw new IllegalArgumentException("unsupported gateway method");
        }
        Call callNewCall = this.httpClient.newCall(builderHeader.build());
        if ("/v2/api/compute/specified".equals(str3)) {
            callNewCall.timeout().timeout(2500L, TimeUnit.MILLISECONDS);
        }
        if ("/v2/api/version".equals(str3) || "/v2/api/announcement".equals(str3)) {
            callNewCall.timeout().timeout(VERSION_CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
        if (cancellation == null) {
            cancellation = new Cancellation();
        }
        try {
            if (!cancellation.attach(callNewCall)) {
                throw new JccException("REQUEST_CANCELLED", 499);
            }
            try {
                Response responseExecute = callNewCall.execute();
                try {
                    learnServerClock(responseExecute);
                    rejectRedirect(responseExecute.code());
                    byte[] bounded = readBounded(responseExecute.body(), "/v2/api/compute/specified".equals(str3) ? 786432 : 65536);
                    if (!responseExecute.isSuccessful()) {
                        throw parseServerError(responseExecute.code(), bounded);
                    }
                    if (bounded == null || bounded.length == 0) {
                        throw new JccException("EMPTY_RESPONSE", 502);
                    }
                    try {
                        JSONObject jSONObject = new JSONObject(new String(bounded, StandardCharsets.UTF_8));
                        if (responseExecute != null) {
                            responseExecute.close();
                        }
                        return jSONObject;
                    } catch (JSONException unused) {
                        throw new JccException("INVALID_RESPONSE", 502);
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
            } catch (JccException e) {
                throw e;
            } catch (IOException e2) {
                throw classifyCallFailure(callNewCall.getCanceled(), e2);
            }
        } finally {
            cancellation.detach(callNewCall);
        }
    }

    private void executeNoContent(String str, String str2, String str3, Cancellation cancellation) throws IOException {
        executeNoContent(str, str2, new JSONObject(), str3, cancellation);
    }

    private void executeNoContent(String str, String str2, JSONObject jSONObject, String str3, Cancellation cancellation) throws IOException {
        JccNetworkPolicy.requireFixedApiUrl(str, str2);
        requireText(str3, 256, "AUTH_REQUIRED");
        Call callNewCall = this.httpClient.newCall(new Request.Builder().url(str).header("Accept", "application/json").header("Accept-Encoding", "identity").header("Authorization", "Bearer " + str3).post(RequestBody.create(jSONObject.toString(), JSON)).build());
        if (cancellation == null) {
            cancellation = new Cancellation();
        }
        if (!cancellation.attach(callNewCall)) {
            throw new JccException("REQUEST_CANCELLED", 499);
        }
        executeNoContentCall(callNewCall, cancellation);
    }

    static void executeNoContentCall(Call call, Cancellation cancellation) throws IOException {
        try {
            try {
                Response responseExecute = call.execute();
                try {
                    learnServerClock(responseExecute);
                    rejectRedirect(responseExecute.code());
                    if (!responseExecute.isSuccessful()) {
                        throw parseServerError(responseExecute.code(), readBounded(responseExecute.body(), 65536));
                    }
                    if (responseExecute.body() != null && responseExecute.body().contentLength() > 0) {
                        readBounded(responseExecute.body(), 65536);
                    }
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
            } finally {
                cancellation.detach(call);
            }
        } catch (JccException e) {
            throw e;
        } catch (IOException e2) {
            throw classifyCallFailure(call.getCanceled(), e2);
        }
    }

    private static byte[] readBounded(ResponseBody responseBody, int i) throws IOException {
        if (responseBody == null) {
            return null;
        }
        long jContentLength = responseBody.contentLength();
        if (jContentLength > i) {
            throw new JccException("RESPONSE_TOO_LARGE", 502);
        }
        InputStream inputStreamByteStream = responseBody.byteStream();
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(jContentLength >= 0 ? (int) Math.min(jContentLength, 8192L) : 8192);
            byte[] bArr = new byte[8192];
            int i2 = 0;
            while (true) {
                int i3 = inputStreamByteStream.read(bArr);
                if (i3 == -1) {
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    if (inputStreamByteStream != null) {
                        inputStreamByteStream.close();
                    }
                    return byteArray;
                }
                if (i3 != 0) {
                    if (i3 > i - i2) {
                        throw new JccException("RESPONSE_TOO_LARGE", 502);
                    }
                    byteArrayOutputStream.write(bArr, 0, i3);
                    i2 += i3;
                }
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
    }

    private static void learnServerClock(Response response) {
        Date date;
        if (response == null || (date = response.headers().getDate("Date")) == null) {
            return;
        }
        ServerClockOffset.shared().record(date.getTime() / 1000, System.currentTimeMillis() / 1000);
    }

    private static JccException parseServerError(int i, byte[] bArr) {
        if (bArr != null && bArr.length > 0) {
            try {
                JSONObject jSONObjectOptJSONObject = new JSONObject(new String(bArr, StandardCharsets.UTF_8)).optJSONObject("error");
                if (jSONObjectOptJSONObject != null) {
                    String strOptString = jSONObjectOptJSONObject.optString("code", "");
                    if (strOptString.matches("[A-Z0-9_]{1,64}")) {
                        return new JccException(strOptString, i);
                    }
                }
            } catch (JSONException unused) {
            }
        }
        return new JccException("GATEWAY_REJECTED", i);
    }

    static JccException classifyTransportFailure(IOException iOException) {
        if (iOException instanceof UnknownHostException) {
            return new JccException("NET_DNS", 0);
        }
        if (iOException instanceof SSLHandshakeException) {
            return new JccException("NET_TLS", 0);
        }
        if (iOException instanceof SocketTimeoutException) {
            return new JccException("GATEWAY_UNAVAILABLE", 503);
        }
        return new JccException("GATEWAY_UNAVAILABLE", 503);
    }

    static JccException classifyCallFailure(boolean z, IOException iOException) {
        if (z) {
            return new JccException("REQUEST_CANCELLED", 499);
        }
        return classifyTransportFailure(iOException);
    }

    private static void rejectRedirect(int i) throws JccException {
        if (JccNetworkPolicy.isRedirect(i)) {
            throw new JccException("REDIRECT_REJECTED", 502);
        }
    }

    private String requireBearer() throws JccException {
        String strBearerTokenNow = this.sessions.bearerTokenNow();
        if (strBearerTokenNow != null) {
            return strBearerTokenNow;
        }
        throw new JccException("AUTH_REQUIRED", 401);
    }

    private static void requireText(String str, int i, String str2) throws JccException {
        if (str == null || str.isEmpty() || str.getBytes(StandardCharsets.UTF_8).length > i) {
            throw new JccException(str2, 400);
        }
        for (int i2 = 0; i2 < str.length(); i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt < ' ' || cCharAt == 127) {
                throw new JccException(str2, 400);
            }
        }
    }

    private static void requireExactFields(final JSONObject jSONObject, String... strArr) throws JccException {
        int length = jSONObject.length();
        Objects.requireNonNull(jSONObject);
        if (!JccExactFields.matches(length, new JccExactFields.Lookup() { // from class: com.android.support.JccGatewayClient$$ExternalSyntheticLambda0
            @Override // com.android.support.JccExactFields.Lookup
            public final boolean has(String str) {
                return jSONObject.has(str);
            }
        }, strArr)) {
            throw new JccException("INVALID_RESPONSE", 502);
        }
    }

    private static void requireShareMutation(String str, long j, long j2) {
        if (str == null || !str.matches("XF[0-9]{3}") || j <= 0 || j2 <= 0) {
            throw new IllegalArgumentException("canonical share mutation is required");
        }
    }

    private static byte[] decodeBase64Url(String str, int i, String str2) throws JccException {
        if (str == null || str.isEmpty() || str.indexOf(61) >= 0) {
            throw new JccException(str2, 502);
        }
        try {
            byte[] bArrDecode = Base64.getUrlDecoder().decode(str);
            if (bArrDecode.length == 0 || bArrDecode.length > i) {
                throw new JccException(str2, 502);
            }
            return bArrDecode;
        } catch (IllegalArgumentException unused) {
            throw new JccException(str2, 502);
        }
    }

    private static PublicKey loadVersionPublicKey() throws JccException {
        try {
            return KeyFactory.getInstance("EC").generatePublic(new X509EncodedKeySpec(decodeBase64Url(JccReleaseConfig.VERSION_POLICY_PUBLIC_KEY_SPKI_BASE64URL, 1024, "VERSION_KEY_INVALID")));
        } catch (Exception unused) {
            throw new JccException("VERSION_KEY_INVALID", 503);
        }
    }

    public static final class Cancellation {
        private Call call;
        private boolean cancelled;

        synchronized boolean attach(Call call) {
            if (this.cancelled) {
                return false;
            }
            this.call = call;
            return true;
        }

        synchronized void detach(Call call) {
            if (this.call == call) {
                this.call = null;
            }
        }

        public synchronized void cancel() {
            this.cancelled = true;
            Call call = this.call;
            if (call != null) {
                call.cancel();
                this.call = null;
            }
        }

        public synchronized boolean isCancelled() {
            return this.cancelled;
        }
    }

    public static final class LoginResult {
        public final long expiresAtEpochSeconds;
        public final long sessionGeneration;

        LoginResult(long j, long j2) {
            this.expiresAtEpochSeconds = j;
            this.sessionGeneration = j2;
        }
    }

    public static final class ShareStartResult {
        public final long expiresAtEpochSeconds;
        public final String room;
        public final String shareUrl;

        ShareStartResult(String str, String str2, long j) {
            this.room = str;
            this.shareUrl = str2;
            this.expiresAtEpochSeconds = j;
        }
    }

    public static final class VersionPolicy {
        public final String downloadUrl;
        public final long expiresAt;
        public final String latestVersion;
        public final String minimumVersion;
        public final long publishedAt;
        public final long revision;

        VersionPolicy(long j, String str, String str2, String str3, long j2, long j3) {
            this.revision = j;
            this.minimumVersion = str;
            this.latestVersion = str2;
            this.downloadUrl = str3;
            this.publishedAt = j2;
            this.expiresAt = j3;
        }
    }

    public static final class JccException extends IOException {
        public final String code;
        public final int status;

        JccException(String str, int i) {
            super(str);
            this.code = str;
            this.status = i;
        }
    }
}
