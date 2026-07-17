package androidx.constraintlayout.core.widgets.analyzer;

import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class ChainRun extends WidgetRun {
    private int mChainStyle;
    ArrayList<WidgetRun> mWidgets;

    public ChainRun(ConstraintWidget constraintWidget, int i) {
        super(constraintWidget);
        this.mWidgets = new ArrayList<>();
        this.orientation = i;
        build();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ChainRun ");
        sb.append(this.orientation == 0 ? "horizontal : " : "vertical : ");
        for (WidgetRun widgetRun : this.mWidgets) {
            sb.append("<");
            sb.append(widgetRun);
            sb.append("> ");
        }
        return sb.toString();
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun
    boolean supportsWrapComputation() {
        int size = this.mWidgets.size();
        for (int i = 0; i < size; i++) {
            if (!this.mWidgets.get(i).supportsWrapComputation()) {
                return false;
            }
        }
        return true;
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun
    public long getWrapDimension() {
        int size = this.mWidgets.size();
        long wrapDimension = 0;
        for (int i = 0; i < size; i++) {
            WidgetRun widgetRun = this.mWidgets.get(i);
            wrapDimension = wrapDimension + ((long) widgetRun.start.mMargin) + widgetRun.getWrapDimension() + ((long) widgetRun.end.mMargin);
        }
        return wrapDimension;
    }

    private void build() {
        ConstraintWidget constraintWidget;
        ConstraintWidget constraintWidget2 = this.mWidget;
        ConstraintWidget previousChainMember = constraintWidget2.getPreviousChainMember(this.orientation);
        while (true) {
            ConstraintWidget constraintWidget3 = previousChainMember;
            constraintWidget = constraintWidget2;
            constraintWidget2 = constraintWidget3;
            if (constraintWidget2 == null) {
                break;
            } else {
                previousChainMember = constraintWidget2.getPreviousChainMember(this.orientation);
            }
        }
        this.mWidget = constraintWidget;
        this.mWidgets.add(constraintWidget.getRun(this.orientation));
        ConstraintWidget nextChainMember = constraintWidget.getNextChainMember(this.orientation);
        while (nextChainMember != null) {
            this.mWidgets.add(nextChainMember.getRun(this.orientation));
            nextChainMember = nextChainMember.getNextChainMember(this.orientation);
        }
        for (WidgetRun widgetRun : this.mWidgets) {
            if (this.orientation == 0) {
                widgetRun.mWidget.horizontalChainRun = this;
            } else if (this.orientation == 1) {
                widgetRun.mWidget.verticalChainRun = this;
            }
        }
        if (this.orientation == 0 && ((ConstraintWidgetContainer) this.mWidget.getParent()).isRtl() && this.mWidgets.size() > 1) {
            ArrayList<WidgetRun> arrayList = this.mWidgets;
            this.mWidget = arrayList.get(arrayList.size() - 1).mWidget;
        }
        this.mChainStyle = this.orientation == 0 ? this.mWidget.getHorizontalChainStyle() : this.mWidget.getVerticalChainStyle();
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun
    void clear() {
        this.mRunGroup = null;
        Iterator<WidgetRun> it = this.mWidgets.iterator();
        while (it.hasNext()) {
            it.next().clear();
        }
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun
    void reset() {
        this.start.resolved = false;
        this.end.resolved = false;
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun, androidx.constraintlayout.core.widgets.analyzer.Dependency
    public void update(Dependency dependency) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        float f;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        float f2;
        int i11;
        int i12;
        int i13;
        if (this.start.resolved && this.end.resolved) {
            ConstraintWidget parent = this.mWidget.getParent();
            boolean zIsRtl = parent instanceof ConstraintWidgetContainer ? ((ConstraintWidgetContainer) parent).isRtl() : false;
            int i14 = this.end.value - this.start.value;
            int size = this.mWidgets.size();
            int i15 = 0;
            while (true) {
                i = -1;
                i2 = 8;
                if (i15 >= size) {
                    i15 = -1;
                    break;
                } else if (this.mWidgets.get(i15).mWidget.getVisibility() != 8) {
                    break;
                } else {
                    i15++;
                }
            }
            int i16 = size - 1;
            int i17 = i16;
            while (true) {
                if (i17 < 0) {
                    break;
                }
                if (this.mWidgets.get(i17).mWidget.getVisibility() != 8) {
                    i = i17;
                    break;
                }
                i17--;
            }
            int i18 = 0;
            while (i18 < 2) {
                int i19 = 0;
                i4 = 0;
                i5 = 0;
                int i20 = 0;
                f = 0.0f;
                while (i19 < size) {
                    WidgetRun widgetRun = this.mWidgets.get(i19);
                    if (widgetRun.mWidget.getVisibility() != i2) {
                        i20++;
                        if (i19 > 0 && i19 >= i15) {
                            i4 += widgetRun.start.mMargin;
                        }
                        int i21 = widgetRun.mDimension.value;
                        boolean z = widgetRun.mDimensionBehavior != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
                        if (z) {
                            if (this.orientation == 0 && !widgetRun.mWidget.mHorizontalRun.mDimension.resolved) {
                                return;
                            }
                            if (this.orientation == 1 && !widgetRun.mWidget.mVerticalRun.mDimension.resolved) {
                                return;
                            }
                        } else {
                            if (widgetRun.matchConstraintsType == 1 && i18 == 0) {
                                i21 = widgetRun.mDimension.wrapValue;
                                i5++;
                            } else if (widgetRun.mDimension.resolved) {
                            }
                            z = true;
                        }
                        if (z) {
                            i4 += i21;
                        } else {
                            i5++;
                            float f3 = widgetRun.mWidget.mWeight[this.orientation];
                            if (f3 >= 0.0f) {
                                f += f3;
                            }
                        }
                        if (i19 < i16 && i19 < i) {
                            i4 += -widgetRun.end.mMargin;
                        }
                    }
                    i19++;
                    i2 = 8;
                }
                if (i4 < i14 || i5 == 0) {
                    i3 = i20;
                    break;
                } else {
                    i18++;
                    i2 = 8;
                }
            }
            i3 = 0;
            i4 = 0;
            i5 = 0;
            f = 0.0f;
            int i22 = this.start.value;
            if (zIsRtl) {
                i22 = this.end.value;
            }
            if (i4 > i14) {
                i22 = zIsRtl ? i22 + ((int) (((i4 - i14) / 2.0f) + 0.5f)) : i22 - ((int) (((i4 - i14) / 2.0f) + 0.5f));
            }
            if (i5 > 0) {
                float f4 = i14 - i4;
                int i23 = (int) ((f4 / i5) + 0.5f);
                int i24 = 0;
                int i25 = 0;
                while (i24 < size) {
                    WidgetRun widgetRun2 = this.mWidgets.get(i24);
                    int i26 = i23;
                    if (widgetRun2.mWidget.getVisibility() == 8 || widgetRun2.mDimensionBehavior != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || widgetRun2.mDimension.resolved) {
                        i10 = i22;
                        f2 = f4;
                        i11 = i4;
                    } else {
                        int i27 = f > 0.0f ? (int) (((widgetRun2.mWidget.mWeight[this.orientation] * f4) / f) + 0.5f) : i26;
                        if (this.orientation == 0) {
                            i12 = widgetRun2.mWidget.mMatchConstraintMaxWidth;
                            f2 = f4;
                            i13 = widgetRun2.mWidget.mMatchConstraintMinWidth;
                        } else {
                            f2 = f4;
                            i12 = widgetRun2.mWidget.mMatchConstraintMaxHeight;
                            i13 = widgetRun2.mWidget.mMatchConstraintMinHeight;
                        }
                        i11 = i4;
                        i10 = i22;
                        int iMax = Math.max(i13, widgetRun2.matchConstraintsType == 1 ? Math.min(i27, widgetRun2.mDimension.wrapValue) : i27);
                        if (i12 > 0) {
                            iMax = Math.min(i12, iMax);
                        }
                        if (iMax != i27) {
                            i25++;
                            i27 = iMax;
                        }
                        widgetRun2.mDimension.resolve(i27);
                    }
                    i24++;
                    i23 = i26;
                    f4 = f2;
                    i4 = i11;
                    i22 = i10;
                }
                i6 = i22;
                int i28 = i4;
                if (i25 > 0) {
                    i5 -= i25;
                    int i29 = 0;
                    for (int i30 = 0; i30 < size; i30++) {
                        WidgetRun widgetRun3 = this.mWidgets.get(i30);
                        if (widgetRun3.mWidget.getVisibility() != 8) {
                            if (i30 > 0 && i30 >= i15) {
                                i29 += widgetRun3.start.mMargin;
                            }
                            i29 += widgetRun3.mDimension.value;
                            if (i30 < i16 && i30 < i) {
                                i29 += -widgetRun3.end.mMargin;
                            }
                        }
                    }
                    i4 = i29;
                } else {
                    i4 = i28;
                }
                i8 = 2;
                if (this.mChainStyle == 2 && i25 == 0) {
                    i7 = 0;
                    this.mChainStyle = 0;
                } else {
                    i7 = 0;
                }
            } else {
                i6 = i22;
                i7 = 0;
                i8 = 2;
            }
            if (i4 > i14) {
                this.mChainStyle = i8;
            }
            if (i3 > 0 && i5 == 0 && i15 == i) {
                this.mChainStyle = i8;
            }
            int i31 = this.mChainStyle;
            if (i31 == 1) {
                if (i3 > 1) {
                    i9 = (i14 - i4) / (i3 - 1);
                } else {
                    i9 = i3 == 1 ? (i14 - i4) / 2 : i7;
                }
                if (i5 > 0) {
                    i9 = i7;
                }
                int i32 = i6;
                for (int i33 = i7; i33 < size; i33++) {
                    WidgetRun widgetRun4 = this.mWidgets.get(zIsRtl ? size - (i33 + 1) : i33);
                    if (widgetRun4.mWidget.getVisibility() == 8) {
                        widgetRun4.start.resolve(i32);
                        widgetRun4.end.resolve(i32);
                    } else {
                        if (i33 > 0) {
                            i32 = zIsRtl ? i32 - i9 : i32 + i9;
                        }
                        if (i33 > 0 && i33 >= i15) {
                            if (zIsRtl) {
                                i32 -= widgetRun4.start.mMargin;
                            } else {
                                i32 += widgetRun4.start.mMargin;
                            }
                        }
                        if (zIsRtl) {
                            widgetRun4.end.resolve(i32);
                        } else {
                            widgetRun4.start.resolve(i32);
                        }
                        int i34 = widgetRun4.mDimension.value;
                        if (widgetRun4.mDimensionBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widgetRun4.matchConstraintsType == 1) {
                            i34 = widgetRun4.mDimension.wrapValue;
                        }
                        i32 = zIsRtl ? i32 - i34 : i32 + i34;
                        if (zIsRtl) {
                            widgetRun4.start.resolve(i32);
                        } else {
                            widgetRun4.end.resolve(i32);
                        }
                        widgetRun4.mResolved = true;
                        if (i33 < i16 && i33 < i) {
                            if (zIsRtl) {
                                i32 -= -widgetRun4.end.mMargin;
                            } else {
                                i32 += -widgetRun4.end.mMargin;
                            }
                        }
                    }
                }
                return;
            }
            if (i31 == 0) {
                int i35 = (i14 - i4) / (i3 + 1);
                if (i5 > 0) {
                    i35 = i7;
                }
                int i36 = i6;
                for (int i37 = i7; i37 < size; i37++) {
                    WidgetRun widgetRun5 = this.mWidgets.get(zIsRtl ? size - (i37 + 1) : i37);
                    if (widgetRun5.mWidget.getVisibility() == 8) {
                        widgetRun5.start.resolve(i36);
                        widgetRun5.end.resolve(i36);
                    } else {
                        int i38 = zIsRtl ? i36 - i35 : i36 + i35;
                        if (i37 > 0 && i37 >= i15) {
                            if (zIsRtl) {
                                i38 -= widgetRun5.start.mMargin;
                            } else {
                                i38 += widgetRun5.start.mMargin;
                            }
                        }
                        if (zIsRtl) {
                            widgetRun5.end.resolve(i38);
                        } else {
                            widgetRun5.start.resolve(i38);
                        }
                        int iMin = widgetRun5.mDimension.value;
                        if (widgetRun5.mDimensionBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widgetRun5.matchConstraintsType == 1) {
                            iMin = Math.min(iMin, widgetRun5.mDimension.wrapValue);
                        }
                        i36 = zIsRtl ? i38 - iMin : i38 + iMin;
                        if (zIsRtl) {
                            widgetRun5.start.resolve(i36);
                        } else {
                            widgetRun5.end.resolve(i36);
                        }
                        if (i37 < i16 && i37 < i) {
                            if (zIsRtl) {
                                i36 -= -widgetRun5.end.mMargin;
                            } else {
                                i36 += -widgetRun5.end.mMargin;
                            }
                        }
                    }
                }
                return;
            }
            if (i31 == 2) {
                float horizontalBiasPercent = this.orientation == 0 ? this.mWidget.getHorizontalBiasPercent() : this.mWidget.getVerticalBiasPercent();
                if (zIsRtl) {
                    horizontalBiasPercent = 1.0f - horizontalBiasPercent;
                }
                int i39 = (int) (((i14 - i4) * horizontalBiasPercent) + 0.5f);
                if (i39 < 0 || i5 > 0) {
                    i39 = i7;
                }
                int i40 = zIsRtl ? i6 - i39 : i6 + i39;
                for (int i41 = i7; i41 < size; i41++) {
                    WidgetRun widgetRun6 = this.mWidgets.get(zIsRtl ? size - (i41 + 1) : i41);
                    if (widgetRun6.mWidget.getVisibility() == 8) {
                        widgetRun6.start.resolve(i40);
                        widgetRun6.end.resolve(i40);
                    } else {
                        if (i41 > 0 && i41 >= i15) {
                            if (zIsRtl) {
                                i40 -= widgetRun6.start.mMargin;
                            } else {
                                i40 += widgetRun6.start.mMargin;
                            }
                        }
                        if (zIsRtl) {
                            widgetRun6.end.resolve(i40);
                        } else {
                            widgetRun6.start.resolve(i40);
                        }
                        int i42 = widgetRun6.mDimension.value;
                        if (widgetRun6.mDimensionBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widgetRun6.matchConstraintsType == 1) {
                            i42 = widgetRun6.mDimension.wrapValue;
                        }
                        i40 = zIsRtl ? i40 - i42 : i40 + i42;
                        if (zIsRtl) {
                            widgetRun6.start.resolve(i40);
                        } else {
                            widgetRun6.end.resolve(i40);
                        }
                        if (i41 < i16 && i41 < i) {
                            if (zIsRtl) {
                                i40 -= -widgetRun6.end.mMargin;
                            } else {
                                i40 += -widgetRun6.end.mMargin;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun
    public void applyToWidget() {
        for (int i = 0; i < this.mWidgets.size(); i++) {
            this.mWidgets.get(i).applyToWidget();
        }
    }

    private ConstraintWidget getFirstVisibleWidget() {
        for (int i = 0; i < this.mWidgets.size(); i++) {
            WidgetRun widgetRun = this.mWidgets.get(i);
            if (widgetRun.mWidget.getVisibility() != 8) {
                return widgetRun.mWidget;
            }
        }
        return null;
    }

    private ConstraintWidget getLastVisibleWidget() {
        for (int size = this.mWidgets.size() - 1; size >= 0; size--) {
            WidgetRun widgetRun = this.mWidgets.get(size);
            if (widgetRun.mWidget.getVisibility() != 8) {
                return widgetRun.mWidget;
            }
        }
        return null;
    }

    @Override // androidx.constraintlayout.core.widgets.analyzer.WidgetRun
    void apply() {
        Iterator<WidgetRun> it = this.mWidgets.iterator();
        while (it.hasNext()) {
            it.next().apply();
        }
        int size = this.mWidgets.size();
        if (size < 1) {
            return;
        }
        ConstraintWidget constraintWidget = this.mWidgets.get(0).mWidget;
        ConstraintWidget constraintWidget2 = this.mWidgets.get(size - 1).mWidget;
        if (this.orientation == 0) {
            ConstraintAnchor constraintAnchor = constraintWidget.mLeft;
            ConstraintAnchor constraintAnchor2 = constraintWidget2.mRight;
            DependencyNode target = getTarget(constraintAnchor, 0);
            int margin = constraintAnchor.getMargin();
            ConstraintWidget firstVisibleWidget = getFirstVisibleWidget();
            if (firstVisibleWidget != null) {
                margin = firstVisibleWidget.mLeft.getMargin();
            }
            if (target != null) {
                addTarget(this.start, target, margin);
            }
            DependencyNode target2 = getTarget(constraintAnchor2, 0);
            int margin2 = constraintAnchor2.getMargin();
            ConstraintWidget lastVisibleWidget = getLastVisibleWidget();
            if (lastVisibleWidget != null) {
                margin2 = lastVisibleWidget.mRight.getMargin();
            }
            if (target2 != null) {
                addTarget(this.end, target2, -margin2);
            }
        } else {
            ConstraintAnchor constraintAnchor3 = constraintWidget.mTop;
            ConstraintAnchor constraintAnchor4 = constraintWidget2.mBottom;
            DependencyNode target3 = getTarget(constraintAnchor3, 1);
            int margin3 = constraintAnchor3.getMargin();
            ConstraintWidget firstVisibleWidget2 = getFirstVisibleWidget();
            if (firstVisibleWidget2 != null) {
                margin3 = firstVisibleWidget2.mTop.getMargin();
            }
            if (target3 != null) {
                addTarget(this.start, target3, margin3);
            }
            DependencyNode target4 = getTarget(constraintAnchor4, 1);
            int margin4 = constraintAnchor4.getMargin();
            ConstraintWidget lastVisibleWidget2 = getLastVisibleWidget();
            if (lastVisibleWidget2 != null) {
                margin4 = lastVisibleWidget2.mBottom.getMargin();
            }
            if (target4 != null) {
                addTarget(this.end, target4, -margin4);
            }
        }
        this.start.updateDelegate = this;
        this.end.updateDelegate = this;
    }
}
