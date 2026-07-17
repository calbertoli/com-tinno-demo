package com.tinno.demo.sellingpoint;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.Fragment;
import com.tinno.demo.R;

/* JADX INFO: loaded from: classes2.dex */
public class TabFragment extends Fragment {
    private int mIndex;
    private int[] mSellingPointIdArray = {R.drawable.img_bg_selling_point_1, R.drawable.img_bg_selling_point_2, R.drawable.img_bg_selling_point_3, R.drawable.img_bg_selling_point_4, R.drawable.img_bg_selling_point_5};
    private ImageView mSellingPointView;

    public TabFragment() {
    }

    public TabFragment(int i) {
        this.mIndex = i;
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View viewInflate = layoutInflater.inflate(R.layout.fragment_selling_point_item, viewGroup);
        init(viewInflate);
        return viewInflate;
    }

    private void init(View view) {
        ImageView imageView = (ImageView) view.findViewById(R.id.iv_selling_point);
        this.mSellingPointView = imageView;
        imageView.setImageResource(this.mSellingPointIdArray[this.mIndex]);
    }
}
