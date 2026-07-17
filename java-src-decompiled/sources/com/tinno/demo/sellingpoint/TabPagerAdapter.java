package com.tinno.demo.sellingpoint;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/* JADX INFO: loaded from: classes2.dex */
public class TabPagerAdapter extends FragmentStateAdapter {
    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        return 5;
    }

    public TabPagerAdapter(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override // androidx.viewpager2.adapter.FragmentStateAdapter
    public Fragment createFragment(int i) {
        return new TabFragment(i);
    }
}
