package com.google.android.exoplayer2.offline;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface FilterableManifest<T> {
    T copy(List<StreamKey> list);
}
