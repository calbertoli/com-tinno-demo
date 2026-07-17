package com.google.android.exoplayer2.source.smoothstreaming;

import com.google.android.exoplayer2.source.chunk.ChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.upstream.CmcdConfiguration;
import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.android.exoplayer2.upstream.TransferListener;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface SsChunkSource extends ChunkSource {

    public interface Factory {
        SsChunkSource createChunkSource(LoaderErrorThrower loaderErrorThrower, SsManifest ssManifest, int i, ExoTrackSelection exoTrackSelection, TransferListener transferListener, CmcdConfiguration cmcdConfiguration);
    }

    void updateManifest(SsManifest ssManifest);

    void updateTrackSelection(ExoTrackSelection exoTrackSelection);
}
