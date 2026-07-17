package com.google.android.exoplayer2.extractor.avi;

import com.google.android.exoplayer2.util.ParsableByteArray;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
final class StreamNameChunk implements AviChunk {
    public final String name;

    @Override // com.google.android.exoplayer2.extractor.avi.AviChunk
    public int getType() {
        return AviExtractor.FOURCC_strn;
    }

    public static StreamNameChunk parseFrom(ParsableByteArray parsableByteArray) {
        return new StreamNameChunk(parsableByteArray.readString(parsableByteArray.bytesLeft()));
    }

    private StreamNameChunk(String str) {
        this.name = str;
    }
}
