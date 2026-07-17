package com.google.android.exoplayer2.source;

import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.util.Assertions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public class FilteringMediaSource extends WrappingMediaSource {
    private final ImmutableSet<Integer> trackTypes;

    public FilteringMediaSource(MediaSource mediaSource, int i) {
        this(mediaSource, ImmutableSet.of(Integer.valueOf(i)));
    }

    public FilteringMediaSource(MediaSource mediaSource, Set<Integer> set) {
        super(mediaSource);
        this.trackTypes = ImmutableSet.copyOf((Collection) set);
    }

    @Override // com.google.android.exoplayer2.source.WrappingMediaSource, com.google.android.exoplayer2.source.MediaSource
    public MediaPeriod createPeriod(MediaSource.MediaPeriodId mediaPeriodId, Allocator allocator, long j) {
        return new FilteringMediaPeriod(super.createPeriod(mediaPeriodId, allocator, j), this.trackTypes);
    }

    @Override // com.google.android.exoplayer2.source.WrappingMediaSource, com.google.android.exoplayer2.source.MediaSource
    public void releasePeriod(MediaPeriod mediaPeriod) {
        super.releasePeriod(((FilteringMediaPeriod) mediaPeriod).mediaPeriod);
    }

    private static final class FilteringMediaPeriod implements MediaPeriod, MediaPeriod.Callback {
        private MediaPeriod.Callback callback;
        private TrackGroupArray filteredTrackGroups;
        public final MediaPeriod mediaPeriod;
        private final ImmutableSet<Integer> trackTypes;

        public FilteringMediaPeriod(MediaPeriod mediaPeriod, ImmutableSet<Integer> immutableSet) {
            this.mediaPeriod = mediaPeriod;
            this.trackTypes = immutableSet;
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public void prepare(MediaPeriod.Callback callback, long j) {
            this.callback = callback;
            this.mediaPeriod.prepare(this, j);
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public void maybeThrowPrepareError() throws IOException {
            this.mediaPeriod.maybeThrowPrepareError();
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public TrackGroupArray getTrackGroups() {
            return (TrackGroupArray) Assertions.checkNotNull(this.filteredTrackGroups);
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public List<StreamKey> getStreamKeys(List<ExoTrackSelection> list) {
            return this.mediaPeriod.getStreamKeys(list);
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public long selectTracks(ExoTrackSelection[] exoTrackSelectionArr, boolean[] zArr, SampleStream[] sampleStreamArr, boolean[] zArr2, long j) {
            return this.mediaPeriod.selectTracks(exoTrackSelectionArr, zArr, sampleStreamArr, zArr2, j);
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public void discardBuffer(long j, boolean z) {
            this.mediaPeriod.discardBuffer(j, z);
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public long readDiscontinuity() {
            return this.mediaPeriod.readDiscontinuity();
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public long seekToUs(long j) {
            return this.mediaPeriod.seekToUs(j);
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod
        public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
            return this.mediaPeriod.getAdjustedSeekPositionUs(j, seekParameters);
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public long getBufferedPositionUs() {
            return this.mediaPeriod.getBufferedPositionUs();
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public long getNextLoadPositionUs() {
            return this.mediaPeriod.getNextLoadPositionUs();
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public boolean continueLoading(long j) {
            return this.mediaPeriod.continueLoading(j);
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public boolean isLoading() {
            return this.mediaPeriod.isLoading();
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
        public void reevaluateBuffer(long j) {
            this.mediaPeriod.reevaluateBuffer(j);
        }

        @Override // com.google.android.exoplayer2.source.MediaPeriod.Callback
        public void onPrepared(MediaPeriod mediaPeriod) {
            TrackGroupArray trackGroups = mediaPeriod.getTrackGroups();
            ImmutableList.Builder builder = ImmutableList.builder();
            for (int i = 0; i < trackGroups.length; i++) {
                TrackGroup trackGroup = trackGroups.get(i);
                if (this.trackTypes.contains(Integer.valueOf(trackGroup.type))) {
                    builder.add(trackGroup);
                }
            }
            this.filteredTrackGroups = new TrackGroupArray((TrackGroup[]) builder.build().toArray(new TrackGroup[0]));
            ((MediaPeriod.Callback) Assertions.checkNotNull(this.callback)).onPrepared(this);
        }

        @Override // com.google.android.exoplayer2.source.SequenceableLoader.Callback
        public void onContinueLoadingRequested(MediaPeriod mediaPeriod) {
            ((MediaPeriod.Callback) Assertions.checkNotNull(this.callback)).onContinueLoadingRequested(this);
        }
    }
}
