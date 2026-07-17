package androidx.constraintlayout.core.dsl;

import androidx.constraintlayout.core.dsl.Helper;
import com.google.android.exoplayer2.text.ttml.TtmlNode;

/* JADX INFO: loaded from: classes.dex */
public abstract class Guideline extends Helper {
    private int mEnd;
    private float mPercent;
    private int mStart;

    public int getEnd() {
        return this.mEnd;
    }

    public float getPercent() {
        return this.mPercent;
    }

    public int getStart() {
        return this.mStart;
    }

    Guideline(String str) {
        super(str, new Helper.HelperType(""));
        this.mStart = Integer.MIN_VALUE;
        this.mEnd = Integer.MIN_VALUE;
        this.mPercent = Float.NaN;
    }

    public void setStart(int i) {
        this.mStart = i;
        this.configMap.put(TtmlNode.START, String.valueOf(this.mStart));
    }

    public void setEnd(int i) {
        this.mEnd = i;
        this.configMap.put(TtmlNode.END, String.valueOf(this.mEnd));
    }

    public void setPercent(float f) {
        this.mPercent = f;
        this.configMap.put("percent", String.valueOf(this.mPercent));
    }
}
