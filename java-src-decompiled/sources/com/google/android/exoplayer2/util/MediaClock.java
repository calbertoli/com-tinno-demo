package com.google.android.exoplayer2.util;

import com.google.android.exoplayer2.PlaybackParameters;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface MediaClock {
    PlaybackParameters getPlaybackParameters();

    long getPositionUs();

    void setPlaybackParameters(PlaybackParameters playbackParameters);
}
