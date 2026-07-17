package com.google.android.exoplayer2;

import com.google.android.exoplayer2.source.MediaPeriodId;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface LoadControl {

    @Deprecated
    public static final MediaPeriodId EMPTY_MEDIA_PERIOD_ID = new MediaPeriodId(new Object());

    Allocator getAllocator();

    long getBackBufferDurationUs();

    void onPrepared();

    void onReleased();

    void onStopped();

    boolean retainBackBufferFromKeyframe();

    boolean shouldContinueLoading(long j, long j2, float f);

    default void onTracksSelected(Timeline timeline, MediaPeriodId mediaPeriodId, Renderer[] rendererArr, TrackGroupArray trackGroupArray, ExoTrackSelection[] exoTrackSelectionArr) {
        onTracksSelected(rendererArr, trackGroupArray, exoTrackSelectionArr);
    }

    @Deprecated
    default void onTracksSelected(Renderer[] rendererArr, TrackGroupArray trackGroupArray, ExoTrackSelection[] exoTrackSelectionArr) {
        onTracksSelected(Timeline.EMPTY, EMPTY_MEDIA_PERIOD_ID, rendererArr, trackGroupArray, exoTrackSelectionArr);
    }

    default boolean shouldStartPlayback(Timeline timeline, MediaPeriodId mediaPeriodId, long j, float f, boolean z, long j2) {
        return shouldStartPlayback(j, f, z, j2);
    }

    @Deprecated
    default boolean shouldStartPlayback(long j, float f, boolean z, long j2) {
        return shouldStartPlayback(Timeline.EMPTY, EMPTY_MEDIA_PERIOD_ID, j, f, z, j2);
    }
}
