package com.android.support;

import java.net.URI;
import java.net.URISyntaxException;

/* loaded from: classes.dex */
public final class JccNetworkPolicy {
    public static final String ANNOUNCEMENT_URL;
    public static final String DIAGNOSTIC_CONTROL_URL;
    public static final String DIAGNOSTIC_REGISTER_URL;
    public static final String JCC_API_PREFIX;
    public static final boolean JCC_CANDIDATE_BUILD = false;
    public static final String ORIGIN = "https://jcc.zhuzhufaka.cn";
    public static final String PEER_SYNC_URL;
    public static final String PRESENCE_URL;
    public static final String SESSION_CHALLENGE_URL;
    public static final String SESSION_LOGIN_URL;
    public static final String SESSION_LOGOUT_URL;
    public static final String SESSION_REFRESH_URL;
    public static final String SESSION_UNBIND_URL;
    public static final String SHARE_PUBLISH_URL;
    public static final String SHARE_START_URL;
    public static final String SHARE_STOP_URL;
    public static final String TTS_URL;
    public static final String VERSION_URL;

    public static boolean isRedirect(int i) {
        return i == 301 || i == 302 || i == 303 || i == 307 || i == 308;
    }

    static {
        String strRequireApiPrefix = requireApiPrefix(false, "");
        JCC_API_PREFIX = strRequireApiPrefix;
        SESSION_LOGIN_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/session/login");
        SESSION_CHALLENGE_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/session/challenge");
        SESSION_REFRESH_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/session/refresh");
        SESSION_LOGOUT_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/session/logout");
        SESSION_UNBIND_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/session/unbind");
        PRESENCE_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/presence");
        PEER_SYNC_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/peer-sync");
        TTS_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/tts");
        ANNOUNCEMENT_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/announcement");
        VERSION_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/version");
        SHARE_START_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/share/start");
        SHARE_PUBLISH_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/share/publish");
        SHARE_STOP_URL = apiUrl(false, strRequireApiPrefix, "/v2/api/share/stop");
        DIAGNOSTIC_REGISTER_URL = diagnosticUrl("/diag/v1/installations/register");
        DIAGNOSTIC_CONTROL_URL = diagnosticUrl("/diag/v1/control");
    }

    private JccNetworkPolicy() {
    }

    private static String requireApiPrefix(boolean z, String str) {
        if (!z) {
            if (str == null || !str.isEmpty()) {
                throw new IllegalArgumentException("formal JCC prefix must be empty");
            }
            return "";
        }
        return requireCandidatePrefix(str);
    }

    private static String requireCandidatePrefix(String str) {
        if (str == null || !str.matches("/[0-9a-f]{48}")) {
            throw new IllegalArgumentException("candidate JCC prefix is required");
        }
        return str;
    }

    private static String requireV2Path(String str) {
        if (str == null || !str.matches("/v2/[a-z0-9-]+(?:/[a-z0-9-]+)*") || str.contains("..") || str.indexOf(37) >= 0 || str.indexOf(63) >= 0 || str.indexOf(35) >= 0) {
            throw new IllegalArgumentException("candidate JCC endpoint is required");
        }
        return str;
    }

    public static String apiUrl(boolean z, String str, String str2) {
        String strRequireV2Path = requireV2Path(str2);
        if (z) {
            return ORIGIN + requireCandidatePrefix(str) + strRequireV2Path;
        }
        if (str == null || !str.isEmpty()) {
            throw new IllegalArgumentException("formal JCC prefix must be empty");
        }
        return ORIGIN + strRequireV2Path;
    }

    public static String candidateUrl(String str, String str2) {
        return apiUrl(true, str, str2);
    }

    public static String diagnosticUrl(String str) {
        return ORIGIN + requireDiagnosticPath(str);
    }

    public static String diagnosticUrlFor(boolean z, String str, String str2) {
        String strRequireDiagnosticPath = requireDiagnosticPath(str2);
        if (z) {
            return ORIGIN + requireCandidatePrefix(str) + strRequireDiagnosticPath;
        }
        if (str == null || !str.isEmpty()) {
            throw new IllegalArgumentException("formal JCC prefix must be empty");
        }
        return ORIGIN + strRequireDiagnosticPath;
    }

    public static String requireFixedDiagnosticUrl(String str, String str2) {
        String strDiagnosticUrl = diagnosticUrl(str2);
        try {
            URI uri = new URI(str);
            if ("https".equals(uri.getScheme()) && "jcc.zhuzhufaka.cn".equals(uri.getHost()) && ((uri.getPort() == -1 || uri.getPort() == 443) && uri.getRawUserInfo() == null && uri.getRawQuery() == null && uri.getRawFragment() == null && str.equals(strDiagnosticUrl))) {
                return str;
            }
            throw new IllegalArgumentException("fixed diagnostic endpoint is required");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("fixed diagnostic endpoint is required", e);
        }
    }

    public static String requireFixedApiUrl(String str, String str2) {
        if (str == null || str2 == null || !str2.startsWith("/v2/api/")) {
            throw new IllegalArgumentException("fixed JCC endpoint is required");
        }
        String strApiUrl = apiUrl(false, JCC_API_PREFIX, str2);
        try {
            URI uri = new URI(str);
            if ("https".equals(uri.getScheme()) && "jcc.zhuzhufaka.cn".equals(uri.getHost()) && ((uri.getPort() == -1 || uri.getPort() == 443) && uri.getRawUserInfo() == null && uri.getRawQuery() == null && uri.getRawFragment() == null && str.equals(strApiUrl))) {
                return str;
            }
            throw new IllegalArgumentException("fixed JCC endpoint is required");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("fixed JCC endpoint is required", e);
        }
    }

    public static String requireFixedOriginUrl(String str) {
        if (str == null) {
            throw new IllegalArgumentException("fixed JCC origin is required");
        }
        try {
            URI uri = new URI(str);
            String rawPath = uri.getRawPath();
            if ("https".equals(uri.getScheme()) && "jcc.zhuzhufaka.cn".equals(uri.getHost()) && ((uri.getPort() == -1 || uri.getPort() == 443) && uri.getRawUserInfo() == null && uri.getRawQuery() == null && uri.getRawFragment() == null && rawPath != null && rawPath.startsWith("/") && !rawPath.contains("/../") && !rawPath.endsWith("/..") && str.equals(ORIGIN + rawPath))) {
                return str;
            }
            throw new IllegalArgumentException("fixed JCC origin is required");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("fixed JCC origin is required", e);
        }
    }

    private static String requireDiagnosticPath(String str) {
        if (str == null || str.length() > 256 || !str.matches("/diag/v1/[A-Za-z0-9_-]+(?:/[A-Za-z0-9_-]+)*") || str.contains("..") || str.indexOf(37) >= 0 || str.indexOf(63) >= 0 || str.indexOf(35) >= 0) {
            throw new IllegalArgumentException("fixed diagnostic endpoint is required");
        }
        return str;
    }
}
