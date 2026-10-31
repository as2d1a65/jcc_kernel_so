package com.android.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* loaded from: classes.dex */
final class BoardHeroAnchorSnapshot {
    static final BoardHeroAnchorSnapshot EMPTY = new BoardHeroAnchorSnapshot(null, 0, false, 0, 0);
    final List<BoardHeroAnchor> anchors;
    final int generation;
    final long nativeSequence;
    final boolean planning;
    final long receivedAtMs;

    private BoardHeroAnchorSnapshot(List<BoardHeroAnchor> list, long j, boolean z, long j2, int i) {
        this(list, j, z, j2, i, true);
    }

    private BoardHeroAnchorSnapshot(List<BoardHeroAnchor> list, long j, boolean z, long j2, int i, boolean z2) {
        if (list == null) {
            list = null;
        } else if (z2) {
            list = Collections.unmodifiableList(new ArrayList(list));
        }
        this.anchors = list;
        this.nativeSequence = j;
        this.planning = z;
        this.receivedAtMs = j2;
        this.generation = i;
    }

    BoardHeroAnchorSnapshot next(List<BoardHeroAnchor> list, long j, boolean z, long j2) {
        if (j <= this.nativeSequence) {
            return this;
        }
        if (this.planning == z && sameAnchors(this.anchors, list)) {
            return new BoardHeroAnchorSnapshot(this.anchors, j, z, j2, this.generation, false);
        }
        return new BoardHeroAnchorSnapshot(list, j, z, j2, this.generation + 1);
    }

    BoardHeroAnchorSnapshot cleared() {
        return new BoardHeroAnchorSnapshot(null, this.nativeSequence, false, 0L, this.generation + 1);
    }

    boolean isFresh(long j, long j2) {
        long j3 = this.receivedAtMs;
        return j3 > 0 && j2 >= 0 && j >= j3 && j - j3 <= j2;
    }

    private static boolean sameAnchors(List<BoardHeroAnchor> list, List<BoardHeroAnchor> list2) {
        if (list == list2) {
            return true;
        }
        if (list == null || list2 == null || list.size() != list2.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            BoardHeroAnchor boardHeroAnchor = list.get(i);
            BoardHeroAnchor boardHeroAnchor2 = list2.get(i);
            if (boardHeroAnchor != boardHeroAnchor2 && (boardHeroAnchor == null || boardHeroAnchor2 == null || boardHeroAnchor.heroId != boardHeroAnchor2.heroId || boardHeroAnchor.unitId != boardHeroAnchor2.unitId || boardHeroAnchor.ownedCopies != boardHeroAnchor2.ownedCopies || boardHeroAnchor.area != boardHeroAnchor2.area || boardHeroAnchor.col != boardHeroAnchor2.col || boardHeroAnchor.row != boardHeroAnchor2.row || Float.compare(boardHeroAnchor.x, boardHeroAnchor2.x) != 0 || Float.compare(boardHeroAnchor.y, boardHeroAnchor2.y) != 0)) {
                return false;
            }
        }
        return true;
    }
}
