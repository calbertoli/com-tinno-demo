package com.google.android.exoplayer2;

import com.google.android.exoplayer2.drm.DrmSession;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class FormatHolder {
    public DrmSession drmSession;
    public Format format;

    public void clear() {
        this.drmSession = null;
        this.format = null;
    }
}
