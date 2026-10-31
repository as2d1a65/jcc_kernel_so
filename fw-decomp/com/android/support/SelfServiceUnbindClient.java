package com.android.support;

import androidx.core.app.NotificationCompat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProtocolException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
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
public final class SelfServiceUnbindClient {
    private static final HttpUrl BASE_URL = HttpUrl.get("https://www.wigwy.xyz/");
    private static final MediaType EMPTY_BODY_TYPE = MediaType.get("application/octet-stream");
    static final int MAX_CARD_LENGTH = 128;
    static final int MAX_RESPONSE_BYTES = 16384;
    private final boolean candidateBuild;
    private final Transport transport;

    interface Transport {
        TransportResponse post(HttpUrl httpUrl) throws IOException;
    }

    static final class TransportResponse {
        final byte[] body;
        final int status;

        TransportResponse(int i, byte[] bArr) {
            this.status = i;
            this.body = bArr;
        }
    }

    public static final class Result {
        private final String message;
        private final boolean success;

        Result(boolean z, String str) {
            this.success = z;
            this.message = str;
        }

        public boolean isSuccess() {
            return this.success;
        }

        public String getMessage() {
            return this.message;
        }
    }

    public SelfServiceUnbindClient() {
        this(new OkHttpTransport(), false);
    }

    SelfServiceUnbindClient(Transport transport) {
        this(transport, false);
    }

    SelfServiceUnbindClient(Transport transport, boolean z) {
        if (transport == null) {
            throw new IllegalArgumentException("transport == null");
        }
        this.transport = transport;
        this.candidateBuild = z;
    }

    public Result unbind(String str) throws IOException {
        TransportResponse transportResponsePost;
        if (!CandidateBusinessEndpointPolicy.allowsProductionBusinessEndpoints(false) && this.candidateBuild) {
            throw new IOException("候选版已关闭在线更换设备");
        }
        HttpUrl httpUrlBuildUrl = buildUrl(str);
        try {
            transportResponsePost = this.transport.post(httpUrlBuildUrl);
        } catch (IOException e) {
            if (e instanceof ProtocolException) {
                throw e;
            }
            transportResponsePost = this.transport.post(httpUrlBuildUrl);
        }
        return parse(transportResponsePost.status, transportResponsePost.body);
    }

    public static String normalizeCard(String str) {
        String strTrim = str == null ? "" : str.trim();
        if (strTrim.isEmpty()) {
            throw new IllegalArgumentException("请输入使用码");
        }
        if (strTrim.length() > 128) {
            throw new IllegalArgumentException("使用码长度不能超过128个字符");
        }
        for (int i = 0; i < strTrim.length(); i++) {
            if (Character.isISOControl(strTrim.charAt(i))) {
                throw new IllegalArgumentException("使用码包含无效字符");
            }
        }
        return strTrim;
    }

    static HttpUrl buildUrl(String str) {
        return BASE_URL.newBuilder().addPathSegment("api").addPathSegment("unbundling").addPathSegment("24595").addPathSegment(normalizeCard(str)).build();
    }

    private static Result parse(int i, byte[] bArr) throws IOException {
        if (i >= 500) {
            return new Result(false, unbindMessage("13", "更换设备服务暂时不可用", "请稍后再试"));
        }
        if (i == 429) {
            return new Result(false, unbindMessage("04", "更换设备请求过于频繁", "请稍后再试"));
        }
        if (bArr == null || bArr.length == 0 || bArr.length > 16384) {
            throw protocolError("更换设备服务响应无效");
        }
        try {
            JSONObject jSONObject = new JSONObject(new String(bArr, StandardCharsets.UTF_8));
            int iOptInt = jSONObject.optInt("code", Integer.MIN_VALUE);
            if (iOptInt == Integer.MIN_VALUE) {
                throw protocolError("更换设备服务响应缺少状态码");
            }
            boolean z = i >= 200 && i < 300 && iOptInt == 200;
            String customerMessage = toCustomerMessage(z, jSONObject.optString(NotificationCompat.CATEGORY_MESSAGE, "").trim());
            if (customerMessage.length() > 256) {
                customerMessage = customerMessage.substring(0, 256);
            }
            return new Result(z, customerMessage);
        } catch (JSONException unused) {
            throw protocolError("更换设备服务响应格式错误");
        }
    }

    static String toCustomerMessage(boolean z, String str) {
        if (z) {
            return "旧设备关联已解除，请返回游戏重新验证；如仍提示限制或到期，请联系客服核查对应状态。";
        }
        if (str == null) {
            str = "";
        }
        if (str.contains("原机械码") || str.contains("原机器码") || str.contains("需要原设备") || str.contains("缺少原设备") || str.contains("让作者升级")) {
            return unbindMessage("01", "本次更换设备需要核对原设备记录", "请联系客服协助更换，提供使用码、本机编号和发生时间；重复点击不会补齐记录");
        }
        if (str.contains("封禁") || str.contains("禁用") || str.contains("冻结")) {
            return unbindMessage("02", "当前使用资格受到限制", "请联系客服核查限制原因；更换设备不会解除封禁或停用");
        }
        if (str.contains("过期") || str.contains("到期")) {
            return unbindMessage("03", "验证服务提示使用码已到期", "请核对有效期或续费；刚续费仍出现时请联系客服");
        }
        if (str.contains("频繁") || str.contains("冷却") || str.contains("间隔") || str.contains("分钟") || str.contains("小时")) {
            return unbindMessage("04", "尚未到允许再次更换设备的时间", "请稍后再试；具体等待时间请联系客服查询");
        }
        if (str.contains("次数") || str.contains("上限")) {
            return unbindMessage("05", "更换设备次数已受限制", "请联系客服核对剩余次数和恢复时间");
        }
        if (str.contains("未绑定") || str.contains("没有绑定") || str.contains("未使用")) {
            return unbindMessage("06", "当前没有可解除的设备关联", "请返回游戏直接验证；仍提示设备不符时，请联系客服核查记录");
        }
        if (str.contains("卡密不存在") || str.contains("使用码不存在") || str.contains("卡密无效") || str.contains("无效卡密") || str.contains("卡密错误") || str.contains("使用码错误")) {
            return unbindMessage("07", "服务未确认这个使用码", "请核对是否完整粘贴；确认无误后请联系客服");
        }
        if (str.contains("不允许") || str.contains("禁止解绑") || str.contains("关闭解绑")) {
            return unbindMessage("08", "当前使用码未开放自助更换设备", "请联系客服处理设备关联");
        }
        return unbindMessage("00", "更换设备未完成，服务未给出可确认的原因", "请保留使用码，并把提示截图、本机编号和发生时间发给客服核查");
    }

    public static String failureMessage(Throwable th) {
        if (th instanceof UnknownHostException) {
            return unbindMessage("09", "当前网络没有找到更换设备服务", "请换一条网络后重试");
        }
        if (th instanceof SSLException) {
            return unbindMessage("10", "与更换设备服务的安全连接未建立", "请开启自动日期和时间，再换一条网络重试");
        }
        if (th instanceof ProtocolException) {
            return unbindMessage("11", "更换设备服务返回的结果异常", "请稍后重试；持续出现请联系客服，尚未确认设备是否解除");
        }
        if (th instanceof IOException) {
            return unbindMessage("12", "更换设备的请求未取得结果", "请恢复网络后先返回游戏验证；仍提示设备不符再联系客服，避免连续提交");
        }
        return unbindMessage("00", "更换设备未完成", "请核对使用码后重试；持续出现请把提示截图和发生时间发给客服");
    }

    private static String unbindMessage(String str, String str2, String str3) {
        return "原因：" + str2 + "。\n处理：" + str3 + "。\n问题编号：U" + str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static ProtocolException protocolError(String str) {
        return new ProtocolException(str);
    }

    private static final class OkHttpTransport implements Transport {
        private final OkHttpClient client;

        private OkHttpTransport() {
            this.client = new OkHttpClient.Builder().dispatcher(ThreadNaming.dispatcher()).followRedirects(false).followSslRedirects(false).connectTimeout(10L, TimeUnit.SECONDS).readTimeout(10L, TimeUnit.SECONDS).callTimeout(15L, TimeUnit.SECONDS).build();
        }

        @Override // com.android.support.SelfServiceUnbindClient.Transport
        public TransportResponse post(HttpUrl httpUrl) throws IOException {
            Response responseExecute = this.client.newCall(new Request.Builder().url(httpUrl).post(RequestBody.create(new byte[0], SelfServiceUnbindClient.EMPTY_BODY_TYPE)).build()).execute();
            try {
                ResponseBody responseBodyBody = responseExecute.body();
                if (responseBodyBody == null) {
                    throw SelfServiceUnbindClient.protocolError("更换设备服务响应无效");
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
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    byte[] bArr = new byte[2048];
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
                        if (i > 16384) {
                            throw SelfServiceUnbindClient.protocolError("更换设备服务响应无效");
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
