package com.android.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
public final class SpecifiedPredictionPreview {
    public static final String KIND_HEX = "hex";
    public static final String KIND_RECAST = "recast";
    public static final String KIND_SHOP = "shop";
    public final long frame;
    public final List<Item> items;
    public final String kind;
    public final String matchGeneration;
    public final String message;
    public final boolean ready;
    public final long revision;
    public final long sessionGeneration;
    public final String state;

    public static final class Item {
        public final int id;
        public final int inputId;
        public final int resultId;
        public final boolean selected;
        public final int slot;
        public final int tier;

        public Item(int i, int i2, int i3, boolean z, int i4) {
            this.slot = i;
            this.inputId = i2;
            this.id = i3;
            this.resultId = i3;
            this.selected = z;
            this.tier = i4;
        }
    }

    public SpecifiedPredictionPreview(String str, String str2, long j, boolean z, String str3, String str4, long j2, long j3, List<Item> list) {
        List<Item> listEmptyList;
        indexOf(str);
        this.kind = str;
        this.matchGeneration = str2;
        this.sessionGeneration = j;
        this.ready = z;
        this.state = str3;
        this.message = str4;
        this.revision = j2;
        this.frame = j3;
        if (z) {
            listEmptyList = Collections.unmodifiableList(new ArrayList(list));
        } else {
            listEmptyList = Collections.emptyList();
        }
        this.items = listEmptyList;
    }

    public static SpecifiedPredictionPreview empty(String str) {
        return empty(str, "waiting", "");
    }

    static SpecifiedPredictionPreview empty(String str, String str2, String str3) {
        return new SpecifiedPredictionPreview(str, "", 0L, false, str2, str3, 0L, 0L, Collections.emptyList());
    }

    static int indexOf(String str) {
        if (KIND_SHOP.equals(str)) {
            return 0;
        }
        if (KIND_RECAST.equals(str)) {
            return 1;
        }
        if (KIND_HEX.equals(str)) {
            return 2;
        }
        throw new IllegalArgumentException("Unknown specified preview kind: " + str);
    }

    static String kindAt(int i) {
        if (i == 0) {
            return KIND_SHOP;
        }
        if (i == 1) {
            return KIND_RECAST;
        }
        if (i == 2) {
            return KIND_HEX;
        }
        throw new IllegalArgumentException("Unknown specified preview index: " + i);
    }

    boolean accepts(String str, long j, long j2) {
        return MatchStatePolicy.accepts(str, this.matchGeneration) && j > 0 && this.sessionGeneration == j && this.revision > j2;
    }
}
