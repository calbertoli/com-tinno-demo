package androidx.constraintlayout.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.util.SparseArray;
import android.util.Xml;
import com.google.android.exoplayer2.text.ttml.TtmlNode;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;

/* JADX INFO: loaded from: classes.dex */
public class ConstraintLayoutStates {
    private static final boolean DEBUG = false;
    public static final String TAG = "ConstraintLayoutStates";
    private final ConstraintLayout mConstraintLayout;
    ConstraintSet mDefaultConstraintSet;
    int mCurrentStateId = -1;
    int mCurrentConstraintNumber = -1;
    private SparseArray<State> mStateList = new SparseArray<>();
    private SparseArray<ConstraintSet> mConstraintSetMap = new SparseArray<>();
    private ConstraintsChangedListener mConstraintsChangedListener = null;

    public void setOnConstraintsChanged(ConstraintsChangedListener constraintsChangedListener) {
        this.mConstraintsChangedListener = constraintsChangedListener;
    }

    ConstraintLayoutStates(Context context, ConstraintLayout constraintLayout, int i) {
        this.mConstraintLayout = constraintLayout;
        load(context, i);
    }

    public boolean needsToChange(int i, float f, float f2) {
        int i2 = this.mCurrentStateId;
        if (i2 != i) {
            return true;
        }
        State stateValueAt = i == -1 ? this.mStateList.valueAt(0) : this.mStateList.get(i2);
        return (this.mCurrentConstraintNumber == -1 || !stateValueAt.mVariants.get(this.mCurrentConstraintNumber).match(f, f2)) && this.mCurrentConstraintNumber != stateValueAt.findMatch(f, f2);
    }

    public void updateConstraints(int i, float f, float f2) {
        ConstraintSet constraintSet;
        int i2;
        State stateValueAt;
        int iFindMatch;
        int i3;
        int i4 = this.mCurrentStateId;
        if (i4 == i) {
            if (i == -1) {
                stateValueAt = this.mStateList.valueAt(0);
            } else {
                stateValueAt = this.mStateList.get(i4);
            }
            if ((this.mCurrentConstraintNumber == -1 || !stateValueAt.mVariants.get(this.mCurrentConstraintNumber).match(f, f2)) && this.mCurrentConstraintNumber != (iFindMatch = stateValueAt.findMatch(f, f2))) {
                ConstraintSet constraintSet2 = iFindMatch == -1 ? this.mDefaultConstraintSet : stateValueAt.mVariants.get(iFindMatch).mConstraintSet;
                if (iFindMatch == -1) {
                    i3 = stateValueAt.mConstraintID;
                } else {
                    i3 = stateValueAt.mVariants.get(iFindMatch).mConstraintID;
                }
                if (constraintSet2 == null) {
                    return;
                }
                this.mCurrentConstraintNumber = iFindMatch;
                ConstraintsChangedListener constraintsChangedListener = this.mConstraintsChangedListener;
                if (constraintsChangedListener != null) {
                    constraintsChangedListener.preLayoutChange(-1, i3);
                }
                constraintSet2.applyTo(this.mConstraintLayout);
                ConstraintsChangedListener constraintsChangedListener2 = this.mConstraintsChangedListener;
                if (constraintsChangedListener2 != null) {
                    constraintsChangedListener2.postLayoutChange(-1, i3);
                    return;
                }
                return;
            }
            return;
        }
        this.mCurrentStateId = i;
        State state = this.mStateList.get(i);
        int iFindMatch2 = state.findMatch(f, f2);
        if (iFindMatch2 == -1) {
            constraintSet = state.mConstraintSet;
        } else {
            constraintSet = state.mVariants.get(iFindMatch2).mConstraintSet;
        }
        if (iFindMatch2 == -1) {
            i2 = state.mConstraintID;
        } else {
            i2 = state.mVariants.get(iFindMatch2).mConstraintID;
        }
        if (constraintSet == null) {
            Log.v("ConstraintLayoutStates", "NO Constraint set found ! id=" + i + ", dim =" + f + ", " + f2);
            return;
        }
        this.mCurrentConstraintNumber = iFindMatch2;
        ConstraintsChangedListener constraintsChangedListener3 = this.mConstraintsChangedListener;
        if (constraintsChangedListener3 != null) {
            constraintsChangedListener3.preLayoutChange(i, i2);
        }
        constraintSet.applyTo(this.mConstraintLayout);
        ConstraintsChangedListener constraintsChangedListener4 = this.mConstraintsChangedListener;
        if (constraintsChangedListener4 != null) {
            constraintsChangedListener4.postLayoutChange(i, i2);
        }
    }

    static class State {
        int mConstraintID;
        ConstraintSet mConstraintSet;
        int mId;
        ArrayList<Variant> mVariants = new ArrayList<>();

        State(Context context, XmlPullParser xmlPullParser) {
            this.mConstraintID = -1;
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
                        ConstraintSet constraintSet = new ConstraintSet();
                        this.mConstraintSet = constraintSet;
                        constraintSet.clone(context, this.mConstraintID);
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
        ConstraintSet mConstraintSet;
        int mId;
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
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(Xml.asAttributeSet(xmlPullParser), R.styleable.Variant);
            int indexCount = typedArrayObtainStyledAttributes.getIndexCount();
            for (int i = 0; i < indexCount; i++) {
                int index = typedArrayObtainStyledAttributes.getIndex(i);
                if (index == R.styleable.Variant_constraints) {
                    this.mConstraintID = typedArrayObtainStyledAttributes.getResourceId(index, this.mConstraintID);
                    String resourceTypeName = context.getResources().getResourceTypeName(this.mConstraintID);
                    context.getResources().getResourceName(this.mConstraintID);
                    if (TtmlNode.TAG_LAYOUT.equals(resourceTypeName)) {
                        ConstraintSet constraintSet = new ConstraintSet();
                        this.mConstraintSet = constraintSet;
                        constraintSet.clone(context, this.mConstraintID);
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

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /* JADX WARN: Removed duplicated region for block: B:27:0x0058  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void load(android.content.Context r11, int r12) {
        /*
            r10 = this;
            java.lang.String r0 = "Error parsing resource: "
            java.lang.String r1 = "ConstraintLayoutStates"
            android.content.res.Resources r2 = r11.getResources()
            android.content.res.XmlResourceParser r2 = r2.getXml(r12)
            int r3 = r2.getEventType()     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            r4 = 0
        L11:
            r5 = 1
            if (r3 == r5) goto La4
            r6 = 2
            if (r3 == r6) goto L19
            goto L7c
        L19:
            java.lang.String r3 = r2.getName()     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            int r7 = r3.hashCode()     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            r8 = 4
            r9 = 3
            switch(r7) {
                case -1349929691: goto L4e;
                case 80204913: goto L44;
                case 1382829617: goto L3b;
                case 1657696882: goto L31;
                case 1901439077: goto L27;
                default: goto L26;
            }     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
        L26:
            goto L58
        L27:
            java.lang.String r5 = "Variant"
            boolean r3 = r3.equals(r5)     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            if (r3 == 0) goto L58
            r5 = r9
            goto L59
        L31:
            java.lang.String r5 = "layoutDescription"
            boolean r3 = r3.equals(r5)     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            if (r3 == 0) goto L58
            r5 = 0
            goto L59
        L3b:
            java.lang.String r7 = "StateSet"
            boolean r3 = r3.equals(r7)     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            if (r3 == 0) goto L58
            goto L59
        L44:
            java.lang.String r5 = "State"
            boolean r3 = r3.equals(r5)     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            if (r3 == 0) goto L58
            r5 = r6
            goto L59
        L4e:
            java.lang.String r5 = "ConstraintSet"
            boolean r3 = r3.equals(r5)     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            if (r3 == 0) goto L58
            r5 = r8
            goto L59
        L58:
            r5 = -1
        L59:
            if (r5 == r6) goto L6f
            if (r5 == r9) goto L64
            if (r5 == r8) goto L60
            goto L7c
        L60:
            r10.parseConstraintSet(r11, r2)     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            goto L7c
        L64:
            androidx.constraintlayout.widget.ConstraintLayoutStates$Variant r3 = new androidx.constraintlayout.widget.ConstraintLayoutStates$Variant     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            r3.<init>(r11, r2)     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            if (r4 == 0) goto L7c
            r4.add(r3)     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            goto L7c
        L6f:
            androidx.constraintlayout.widget.ConstraintLayoutStates$State r3 = new androidx.constraintlayout.widget.ConstraintLayoutStates$State     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            r3.<init>(r11, r2)     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            android.util.SparseArray<androidx.constraintlayout.widget.ConstraintLayoutStates$State> r4 = r10.mStateList     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            int r5 = r3.mId     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            r4.put(r5, r3)     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            r4 = r3
        L7c:
            int r3 = r2.next()     // Catch: java.io.IOException -> L81 org.xmlpull.v1.XmlPullParserException -> L93
            goto L11
        L81:
            r11 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>(r0)
            java.lang.StringBuilder r12 = r2.append(r12)
            java.lang.String r12 = r12.toString()
            android.util.Log.e(r1, r12, r11)
            goto La4
        L93:
            r11 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>(r0)
            java.lang.StringBuilder r12 = r2.append(r12)
            java.lang.String r12 = r12.toString()
            android.util.Log.e(r1, r12, r11)
        La4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.constraintlayout.widget.ConstraintLayoutStates.load(android.content.Context, int):void");
    }

    private void parseConstraintSet(Context context, XmlPullParser xmlPullParser) {
        ConstraintSet constraintSet = new ConstraintSet();
        int attributeCount = xmlPullParser.getAttributeCount();
        for (int i = 0; i < attributeCount; i++) {
            String attributeName = xmlPullParser.getAttributeName(i);
            String attributeValue = xmlPullParser.getAttributeValue(i);
            if (attributeName != null && attributeValue != null && TtmlNode.ATTR_ID.equals(attributeName)) {
                int identifier = attributeValue.contains("/") ? context.getResources().getIdentifier(attributeValue.substring(attributeValue.indexOf(47) + 1), TtmlNode.ATTR_ID, context.getPackageName()) : -1;
                if (identifier == -1) {
                    if (attributeValue.length() > 1) {
                        identifier = Integer.parseInt(attributeValue.substring(1));
                    } else {
                        Log.e("ConstraintLayoutStates", "error in parsing id");
                    }
                }
                constraintSet.load(context, xmlPullParser);
                this.mConstraintSetMap.put(identifier, constraintSet);
                return;
            }
        }
    }
}
