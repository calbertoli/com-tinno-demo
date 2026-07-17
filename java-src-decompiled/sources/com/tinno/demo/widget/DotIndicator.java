package com.tinno.demo.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;
import com.tinno.demo.R;

/* JADX INFO: loaded from: classes2.dex */
public class DotIndicator extends LinearLayout {
    private Context mContext;
    private int mCount;
    private int mPosition;

    public DotIndicator(Context context) {
        this(context, null);
    }

    public DotIndicator(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public DotIndicator(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mContext = context;
    }

    public void setupWithViewPager(ViewPager2 viewPager2) {
        setDots(viewPager2.getAdapter().getItemCount());
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() { // from class: com.tinno.demo.widget.DotIndicator.1
            @Override // androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
            public void onPageSelected(int i) {
                DotIndicator.this.updateDots(i);
            }
        });
    }

    private void setDots(int i) {
        removeAllViews();
        this.mCount = i;
        int i2 = 0;
        while (i2 < i) {
            ImageView imageView = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            layoutParams.setMargins(dpToPx(10.0f), 0, 10, dpToPx(10.0f));
            imageView.setLayoutParams(layoutParams);
            imageView.setImageResource(i2 == 0 ? R.drawable.dot_selected : R.drawable.dot_default);
            addView(imageView);
            i2++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateDots(int i) {
        int i2 = (i == 1 || i == 4) ? R.color.white : R.color.dot_selected;
        for (int i3 = 0; i3 < this.mCount; i3++) {
            ImageView imageView = (ImageView) getChildAt(i3);
            if (i3 == i) {
                imageView.setImageResource(R.drawable.dot_selected);
                updateDotDrawable(imageView, i2);
            } else {
                imageView.setImageResource(R.drawable.dot_default);
                updateDotDrawable(imageView, R.color.dot_default);
            }
        }
        this.mPosition = i;
    }

    private void updateDotDrawable(ImageView imageView, int i) {
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof GradientDrawable) {
            ((GradientDrawable) drawable).setColor(ContextCompat.getColor(this.mContext, i));
        }
    }

    public int dpToPx(float f) {
        return (int) ((f * getContext().getResources().getDisplayMetrics().density) + 0.5f);
    }
}
