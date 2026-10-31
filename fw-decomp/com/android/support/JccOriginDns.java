package com.android.support;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import kotlin.io.encoding.Base64;
import okhttp3.Dns;

/* loaded from: classes.dex */
public final class JccOriginDns implements Dns {
    private volatile List<InetAddress> cached = Collections.emptyList();
    private CompletableFuture<List<InetAddress>> pending;
    private long refreshedAt;
    private final Dns system;
    public static final JccOriginDns SHARED = new JccOriginDns(Dns.SYSTEM);
    private static final long REFRESH_NANOS = TimeUnit.MINUTES.toNanos(5);

    public JccOriginDns(Dns dns) {
        if (dns == null) {
            throw new IllegalArgumentException("system == null");
        }
        this.system = dns;
    }

    @Override // okhttp3.Dns
    public List<InetAddress> lookup(final String str) throws ExecutionException, InterruptedException, TimeoutException, UnknownHostException {
        CompletableFuture<List<InetAddress>> completableFuture;
        if (!"jcc.zhuzhufaka.cn".equalsIgnoreCase(str)) {
            return this.system.lookup(str);
        }
        synchronized (this) {
            if (this.pending == null && (this.refreshedAt == 0 || System.nanoTime() - this.refreshedAt >= REFRESH_NANOS)) {
                final CompletableFuture<List<InetAddress>> completableFuture2 = new CompletableFuture<>();
                this.pending = completableFuture2;
                Thread thread = new Thread(new Runnable() { // from class: com.android.support.JccOriginDns$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m1221lambda$lookup$0$comandroidsupportJccOriginDns(str, completableFuture2);
                    }
                }, "jcc-origin-dns");
                thread.setDaemon(true);
                thread.start();
            }
            completableFuture = this.pending;
        }
        if (completableFuture != null && this.cached.isEmpty()) {
            try {
                completableFuture.get(200L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
            } catch (Exception unused2) {
            }
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(InetAddress.getByAddress(str, new byte[]{43, -30, Base64.padSymbol, 21}));
        for (InetAddress inetAddress : this.cached) {
            if (!arrayList.contains(inetAddress)) {
                arrayList.add(inetAddress);
            }
        }
        return arrayList;
    }

    /* renamed from: lambda$lookup$0$com-android-support-JccOriginDns, reason: not valid java name */
    /* synthetic */ void m1221lambda$lookup$0$comandroidsupportJccOriginDns(String str, CompletableFuture completableFuture) {
        List listEmptyList = Collections.emptyList();
        try {
            listEmptyList = new ArrayList(this.system.lookup(str));
        } catch (Exception unused) {
        }
        synchronized (this) {
            this.cached = Collections.unmodifiableList(listEmptyList);
            this.refreshedAt = System.nanoTime();
            this.pending = null;
            completableFuture.complete(this.cached);
        }
    }
}
