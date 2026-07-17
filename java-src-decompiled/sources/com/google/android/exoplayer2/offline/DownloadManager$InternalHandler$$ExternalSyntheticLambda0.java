package com.google.android.exoplayer2.offline;

import com.google.android.exoplayer2.offline.DownloadManager;
import java.util.Comparator;

/* JADX INFO: compiled from: D8$$SyntheticClass */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class DownloadManager$InternalHandler$$ExternalSyntheticLambda0 implements Comparator {
    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return DownloadManager.InternalHandler.compareStartTimes((Download) obj, (Download) obj2);
    }
}
