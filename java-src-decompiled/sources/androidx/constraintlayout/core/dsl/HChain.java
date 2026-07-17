package androidx.constraintlayout.core.dsl;

import androidx.constraintlayout.core.dsl.Chain;
import androidx.constraintlayout.core.dsl.Constraint;
import androidx.constraintlayout.core.dsl.Helper;
import com.google.android.exoplayer2.text.ttml.TtmlNode;

/* JADX INFO: loaded from: classes.dex */
public class HChain extends Chain {
    private HAnchor mEnd;
    private HAnchor mLeft;
    private HAnchor mRight;
    private HAnchor mStart;

    public HAnchor getEnd() {
        return this.mEnd;
    }

    public HAnchor getLeft() {
        return this.mLeft;
    }

    public HAnchor getRight() {
        return this.mRight;
    }

    public HAnchor getStart() {
        return this.mStart;
    }

    public class HAnchor extends Chain.Anchor {
        HAnchor(Constraint.HSide hSide) {
            super(Constraint.Side.valueOf(hSide.name()));
        }
    }

    public HChain(String str) {
        super(str);
        this.mLeft = new HAnchor(Constraint.HSide.LEFT);
        this.mRight = new HAnchor(Constraint.HSide.RIGHT);
        this.mStart = new HAnchor(Constraint.HSide.START);
        this.mEnd = new HAnchor(Constraint.HSide.END);
        this.type = new Helper.HelperType(typeMap.get(Helper.Type.HORIZONTAL_CHAIN));
    }

    public HChain(String str, String str2) {
        super(str);
        this.mLeft = new HAnchor(Constraint.HSide.LEFT);
        this.mRight = new HAnchor(Constraint.HSide.RIGHT);
        this.mStart = new HAnchor(Constraint.HSide.START);
        this.mEnd = new HAnchor(Constraint.HSide.END);
        this.config = str2;
        this.type = new Helper.HelperType(typeMap.get(Helper.Type.HORIZONTAL_CHAIN));
        this.configMap = convertConfigToMap();
        if (this.configMap.containsKey("contains")) {
            Ref.addStringToReferences(this.configMap.get("contains"), this.references);
        }
    }

    public void linkToLeft(Constraint.HAnchor hAnchor) {
        linkToLeft(hAnchor, 0);
    }

    public void linkToLeft(Constraint.HAnchor hAnchor, int i) {
        linkToLeft(hAnchor, i, Integer.MIN_VALUE);
    }

    public void linkToLeft(Constraint.HAnchor hAnchor, int i, int i2) {
        this.mLeft.mConnection = hAnchor;
        this.mLeft.mMargin = i;
        this.mLeft.mGoneMargin = i2;
        this.configMap.put(TtmlNode.LEFT, this.mLeft.toString());
    }

    public void linkToRight(Constraint.HAnchor hAnchor) {
        linkToRight(hAnchor, 0);
    }

    public void linkToRight(Constraint.HAnchor hAnchor, int i) {
        linkToRight(hAnchor, i, Integer.MIN_VALUE);
    }

    public void linkToRight(Constraint.HAnchor hAnchor, int i, int i2) {
        this.mRight.mConnection = hAnchor;
        this.mRight.mMargin = i;
        this.mRight.mGoneMargin = i2;
        this.configMap.put(TtmlNode.RIGHT, this.mRight.toString());
    }

    public void linkToStart(Constraint.HAnchor hAnchor) {
        linkToStart(hAnchor, 0);
    }

    public void linkToStart(Constraint.HAnchor hAnchor, int i) {
        linkToStart(hAnchor, i, Integer.MIN_VALUE);
    }

    public void linkToStart(Constraint.HAnchor hAnchor, int i, int i2) {
        this.mStart.mConnection = hAnchor;
        this.mStart.mMargin = i;
        this.mStart.mGoneMargin = i2;
        this.configMap.put(TtmlNode.START, this.mStart.toString());
    }

    public void linkToEnd(Constraint.HAnchor hAnchor) {
        linkToEnd(hAnchor, 0);
    }

    public void linkToEnd(Constraint.HAnchor hAnchor, int i) {
        linkToEnd(hAnchor, i, Integer.MIN_VALUE);
    }

    public void linkToEnd(Constraint.HAnchor hAnchor, int i, int i2) {
        this.mEnd.mConnection = hAnchor;
        this.mEnd.mMargin = i;
        this.mEnd.mGoneMargin = i2;
        this.configMap.put(TtmlNode.END, this.mEnd.toString());
    }
}
