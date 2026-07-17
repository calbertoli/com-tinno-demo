package com.google.android.exoplayer2.ui;

import android.view.ViewGroup;
import com.google.common.collect.ImmutableList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface AdViewProvider {
    ViewGroup getAdViewGroup();

    default List<AdOverlayInfo> getAdOverlayInfos() {
        return ImmutableList.of();
    }
}
