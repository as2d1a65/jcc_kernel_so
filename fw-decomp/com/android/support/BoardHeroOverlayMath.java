package com.android.support;

import com.android.support.BoardHeroOverlayMath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/* loaded from: classes.dex */
final class BoardHeroOverlayMath {
    private static final float[][] CANDIDATES = {new float[]{0.0f, 0.0f}, new float[]{-0.35f, 0.0f}, new float[]{0.35f, 0.0f}, new float[]{0.0f, 1.0f}};
    static final float LARGE_JUMP_SCREEN_FRACTION = 0.45f;
    static final long MAX_AGE_MS = 1500;
    private static final float MAX_SMOOTH_DT_SECONDS = 0.1f;
    static final float SMOOTH_TAU_SECONDS = 0.05f;
    static final float SNAP_DISTANCE_PX = 0.5f;

    static boolean shouldRender(boolean z, boolean z2, long j, long j2) {
        return z && z2 && j > 0 && j2 >= j && j2 - j <= MAX_AGE_MS;
    }

    static boolean shouldShowMarker(boolean z, boolean z2) {
        return z && !z2;
    }

    static float toTopOriginY(float f, float f2) {
        return (1.0f - f) * f2;
    }

    private BoardHeroOverlayMath() {
    }

    static float smoothingAlpha(float f) {
        return (float) (1.0d - Math.exp((-Math.max(0.0f, Math.min(MAX_SMOOTH_DT_SECONDS, f))) / SMOOTH_TAU_SECONDS));
    }

    static float stepPosition(float f, float f2, float f3) {
        return stepPositionWithAlpha(f, f2, smoothingAlpha(f3));
    }

    static float stepPositionWithAlpha(float f, float f2, float f3) {
        float f4 = f2 - f;
        if (Math.abs(f4) >= SNAP_DISTANCE_PX) {
            float fMax = f + (Math.max(0.0f, Math.min(1.0f, f3)) * f4);
            if (Math.abs(f2 - fMax) >= SNAP_DISTANCE_PX) {
                if (f4 > 0.0f) {
                    return Math.min(fMax, f2);
                }
                if (f4 < 0.0f) {
                    return Math.max(fMax, f2);
                }
            }
        }
        return f2;
    }

    static boolean shouldSnapForLargeJump(float f, float f2, float f3, float f4, int i, int i2) {
        if (i <= 0 || i2 <= 0) {
            return true;
        }
        float fMax = Math.max(96.0f, ((float) Math.hypot(i, i2)) * LARGE_JUMP_SCREEN_FRACTION);
        float f5 = f3 - f;
        float f6 = f4 - f2;
        return (f5 * f5) + (f6 * f6) >= fMax * fMax;
    }

    static boolean shouldRetarget(float f, float f2, float f3, float f4, float f5) {
        float f6 = f3 - f;
        float f7 = f4 - f2;
        float fMax = Math.max(0.0f, f5);
        return (f6 * f6) + (f7 * f7) >= fMax * fMax;
    }

    static List<MarkerPlacement> layout(List<MarkerRequest> list, int i, int i2, int i3, int i4, int i5, int i6, float f) {
        MarkerPlacement markerPlacement;
        if (list == null || list.isEmpty() || i <= 0 || i2 <= 0) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(list);
        arrayList.sort(new Comparator() { // from class: com.android.support.BoardHeroOverlayMath$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return BoardHeroOverlayMath.lambda$layout$0((BoardHeroOverlayMath.MarkerRequest) obj, (BoardHeroOverlayMath.MarkerRequest) obj2);
            }
        });
        ArrayList arrayList2 = new ArrayList();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            MarkerRequest markerRequest = (MarkerRequest) it.next();
            float[][] fArr = CANDIDATES;
            int length = fArr.length;
            int i7 = 0;
            while (true) {
                if (i7 >= length) {
                    markerPlacement = null;
                    break;
                }
                float[] fArr2 = fArr[i7];
                markerPlacement = new MarkerPlacement(markerRequest, clamp((markerRequest.anchorX - (markerRequest.width / 2.0f)) + (fArr2[0] * markerRequest.width), i3, (i - i5) - markerRequest.width), clamp(markerRequest.anchorY + f + (fArr2[1] * markerRequest.height), i4, (i2 - i6) - markerRequest.height), false);
                if (!intersectsAny(markerPlacement, arrayList2)) {
                    break;
                }
                i7++;
            }
            if (markerPlacement == null) {
                float f2 = markerRequest.height;
                markerPlacement = new MarkerPlacement(markerRequest, clamp(markerRequest.anchorX - (f2 / 2.0f), i3, (i - i5) - f2), clamp(markerRequest.anchorY + f, i4, (i2 - i6) - f2), true);
            }
            arrayList2.add(markerPlacement);
        }
        return Collections.unmodifiableList(arrayList2);
    }

    static /* synthetic */ int lambda$layout$0(MarkerRequest markerRequest, MarkerRequest markerRequest2) {
        int iCompare = Integer.compare(markerRequest.priority, markerRequest2.priority);
        return iCompare != 0 ? iCompare : Float.compare(markerRequest.anchorY, markerRequest2.anchorY);
    }

    private static boolean intersectsAny(MarkerPlacement markerPlacement, List<MarkerPlacement> list) {
        for (MarkerPlacement markerPlacement2 : list) {
            float f = markerPlacement.avatarOnly ? markerPlacement.request.height : markerPlacement.request.width;
            if (markerPlacement.left < markerPlacement2.left + (markerPlacement2.avatarOnly ? markerPlacement2.request.height : markerPlacement2.request.width) && markerPlacement.left + f > markerPlacement2.left && markerPlacement.top < markerPlacement2.top + markerPlacement2.request.height && markerPlacement.top + markerPlacement.request.height > markerPlacement2.top) {
                return true;
            }
        }
        return false;
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    static final class MarkerRequest {
        final float anchorX;
        final float anchorY;
        final float height;
        final int heroId;
        final boolean isEnemy;
        final int ownedCopies;
        final int priority;
        final String slotKey;
        final float width;

        MarkerRequest(String str, int i, float f, float f2, float f3, float f4, int i2) {
            this(str, i, f, f2, f3, f4, i2, -1, false);
        }

        MarkerRequest(String str, int i, float f, float f2, float f3, float f4, int i2, int i3, boolean z) {
            this.slotKey = str;
            this.heroId = i;
            this.anchorX = f;
            this.anchorY = f2;
            this.width = f3;
            this.height = f4;
            this.priority = i2;
            this.ownedCopies = i3;
            this.isEnemy = z;
        }
    }

    static final class MarkerPlacement {
        final boolean avatarOnly;
        final float left;
        final MarkerRequest request;
        final float top;

        MarkerPlacement(MarkerRequest markerRequest, float f, float f2, boolean z) {
            this.request = markerRequest;
            this.left = f;
            this.top = f2;
            this.avatarOnly = z;
        }
    }
}
