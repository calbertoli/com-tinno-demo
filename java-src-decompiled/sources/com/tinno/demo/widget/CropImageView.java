package com.tinno.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/* JADX INFO: loaded from: classes2.dex */
public class CropImageView extends ImageView {
    public CropImageView(Context context) {
        super(context);
    }

    public CropImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onMeasure(int i, int i2) {
        int iMin = Math.min(getMaxWidth(), View.MeasureSpec.getSize(i));
        if (getDrawable() != null) {
            int intrinsicHeight = (int) ((getDrawable().getIntrinsicHeight() * iMin) / getDrawable().getIntrinsicWidth());
            Log.d("CropImageView", "height:" + intrinsicHeight + " width:" + iMin + " radio:" + (getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth()));
            setMeasuredDimension(iMin, intrinsicHeight);
            return;
        }
        super.onMeasure(i, i2);
    }
}
