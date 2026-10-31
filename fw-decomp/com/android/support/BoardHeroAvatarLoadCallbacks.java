package com.android.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* loaded from: classes.dex */
final class BoardHeroAvatarLoadCallbacks {
    private final Map<String, List<Runnable>> callbacksByPath = new HashMap();

    BoardHeroAvatarLoadCallbacks() {
    }

    synchronized boolean join(String str, Runnable runnable) {
        List<Runnable> list = this.callbacksByPath.get(str);
        if (list != null) {
            if (runnable != null) {
                list.add(runnable);
            }
            return false;
        }
        ArrayList arrayList = new ArrayList();
        if (runnable != null) {
            arrayList.add(runnable);
        }
        this.callbacksByPath.put(str, arrayList);
        return true;
    }

    synchronized List<Runnable> complete(String str) {
        List<Runnable> listRemove = this.callbacksByPath.remove(str);
        if (listRemove != null && !listRemove.isEmpty()) {
            return listRemove;
        }
        return Collections.emptyList();
    }

    synchronized void clear() {
        this.callbacksByPath.clear();
    }
}
