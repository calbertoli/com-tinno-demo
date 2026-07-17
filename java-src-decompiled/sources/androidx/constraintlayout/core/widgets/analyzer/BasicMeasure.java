package androidx.constraintlayout.core.widgets.analyzer;

import androidx.constraintlayout.core.LinearSystem;
import androidx.constraintlayout.core.widgets.ConstraintAnchor;
import androidx.constraintlayout.core.widgets.ConstraintWidget;
import androidx.constraintlayout.core.widgets.ConstraintWidgetContainer;
import androidx.constraintlayout.core.widgets.Guideline;
import androidx.constraintlayout.core.widgets.Helper;
import androidx.constraintlayout.core.widgets.Optimizer;
import androidx.constraintlayout.core.widgets.VirtualLayout;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class BasicMeasure {
    public static final int AT_MOST = Integer.MIN_VALUE;
    private static final boolean DEBUG = false;
    private static final boolean DO_NOT_USE = false;
    public static final int EXACTLY = 1073741824;
    public static final int FIXED = -3;
    public static final int MATCH_PARENT = -1;
    private static final int MODE_SHIFT = 30;
    public static final int UNSPECIFIED = 0;
    public static final int WRAP_CONTENT = -2;
    private ConstraintWidgetContainer mConstraintWidgetContainer;
    private final ArrayList<ConstraintWidget> mVariableDimensionsWidgets = new ArrayList<>();
    private Measure mMeasure = new Measure();

    public static class Measure {
        public static int SELF_DIMENSIONS = 0;
        public static int TRY_GIVEN_DIMENSIONS = 1;
        public static int USE_GIVEN_DIMENSIONS = 2;
        public ConstraintWidget.DimensionBehaviour horizontalBehavior;
        public int horizontalDimension;
        public int measureStrategy;
        public int measuredBaseline;
        public boolean measuredHasBaseline;
        public int measuredHeight;
        public boolean measuredNeedsSolverPass;
        public int measuredWidth;
        public ConstraintWidget.DimensionBehaviour verticalBehavior;
        public int verticalDimension;
    }

    public interface Measurer {
        void didMeasures();

        void measure(ConstraintWidget constraintWidget, Measure measure);
    }

    public void updateHierarchy(ConstraintWidgetContainer constraintWidgetContainer) {
        this.mVariableDimensionsWidgets.clear();
        int size = constraintWidgetContainer.mChildren.size();
        for (int i = 0; i < size; i++) {
            ConstraintWidget constraintWidget = constraintWidgetContainer.mChildren.get(i);
            if (constraintWidget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || constraintWidget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                this.mVariableDimensionsWidgets.add(constraintWidget);
            }
        }
        constraintWidgetContainer.invalidateGraph();
    }

    public BasicMeasure(ConstraintWidgetContainer constraintWidgetContainer) {
        this.mConstraintWidgetContainer = constraintWidgetContainer;
    }

    /* JADX WARN: Removed duplicated region for block: B:67:0x00af  */
    /* JADX WARN: Removed duplicated region for block: B:81:0x00c1 A[SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void measureChildren(androidx.constraintlayout.core.widgets.ConstraintWidgetContainer r12) {
        /*
            Method dump skipped, instruction units count: 201
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.core.widgets.analyzer.BasicMeasure.measureChildren(androidx.constraintlayout.core.widgets.ConstraintWidgetContainer):void");
    }

    private void solveLinearSystem(ConstraintWidgetContainer constraintWidgetContainer, String str, int i, int i2, int i3) {
        long jNanoTime = constraintWidgetContainer.mMetrics != null ? System.nanoTime() : 0L;
        int minWidth = constraintWidgetContainer.getMinWidth();
        int minHeight = constraintWidgetContainer.getMinHeight();
        constraintWidgetContainer.setMinWidth(0);
        constraintWidgetContainer.setMinHeight(0);
        constraintWidgetContainer.setWidth(i2);
        constraintWidgetContainer.setHeight(i3);
        constraintWidgetContainer.setMinWidth(minWidth);
        constraintWidgetContainer.setMinHeight(minHeight);
        this.mConstraintWidgetContainer.setPass(i);
        this.mConstraintWidgetContainer.layout();
        if (constraintWidgetContainer.mMetrics != null) {
            long jNanoTime2 = System.nanoTime();
            constraintWidgetContainer.mMetrics.mSolverPasses++;
            constraintWidgetContainer.mMetrics.measuresLayoutDuration += jNanoTime2 - jNanoTime;
        }
    }

    public long solverMeasure(ConstraintWidgetContainer constraintWidgetContainer, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        boolean zDirectMeasureWithOrientation;
        int i10;
        long j;
        int i11;
        boolean z;
        Measurer measurer;
        boolean z2;
        int i12;
        int i13;
        int i14;
        boolean z3;
        BasicMeasure basicMeasure = this;
        Measurer measurer2 = constraintWidgetContainer.getMeasurer();
        int size = constraintWidgetContainer.mChildren.size();
        int width = constraintWidgetContainer.getWidth();
        int height = constraintWidgetContainer.getHeight();
        boolean zEnabled = Optimizer.enabled(i, 128);
        boolean z4 = zEnabled || Optimizer.enabled(i, 64);
        if (z4) {
            for (int i15 = 0; i15 < size; i15++) {
                ConstraintWidget constraintWidget = constraintWidgetContainer.mChildren.get(i15);
                boolean z5 = (constraintWidget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) && (constraintWidget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) && constraintWidget.getDimensionRatio() > 0.0f;
                if ((constraintWidget.isInHorizontalChain() && z5) || ((constraintWidget.isInVerticalChain() && z5) || (constraintWidget instanceof VirtualLayout) || constraintWidget.isInHorizontalChain() || constraintWidget.isInVerticalChain())) {
                    z4 = false;
                    break;
                }
            }
        }
        if (z4 && LinearSystem.sMetrics != null) {
            LinearSystem.sMetrics.measures++;
        }
        boolean z6 = z4 & ((i4 == 1073741824 && i6 == 1073741824) || zEnabled);
        int i16 = 2;
        if (z6) {
            int iMin = Math.min(constraintWidgetContainer.getMaxWidth(), i5);
            int iMin2 = Math.min(constraintWidgetContainer.getMaxHeight(), i7);
            if (i4 == 1073741824 && constraintWidgetContainer.getWidth() != iMin) {
                constraintWidgetContainer.setWidth(iMin);
                constraintWidgetContainer.invalidateGraph();
            }
            if (i6 == 1073741824 && constraintWidgetContainer.getHeight() != iMin2) {
                constraintWidgetContainer.setHeight(iMin2);
                constraintWidgetContainer.invalidateGraph();
            }
            if (i4 == 1073741824 && i6 == 1073741824) {
                zDirectMeasureWithOrientation = constraintWidgetContainer.directMeasure(zEnabled);
                i10 = 2;
            } else {
                boolean zDirectMeasureSetup = constraintWidgetContainer.directMeasureSetup(zEnabled);
                if (i4 == 1073741824) {
                    zDirectMeasureSetup &= constraintWidgetContainer.directMeasureWithOrientation(zEnabled, 0);
                    i10 = 1;
                } else {
                    i10 = 0;
                }
                if (i6 == 1073741824) {
                    zDirectMeasureWithOrientation = constraintWidgetContainer.directMeasureWithOrientation(zEnabled, 1) & zDirectMeasureSetup;
                    i10++;
                } else {
                    zDirectMeasureWithOrientation = zDirectMeasureSetup;
                }
            }
            if (zDirectMeasureWithOrientation) {
                constraintWidgetContainer.updateFromRuns(i4 == 1073741824, i6 == 1073741824);
            }
        } else {
            zDirectMeasureWithOrientation = false;
            i10 = 0;
        }
        if (!zDirectMeasureWithOrientation || i10 != 2) {
            int optimizationLevel = constraintWidgetContainer.getOptimizationLevel();
            if (size > 0) {
                measureChildren(constraintWidgetContainer);
            }
            jNanoTime = constraintWidgetContainer.mMetrics != null ? System.nanoTime() : 0L;
            updateHierarchy(constraintWidgetContainer);
            int size2 = basicMeasure.mVariableDimensionsWidgets.size();
            if (size > 0) {
                solveLinearSystem(constraintWidgetContainer, "First pass", 0, width, height);
            }
            if (size2 > 0) {
                boolean z7 = constraintWidgetContainer.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                boolean z8 = constraintWidgetContainer.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                int iMax = Math.max(constraintWidgetContainer.getWidth(), basicMeasure.mConstraintWidgetContainer.getMinWidth());
                int iMax2 = Math.max(constraintWidgetContainer.getHeight(), basicMeasure.mConstraintWidgetContainer.getMinHeight());
                int i17 = 0;
                boolean zNeedSolverPass = false;
                while (i17 < size2) {
                    ConstraintWidget constraintWidget2 = basicMeasure.mVariableDimensionsWidgets.get(i17);
                    long j2 = jNanoTime;
                    if (constraintWidget2 instanceof VirtualLayout) {
                        int width2 = constraintWidget2.getWidth();
                        int height2 = constraintWidget2.getHeight();
                        i12 = optimizationLevel;
                        boolean zMeasure = zNeedSolverPass | basicMeasure.measure(measurer2, constraintWidget2, Measure.TRY_GIVEN_DIMENSIONS);
                        if (constraintWidgetContainer.mMetrics != null) {
                            i13 = width;
                            i14 = height;
                            constraintWidgetContainer.mMetrics.measuredMatchWidgets++;
                        } else {
                            i13 = width;
                            i14 = height;
                        }
                        int width3 = constraintWidget2.getWidth();
                        int height3 = constraintWidget2.getHeight();
                        if (width3 != width2) {
                            constraintWidget2.setWidth(width3);
                            if (z7 && constraintWidget2.getRight() > iMax) {
                                iMax = Math.max(iMax, constraintWidget2.getRight() + constraintWidget2.getAnchor(ConstraintAnchor.Type.RIGHT).getMargin());
                            }
                            z3 = true;
                        } else {
                            z3 = zMeasure;
                        }
                        if (height3 != height2) {
                            constraintWidget2.setHeight(height3);
                            if (z8 && constraintWidget2.getBottom() > iMax2) {
                                iMax2 = Math.max(iMax2, constraintWidget2.getBottom() + constraintWidget2.getAnchor(ConstraintAnchor.Type.BOTTOM).getMargin());
                            }
                            z3 = true;
                        }
                        zNeedSolverPass = z3 | ((VirtualLayout) constraintWidget2).needSolverPass();
                    } else {
                        i13 = width;
                        i14 = height;
                        i12 = optimizationLevel;
                    }
                    i17++;
                    optimizationLevel = i12;
                    jNanoTime = j2;
                    width = i13;
                    height = i14;
                    i16 = 2;
                }
                j = jNanoTime;
                int i18 = width;
                int i19 = height;
                int i20 = optimizationLevel;
                int i21 = i16;
                int i22 = 0;
                while (i22 < i21) {
                    int i23 = 0;
                    while (i23 < size2) {
                        ConstraintWidget constraintWidget3 = basicMeasure.mVariableDimensionsWidgets.get(i23);
                        if (((constraintWidget3 instanceof Helper) && !(constraintWidget3 instanceof VirtualLayout)) || (constraintWidget3 instanceof Guideline) || constraintWidget3.getVisibility() == 8 || ((z6 && constraintWidget3.mHorizontalRun.mDimension.resolved && constraintWidget3.mVerticalRun.mDimension.resolved) || (constraintWidget3 instanceof VirtualLayout))) {
                            measurer = measurer2;
                        } else {
                            int width4 = constraintWidget3.getWidth();
                            int height4 = constraintWidget3.getHeight();
                            int baselineDistance = constraintWidget3.getBaselineDistance();
                            int i24 = Measure.TRY_GIVEN_DIMENSIONS;
                            if (i22 == 1) {
                                i24 = Measure.USE_GIVEN_DIMENSIONS;
                            }
                            boolean zMeasure2 = zNeedSolverPass | basicMeasure.measure(measurer2, constraintWidget3, i24);
                            if (constraintWidgetContainer.mMetrics != null) {
                                z = zMeasure2;
                                measurer = measurer2;
                                constraintWidgetContainer.mMetrics.measuredMatchWidgets++;
                            } else {
                                z = zMeasure2;
                                measurer = measurer2;
                            }
                            int width5 = constraintWidget3.getWidth();
                            int height5 = constraintWidget3.getHeight();
                            if (width5 != width4) {
                                constraintWidget3.setWidth(width5);
                                if (z7 && constraintWidget3.getRight() > iMax) {
                                    iMax = Math.max(iMax, constraintWidget3.getRight() + constraintWidget3.getAnchor(ConstraintAnchor.Type.RIGHT).getMargin());
                                }
                                z2 = true;
                            } else {
                                z2 = z;
                            }
                            if (height5 != height4) {
                                constraintWidget3.setHeight(height5);
                                if (z8 && constraintWidget3.getBottom() > iMax2) {
                                    iMax2 = Math.max(iMax2, constraintWidget3.getBottom() + constraintWidget3.getAnchor(ConstraintAnchor.Type.BOTTOM).getMargin());
                                }
                                z2 = true;
                            }
                            zNeedSolverPass = (!constraintWidget3.hasBaseline() || baselineDistance == constraintWidget3.getBaselineDistance()) ? z2 : true;
                        }
                        i23++;
                        basicMeasure = this;
                        measurer2 = measurer;
                    }
                    Measurer measurer3 = measurer2;
                    if (!zNeedSolverPass) {
                        break;
                    }
                    i22++;
                    solveLinearSystem(constraintWidgetContainer, "intermediate pass", i22, i18, i19);
                    i21 = 2;
                    zNeedSolverPass = false;
                    basicMeasure = this;
                    measurer2 = measurer3;
                }
                i11 = i20;
            } else {
                j = jNanoTime;
                i11 = optimizationLevel;
            }
            constraintWidgetContainer.setOptimizationLevel(i11);
            jNanoTime = j;
        }
        return constraintWidgetContainer.mMetrics != null ? System.nanoTime() - jNanoTime : jNanoTime;
    }

    private boolean measure(Measurer measurer, ConstraintWidget constraintWidget, int i) {
        this.mMeasure.horizontalBehavior = constraintWidget.getHorizontalDimensionBehaviour();
        this.mMeasure.verticalBehavior = constraintWidget.getVerticalDimensionBehaviour();
        this.mMeasure.horizontalDimension = constraintWidget.getWidth();
        this.mMeasure.verticalDimension = constraintWidget.getHeight();
        this.mMeasure.measuredNeedsSolverPass = false;
        this.mMeasure.measureStrategy = i;
        boolean z = this.mMeasure.horizontalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
        boolean z2 = this.mMeasure.verticalBehavior == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT;
        boolean z3 = z && constraintWidget.mDimensionRatio > 0.0f;
        boolean z4 = z2 && constraintWidget.mDimensionRatio > 0.0f;
        if (z3 && constraintWidget.mResolvedMatchConstraintDefault[0] == 4) {
            this.mMeasure.horizontalBehavior = ConstraintWidget.DimensionBehaviour.FIXED;
        }
        if (z4 && constraintWidget.mResolvedMatchConstraintDefault[1] == 4) {
            this.mMeasure.verticalBehavior = ConstraintWidget.DimensionBehaviour.FIXED;
        }
        measurer.measure(constraintWidget, this.mMeasure);
        constraintWidget.setWidth(this.mMeasure.measuredWidth);
        constraintWidget.setHeight(this.mMeasure.measuredHeight);
        constraintWidget.setHasBaseline(this.mMeasure.measuredHasBaseline);
        constraintWidget.setBaselineDistance(this.mMeasure.measuredBaseline);
        this.mMeasure.measureStrategy = Measure.SELF_DIMENSIONS;
        return this.mMeasure.measuredNeedsSolverPass;
    }
}
