package com.google.android.exoplayer2.upstream.experimental;

import android.os.Handler;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface BandwidthEstimator {
    public static final long ESTIMATE_NOT_AVAILABLE = Long.MIN_VALUE;

    void addEventListener(Handler handler, BandwidthMeter.EventListener eventListener);

    long getBandwidthEstimate();

    void onBytesTransferred(DataSource dataSource, int i);

    void onNetworkTypeChange(long j);

    void onTransferEnd(DataSource dataSource);

    void onTransferInitializing(DataSource dataSource);

    void onTransferStart(DataSource dataSource);

    void removeEventListener(BandwidthMeter.EventListener eventListener);
}
