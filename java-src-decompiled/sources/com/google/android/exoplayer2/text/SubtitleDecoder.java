package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.decoder.Decoder;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface SubtitleDecoder extends Decoder<SubtitleInputBuffer, SubtitleOutputBuffer, SubtitleDecoderException> {
    void setPositionUs(long j);
}
