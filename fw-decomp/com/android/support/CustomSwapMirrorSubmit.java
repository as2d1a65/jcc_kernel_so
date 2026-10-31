package com.android.support;

import java.util.List;
import org.json.JSONArray;

/* loaded from: classes.dex */
final class CustomSwapMirrorSubmit {

    enum Kind {
        NO_BOARD,
        EMPTY_MATCH,
        BUILD_ERROR,
        EMPTY_PAIRS,
        READY
    }

    static final class Result {
        final Exception error;
        final Kind kind;
        final JSONArray pairs;

        Result(Kind kind, JSONArray jSONArray) {
            this(kind, jSONArray, null);
        }

        Result(Kind kind, JSONArray jSONArray, Exception exc) {
            this.kind = kind;
            this.pairs = jSONArray;
            this.error = exc;
        }
    }

    private CustomSwapMirrorSubmit() {
    }

    static Result prepare(BoardPositionInfo boardPositionInfo, int i, int i2) {
        if (boardPositionInfo == null) {
            return new Result(Kind.NO_BOARD, null);
        }
        List<int[]> listCollectMatchedCells = CustomSwapMirrorPolicy.collectMatchedCells(boardPositionInfo, i2);
        if (listCollectMatchedCells.isEmpty()) {
            return new Result(Kind.EMPTY_MATCH, null);
        }
        try {
            JSONArray jSONArrayArrayOf = CustomSwapPairJson.arrayOf(CustomSwapMirrorPolicy.buildPairs(i, listCollectMatchedCells));
            if (jSONArrayArrayOf.length() == 0) {
                return new Result(Kind.EMPTY_PAIRS, jSONArrayArrayOf);
            }
            return new Result(Kind.READY, jSONArrayArrayOf);
        } catch (Exception e) {
            return new Result(Kind.BUILD_ERROR, null, e);
        }
    }
}
