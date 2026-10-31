package com.android.support;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ProtocolException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.Dns;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
public final class XfOnlineCountClient {
    static final String COUNT_HOST = "hdnz.ltebevrn.cn";
    private static final HttpUrl COUNT_URL = HttpUrl.get("https://hdnz.ltebevrn.cn/api.php?action=count");
    private static final String EDITION = "free";
    static final String FALLBACK_ADDRESS = "43.226.61.21";
    static final int MAX_ONLINE_COUNT = 1000000;
    static final int MAX_RESPONSE_BYTES = 8192;
    private final Transport transport;

    interface Transport {
        TransportResponse get(HttpUrl httpUrl) throws IOException;
    }

    static final class TransportResponse {
        final byte[] body;
        final int status;

        TransportResponse(int i, byte[] bArr) {
            this.status = i;
            this.body = bArr;
        }
    }

    public XfOnlineCountClient() {
        this(new OkHttpTransport());
    }

    XfOnlineCountClient(Transport transport) {
        if (transport == null) {
            throw new IllegalArgumentException("transport == null");
        }
        this.transport = transport;
    }

    public int fetchTotalOnline() throws IOException {
        TransportResponse transportResponse = this.transport.get(COUNT_URL);
        return parse(transportResponse.status, transportResponse.body);
    }

    public int reportOnline(String str) throws IOException {
        TransportResponse transportResponse = this.transport.get(presenceUrl("heartbeat", str));
        return parse(transportResponse.status, transportResponse.body);
    }

    public void reportOffline(String str) throws IOException {
        TransportResponse transportResponse = this.transport.get(presenceUrl("offline", str));
        if (transportResponse.status < 200 || transportResponse.status >= 300) {
            throw new ProtocolException("online offline HTTP " + transportResponse.status);
        }
    }

    static HttpUrl countUrl() {
        return COUNT_URL;
    }

    static HttpUrl presenceUrl(String str, String str2) {
        if (!XfOnlinePresenceId.isValid(str2)) {
            throw new IllegalArgumentException("presence id is not an opaque token");
        }
        return COUNT_URL.newBuilder().query(null).addQueryParameter("action", str).addQueryParameter("uid", str2).addQueryParameter("edition", EDITION).build();
    }

    static int parse(int i, byte[] bArr) throws IOException {
        if (i < 200 || i >= 300) {
            throw new ProtocolException("online count HTTP " + i);
        }
        if (bArr == null || bArr.length == 0 || bArr.length > 8192) {
            throw new ProtocolException("online count response size is invalid");
        }
        try {
            JSONObject jSONObject = new JSONObject(new String(bArr, StandardCharsets.UTF_8));
            if (jSONObject.optInt("code", Integer.MIN_VALUE) != 0) {
                throw new ProtocolException("online count service reported a failure");
            }
            Object objOpt = jSONObject.opt("online");
            if (!(objOpt instanceof Integer) && !(objOpt instanceof Long)) {
                throw new ProtocolException("online count field is missing");
            }
            long jLongValue = ((Number) objOpt).longValue();
            if (jLongValue < 0 || jLongValue > 1000000) {
                throw new ProtocolException("online count is out of range");
            }
            return (int) jLongValue;
        } catch (JSONException unused) {
            throw new ProtocolException("online count response is not JSON");
        }
    }

    static final class FallbackDns implements Dns {
        private final Dns system;

        FallbackDns(Dns dns) {
            if (dns == null) {
                throw new IllegalArgumentException("system == null");
            }
            this.system = dns;
        }

        @Override // okhttp3.Dns
        public List<InetAddress> lookup(String str) throws UnknownHostException {
            try {
                List<InetAddress> listLookup = this.system.lookup(str);
                if (!listLookup.isEmpty()) {
                    return listLookup;
                }
            } catch (UnknownHostException e) {
                if (!XfOnlineCountClient.COUNT_HOST.equals(str)) {
                    throw e;
                }
            }
            if (!XfOnlineCountClient.COUNT_HOST.equals(str)) {
                throw new UnknownHostException(str);
            }
            return Collections.singletonList(InetAddress.getByName(XfOnlineCountClient.FALLBACK_ADDRESS));
        }
    }

    private static final class OkHttpTransport implements Transport {
        private final OkHttpClient client;

        private OkHttpTransport() {
            this.client = new OkHttpClient.Builder().dispatcher(ThreadNaming.dispatcher()).followRedirects(false).followSslRedirects(false).dns(new FallbackDns(Dns.SYSTEM)).connectTimeout(6L, TimeUnit.SECONDS).readTimeout(6L, TimeUnit.SECONDS).callTimeout(10L, TimeUnit.SECONDS).build();
        }

        @Override // com.android.support.XfOnlineCountClient.Transport
        public TransportResponse get(HttpUrl httpUrl) throws IOException {
            Response responseExecute = this.client.newCall(new Request.Builder().url(httpUrl).header("Accept", "application/json").header("User-Agent", "XFjcc/OnlineCount-v1").get().build()).execute();
            try {
                ResponseBody responseBodyBody = responseExecute.body();
                if (responseBodyBody == null) {
                    throw new ProtocolException("online count response is empty");
                }
                TransportResponse transportResponse = new TransportResponse(responseExecute.code(), readBounded(responseBodyBody));
                if (responseExecute != null) {
                    responseExecute.close();
                }
                return transportResponse;
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
        }

        private static byte[] readBounded(ResponseBody responseBody) throws IOException {
            InputStream inputStreamByteStream = responseBody.byteStream();
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(512);
                try {
                    byte[] bArr = new byte[1024];
                    int i = 0;
                    while (true) {
                        int i2 = inputStreamByteStream.read(bArr);
                        if (i2 == -1) {
                            byte[] byteArray = byteArrayOutputStream.toByteArray();
                            byteArrayOutputStream.close();
                            if (inputStreamByteStream != null) {
                                inputStreamByteStream.close();
                            }
                            return byteArray;
                        }
                        i += i2;
                        if (i > 8192) {
                            throw new ProtocolException("online count response is too large");
                        }
                        byteArrayOutputStream.write(bArr, 0, i2);
                    }
                } finally {
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
    }
}
