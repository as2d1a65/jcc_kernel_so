package com.android.support;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/* loaded from: classes.dex */
final class TestNetworkService {

    interface Resolver {
        InetAddress[] resolve(String str) throws UnknownHostException;
    }

    enum Candidate {
        NJ_443("NJ 候选服务", "nj.cschannel.anticheatexpert.com", 443),
        NJ_10012("NJ 候选服务", "nj.cschannel.anticheatexpert.com", 10012),
        NJ_80("NJ 候选服务", "nj.cschannel.anticheatexpert.com", 80),
        TYF_443("TYF 候选服务", "tyf.cschannel.anticheatexpert.com", 443),
        TYF_10012("TYF 候选服务", "tyf.cschannel.anticheatexpert.com", 10012),
        TYF_80("TYF 候选服务", "tyf.cschannel.anticheatexpert.com", 80);

        final String host;
        final String label;
        final int port;

        Candidate(String str, String str2, int i) {
            this.label = str;
            this.host = str2;
            this.port = i;
        }

        String title() {
            return this.label + " · TCP " + this.port;
        }
    }

    static List<String> resolve(boolean z, Candidate candidate, Resolver resolver) throws UnknownHostException {
        if (!z) {
            throw new IllegalStateException("仅 TEST 包支持服务实验");
        }
        if (candidate == null || resolver == null) {
            throw new IllegalArgumentException("请选择候选服务");
        }
        InetAddress[] inetAddressArrResolve = resolver.resolve(candidate.host);
        TreeSet treeSet = new TreeSet();
        if (inetAddressArrResolve != null) {
            for (InetAddress inetAddress : inetAddressArrResolve) {
                if ((inetAddress instanceof Inet4Address) && !inetAddress.isAnyLocalAddress() && !inetAddress.isLoopbackAddress() && !inetAddress.isMulticastAddress() && !inetAddress.isLinkLocalAddress()) {
                    treeSet.add(inetAddress.getHostAddress());
                    if (treeSet.size() > 16) {
                        throw new UnknownHostException("解析地址过多，请重新检查网络");
                    }
                }
            }
        }
        if (treeSet.isEmpty()) {
            throw new UnknownHostException("当前网络未返回可实验的 IPv4 地址");
        }
        return Collections.unmodifiableList(new ArrayList(treeSet));
    }

    private TestNetworkService() {
    }
}
