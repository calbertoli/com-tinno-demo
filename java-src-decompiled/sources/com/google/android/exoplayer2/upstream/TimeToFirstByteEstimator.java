package com.google.android.exoplayer2.upstream;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface TimeToFirstByteEstimator {
    long getTimeToFirstByteEstimateUs();

    void onTransferInitializing(DataSpec dataSpec);

    void onTransferStart(DataSpec dataSpec);

    void reset();
}
