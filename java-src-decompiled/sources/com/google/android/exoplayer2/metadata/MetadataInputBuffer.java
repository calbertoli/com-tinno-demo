package com.google.android.exoplayer2.metadata;

import com.google.android.exoplayer2.decoder.DecoderInputBuffer;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class MetadataInputBuffer extends DecoderInputBuffer {
    public long subsampleOffsetUs;

    public MetadataInputBuffer() {
        super(1);
    }
}
