package androidx.constraintlayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class StateSet {
    private static final boolean DEBUG = false;
    public static final String TAG = "ConstraintLayoutStates";
    int mDefaultState = -1;
    int mCurrentStateId = -1;
    int mCurrentConstraintNumber = -1;
    private SparseArray<State> mStateList = new SparseArray<>();
    private ConstraintsChangedListener mConstraintsChangedListener = null;

    public void setOnConstraintsChanged(ConstraintsChangedListener constraintsChangedListener) {
        this.mConstraintsChangedListener = constraintsChangedListener;
    }

    public StateSet(Context context, XmlPullParser xmlPullParser) {
        load(context, xmlPullParser);
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /* JADX WARN: Removed duplicated region for block: B:35:0x0079  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void load(android.content.Context r11, org.xmlpull.v1.XmlPullParser r12) {
        /*
            r10 = this;
            java.lang.String r0 = "Error parsing XML resource"
            java.lang.String r1 = "ConstraintLayoutStates"
            android.util.AttributeSet r2 = android.util.Xml.asAttributeSet(r12)
            int[] r3 = androidx.constraintlayout.widget.R.styleable.StateSet
            android.content.res.TypedArray r2 = r11.obtainStyledAttributes(r2, r3)
            int r3 = r2.getIndexCount()
            r4 = 0
            r5 = r4
        L14:
            if (r5 >= r3) goto L29
            int r6 = r2.getIndex(r5)
            int r7 = androidx.constraintlayout.widget.R.styleable.StateSet_defaultState
            if (r6 != r7) goto L26
            int r7 = r10.mDefaultState
            int r6 = r2.getResourceId(r6, r7)
            r10.mDefaultState = r6
        L26:
            int r5 = r5 + 1
            goto L14
        L29:
            r2.recycle()
            int r2 = r12.getEventType()     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            r3 = 0
        L31:
            r5 = 1
            if (r2 == r5) goto La4
            java.lang.String r6 = "StateSet"
            r7 = 3
            r8 = 2
            if (r2 == r8) goto L48
            if (r2 == r7) goto L3d
            goto L96
        L3d:
            java.lang.String r2 = r12.getName()     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            boolean r2 = r6.equals(r2)     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            if (r2 == 0) goto L96
            return
        L48:
            java.lang.String r2 = r12.getName()     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            int r9 = r2.hashCode()     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            switch(r9) {
                case 80204913: goto L6f;
                case 1301459538: goto L65;
                case 1382829617: goto L5e;
                case 1901439077: goto L54;
                default: goto L53;
            }     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
        L53:
            goto L79
        L54:
            java.lang.String r5 = "Variant"
            boolean r2 = r2.equals(r5)     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            if (r2 == 0) goto L79
            r5 = r7
            goto L7a
        L5e:
            boolean r2 = r2.equals(r6)     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            if (r2 == 0) goto L79
            goto L7a
        L65:
            java.lang.String r5 = "LayoutDescription"
            boolean r2 = r2.equals(r5)     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            if (r2 == 0) goto L79
            r5 = r4
            goto L7a
        L6f:
            java.lang.String r5 = "State"
            boolean r2 = r2.equals(r5)     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            if (r2 == 0) goto L79
            r5 = r8
            goto L7a
        L79:
            r5 = -1
        L7a:
            if (r5 == r8) goto L8a
            if (r5 == r7) goto L7f
            goto L96
        L7f:
            androidx.constraintlayout.widget.StateSet$Variant r2 = new androidx.constraintlayout.widget.StateSet$Variant     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            r2.<init>(r11, r12)     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            if (r3 == 0) goto L96
            r3.add(r2)     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            goto L96
        L8a:
            androidx.constraintlayout.widget.StateSet$State r3 = new androidx.constraintlayout.widget.StateSet$State     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            r3.<init>(r11, r12)     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            android.util.SparseArray<androidx.constraintlayout.widget.StateSet$State> r2 = r10.mStateList     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            int r5 = r3.mId     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            r2.put(r5, r3)     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
        L96:
            int r2 = r12.next()     // Catch: java.io.IOException -> L9b org.xmlpull.v1.XmlPullParserException -> La0
            goto L31
        L9b:
            r11 = move-exception
            android.util.Log.e(r1, r0, r11)
            goto La4
        La0:
            r11 = move-exception
            android.util.Log.e(r1, r0, r11)
        La4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.widget.StateSet.load(android.content.Context, org.xmlpull.v1.XmlPullParser):void");
    }

    public boolean needsToChange(int i, float f, float f2) {
        int i2 = this.mCurrentStateId;
        if (i2 != i) {
            return true;
        }
        State stateValueAt = i == -1 ? this.mStateList.valueAt(0) : this.mStateList.get(i2);
        return (this.mCurrentConstraintNumber == -1 || !stateValueAt.mVariants.get(this.mCurrentConstraintNumber).match(f, f2)) && this.mCurrentConstraintNumber != stateValueAt.findMatch(f, f2);
    }

    public int stateGetConstraintID(int i, int i2, int i3) {
        return updateConstraints(-1, i, i2, i3);
    }

    public int convertToConstraintSet(int i, int i2, float f, float f2) {
        State state = this.mStateList.get(i2);
        if (state == null) {
            return i2;
        }
        if (f == -1.0f || f2 == -1.0f) {
            if (state.mConstraintID == i) {
                return i;
            }
            Iterator<Variant> it = state.mVariants.iterator();
            while (it.hasNext()) {
                if (i == it.next().mConstraintID) {
                    return i;
                }
            }
            return state.mConstraintID;
        }
        Variant variant = null;
        for (Variant variant2 : state.mVariants) {
            if (variant2.match(f, f2)) {
                if (i == variant2.mConstraintID) {
                    return i;
                }
                variant = variant2;
            }
        }
        if (variant != null) {
            return variant.mConstraintID;
        }
        return state.mConstraintID;
    }

    public int updateConstraints(int i, int i2, float f, float f2) {
        State stateValueAt;
        int iFindMatch;
        if (i != i2) {
            State state = this.mStateList.get(i2);
            if (state == null) {
                return -1;
            }
            int iFindMatch2 = state.findMatch(f, f2);
            return iFindMatch2 == -1 ? state.mConstraintID : state.mVariants.get(iFindMatch2).mConstraintID;
        }
        if (i2 == -1) {
            stateValueAt = this.mStateList.valueAt(0);
        } else {
            stateValueAt = this.mStateList.get(this.mCurrentStateId);
        }
        if (stateValueAt == null) {
            return -1;
        }
        return ((this.mCurrentConstraintNumber == -1 || !stateValueAt.mVariants.get(i).match(f, f2)) && i != (iFindMatch = stateValueAt.findMatch(f, f2))) ? iFindMatch == -1 ? stateValueAt.mConstraintID : stateValueAt.mVariants.get(iFindMatch).mConstraintID : i;
    }

    static class State {
        int mConstraintID;
        int mId;
        boolean mIsLayout;
        ArrayList<Variant> mVariants = new ArrayList<>();

        State(Context context, XmlPullParser xmlPullParser) {
            this.mConstraintID = -1;
            this.mIsLayout = false;
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(xmlPullParser), R.styleable.State);
            int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = typedArrayObtainStyledAttributes.getIndex(i);
                if (index == R.styleable.State_android_id) {
                    this.mId = typedArrayObtainStyledAttributes.getResourceId(index, this.mId);
                } else if (index == R.styleable.State_constraints) {
                    this.mConstraintID = typedArrayObtainStyledAttributes.getResourceId(index, this.mConstraintID);
                    String resourceTypeName = context.getResources().getResourceTypeName(this.mConstraintID);
                    context.getResources().getResourceName(this.mConstraintID);
                    if (TtmlNode.TAG_LAYOUT.equals(resourceTypeName)) {
                        this.mIsLayout = true;
                    }
                }
            }
            typedArrayObtainStyledAttributes.recycle();
        }

        void add(Variant variant) {
            this.mVariants.add(variant);
        }

        public int findMatch(float f, float f2) {
            for (int i = 0; i < this.mVariants.size(); i++) {
                if (this.mVariants.get(i).match(f, f2)) {
                    return i;
                }
            }
            return -1;
        }
    }

    static class Variant {
        int mConstraintID;
        int mId;
        boolean mIsLayout;
        float mMaxHeight;
        float mMaxWidth;
        float mMinHeight;
        float mMinWidth;

        Variant(Context context, XmlPullParser xmlPullParser) {
            this.mMinWidth = Float.NaN;
            this.mMinHeight = Float.NaN;
            this.mMaxWidth = Float.NaN;
            this.mMaxHeight = Float.NaN;
            this.mConstraintID = -1;
            this.mIsLayout = false;
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(xmlPullParser), R.styleable.Variant);
            int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = typedArrayObtainStyledAttributes.getIndex(i);
                if (index == R.styleable.Variant_constraints) {
                    this.mConstraintID = typedArrayObtainStyledAttributes.getResourceId(index, this.mConstraintID);
                    String resourceTypeName = context.getResources().getResourceTypeName(this.mConstraintID);
                    context.getResources().getResourceName(this.mConstraintID);
                    if (TtmlNode.TAG_LAYOUT.equals(resourceTypeName)) {
                        this.mIsLayout = true;
                    }
                } else if (index == R.styleable.Variant_region_heightLessThan) {
                    this.mMaxHeight = typedArrayObtainStyledAttributes.getDimension(index, this.mMaxHeight);
                } else if (index == R.styleable.Variant_region_heightMoreThan) {
                    this.mMinHeight = typedArrayObtainStyledAttributes.getDimension(index, this.mMinHeight);
                } else if (index == R.styleable.Variant_region_widthLessThan) {
                    this.mMaxWidth = typedArrayObtainStyledAttributes.getDimension(index, this.mMaxWidth);
                } else if (index == R.styleable.Variant_region_widthMoreThan) {
                    this.mMinWidth = typedArrayObtainStyledAttributes.getDimension(index, this.mMinWidth);
                } else {
                    Log.v("ConstraintLayoutStates", "Unknown tag");
                }
            }
            typedArrayObtainStyledAttributes.recycle();
        }

        boolean match(float f, float f2) {
            if (!Float.isNaN(this.mMinWidth) && f < this.mMinWidth) {
                return false;
            }
            if (!Float.isNaN(this.mMinHeight) && f2 < this.mMinHeight) {
                return false;
            }
            if (Float.isNaN(this.mMaxWidth) || f <= this.mMaxWidth) {
                return Float.isNaN(this.mMaxHeight) || f2 <= this.mMaxHeight;
            }
            return false;
        }
    }
}
