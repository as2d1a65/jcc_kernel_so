package com.android.support;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/* loaded from: classes.dex */
final class ScreenSecureWindowManager implements InvocationHandler {
    private final ScreenSecureController controller;
    private final WindowManager delegate;

    private ScreenSecureWindowManager(WindowManager windowManager, ScreenSecureController screenSecureController) {
        this.delegate = windowManager;
        this.controller = screenSecureController;
    }

    static WindowManager wrap(Context context, WindowManager windowManager) {
        ScreenSecureController.shared().restorePreference(context);
        return wrap(windowManager, ScreenSecureController.shared());
    }

    static WindowManager wrap(WindowManager windowManager, ScreenSecureController screenSecureController) {
        if (windowManager == null) {
            return null;
        }
        return (Proxy.isProxyClass(windowManager.getClass()) && (Proxy.getInvocationHandler(windowManager) instanceof ScreenSecureWindowManager)) ? windowManager : (WindowManager) Proxy.newProxyInstance(WindowManager.class.getClassLoader(), new Class[]{WindowManager.class}, new ScreenSecureWindowManager(windowManager, screenSecureController));
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object obj, Method method, Object[] objArr) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            String name = method.getName();
            name.hashCode();
            switch (name) {
                case "toString":
                    return "ScreenSecure(" + this.delegate + ")";
                case "equals":
                    return Boolean.valueOf(obj == objArr[0]);
                case "hashCode":
                    return Integer.valueOf(System.identityHashCode(obj));
            }
        }
        if ("addView".equals(method.getName()) && objArr != null && objArr.length == 2) {
            Object obj2 = objArr[0];
            if (obj2 instanceof View) {
                this.controller.track((View) obj2);
            }
        }
        try {
            return method.invoke(this.delegate, objArr);
        } catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }
}
