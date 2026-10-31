package com.android.support;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.OvershootInterpolator;
import android.view.animation.PathInterpolator;
import java.util.ArrayList;
import java.util.List;

/* loaded from: classes.dex */
final class AllInPriorityGrid extends ViewGroup {
    private static final int DRAG_SLOP_DP = 8;
    private static final long DROP_MS = 260;
    private static final int LIFT_ELEVATION_DP = 10;
    private static final long LIFT_MS = 130;
    private static final float LIFT_SCALE = 1.12f;
    private static final long SHIFT_MS = 190;
    private final int cellGap;
    private final int cellSize;
    private final List<View> cells;
    private int columns;
    private DragDecorator decorator;
    private float downX;
    private float downY;
    private View dragCell;
    private int dragSlot;
    private boolean dragging;
    private float grabDx;
    private float grabDy;
    private OnOrderChangedListener listener;
    private final List<Integer> order;
    private boolean readOnly;
    private static final PathInterpolator EASE_OUT = new PathInterpolator(0.12f, 0.92f, 0.28f, 1.0f);
    private static final OvershootInterpolator SETTLE = new OvershootInterpolator(1.1f);

    interface CellFactory {
        View create(int i, int i2);
    }

    interface DragDecorator {
        void onDragStateChanged(View view, boolean z);
    }

    interface OnOrderChangedListener {
        void onOrderChanged();

        void onOrderCommitted();
    }

    interface PositionBinder {
        void bind(View view, int i, int i2);
    }

    private static int previousSlotOf(int i, int i2, int i3) {
        return i2 < i3 ? (i < i2 || i >= i3) ? i : i + 1 : (i <= i3 || i > i2) ? i : i - 1;
    }

    AllInPriorityGrid(Context context, int i, int i2) {
        super(context);
        this.order = new ArrayList();
        this.cells = new ArrayList();
        this.columns = 1;
        this.dragSlot = -1;
        this.cellSize = Math.max(1, i);
        this.cellGap = Math.max(0, i2);
        setClipChildren(false);
        setClipToPadding(false);
        setChildrenDrawingOrderEnabled(true);
    }

    void setOnOrderChangedListener(OnOrderChangedListener onOrderChangedListener) {
        this.listener = onOrderChangedListener;
    }

    void setDragDecorator(DragDecorator dragDecorator) {
        this.decorator = dragDecorator;
    }

    List<Integer> order() {
        return new ArrayList(this.order);
    }

    void setEntries(List<Integer> list, CellFactory cellFactory) {
        cancelDrag();
        this.order.clear();
        this.cells.clear();
        if (list != null) {
            this.order.addAll(list);
        }
        removeAllViews();
        for (int i = 0; i < this.order.size(); i++) {
            View viewCreate = cellFactory.create(this.order.get(i).intValue(), i);
            this.cells.add(viewCreate);
            addView(viewCreate);
        }
        requestLayout();
    }

    void rebindPositions(PositionBinder positionBinder) {
        for (int i = 0; i < this.cells.size() && i < this.order.size(); i++) {
            positionBinder.bind(this.cells.get(i), this.order.get(i).intValue(), i);
        }
    }

    private int stride() {
        return this.cellSize + this.cellGap;
    }

    private int slotLeft(int i) {
        return AllInPriorityOrder.slotLeft(i, this.columns, stride());
    }

    private int slotTop(int i) {
        return AllInPriorityOrder.slotTop(i, this.columns, stride());
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int iMax = Math.max(stride(), View.MeasureSpec.getSize(i));
        this.columns = Math.max(1, iMax / stride());
        int size = this.cells.size();
        if (size == 0) {
            i3 = 0;
        } else {
            int i4 = this.columns;
            i3 = ((size + i4) - 1) / i4;
        }
        int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.cellSize, 1073741824);
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            getChildAt(i5).measure(iMakeMeasureSpec, iMakeMeasureSpec);
        }
        setMeasuredDimension(iMax, Math.max(this.cellSize, i3 * stride()));
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        for (int i5 = 0; i5 < this.cells.size(); i5++) {
            View view = this.cells.get(i5);
            int iSlotLeft = slotLeft(i5);
            int iSlotTop = slotTop(i5);
            int i6 = this.cellSize;
            view.layout(iSlotLeft, iSlotTop, iSlotLeft + i6, i6 + iSlotTop);
        }
    }

    @Override // android.view.ViewGroup
    protected int getChildDrawingOrder(int i, int i2) {
        int iIndexOfChild;
        View view = this.dragCell;
        if (view != null && (iIndexOfChild = indexOfChild(view)) >= 0) {
            if (i2 == i - 1) {
                return iIndexOfChild;
            }
            if (i2 >= iIndexOfChild) {
                return i2 + 1;
            }
        }
        return i2;
    }

    void setReadOnly(boolean z) {
        this.readOnly = z;
    }

    boolean isDragging() {
        return this.dragging;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        OnOrderChangedListener onOrderChangedListener;
        if (this.readOnly) {
            return false;
        }
        int i = (int) (getResources().getDisplayMetrics().density * 8.0f);
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            int iCellIndexAt = AllInPriorityOrder.cellIndexAt((int) motionEvent.getX(), (int) motionEvent.getY(), stride(), stride(), this.columns, this.order.size());
            this.dragSlot = iCellIndexAt;
            this.downX = motionEvent.getX();
            this.downY = motionEvent.getY();
            this.dragging = false;
            if (iCellIndexAt >= 0) {
                new ViewParentGuard(this).disallowIntercept(true);
            }
            return iCellIndexAt >= 0;
        }
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                if (this.dragSlot < 0) {
                    return false;
                }
                if (!this.dragging) {
                    float f = i;
                    if (Math.abs(motionEvent.getX() - this.downX) < f && Math.abs(motionEvent.getY() - this.downY) < f) {
                        return true;
                    }
                    beginDrag();
                }
                followFinger(motionEvent.getX(), motionEvent.getY());
                int iCellIndexAt2 = AllInPriorityOrder.cellIndexAt((int) ((motionEvent.getX() - this.grabDx) + (this.cellSize / 2.0f)), (int) ((motionEvent.getY() - this.grabDy) + (this.cellSize / 2.0f)), stride(), stride(), this.columns, this.order.size());
                if (iCellIndexAt2 >= 0 && iCellIndexAt2 != this.dragSlot) {
                    moveDragTo(iCellIndexAt2);
                    followFinger(motionEvent.getX(), motionEvent.getY());
                }
                return true;
            }
            if (actionMasked != 3) {
                return false;
            }
        }
        new ViewParentGuard(this).disallowIntercept(false);
        boolean z = this.dragging;
        endDrag();
        if (z && (onOrderChangedListener = this.listener) != null) {
            onOrderChangedListener.onOrderCommitted();
        }
        return true;
    }

    private static final class ViewParentGuard {
        private final View view;

        ViewParentGuard(View view) {
            this.view = view;
        }

        void disallowIntercept(boolean z) {
            ViewParent parent = this.view.getParent();
            if (parent != null) {
                parent.requestDisallowInterceptTouchEvent(z);
            }
        }
    }

    private boolean animated() {
        return OverlayUiMotion.animationsEnabled(getContext());
    }

    private void beginDrag() {
        this.dragging = true;
        int i = this.dragSlot;
        View view = (i < 0 || i >= this.cells.size()) ? null : this.cells.get(this.dragSlot);
        this.dragCell = view;
        if (view == null) {
            return;
        }
        this.grabDx = this.downX - slotLeft(this.dragSlot);
        this.grabDy = this.downY - slotTop(this.dragSlot);
        DragDecorator dragDecorator = this.decorator;
        if (dragDecorator != null) {
            dragDecorator.onDragStateChanged(this.dragCell, true);
        }
        this.dragCell.setElevation(getResources().getDisplayMetrics().density * 10.0f);
        boolean zAnimated = animated();
        View view2 = this.dragCell;
        if (zAnimated) {
            view2.animate().cancel();
            this.dragCell.animate().scaleX(LIFT_SCALE).scaleY(LIFT_SCALE).setDuration(LIFT_MS).setInterpolator(EASE_OUT).start();
        } else {
            view2.setScaleX(LIFT_SCALE);
            this.dragCell.setScaleY(LIFT_SCALE);
        }
        performHapticFeedback(1);
        invalidate();
    }

    private void followFinger(float f, float f2) {
        int i;
        View view = this.dragCell;
        if (view == null || (i = this.dragSlot) < 0) {
            return;
        }
        view.setTranslationX((f - this.grabDx) - slotLeft(i));
        this.dragCell.setTranslationY((f2 - this.grabDy) - slotTop(this.dragSlot));
    }

    private void moveDragTo(int i) {
        int i2;
        int iPreviousSlotOf;
        int size = this.cells.size();
        if (i < 0 || i >= size || (i2 = this.dragSlot) < 0 || i2 >= size) {
            return;
        }
        float[] fArr = new float[size];
        float[] fArr2 = new float[size];
        for (int i3 = 0; i3 < size; i3++) {
            View view = this.cells.get(i3);
            fArr[i3] = slotLeft(i3) + view.getTranslationX();
            fArr2[i3] = slotTop(i3) + view.getTranslationY();
        }
        AllInPriorityOrder.move(this.order, this.dragSlot, i);
        AllInPriorityOrder.move(this.cells, this.dragSlot, i);
        int i4 = this.dragSlot;
        this.dragSlot = i;
        for (int i5 = 0; i5 < size; i5++) {
            View view2 = this.cells.get(i5);
            if (view2 != this.dragCell && (iPreviousSlotOf = previousSlotOf(i5, i4, i)) != i5) {
                float fSlotLeft = fArr[iPreviousSlotOf] - slotLeft(i5);
                float fSlotTop = fArr2[iPreviousSlotOf] - slotTop(i5);
                view2.animate().cancel();
                view2.setTranslationX(fSlotLeft);
                view2.setTranslationY(fSlotTop);
                if (animated()) {
                    view2.animate().translationX(0.0f).translationY(0.0f).setDuration(SHIFT_MS).setInterpolator(EASE_OUT).start();
                } else {
                    view2.setTranslationX(0.0f);
                    view2.setTranslationY(0.0f);
                }
            }
        }
        requestLayout();
        performHapticFeedback(4);
        OnOrderChangedListener onOrderChangedListener = this.listener;
        if (onOrderChangedListener != null) {
            onOrderChangedListener.onOrderChanged();
        }
    }

    private void endDrag() {
        final View view = this.dragCell;
        this.dragCell = null;
        this.dragSlot = -1;
        this.dragging = false;
        if (view == null) {
            return;
        }
        DragDecorator dragDecorator = this.decorator;
        if (dragDecorator != null) {
            dragDecorator.onDragStateChanged(view, false);
        }
        view.animate().cancel();
        if (animated()) {
            view.animate().translationX(0.0f).translationY(0.0f).scaleX(1.0f).scaleY(1.0f).setDuration(260L).setInterpolator(SETTLE).withEndAction(new Runnable() { // from class: com.android.support.AllInPriorityGrid$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    view.setElevation(0.0f);
                }
            }).start();
        } else {
            settleImmediately(view);
        }
        performHapticFeedback(4);
        invalidate();
    }

    private void cancelDrag() {
        View view = this.dragCell;
        this.dragCell = null;
        this.dragSlot = -1;
        this.dragging = false;
        if (view == null) {
            return;
        }
        DragDecorator dragDecorator = this.decorator;
        if (dragDecorator != null) {
            dragDecorator.onDragStateChanged(view, false);
        }
        view.animate().cancel();
        settleImmediately(view);
    }

    private static void settleImmediately(View view) {
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
        view.setScaleX(1.0f);
        view.setScaleY(1.0f);
        view.setElevation(0.0f);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        cancelDrag();
        super.onDetachedFromWindow();
    }
}
