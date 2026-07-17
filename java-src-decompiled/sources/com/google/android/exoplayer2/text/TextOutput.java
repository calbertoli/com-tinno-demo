package com.google.android.exoplayer2.text;

import java.util.List;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface TextOutput {
    void onCues(CueGroup cueGroup);

    @Deprecated
    default void onCues(List<Cue> list) {
    }
}
