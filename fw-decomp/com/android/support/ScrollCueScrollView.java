package com.android.support;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/* loaded from: classes.dex */
public class ScrollCueScrollView extends ScrollView {
    public ScrollCueScrollView(Context context) {
        super(context);
    }

    public ScrollCueScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ScrollCueScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return ScrollEdgeCue.routeTouch(this, motionEvent) || super.dispatchTouchEvent(motionEvent);
    }
}
