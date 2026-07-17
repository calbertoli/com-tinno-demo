package com.google.android.exoplayer2.upstream.experimental;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface BandwidthStatistic {
    void addSample(long j, long j2);

    long getBandwidthEstimate();

    void reset();
}
