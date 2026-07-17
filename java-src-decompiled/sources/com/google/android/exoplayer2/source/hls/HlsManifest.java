package com.google.android.exoplayer2.source.hls;

import com.google.android.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsMultivariantPlaylist;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class HlsManifest {
    public final HlsMediaPlaylist mediaPlaylist;
    public final HlsMultivariantPlaylist multivariantPlaylist;

    HlsManifest(HlsMultivariantPlaylist hlsMultivariantPlaylist, HlsMediaPlaylist hlsMediaPlaylist) {
        this.multivariantPlaylist = hlsMultivariantPlaylist;
        this.mediaPlaylist = hlsMediaPlaylist;
    }
}
