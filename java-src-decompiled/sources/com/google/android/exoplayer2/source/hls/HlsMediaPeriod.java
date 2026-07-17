package com.google.android.exoplayer2.source.hls;

import android.net.Uri;
import android.text.TextUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.analytics.PlayerId;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.DrmSessionEventListener;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.offline.StreamKey;
import com.google.android.exoplayer2.source.CompositeSequenceableLoaderFactory;
import com.google.android.exoplayer2.source.MediaPeriod;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.SampleStream;
import com.google.android.exoplayer2.source.SequenceableLoader;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper;
import com.google.android.exoplayer2.source.hls.playlist.HlsMultivariantPlaylist;
import com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker;
import com.google.android.exoplayer2.trackselection.ExoTrackSelection;
import com.google.android.exoplayer2.upstream.Allocator;
import com.google.android.exoplayer2.upstream.CmcdConfiguration;
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class HlsMediaPeriod implements MediaPeriod, HlsPlaylistTracker.PlaylistEventListener {
    private final Allocator allocator;
    private final boolean allowChunklessPreparation;
    private int audioVideoSampleStreamWrapperCount;
    private final CmcdConfiguration cmcdConfiguration;
    private SequenceableLoader compositeSequenceableLoader;
    private final CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory;
    private final HlsDataSourceFactory dataSourceFactory;
    private final DrmSessionEventListener.EventDispatcher drmEventDispatcher;
    private final DrmSessionManager drmSessionManager;
    private final MediaSourceEventListener.EventDispatcher eventDispatcher;
    private final HlsExtractorFactory extractorFactory;
    private final LoadErrorHandlingPolicy loadErrorHandlingPolicy;
    private MediaPeriod.Callback mediaPeriodCallback;
    private final TransferListener mediaTransferListener;
    private final int metadataType;
    private int pendingPrepareCount;
    private final PlayerId playerId;
    private final HlsPlaylistTracker playlistTracker;
    private final long timestampAdjusterInitializationTimeoutMs;
    private TrackGroupArray trackGroups;
    private final boolean useSessionKeys;
    private final HlsSampleStreamWrapper.Callback sampleStreamWrapperCallback = new SampleStreamWrapperCallback();
    private final IdentityHashMap<SampleStream, Integer> streamWrapperIndices = new IdentityHashMap<>();
    private final TimestampAdjusterProvider timestampAdjusterProvider = new TimestampAdjusterProvider();
    private HlsSampleStreamWrapper[] sampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private HlsSampleStreamWrapper[] enabledSampleStreamWrappers = new HlsSampleStreamWrapper[0];
    private int[][] manifestUrlIndicesPerWrapper = new int[0][];

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long readDiscontinuity() {
        return C.TIME_UNSET;
    }

    static /* synthetic */ int access$106(HlsMediaPeriod hlsMediaPeriod) {
        int i = hlsMediaPeriod.pendingPrepareCount - 1;
        hlsMediaPeriod.pendingPrepareCount = i;
        return i;
    }

    public HlsMediaPeriod(HlsExtractorFactory hlsExtractorFactory, HlsPlaylistTracker hlsPlaylistTracker, HlsDataSourceFactory hlsDataSourceFactory, TransferListener transferListener, CmcdConfiguration cmcdConfiguration, DrmSessionManager drmSessionManager, DrmSessionEventListener.EventDispatcher eventDispatcher, LoadErrorHandlingPolicy loadErrorHandlingPolicy, MediaSourceEventListener.EventDispatcher eventDispatcher2, Allocator allocator, CompositeSequenceableLoaderFactory compositeSequenceableLoaderFactory, boolean z, int i, boolean z2, PlayerId playerId, long j) {
        this.extractorFactory = hlsExtractorFactory;
        this.playlistTracker = hlsPlaylistTracker;
        this.dataSourceFactory = hlsDataSourceFactory;
        this.mediaTransferListener = transferListener;
        this.cmcdConfiguration = cmcdConfiguration;
        this.drmSessionManager = drmSessionManager;
        this.drmEventDispatcher = eventDispatcher;
        this.loadErrorHandlingPolicy = loadErrorHandlingPolicy;
        this.eventDispatcher = eventDispatcher2;
        this.allocator = allocator;
        this.compositeSequenceableLoaderFactory = compositeSequenceableLoaderFactory;
        this.allowChunklessPreparation = z;
        this.metadataType = i;
        this.useSessionKeys = z2;
        this.playerId = playerId;
        this.timestampAdjusterInitializationTimeoutMs = j;
        this.compositeSequenceableLoader = compositeSequenceableLoaderFactory.createCompositeSequenceableLoader(new SequenceableLoader[0]);
    }

    public void release() {
        this.playlistTracker.removeListener(this);
        for (HlsSampleStreamWrapper hlsSampleStreamWrapper : this.sampleStreamWrappers) {
            hlsSampleStreamWrapper.release();
        }
        this.mediaPeriodCallback = null;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void prepare(MediaPeriod.Callback callback, long j) {
        this.mediaPeriodCallback = callback;
        this.playlistTracker.addListener(this);
        buildAndPrepareSampleStreamWrappers(j);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void maybeThrowPrepareError() throws IOException {
        for (HlsSampleStreamWrapper hlsSampleStreamWrapper : this.sampleStreamWrappers) {
            hlsSampleStreamWrapper.maybeThrowPrepareError();
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public TrackGroupArray getTrackGroups() {
        return (TrackGroupArray) Assertions.checkNotNull(this.trackGroups);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r15v1 */
    /* JADX WARN: Type inference failed for: r15v2, types: [int] */
    /* JADX WARN: Type inference failed for: r15v7 */
    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public List<StreamKey> getStreamKeys(List<ExoTrackSelection> list) {
        int[] iArr;
        TrackGroupArray trackGroups;
        int primaryTrackGroupIndex;
        HlsMediaPeriod hlsMediaPeriod = this;
        HlsMultivariantPlaylist hlsMultivariantPlaylist = (HlsMultivariantPlaylist) Assertions.checkNotNull(hlsMediaPeriod.playlistTracker.getMultivariantPlaylist());
        boolean z = !hlsMultivariantPlaylist.variants.isEmpty();
        int length = hlsMediaPeriod.sampleStreamWrappers.length - hlsMultivariantPlaylist.subtitles.size();
        int i = 0;
        if (z) {
            HlsSampleStreamWrapper hlsSampleStreamWrapper = hlsMediaPeriod.sampleStreamWrappers[0];
            iArr = hlsMediaPeriod.manifestUrlIndicesPerWrapper[0];
            trackGroups = hlsSampleStreamWrapper.getTrackGroups();
            primaryTrackGroupIndex = hlsSampleStreamWrapper.getPrimaryTrackGroupIndex();
        } else {
            iArr = new int[0];
            trackGroups = TrackGroupArray.EMPTY;
            primaryTrackGroupIndex = 0;
        }
        ArrayList arrayList = new ArrayList();
        boolean z2 = false;
        boolean z3 = false;
        for (ExoTrackSelection exoTrackSelection : list) {
            TrackGroup trackGroup = exoTrackSelection.getTrackGroup();
            int iIndexOf = trackGroups.indexOf(trackGroup);
            if (iIndexOf == -1) {
                ?? r15 = z;
                while (true) {
                    HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = hlsMediaPeriod.sampleStreamWrappers;
                    if (r15 >= hlsSampleStreamWrapperArr.length) {
                        break;
                    }
                    if (hlsSampleStreamWrapperArr[r15].getTrackGroups().indexOf(trackGroup) != -1) {
                        int i2 = r15 < length ? 1 : 2;
                        int[] iArr2 = hlsMediaPeriod.manifestUrlIndicesPerWrapper[r15];
                        for (int i3 = 0; i3 < exoTrackSelection.length(); i3++) {
                            arrayList.add(new StreamKey(i2, iArr2[exoTrackSelection.getIndexInTrackGroup(i3)]));
                        }
                    } else {
                        hlsMediaPeriod = this;
                        r15++;
                    }
                }
            } else if (iIndexOf == primaryTrackGroupIndex) {
                for (int i4 = i; i4 < exoTrackSelection.length(); i4++) {
                    arrayList.add(new StreamKey(i, iArr[exoTrackSelection.getIndexInTrackGroup(i4)]));
                }
                z3 = true;
            } else {
                z2 = true;
            }
            hlsMediaPeriod = this;
            i = 0;
        }
        if (z2 && !z3) {
            int i5 = iArr[0];
            int i6 = hlsMultivariantPlaylist.variants.get(iArr[0]).format.bitrate;
            for (int i7 = 1; i7 < iArr.length; i7++) {
                int i8 = hlsMultivariantPlaylist.variants.get(iArr[i7]).format.bitrate;
                if (i8 < i6) {
                    i5 = iArr[i7];
                    i6 = i8;
                }
            }
            arrayList.add(new StreamKey(0, i5));
        }
        return arrayList;
    }

    /* JADX WARN: Removed duplicated region for block: B:56:0x00df  */
    @Override // com.google.android.exoplayer2.source.MediaPeriod
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public long selectTracks(com.google.android.exoplayer2.trackselection.ExoTrackSelection[] r22, boolean[] r23, com.google.android.exoplayer2.source.SampleStream[] r24, boolean[] r25, long r26) {
        /*
            Method dump skipped, instruction units count: 276
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.hls.HlsMediaPeriod.selectTracks(com.google.android.exoplayer2.trackselection.ExoTrackSelection[], boolean[], com.google.android.exoplayer2.source.SampleStream[], boolean[], long):long");
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public void discardBuffer(long j, boolean z) {
        for (HlsSampleStreamWrapper hlsSampleStreamWrapper : this.enabledSampleStreamWrappers) {
            hlsSampleStreamWrapper.discardBuffer(j, z);
        }
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public void reevaluateBuffer(long j) {
        this.compositeSequenceableLoader.reevaluateBuffer(j);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean continueLoading(long j) {
        if (this.trackGroups == null) {
            for (HlsSampleStreamWrapper hlsSampleStreamWrapper : this.sampleStreamWrappers) {
                hlsSampleStreamWrapper.continuePreparing();
            }
            return false;
        }
        return this.compositeSequenceableLoader.continueLoading(j);
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public boolean isLoading() {
        return this.compositeSequenceableLoader.isLoading();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public long getNextLoadPositionUs() {
        return this.compositeSequenceableLoader.getNextLoadPositionUs();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod, com.google.android.exoplayer2.source.SequenceableLoader
    public long getBufferedPositionUs() {
        return this.compositeSequenceableLoader.getBufferedPositionUs();
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long seekToUs(long j) {
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.enabledSampleStreamWrappers;
        if (hlsSampleStreamWrapperArr.length > 0) {
            boolean zSeekToUs = hlsSampleStreamWrapperArr[0].seekToUs(j, false);
            int i = 1;
            while (true) {
                HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr2 = this.enabledSampleStreamWrappers;
                if (i >= hlsSampleStreamWrapperArr2.length) {
                    break;
                }
                hlsSampleStreamWrapperArr2[i].seekToUs(j, zSeekToUs);
                i++;
            }
            if (zSeekToUs) {
                this.timestampAdjusterProvider.reset();
            }
        }
        return j;
    }

    @Override // com.google.android.exoplayer2.source.MediaPeriod
    public long getAdjustedSeekPositionUs(long j, SeekParameters seekParameters) {
        for (HlsSampleStreamWrapper hlsSampleStreamWrapper : this.enabledSampleStreamWrappers) {
            if (hlsSampleStreamWrapper.isVideoSampleStream()) {
                return hlsSampleStreamWrapper.getAdjustedSeekPositionUs(j, seekParameters);
            }
        }
        return j;
    }

    @Override // com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistEventListener
    public void onPlaylistChanged() {
        for (HlsSampleStreamWrapper hlsSampleStreamWrapper : this.sampleStreamWrappers) {
            hlsSampleStreamWrapper.onPlaylistUpdated();
        }
        this.mediaPeriodCallback.onContinueLoadingRequested(this);
    }

    @Override // com.google.android.exoplayer2.source.hls.playlist.HlsPlaylistTracker.PlaylistEventListener
    public boolean onPlaylistError(Uri uri, LoadErrorHandlingPolicy.LoadErrorInfo loadErrorInfo, boolean z) {
        boolean zOnPlaylistError = true;
        for (HlsSampleStreamWrapper hlsSampleStreamWrapper : this.sampleStreamWrappers) {
            zOnPlaylistError &= hlsSampleStreamWrapper.onPlaylistError(uri, loadErrorInfo, z);
        }
        this.mediaPeriodCallback.onContinueLoadingRequested(this);
        return zOnPlaylistError;
    }

    private void buildAndPrepareSampleStreamWrappers(long j) {
        Map<String, DrmInitData> mapEmptyMap;
        HlsMultivariantPlaylist hlsMultivariantPlaylist = (HlsMultivariantPlaylist) Assertions.checkNotNull(this.playlistTracker.getMultivariantPlaylist());
        if (this.useSessionKeys) {
            mapEmptyMap = deriveOverridingDrmInitData(hlsMultivariantPlaylist.sessionKeyDrmInitData);
        } else {
            mapEmptyMap = Collections.emptyMap();
        }
        Map<String, DrmInitData> map = mapEmptyMap;
        int i = 1;
        boolean z = !hlsMultivariantPlaylist.variants.isEmpty();
        List<HlsMultivariantPlaylist.Rendition> list = hlsMultivariantPlaylist.audios;
        List<HlsMultivariantPlaylist.Rendition> list2 = hlsMultivariantPlaylist.subtitles;
        int i2 = 0;
        this.pendingPrepareCount = 0;
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        if (z) {
            buildAndPrepareMainSampleStreamWrapper(hlsMultivariantPlaylist, j, arrayList, arrayList2, map);
        }
        buildAndPrepareAudioSampleStreamWrappers(j, list, arrayList, arrayList2, map);
        this.audioVideoSampleStreamWrapperCount = arrayList.size();
        int i3 = 0;
        while (i3 < list2.size()) {
            HlsMultivariantPlaylist.Rendition rendition = list2.get(i3);
            String str = "subtitle:" + i3 + ":" + rendition.name;
            Uri[] uriArr = new Uri[i];
            uriArr[i2] = rendition.url;
            Format[] formatArr = new Format[i];
            formatArr[i2] = rendition.format;
            ArrayList arrayList3 = arrayList2;
            int i4 = i3;
            HlsSampleStreamWrapper hlsSampleStreamWrapperBuildSampleStreamWrapper = buildSampleStreamWrapper(str, 3, uriArr, formatArr, null, Collections.emptyList(), map, j);
            arrayList3.add(new int[]{i4});
            arrayList.add(hlsSampleStreamWrapperBuildSampleStreamWrapper);
            hlsSampleStreamWrapperBuildSampleStreamWrapper.prepareWithMultivariantPlaylistInfo(new TrackGroup[]{new TrackGroup(str, rendition.format)}, 0, new int[0]);
            i3 = i4 + 1;
            i2 = 0;
            arrayList2 = arrayList3;
            i = 1;
        }
        int i5 = i2;
        this.sampleStreamWrappers = (HlsSampleStreamWrapper[]) arrayList.toArray(new HlsSampleStreamWrapper[i5]);
        this.manifestUrlIndicesPerWrapper = (int[][]) arrayList2.toArray(new int[i5][]);
        this.pendingPrepareCount = this.sampleStreamWrappers.length;
        for (int i6 = i5; i6 < this.audioVideoSampleStreamWrapperCount; i6++) {
            this.sampleStreamWrappers[i6].setIsPrimaryTimestampSource(true);
        }
        HlsSampleStreamWrapper[] hlsSampleStreamWrapperArr = this.sampleStreamWrappers;
        int length = hlsSampleStreamWrapperArr.length;
        for (int i7 = i5; i7 < length; i7++) {
            hlsSampleStreamWrapperArr[i7].continuePreparing();
        }
        this.enabledSampleStreamWrappers = this.sampleStreamWrappers;
    }

    private void buildAndPrepareMainSampleStreamWrapper(HlsMultivariantPlaylist hlsMultivariantPlaylist, long j, List<HlsSampleStreamWrapper> list, List<int[]> list2, Map<String, DrmInitData> map) {
        boolean z;
        boolean z2;
        int size = hlsMultivariantPlaylist.variants.size();
        int[] iArr = new int[size];
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < hlsMultivariantPlaylist.variants.size(); i3++) {
            Format format = hlsMultivariantPlaylist.variants.get(i3).format;
            if (format.height > 0 || Util.getCodecsOfType(format.codecs, 2) != null) {
                iArr[i3] = 2;
                i++;
            } else if (Util.getCodecsOfType(format.codecs, 1) != null) {
                iArr[i3] = 1;
                i2++;
            } else {
                iArr[i3] = -1;
            }
        }
        if (i > 0) {
            size = i;
            z = true;
            z2 = false;
        } else if (i2 < size) {
            size -= i2;
            z = false;
            z2 = true;
        } else {
            z = false;
            z2 = false;
        }
        Uri[] uriArr = new Uri[size];
        Format[] formatArr = new Format[size];
        int[] iArr2 = new int[size];
        int i4 = 0;
        for (int i5 = 0; i5 < hlsMultivariantPlaylist.variants.size(); i5++) {
            if ((!z || iArr[i5] == 2) && (!z2 || iArr[i5] != 1)) {
                HlsMultivariantPlaylist.Variant variant = hlsMultivariantPlaylist.variants.get(i5);
                uriArr[i4] = variant.url;
                formatArr[i4] = variant.format;
                iArr2[i4] = i5;
                i4++;
            }
        }
        String str = formatArr[0].codecs;
        int codecCountOfType = Util.getCodecCountOfType(str, 2);
        int codecCountOfType2 = Util.getCodecCountOfType(str, 1);
        boolean z3 = (codecCountOfType2 == 1 || (codecCountOfType2 == 0 && hlsMultivariantPlaylist.audios.isEmpty())) && codecCountOfType <= 1 && codecCountOfType2 + codecCountOfType > 0;
        HlsSampleStreamWrapper hlsSampleStreamWrapperBuildSampleStreamWrapper = buildSampleStreamWrapper("main", (z || codecCountOfType2 <= 0) ? 0 : 1, uriArr, formatArr, hlsMultivariantPlaylist.muxedAudioFormat, hlsMultivariantPlaylist.muxedCaptionFormats, map, j);
        list.add(hlsSampleStreamWrapperBuildSampleStreamWrapper);
        list2.add(iArr2);
        if (this.allowChunklessPreparation && z3) {
            ArrayList arrayList = new ArrayList();
            if (codecCountOfType > 0) {
                Format[] formatArr2 = new Format[size];
                for (int i6 = 0; i6 < size; i6++) {
                    formatArr2[i6] = deriveVideoFormat(formatArr[i6]);
                }
                arrayList.add(new TrackGroup("main", formatArr2));
                if (codecCountOfType2 > 0 && (hlsMultivariantPlaylist.muxedAudioFormat != null || hlsMultivariantPlaylist.audios.isEmpty())) {
                    arrayList.add(new TrackGroup("main:audio", deriveAudioFormat(formatArr[0], hlsMultivariantPlaylist.muxedAudioFormat, false)));
                }
                List<Format> list3 = hlsMultivariantPlaylist.muxedCaptionFormats;
                if (list3 != null) {
                    for (int i7 = 0; i7 < list3.size(); i7++) {
                        arrayList.add(new TrackGroup("main:cc:" + i7, list3.get(i7)));
                    }
                }
            } else {
                Format[] formatArr3 = new Format[size];
                for (int i8 = 0; i8 < size; i8++) {
                    formatArr3[i8] = deriveAudioFormat(formatArr[i8], hlsMultivariantPlaylist.muxedAudioFormat, true);
                }
                arrayList.add(new TrackGroup("main", formatArr3));
            }
            TrackGroup trackGroup = new TrackGroup("main:id3", new Format.Builder().setId("ID3").setSampleMimeType(MimeTypes.APPLICATION_ID3).build());
            arrayList.add(trackGroup);
            hlsSampleStreamWrapperBuildSampleStreamWrapper.prepareWithMultivariantPlaylistInfo((TrackGroup[]) arrayList.toArray(new TrackGroup[0]), 0, arrayList.indexOf(trackGroup));
        }
    }

    private void buildAndPrepareAudioSampleStreamWrappers(long j, List<HlsMultivariantPlaylist.Rendition> list, List<HlsSampleStreamWrapper> list2, List<int[]> list3, Map<String, DrmInitData> map) {
        ArrayList arrayList = new ArrayList(list.size());
        ArrayList arrayList2 = new ArrayList(list.size());
        ArrayList arrayList3 = new ArrayList(list.size());
        HashSet hashSet = new HashSet();
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i).name;
            if (hashSet.add(str)) {
                arrayList.clear();
                arrayList2.clear();
                arrayList3.clear();
                boolean z = true;
                for (int i2 = 0; i2 < list.size(); i2++) {
                    if (Util.areEqual(str, list.get(i2).name)) {
                        HlsMultivariantPlaylist.Rendition rendition = list.get(i2);
                        arrayList3.add(Integer.valueOf(i2));
                        arrayList.add(rendition.url);
                        arrayList2.add(rendition.format);
                        z &= Util.getCodecCountOfType(rendition.format.codecs, 1) == 1;
                    }
                }
                String str2 = "audio:" + str;
                HlsSampleStreamWrapper hlsSampleStreamWrapperBuildSampleStreamWrapper = buildSampleStreamWrapper(str2, 1, (Uri[]) arrayList.toArray((Uri[]) Util.castNonNullTypeArray(new Uri[0])), (Format[]) arrayList2.toArray(new Format[0]), null, Collections.emptyList(), map, j);
                list3.add(Ints.toArray(arrayList3));
                list2.add(hlsSampleStreamWrapperBuildSampleStreamWrapper);
                if (this.allowChunklessPreparation && z) {
                    hlsSampleStreamWrapperBuildSampleStreamWrapper.prepareWithMultivariantPlaylistInfo(new TrackGroup[]{new TrackGroup(str2, (Format[]) arrayList2.toArray(new Format[0]))}, 0, new int[0]);
                }
            }
        }
    }

    private HlsSampleStreamWrapper buildSampleStreamWrapper(String str, int i, Uri[] uriArr, Format[] formatArr, Format format, List<Format> list, Map<String, DrmInitData> map, long j) {
        return new HlsSampleStreamWrapper(str, i, this.sampleStreamWrapperCallback, new HlsChunkSource(this.extractorFactory, this.playlistTracker, uriArr, formatArr, this.dataSourceFactory, this.mediaTransferListener, this.timestampAdjusterProvider, this.timestampAdjusterInitializationTimeoutMs, list, this.playerId, this.cmcdConfiguration), map, this.allocator, j, format, this.drmSessionManager, this.drmEventDispatcher, this.loadErrorHandlingPolicy, this.eventDispatcher, this.metadataType);
    }

    private static Map<String, DrmInitData> deriveOverridingDrmInitData(List<DrmInitData> list) {
        ArrayList arrayList = new ArrayList(list);
        HashMap map = new HashMap();
        int i = 0;
        while (i < arrayList.size()) {
            DrmInitData drmInitDataMerge = list.get(i);
            String str = drmInitDataMerge.schemeType;
            i++;
            int i2 = i;
            while (i2 < arrayList.size()) {
                DrmInitData drmInitData = (DrmInitData) arrayList.get(i2);
                if (TextUtils.equals(drmInitData.schemeType, str)) {
                    drmInitDataMerge = drmInitDataMerge.merge(drmInitData);
                    arrayList.remove(i2);
                } else {
                    i2++;
                }
            }
            map.put(str, drmInitDataMerge);
        }
        return map;
    }

    private static Format deriveVideoFormat(Format format) {
        String codecsOfType = Util.getCodecsOfType(format.codecs, 2);
        return new Format.Builder().setId(format.id).setLabel(format.label).setContainerMimeType(format.containerMimeType).setSampleMimeType(MimeTypes.getMediaMimeType(codecsOfType)).setCodecs(codecsOfType).setMetadata(format.metadata).setAverageBitrate(format.averageBitrate).setPeakBitrate(format.peakBitrate).setWidth(format.width).setHeight(format.height).setFrameRate(format.frameRate).setSelectionFlags(format.selectionFlags).setRoleFlags(format.roleFlags).build();
    }

    private static Format deriveAudioFormat(Format format, Format format2, boolean z) {
        String codecsOfType;
        Metadata metadata;
        int i;
        String str;
        int i2;
        int i3;
        String str2;
        if (format2 != null) {
            codecsOfType = format2.codecs;
            metadata = format2.metadata;
            i2 = format2.channelCount;
            i = format2.selectionFlags;
            i3 = format2.roleFlags;
            str = format2.language;
            str2 = format2.label;
        } else {
            codecsOfType = Util.getCodecsOfType(format.codecs, 1);
            metadata = format.metadata;
            if (z) {
                i2 = format.channelCount;
                i = format.selectionFlags;
                i3 = format.roleFlags;
                str = format.language;
                str2 = format.label;
            } else {
                i = 0;
                str = null;
                i2 = -1;
                i3 = 0;
                str2 = null;
            }
        }
        return new Format.Builder().setId(format.id).setLabel(str2).setContainerMimeType(format.containerMimeType).setSampleMimeType(MimeTypes.getMediaMimeType(codecsOfType)).setCodecs(codecsOfType).setMetadata(metadata).setAverageBitrate(z ? format.averageBitrate : -1).setPeakBitrate(z ? format.peakBitrate : -1).setChannelCount(i2).setSelectionFlags(i).setRoleFlags(i3).setLanguage(str).build();
    }

    private class SampleStreamWrapperCallback implements HlsSampleStreamWrapper.Callback {
        private SampleStreamWrapperCallback() {
        }

        @Override // com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper.Callback
        public void onPrepared() {
            if (HlsMediaPeriod.access$106(HlsMediaPeriod.this) > 0) {
                return;
            }
            int i = 0;
            for (HlsSampleStreamWrapper hlsSampleStreamWrapper : HlsMediaPeriod.this.sampleStreamWrappers) {
                i += hlsSampleStreamWrapper.getTrackGroups().length;
            }
            TrackGroup[] trackGroupArr = new TrackGroup[i];
            int i2 = 0;
            for (HlsSampleStreamWrapper hlsSampleStreamWrapper2 : HlsMediaPeriod.this.sampleStreamWrappers) {
                int i3 = hlsSampleStreamWrapper2.getTrackGroups().length;
                int i4 = 0;
                while (i4 < i3) {
                    trackGroupArr[i2] = hlsSampleStreamWrapper2.getTrackGroups().get(i4);
                    i4++;
                    i2++;
                }
            }
            HlsMediaPeriod.this.trackGroups = new TrackGroupArray(trackGroupArr);
            HlsMediaPeriod.this.mediaPeriodCallback.onPrepared(HlsMediaPeriod.this);
        }

        @Override // com.google.android.exoplayer2.source.hls.HlsSampleStreamWrapper.Callback
        public void onPlaylistRefreshRequired(Uri uri) {
            HlsMediaPeriod.this.playlistTracker.refreshPlaylist(uri);
        }

        @Override // com.google.android.exoplayer2.source.SequenceableLoader.Callback
        public void onContinueLoadingRequested(HlsSampleStreamWrapper hlsSampleStreamWrapper) {
            HlsMediaPeriod.this.mediaPeriodCallback.onContinueLoadingRequested(HlsMediaPeriod.this);
        }
    }
}
