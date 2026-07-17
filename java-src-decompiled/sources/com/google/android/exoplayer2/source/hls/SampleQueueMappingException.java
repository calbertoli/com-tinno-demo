package com.google.android.exoplayer2.source.hls;

import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class SampleQueueMappingException extends IOException {
    public SampleQueueMappingException(String str) {
        super("Unable to bind a sample queue to TrackGroup with MIME type " + str + ".");
    }
}
