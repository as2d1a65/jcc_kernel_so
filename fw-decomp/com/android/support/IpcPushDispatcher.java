package com.android.support;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONObject;

/* loaded from: classes.dex */
final class IpcPushDispatcher {
    private final Map<String, Handler> handlers;

    @FunctionalInterface
    interface Handler {
        void handle(JSONObject jSONObject) throws Exception;
    }

    IpcPushDispatcher(Map<String, Handler> map) {
        if (map == null) {
            throw new NullPointerException("handlers");
        }
        this.handlers = Collections.unmodifiableMap(new LinkedHashMap(map));
    }

    boolean dispatch(String str, JSONObject jSONObject) throws Exception {
        if (str == null) {
            throw new NullPointerException("method");
        }
        Handler handler = this.handlers.get(str);
        if (handler == null) {
            return false;
        }
        handler.handle(jSONObject);
        return true;
    }
}
