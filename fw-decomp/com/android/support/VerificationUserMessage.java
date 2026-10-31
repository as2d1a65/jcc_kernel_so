package com.android.support;

import androidx.core.view.MotionEventCompat;
import com.android.support.trust.TrustError;
import java.util.Locale;
import kotlin.text.Typography;

/* loaded from: classes.dex */
public final class VerificationUserMessage {
    private VerificationUserMessage() {
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    public static String forCode(String str, String str2) {
        String str3;
        String str4;
        String str5;
        if (str == null) {
            str = "UNKNOWN";
        }
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -2038013325:
                if (str.equals("ADB_DEBUG")) {
                    c = 0;
                    break;
                }
                break;
            case -1987808683:
                if (str.equals("UPDATE_REQUIRED")) {
                    c = 1;
                    break;
                }
                break;
            case -1900056923:
                if (str.equals("TRUST_REJECTED")) {
                    c = 2;
                    break;
                }
                break;
            case -1824356621:
                if (str.equals("MAINTENANCE")) {
                    c = 3;
                    break;
                }
                break;
            case -1820247863:
                if (str.equals("INJECT_PENDING")) {
                    c = 4;
                    break;
                }
                break;
            case -1733271737:
                if (str.equals("NET_DNS")) {
                    c = 5;
                    break;
                }
                break;
            case -1733256423:
                if (str.equals("NET_TLS")) {
                    c = 6;
                    break;
                }
                break;
            case -1314247541:
                if (str.equals("WIG_AUTHORIZATION_DENIED")) {
                    c = 7;
                    break;
                }
                break;
            case -1191502261:
                if (str.equals("LOGIN_TIMEOUT")) {
                    c = '\b';
                    break;
                }
                break;
            case -1015541971:
                if (str.equals("INTEGRITY")) {
                    c = '\t';
                    break;
                }
                break;
            case -872543224:
                if (str.equals("NET_CONNECT")) {
                    c = '\n';
                    break;
                }
                break;
            case -866047748:
                if (str.equals("LICENSE_BANNED")) {
                    c = 11;
                    break;
                }
                break;
            case -772326846:
                if (str.equals("INPUT_INVALID")) {
                    c = '\f';
                    break;
                }
                break;
            case -744018507:
                if (str.equals("SESSION_REVOKED")) {
                    c = '\r';
                    break;
                }
                break;
            case -653650923:
                if (str.equals("WIG_AUTHORIZATION_EXPIRED")) {
                    c = 14;
                    break;
                }
                break;
            case -541500857:
                if (str.equals("SESSION_OCCUPIED")) {
                    c = 15;
                    break;
                }
                break;
            case -508431662:
                if (str.equals("WIG_HTTP")) {
                    c = 16;
                    break;
                }
                break;
            case -494039813:
                if (str.equals("PRESENCE")) {
                    c = 17;
                    break;
                }
                break;
            case -455626134:
                if (str.equals("TRANSIENT")) {
                    c = 18;
                    break;
                }
                break;
            case -407934876:
                if (str.equals("LOCAL_CHECK_BUSY")) {
                    c = 19;
                    break;
                }
                break;
            case -374260356:
                if (str.equals("DEVICE_BINDING")) {
                    c = 20;
                    break;
                }
                break;
            case -186703013:
                if (str.equals("GATEWAY_SESSION")) {
                    c = 21;
                    break;
                }
                break;
            case -145897602:
                if (str.equals("DEVICE_CONFIRMATION")) {
                    c = 22;
                    break;
                }
                break;
            case 415145718:
                if (str.equals("LOCAL_CHECK_TIMEOUT")) {
                    c = 23;
                    break;
                }
                break;
            case 613648965:
                if (str.equals("DEVICE_DISABLED")) {
                    c = 24;
                    break;
                }
                break;
            case 630175426:
                if (str.equals("CLOUD_FUNCTION")) {
                    c = 25;
                    break;
                }
                break;
            case 659453081:
                if (str.equals("CANCELED")) {
                    c = 26;
                    break;
                }
                break;
            case 820681482:
                if (str.equals("BUILD_REGISTRATION")) {
                    c = 27;
                    break;
                }
                break;
            case 1295576937:
                if (str.equals("WIG_REJECT")) {
                    c = 28;
                    break;
                }
                break;
            case 1425308041:
                if (str.equals("WIG_PARSE")) {
                    c = 29;
                    break;
                }
                break;
            case 1433767024:
                if (str.equals("USER_DISABLED")) {
                    c = 30;
                    break;
                }
                break;
            case 1444216287:
                if (str.equals("LOCAL_CHECK_UNKNOWN")) {
                    c = 31;
                    break;
                }
                break;
            case 1856641340:
                if (str.equals("LOCAL_UNAVAILABLE")) {
                    c = ' ';
                    break;
                }
                break;
            case 2067056115:
                if (str.equals("ENVIRONMENT")) {
                    c = '!';
                    break;
                }
                break;
            case 2086094106:
                if (str.equals("RESPONSE_TAMPERED")) {
                    c = Typography.quote;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                str3 = "19";
                str4 = "设备的调试功能处于开启状态";
                str5 = "请关闭设置中的“USB 调试”和“无线调试”，再重新打开游戏和记牌器";
                break;
            case 1:
                str3 = "22";
                str4 = "当前版本已停止支持，需要更新";
                str5 = "请通过官方入口更新后重试；请先保留使用码";
                break;
            case 2:
            case '\r':
                str3 = "23";
                str4 = "当前登录已被终止";
                str5 = "换机、重新登录或授权状态变化后可能出现，请重新验证；非本人操作或反复出现请联系客服";
                break;
            case 3:
                str3 = "27";
                str4 = "验证服务已暂停开放";
                str5 = "请查看公告或联系客服确认恢复时间，稍后再试";
                break;
            case 4:
                str3 = "11";
                str4 = "记牌器还没有连上游戏";
                str5 = "请回到记牌器首页点击“连接游戏”，连接成功后再验证";
                break;
            case 5:
                str3 = "02";
                str4 = "当前网络没有找到验证服务";
                str5 = "请切换手机流量或其他 Wi-Fi 后重试；持续出现请联系客服";
                break;
            case 6:
                str3 = "04";
                str4 = "设备与验证服务的安全连接未建立";
                str5 = "请开启自动日期和时间，再切换网络重试；仍失败请联系客服";
                break;
            case 7:
            case MotionEventCompat.AXIS_RELATIVE_Y /* 28 */:
                str3 = "06";
                str4 = "使用码未通过验证，服务尚未说明具体原因";
                str5 = "请核对使用码；确认无误后联系客服查使用码状态，无需反复解绑或重装";
                break;
            case '\b':
                str3 = "30";
                str4 = "本次验证等待超时，尚未取得最终结果";
                str5 = "请检查网络并重新验证；持续出现请把提示截图和发生时间发给客服";
                break;
            case '\t':
                str3 = "17";
                str4 = "当前程序运行检查未通过";
                str5 = "请先完全退出游戏和记牌器再打开；仍失败请联系客服核对安装版本。此提示本身不代表已被封禁";
                break;
            case '\n':
                str3 = "03";
                str4 = "连接验证服务失败或等待超时";
                str5 = "请检查网络后重试，也可换一条网络；持续出现请联系客服";
                break;
            case 11:
                str3 = "24";
                str4 = "验证服务已限制当前使用资格，未返回具体限制原因";
                str5 = "请联系客服核查限制记录；更换设备或重装不会解除限制";
                break;
            case '\f':
                str3 = "01";
                str4 = "使用码为空或格式不正确";
                str5 = "请重新粘贴完整使用码，检查是否多了空格或换行";
                break;
            case 14:
                str3 = "07";
                str4 = "验证服务提示使用码已到期";
                str5 = "请核对有效期并续费；刚购买或续费仍出现时，请联系客服核查";
                break;
            case 15:
                str3 = "28";
                str4 = "该使用资格已有正在运行的登录";
                str5 = "请退出其他设备或重复打开的游戏和记牌器，稍等后重试；确认均已退出仍出现，请联系客服";
                break;
            case 16:
            case 18:
                str3 = "05";
                str4 = "验证服务暂时没有正常回复";
                str5 = "请稍后再试；持续出现请联系客服检查服务状态";
                break;
            case 17:
                str3 = "16";
                str4 = "在线状态没有及时更新";
                str5 = "请恢复网络后重新验证；持续出现请联系客服";
                break;
            case 19:
                str3 = "13";
                str4 = "设备上的启动检查还在进行";
                str5 = "请稍等再试；持续出现请完全退出游戏和记牌器再打开";
                break;
            case 20:
                str3 = "20";
                str4 = "使用码记录的设备与本次设备信息不一致";
                str5 = "换过设备请使用“更换设备”；同一设备或已解绑仍出现，请联系客服核对绑定记录";
                break;
            case 21:
                str3 = "09";
                str4 = "本次登录状态未建立或已失效";
                str5 = "请重新点击“开始验证”；持续出现请联系客服，无需先解绑";
                break;
            case 22:
                str3 = "29";
                str4 = "本次设备身份确认未通过";
                str5 = "请重新打开游戏和记牌器；同一设备持续出现时，请联系客服检查设备记录";
                break;
            case 23:
                str3 = "12";
                str4 = "设备上的启动检查等待超时";
                str5 = "请保持游戏在前台后重试；持续出现请完全退出游戏和记牌器再打开";
                break;
            case 24:
                str3 = "26";
                str4 = "当前设备已被限制使用，服务未说明具体触发原因";
                str5 = "请联系客服核查设备限制记录；解绑只处理设备关联，不会解除限制";
                break;
            case 25:
                str3 = "10";
                str4 = "在线验证未完成，尚未确认具体原因";
                str5 = "请稍后重试；持续出现请联系客服检查服务和当前版本";
                break;
            case 26:
                str3 = "15";
                str4 = "本次验证已中止";
                str5 = "如有新的验证正在进行，请等待结果；否则请重新点击“开始验证”";
                break;
            case 27:
                str3 = "21";
                str4 = "验证服务暂未接受当前安装版本";
                str5 = "请联系客服核对版本；已使用官方包也请保留现场，无需反复卸载";
                break;
            case 29:
            case '\"':
                str3 = "08";
                str4 = "收到的验证结果异常，本次未能确认登录";
                str5 = "请稍后重试或切换网络；持续出现请联系客服检查版本和服务状态";
                break;
            case 30:
                str3 = "25";
                str4 = "当前使用码对应的使用资格已停用";
                str5 = "请联系客服核查停用原因；到期续费、更换设备和解除停用是不同处理";
                break;
            case 31:
            case ' ':
                str3 = "14";
                str4 = "设备上的启动检查未完成";
                str5 = "请完全退出游戏和记牌器再打开；仍失败请联系客服检查版本与设备兼容情况";
                break;
            case MotionEventCompat.AXIS_GENERIC_2 /* 33 */:
                str3 = "18";
                str4 = "当前设备运行状态未通过检查";
                str5 = "请关闭与游戏同时运行的修改或多开工具后重启；仍出现请联系客服核查兼容情况";
                break;
            default:
                str3 = "00";
                str4 = "登录未完成，现有结果不足以确认原因";
                str5 = "请重试一次；仍出现请把提示截图、发生时间、版本及本机编号发给客服";
                break;
        }
        return "原因：" + str4 + "。\n处理：" + str5 + "。\n问题编号：L" + str3 + ((str2 == null || !str2.matches("LF-[A-F0-9]{16}")) ? "\n诊断尚未确认上传，请提供截图和发生时间" : "\n客服参考号：" + str2);
    }

    public static String fromInternal(String str, String str2) {
        for (String str3 : (str == null ? "" : str.toUpperCase(Locale.US)).split("[^A-Z0-9_]+")) {
            String strCustomerCode = GatewayFailurePolicy.customerCode(str3, 0);
            if (!"UNKNOWN".equals(strCustomerCode)) {
                return forCode(strCustomerCode, str2);
            }
        }
        return forCode("UNKNOWN", str2);
    }

    public static String forRevocation(String str) {
        String upperCase = str == null ? "" : str.toUpperCase(Locale.US);
        if (upperCase.startsWith("NATIVE_")) {
            try {
                return forCode(codeForLeaseError(TrustError.valueOf(upperCase.substring(7))), null);
            } catch (IllegalArgumentException unused) {
                return forCode("LOCAL_CHECK_UNKNOWN", null);
            }
        }
        if (upperCase.startsWith("SERVER_")) {
            upperCase = upperCase.substring(7);
        }
        if ("REVOKED".equals(upperCase) || "PRESENCE_DENIED".equals(upperCase)) {
            upperCase = "SESSION_REVOKED";
        }
        return fromInternal(upperCase, null);
    }

    static String codeForLeaseError(TrustError trustError, String str) {
        String strCustomerCode = GatewayFailurePolicy.customerCode(str, 0);
        return ((trustError == TrustError.TRANSPORT || trustError == TrustError.REVOKED || trustError == TrustError.BUILD_NOT_REGISTERED || trustError == TrustError.LEASE_EXPIRED) && !"UNKNOWN".equals(strCustomerCode)) ? strCustomerCode : codeForLeaseError(trustError);
    }

    /* renamed from: com.android.support.VerificationUserMessage$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$android$support$trust$TrustError;

        static {
            int[] iArr = new int[TrustError.values().length];
            $SwitchMap$com$android$support$trust$TrustError = iArr;
            try {
                iArr[TrustError.TRANSPORT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$android$support$trust$TrustError[TrustError.BUILD_NOT_REGISTERED.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$android$support$trust$TrustError[TrustError.WRONG_LENGTH.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$android$support$trust$TrustError[TrustError.MALFORMED_LEASE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$android$support$trust$TrustError[TrustError.BAD_SIGNATURE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$android$support$trust$TrustError[TrustError.CLOCK_OVERFLOW.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$android$support$trust$TrustError[TrustError.LEASE_EXPIRED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$android$support$trust$TrustError[TrustError.REVOKED.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$android$support$trust$TrustError[TrustError.NATIVE_UNAVAILABLE.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$android$support$trust$TrustError[TrustError.CANCELED.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$android$support$trust$TrustError[TrustError.VM_REJECTED.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$android$support$trust$TrustError[TrustError.VM_PROGRAM_INVALID.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$android$support$trust$TrustError[TrustError.MANIFEST_INVALID.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
        }
    }

    static String codeForLeaseError(TrustError trustError) {
        if (trustError == null) {
            return "UNKNOWN";
        }
        switch (AnonymousClass1.$SwitchMap$com$android$support$trust$TrustError[trustError.ordinal()]) {
        }
        return "UNKNOWN";
    }
}
