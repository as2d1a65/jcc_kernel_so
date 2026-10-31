package com.android.support;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import javax.net.SocketFactory;

/* loaded from: classes.dex */
public final class JccNetworkRoute {
    private static final SocketFactory CURRENT_ROUTE = new SocketFactory() { // from class: com.android.support.JccNetworkRoute.1
        private SocketFactory current() {
            SocketFactory socketFactoryValidatedSocketFactory = JccNetworkRoute.validatedSocketFactory();
            return socketFactoryValidatedSocketFactory == null ? SocketFactory.getDefault() : socketFactoryValidatedSocketFactory;
        }

        @Override // javax.net.SocketFactory
        public Socket createSocket() throws IOException {
            return current().createSocket();
        }

        @Override // javax.net.SocketFactory
        public Socket createSocket(String str, int i) throws IOException {
            return current().createSocket(str, i);
        }

        @Override // javax.net.SocketFactory
        public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
            return current().createSocket(inetAddress, i);
        }

        @Override // javax.net.SocketFactory
        public Socket createSocket(String str, int i, InetAddress inetAddress, int i2) throws IOException {
            return current().createSocket(str, i, inetAddress, i2);
        }

        @Override // javax.net.SocketFactory
        public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress2, int i2) throws IOException {
            return current().createSocket(inetAddress, i, inetAddress2, i2);
        }
    };
    private static final String TAG = "JccNetworkRoute";
    private static volatile Context applicationContext;

    private JccNetworkRoute() {
    }

    public static void initialize(Context context) {
        if (context != null) {
            applicationContext = context.getApplicationContext();
        }
    }

    public static SocketFactory validatedSocketFactory() {
        Network networkValidatedNetwork = validatedNetwork();
        if (networkValidatedNetwork == null) {
            return null;
        }
        return networkValidatedNetwork.getSocketFactory();
    }

    public static SocketFactory socketFactory() {
        return CURRENT_ROUTE;
    }

    public static Network validatedNetwork() {
        ConnectivityManager connectivityManager;
        Network activeNetwork;
        Context context = applicationContext;
        if (context == null || (connectivityManager = (ConnectivityManager) context.getSystemService("connectivity")) == null) {
            return null;
        }
        try {
            activeNetwork = connectivityManager.getActiveNetwork();
        } catch (RuntimeException unused) {
        }
        if (isValidatedInternet(connectivityManager.getNetworkCapabilities(activeNetwork))) {
            Log.i(TAG, "validated route=active");
            return activeNetwork;
        }
        for (Network network : connectivityManager.getAllNetworks()) {
            if (isValidatedInternet(connectivityManager.getNetworkCapabilities(network))) {
                Log.i(TAG, "validated route=alternate");
                return network;
            }
        }
        return null;
    }

    public static boolean isValidatedInternet(NetworkCapabilities networkCapabilities) {
        return networkCapabilities != null && networkCapabilities.hasCapability(12) && networkCapabilities.hasCapability(16);
    }
}
