package com.android.support;

import android.content.Context;
import android.os.Build;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import kotlin.UByte;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.json.JSONException;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class LoginFailureReporter {
    private static final String PATH = "/diag/v1/login-failures";
    private static volatile String cachedApkHash;
    private static volatile String cachedApkPath;
    private final Transport transport;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final OkHttpClient HTTP = new OkHttpClient.Builder().dispatcher(ThreadNaming.dispatcher()).followRedirects(false).followSslRedirects(false).connectTimeout(3, TimeUnit.SECONDS).readTimeout(3, TimeUnit.SECONDS).callTimeout(4, TimeUnit.SECONDS).build();

    interface Transport {
        String post(byte[] bArr) throws IOException;
    }

    LoginFailureReporter(Transport transport) {
        this.transport = transport;
    }

    static String report(Context context, VerificationTrace verificationTrace, String str, String str2, String str3) throws JSONException {
        try {
            JSONObject jSONObjectReportPayload = verificationTrace.reportPayload(str);
            jSONObjectReportPayload.put("version_code", 12);
            jSONObjectReportPayload.put("version_name", BuildConfig.VERSION_NAME);
            jSONObjectReportPayload.put("package_name", "com.android.support");
            jSONObjectReportPayload.put("lane", "formal");
            if (str2 == null) {
                str2 = "";
            }
            jSONObjectReportPayload.put("device_fingerprint", hash(str2.getBytes(StandardCharsets.UTF_8)));
            if (str3 == null) {
                str3 = "";
            }
            jSONObjectReportPayload.put("license_fingerprint", hash(str3.getBytes(StandardCharsets.UTF_8)));
            jSONObjectReportPayload.put("apk_sha256", apkHash(context));
            jSONObjectReportPayload.put("android_sdk", Build.VERSION.SDK_INT);
            final String strDiagnosticUrl = JccNetworkPolicy.diagnosticUrl(PATH);
            JccNetworkPolicy.requireFixedDiagnosticUrl(strDiagnosticUrl, PATH);
            return new LoginFailureReporter(new Transport() { // from class: com.android.support.LoginFailureReporter$$ExternalSyntheticLambda0
                @Override // com.android.support.LoginFailureReporter.Transport
                public final String post(byte[] bArr) {
                    return LoginFailureReporter.lambda$report$0(strDiagnosticUrl, bArr);
                }
            }).submit(jSONObjectReportPayload);
        } catch (Exception unused) {
            return "";
        }
    }

    static /* synthetic */ String lambda$report$0(String str, byte[] bArr) throws IOException {
        Response responseExecute = HTTP.newCall(new Request.Builder().url(str).post(RequestBody.create(bArr, JSON)).build()).execute();
        try {
            if (responseExecute.code() == 201 && responseExecute.body() != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                InputStream inputStreamByteStream = responseExecute.body().byteStream();
                byte[] bArr2 = new byte[1024];
                do {
                    int i = inputStreamByteStream.read(bArr2);
                    if (i != -1) {
                        byteArrayOutputStream.write(bArr2, 0, i);
                    } else {
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        if (byteArray.length > 4096) {
                            if (responseExecute != null) {
                                responseExecute.close();
                            }
                            return "";
                        }
                        String str2 = new String(byteArray, StandardCharsets.UTF_8);
                        if (responseExecute != null) {
                            responseExecute.close();
                        }
                        return str2;
                    }
                } while (byteArrayOutputStream.size() <= 4096);
                if (responseExecute != null) {
                    responseExecute.close();
                }
                return "";
            }
            if (responseExecute != null) {
                responseExecute.close();
            }
            return "";
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

    String submit(JSONObject jSONObject) {
        String strPost;
        try {
            byte[] bytes = jSONObject.toString().getBytes(StandardCharsets.UTF_8);
            if (bytes.length <= 16384 && (strPost = this.transport.post(bytes)) != null && strPost.length() <= 4096) {
                JSONObject jSONObject2 = new JSONObject(strPost);
                String strOptString = jSONObject2.optString("reference", "");
                if (Boolean.TRUE.equals(jSONObject2.opt("stored")) && strOptString.matches("LF-[A-F0-9]{16}") && jSONObject.getString("attempt_id").equals(jSONObject2.optString("attempt_id")) && hash(bytes).equals(jSONObject2.optString("request_sha256"))) {
                    if (jSONObject2.optLong("created_at", 0L) > 0) {
                        return strOptString;
                    }
                }
            }
        } catch (Exception unused) {
        }
        return "";
    }

    static String hash(byte[] bArr) throws Exception {
        return hex(MessageDigest.getInstance("SHA-256").digest(bArr));
    }

    private static String hex(byte[] bArr) {
        StringBuilder sb = new StringBuilder(64);
        for (byte b : bArr) {
            sb.append(String.format(Locale.US, "%02x", Integer.valueOf(b & UByte.MAX_VALUE)));
        }
        return sb.toString();
    }

    private static String apkHash(Context context) throws NoSuchAlgorithmException, IOException {
        try {
            String str = context.getApplicationInfo().sourceDir;
            if (str.equals(cachedApkPath) && cachedApkHash != null) {
                return cachedApkHash;
            }
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            FileInputStream fileInputStream = new FileInputStream(str);
            try {
                byte[] bArr = new byte[65536];
                while (true) {
                    int i = fileInputStream.read(bArr);
                    if (i == -1) {
                        fileInputStream.close();
                        String strHex = hex(messageDigest.digest());
                        cachedApkHash = strHex;
                        cachedApkPath = str;
                        return strHex;
                    }
                    messageDigest.update(bArr, 0, i);
                }
            } finally {
            }
        } catch (Exception unused) {
            return "";
        }
    }
}
