package androidx.constraintlayout.motion.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import androidx.constraintlayout.motion.utils.ViewSpline;
import androidx.constraintlayout.widget.ConstraintAttribute;
import androidx.constraintlayout.widget.R;
import com.google.common.base.Ascii;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class KeyAttributes extends Key {
    private static final boolean DEBUG = false;
    public static final int KEY_TYPE = 1;
    static final String NAME = "KeyAttribute";
    private static final String TAG = "KeyAttributes";
    private String mTransitionEasing;
    private int mCurveFit = -1;
    private boolean mVisibility = false;
    private float mAlpha = Float.NaN;
    private float mElevation = Float.NaN;
    private float mRotation = Float.NaN;
    private float mRotationX = Float.NaN;
    private float mRotationY = Float.NaN;
    private float mPivotX = Float.NaN;
    private float mPivotY = Float.NaN;
    private float mTransitionPathRotate = Float.NaN;
    private float mScaleX = Float.NaN;
    private float mScaleY = Float.NaN;
    private float mTranslationX = Float.NaN;
    private float mTranslationY = Float.NaN;
    private float mTranslationZ = Float.NaN;
    private float mProgress = Float.NaN;

    int getCurveFit() {
        return this.mCurveFit;
    }

    public KeyAttributes() {
        this.mType = 1;
        this.mCustomConstraints = new HashMap<>();
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    public void load(Context context, AttributeSet attributeSet) {
        Loader.read(this, context.obtainStyledAttributes(attributeSet, R.styleable.KeyAttribute));
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    public void getAttributeNames(HashSet<String> hashSet) {
        if (!Float.isNaN(this.mAlpha)) {
            hashSet.add("alpha");
        }
        if (!Float.isNaN(this.mElevation)) {
            hashSet.add("elevation");
        }
        if (!Float.isNaN(this.mRotation)) {
            hashSet.add(Key.ROTATION);
        }
        if (!Float.isNaN(this.mRotationX)) {
            hashSet.add("rotationX");
        }
        if (!Float.isNaN(this.mRotationY)) {
            hashSet.add("rotationY");
        }
        if (!Float.isNaN(this.mPivotX)) {
            hashSet.add(Key.PIVOT_X);
        }
        if (!Float.isNaN(this.mPivotY)) {
            hashSet.add(Key.PIVOT_Y);
        }
        if (!Float.isNaN(this.mTranslationX)) {
            hashSet.add("translationX");
        }
        if (!Float.isNaN(this.mTranslationY)) {
            hashSet.add("translationY");
        }
        if (!Float.isNaN(this.mTranslationZ)) {
            hashSet.add("translationZ");
        }
        if (!Float.isNaN(this.mTransitionPathRotate)) {
            hashSet.add("transitionPathRotate");
        }
        if (!Float.isNaN(this.mScaleX)) {
            hashSet.add("scaleX");
        }
        if (!Float.isNaN(this.mScaleY)) {
            hashSet.add("scaleY");
        }
        if (!Float.isNaN(this.mProgress)) {
            hashSet.add("progress");
        }
        if (this.mCustomConstraints.size() > 0) {
            Iterator<String> it = this.mCustomConstraints.keySet().iterator();
            while (it.hasNext()) {
                hashSet.add("CUSTOM," + it.next());
            }
        }
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    public void setInterpolation(HashMap<String, Integer> map) {
        if (this.mCurveFit == -1) {
            return;
        }
        if (!Float.isNaN(this.mAlpha)) {
            map.put("alpha", Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mElevation)) {
            map.put("elevation", Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mRotation)) {
            map.put(Key.ROTATION, Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mRotationX)) {
            map.put("rotationX", Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mRotationY)) {
            map.put("rotationY", Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mPivotX)) {
            map.put(Key.PIVOT_X, Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mPivotY)) {
            map.put(Key.PIVOT_Y, Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mTranslationX)) {
            map.put("translationX", Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mTranslationY)) {
            map.put("translationY", Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mTranslationZ)) {
            map.put("translationZ", Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mTransitionPathRotate)) {
            map.put("transitionPathRotate", Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mScaleX)) {
            map.put("scaleX", Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mScaleY)) {
            map.put("scaleY", Integer.valueOf(this.mCurveFit));
        }
        if (!Float.isNaN(this.mProgress)) {
            map.put("progress", Integer.valueOf(this.mCurveFit));
        }
        if (this.mCustomConstraints.size() > 0) {
            Iterator<String> it = this.mCustomConstraints.keySet().iterator();
            while (it.hasNext()) {
                map.put("CUSTOM," + it.next(), Integer.valueOf(this.mCurveFit));
            }
        }
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    @Override // androidx.constraintlayout.motion.widget.Key
    public void addValues(HashMap<String, ViewSpline> map) {
        for (String str : map.keySet()) {
            ViewSpline viewSpline = map.get(str);
            if (viewSpline != null) {
                byte b = 7;
                if (str.startsWith("CUSTOM")) {
                    ConstraintAttribute constraintAttribute = this.mCustomConstraints.get(str.substring(7));
                    if (constraintAttribute != null) {
                        ((ViewSpline.CustomSet) viewSpline).setPoint(this.mFramePosition, constraintAttribute);
                    }
                } else {
                    str.hashCode();
                    switch (str.hashCode()) {
                        case -1249320806:
                            b = str.equals("rotationX") ? (byte) 0 : (byte) -1;
                            break;
                        case -1249320805:
                            b = str.equals("rotationY") ? (byte) 1 : (byte) -1;
                            break;
                        case -1225497657:
                            b = str.equals("translationX") ? (byte) 2 : (byte) -1;
                            break;
                        case -1225497656:
                            b = str.equals("translationY") ? (byte) 3 : (byte) -1;
                            break;
                        case -1225497655:
                            b = str.equals("translationZ") ? (byte) 4 : (byte) -1;
                            break;
                        case -1001078227:
                            b = str.equals("progress") ? (byte) 5 : (byte) -1;
                            break;
                        case -908189618:
                            b = str.equals("scaleX") ? (byte) 6 : (byte) -1;
                            break;
                        case -908189617:
                            if (!str.equals("scaleY")) {
                                b = -1;
                            }
                            break;
                        case -760884510:
                            b = str.equals(Key.PIVOT_X) ? (byte) 8 : (byte) -1;
                            break;
                        case -760884509:
                            b = str.equals(Key.PIVOT_Y) ? (byte) 9 : (byte) -1;
                            break;
                        case -40300674:
                            b = str.equals(Key.ROTATION) ? (byte) 10 : (byte) -1;
                            break;
                        case -4379043:
                            b = str.equals("elevation") ? Ascii.VT : (byte) -1;
                            break;
                        case 37232917:
                            b = str.equals("transitionPathRotate") ? Ascii.FF : (byte) -1;
                            break;
                        case 92909918:
                            b = str.equals("alpha") ? Ascii.CR : (byte) -1;
                            break;
                        default:
                            b = -1;
                            break;
                    }
                    switch (b) {
                        case 0:
                            if (!Float.isNaN(this.mRotationX)) {
                                viewSpline.setPoint(this.mFramePosition, this.mRotationX);
                            }
                            break;
                        case 1:
                            if (!Float.isNaN(this.mRotationY)) {
                                viewSpline.setPoint(this.mFramePosition, this.mRotationY);
                            }
                            break;
                        case 2:
                            if (!Float.isNaN(this.mTranslationX)) {
                                viewSpline.setPoint(this.mFramePosition, this.mTranslationX);
                            }
                            break;
                        case 3:
                            if (!Float.isNaN(this.mTranslationY)) {
                                viewSpline.setPoint(this.mFramePosition, this.mTranslationY);
                            }
                            break;
                        case 4:
                            if (!Float.isNaN(this.mTranslationZ)) {
                                viewSpline.setPoint(this.mFramePosition, this.mTranslationZ);
                            }
                            break;
                        case 5:
                            if (!Float.isNaN(this.mProgress)) {
                                viewSpline.setPoint(this.mFramePosition, this.mProgress);
                            }
                            break;
                        case 6:
                            if (!Float.isNaN(this.mScaleX)) {
                                viewSpline.setPoint(this.mFramePosition, this.mScaleX);
                            }
                            break;
                        case 7:
                            if (!Float.isNaN(this.mScaleY)) {
                                viewSpline.setPoint(this.mFramePosition, this.mScaleY);
                            }
                            break;
                        case 8:
                            if (!Float.isNaN(this.mRotationX)) {
                                viewSpline.setPoint(this.mFramePosition, this.mPivotX);
                            }
                            break;
                        case 9:
                            if (!Float.isNaN(this.mRotationY)) {
                                viewSpline.setPoint(this.mFramePosition, this.mPivotY);
                            }
                            break;
                        case 10:
                            if (!Float.isNaN(this.mRotation)) {
                                viewSpline.setPoint(this.mFramePosition, this.mRotation);
                            }
                            break;
                        case 11:
                            if (!Float.isNaN(this.mElevation)) {
                                viewSpline.setPoint(this.mFramePosition, this.mElevation);
                            }
                            break;
                        case 12:
                            if (!Float.isNaN(this.mTransitionPathRotate)) {
                                viewSpline.setPoint(this.mFramePosition, this.mTransitionPathRotate);
                            }
                            break;
                        case 13:
                            if (!Float.isNaN(this.mAlpha)) {
                                viewSpline.setPoint(this.mFramePosition, this.mAlpha);
                            }
                            break;
                    }
                }
            }
        }
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    @Override // androidx.constraintlayout.motion.widget.Key
    public void setValue(String str, Object obj) {
        str.hashCode();
        byte b = -1;
        switch (str.hashCode()) {
            case -1913008125:
                if (str.equals(Key.MOTIONPROGRESS)) {
                    b = 0;
                }
                break;
            case -1812823328:
                if (str.equals("transitionEasing")) {
                    b = 1;
                }
                break;
            case -1249320806:
                if (str.equals("rotationX")) {
                    b = 2;
                }
                break;
            case -1249320805:
                if (str.equals("rotationY")) {
                    b = 3;
                }
                break;
            case -1225497657:
                if (str.equals("translationX")) {
                    b = 4;
                }
                break;
            case -1225497656:
                if (str.equals("translationY")) {
                    b = 5;
                }
                break;
            case -1225497655:
                if (str.equals("translationZ")) {
                    b = 6;
                }
                break;
            case -908189618:
                if (str.equals("scaleX")) {
                    b = 7;
                }
                break;
            case -908189617:
                if (str.equals("scaleY")) {
                    b = 8;
                }
                break;
            case -760884510:
                if (str.equals(Key.PIVOT_X)) {
                    b = 9;
                }
                break;
            case -760884509:
                if (str.equals(Key.PIVOT_Y)) {
                    b = 10;
                }
                break;
            case -40300674:
                if (str.equals(Key.ROTATION)) {
                    b = Ascii.VT;
                }
                break;
            case -4379043:
                if (str.equals("elevation")) {
                    b = Ascii.FF;
                }
                break;
            case 37232917:
                if (str.equals("transitionPathRotate")) {
                    b = Ascii.CR;
                }
                break;
            case 92909918:
                if (str.equals("alpha")) {
                    b = Ascii.SO;
                }
                break;
            case 579057826:
                if (str.equals("curveFit")) {
                    b = Ascii.SI;
                }
                break;
            case 1941332754:
                if (str.equals("visibility")) {
                    b = Ascii.DLE;
                }
                break;
        }
        switch (b) {
            case 0:
                this.mProgress = toFloat(obj);
                break;
            case 1:
                this.mTransitionEasing = obj.toString();
                break;
            case 2:
                this.mRotationX = toFloat(obj);
                break;
            case 3:
                this.mRotationY = toFloat(obj);
                break;
            case 4:
                this.mTranslationX = toFloat(obj);
                break;
            case 5:
                this.mTranslationY = toFloat(obj);
                break;
            case 6:
                this.mTranslationZ = toFloat(obj);
                break;
            case 7:
                this.mScaleX = toFloat(obj);
                break;
            case 8:
                this.mScaleY = toFloat(obj);
                break;
            case 9:
                this.mPivotX = toFloat(obj);
                break;
            case 10:
                this.mPivotY = toFloat(obj);
                break;
            case 11:
                this.mRotation = toFloat(obj);
                break;
            case 12:
                this.mElevation = toFloat(obj);
                break;
            case 13:
                this.mTransitionPathRotate = toFloat(obj);
                break;
            case 14:
                this.mAlpha = toFloat(obj);
                break;
            case 15:
                this.mCurveFit = toInt(obj);
                break;
            case 16:
                this.mVisibility = toBoolean(obj);
                break;
        }
    }

    private static class Loader {
        private static final int ANDROID_ALPHA = 1;
        private static final int ANDROID_ELEVATION = 2;
        private static final int ANDROID_PIVOT_X = 19;
        private static final int ANDROID_PIVOT_Y = 20;
        private static final int ANDROID_ROTATION = 4;
        private static final int ANDROID_ROTATION_X = 5;
        private static final int ANDROID_ROTATION_Y = 6;
        private static final int ANDROID_SCALE_X = 7;
        private static final int ANDROID_SCALE_Y = 14;
        private static final int ANDROID_TRANSLATION_X = 15;
        private static final int ANDROID_TRANSLATION_Y = 16;
        private static final int ANDROID_TRANSLATION_Z = 17;
        private static final int CURVE_FIT = 13;
        private static final int FRAME_POSITION = 12;
        private static final int PROGRESS = 18;
        private static final int TARGET_ID = 10;
        private static final int TRANSITION_EASING = 9;
        private static final int TRANSITION_PATH_ROTATE = 8;
        private static SparseIntArray sAttrMap;

        private Loader() {
        }

        static {
            SparseIntArray sparseIntArray = new SparseIntArray();
            sAttrMap = sparseIntArray;
            sparseIntArray.append(R.styleable.KeyAttribute_android_alpha, 1);
            sAttrMap.append(R.styleable.KeyAttribute_android_elevation, 2);
            sAttrMap.append(R.styleable.KeyAttribute_android_rotation, 4);
            sAttrMap.append(R.styleable.KeyAttribute_android_rotationX, 5);
            sAttrMap.append(R.styleable.KeyAttribute_android_rotationY, 6);
            sAttrMap.append(R.styleable.KeyAttribute_android_transformPivotX, 19);
            sAttrMap.append(R.styleable.KeyAttribute_android_transformPivotY, 20);
            sAttrMap.append(R.styleable.KeyAttribute_android_scaleX, 7);
            sAttrMap.append(R.styleable.KeyAttribute_transitionPathRotate, 8);
            sAttrMap.append(R.styleable.KeyAttribute_transitionEasing, 9);
            sAttrMap.append(R.styleable.KeyAttribute_motionTarget, 10);
            sAttrMap.append(R.styleable.KeyAttribute_framePosition, 12);
            sAttrMap.append(R.styleable.KeyAttribute_curveFit, 13);
            sAttrMap.append(R.styleable.KeyAttribute_android_scaleY, 14);
            sAttrMap.append(R.styleable.KeyAttribute_android_translationX, 15);
            sAttrMap.append(R.styleable.KeyAttribute_android_translationY, 16);
            sAttrMap.append(R.styleable.KeyAttribute_android_translationZ, 17);
            sAttrMap.append(R.styleable.KeyAttribute_motionProgress, 18);
        }

        public static void read(KeyAttributes keyAttributes, TypedArray typedArray) {
            int indexCount = typedArray.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = typedArray.getIndex(i);
                switch (sAttrMap.get(index)) {
                    case 1:
                        keyAttributes.mAlpha = typedArray.getFloat(index, keyAttributes.mAlpha);
                        break;
                    case 2:
                        keyAttributes.mElevation = typedArray.getDimension(index, keyAttributes.mElevation);
                        break;
                    case 3:
                    case 11:
                    default:
                        Log.e(KeyAttributes.NAME, "unused attribute 0x" + Integer.toHexString(index) + "   " + sAttrMap.get(index));
                        break;
                    case 4:
                        keyAttributes.mRotation = typedArray.getFloat(index, keyAttributes.mRotation);
                        break;
                    case 5:
                        keyAttributes.mRotationX = typedArray.getFloat(index, keyAttributes.mRotationX);
                        break;
                    case 6:
                        keyAttributes.mRotationY = typedArray.getFloat(index, keyAttributes.mRotationY);
                        break;
                    case 7:
                        keyAttributes.mScaleX = typedArray.getFloat(index, keyAttributes.mScaleX);
                        break;
                    case 8:
                        keyAttributes.mTransitionPathRotate = typedArray.getFloat(index, keyAttributes.mTransitionPathRotate);
                        break;
                    case 9:
                        keyAttributes.mTransitionEasing = typedArray.getString(index);
                        break;
                    case 10:
                        if (MotionLayout.IS_IN_EDIT_MODE) {
                            keyAttributes.mTargetId = typedArray.getResourceId(index, keyAttributes.mTargetId);
                            if (keyAttributes.mTargetId == -1) {
                                keyAttributes.mTargetString = typedArray.getString(index);
                            }
                        } else if (typedArray.peekValue(index).type == 3) {
                            keyAttributes.mTargetString = typedArray.getString(index);
                        } else {
                            keyAttributes.mTargetId = typedArray.getResourceId(index, keyAttributes.mTargetId);
                        }
                        break;
                    case 12:
                        keyAttributes.mFramePosition = typedArray.getInt(index, keyAttributes.mFramePosition);
                        break;
                    case 13:
                        keyAttributes.mCurveFit = typedArray.getInteger(index, keyAttributes.mCurveFit);
                        break;
                    case 14:
                        keyAttributes.mScaleY = typedArray.getFloat(index, keyAttributes.mScaleY);
                        break;
                    case 15:
                        keyAttributes.mTranslationX = typedArray.getDimension(index, keyAttributes.mTranslationX);
                        break;
                    case 16:
                        keyAttributes.mTranslationY = typedArray.getDimension(index, keyAttributes.mTranslationY);
                        break;
                    case 17:
                        keyAttributes.mTranslationZ = typedArray.getDimension(index, keyAttributes.mTranslationZ);
                        break;
                    case 18:
                        keyAttributes.mProgress = typedArray.getFloat(index, keyAttributes.mProgress);
                        break;
                    case 19:
                        keyAttributes.mPivotX = typedArray.getDimension(index, keyAttributes.mPivotX);
                        break;
                    case 20:
                        keyAttributes.mPivotY = typedArray.getDimension(index, keyAttributes.mPivotY);
                        break;
                }
            }
        }
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    public Key copy(Key key) {
        super.copy(key);
        KeyAttributes keyAttributes = (KeyAttributes) key;
        this.mCurveFit = keyAttributes.mCurveFit;
        this.mVisibility = keyAttributes.mVisibility;
        this.mAlpha = keyAttributes.mAlpha;
        this.mElevation = keyAttributes.mElevation;
        this.mRotation = keyAttributes.mRotation;
        this.mRotationX = keyAttributes.mRotationX;
        this.mRotationY = keyAttributes.mRotationY;
        this.mPivotX = keyAttributes.mPivotX;
        this.mPivotY = keyAttributes.mPivotY;
        this.mTransitionPathRotate = keyAttributes.mTransitionPathRotate;
        this.mScaleX = keyAttributes.mScaleX;
        this.mScaleY = keyAttributes.mScaleY;
        this.mTranslationX = keyAttributes.mTranslationX;
        this.mTranslationY = keyAttributes.mTranslationY;
        this.mTranslationZ = keyAttributes.mTranslationZ;
        this.mProgress = keyAttributes.mProgress;
        this.mTransitionEasing = keyAttributes.mTransitionEasing;
        return this;
    }

    @Override // androidx.constraintlayout.motion.widget.Key
    /* JADX INFO: renamed from: clone */
    public Key mo13clone() {
        return new KeyAttributes().copy(this);
    }
}
