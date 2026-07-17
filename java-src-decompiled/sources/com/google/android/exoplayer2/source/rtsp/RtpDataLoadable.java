package com.google.android.exoplayer2.source.rtsp;

import android.os.Handler;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.DefaultExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.source.rtsp.RtpDataChannel;
import com.google.android.exoplayer2.upstream.DataReader;
import com.google.android.exoplayer2.upstream.DataSourceUtil;
import com.google.android.exoplayer2.upstream.Loader;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
final class RtpDataLoadable implements Loader.Loadable {
    private RtpDataChannel dataChannel;
    private final EventListener eventListener;
    private RtpExtractor extractor;
    private DefaultExtractorInput extractorInput;
    private volatile boolean loadCancelled;
    private volatile long nextRtpTimestamp;
    private final ExtractorOutput output;
    private final RtpDataChannel.Factory rtpDataChannelFactory;
    public final RtspMediaTrack rtspMediaTrack;
    public final int trackId;
    private final Handler playbackThreadHandler = Util.createHandlerForCurrentLooper();
    private volatile long pendingSeekPositionUs = C.TIME_UNSET;

    public interface EventListener {
        void onTransportReady(String str, RtpDataChannel rtpDataChannel);
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
    public void cancelLoad() {
        this.loadCancelled = true;
    }

    public void seekToUs(long j, long j2) {
        this.pendingSeekPositionUs = j;
        this.nextRtpTimestamp = j2;
    }

    public RtpDataLoadable(int i, RtspMediaTrack rtspMediaTrack, EventListener eventListener, ExtractorOutput extractorOutput, RtpDataChannel.Factory factory) {
        this.trackId = i;
        this.rtspMediaTrack = rtspMediaTrack;
        this.eventListener = eventListener;
        this.output = extractorOutput;
        this.rtpDataChannelFactory = factory;
    }

    public void setTimestamp(long j) {
        if (j == C.TIME_UNSET || ((RtpExtractor) Assertions.checkNotNull(this.extractor)).hasReadFirstRtpPacket()) {
            return;
        }
        this.extractor.setFirstTimestamp(j);
    }

    public void setSequenceNumber(int i) {
        if (((RtpExtractor) Assertions.checkNotNull(this.extractor)).hasReadFirstRtpPacket()) {
            return;
        }
        this.extractor.setFirstSequenceNumber(i);
    }

    @Override // com.google.android.exoplayer2.upstream.Loader.Loadable
    public void load() throws IOException {
        if (this.loadCancelled) {
            this.loadCancelled = false;
        }
        try {
            if (this.dataChannel == null) {
                RtpDataChannel rtpDataChannelCreateAndOpenDataChannel = this.rtpDataChannelFactory.createAndOpenDataChannel(this.trackId);
                this.dataChannel = rtpDataChannelCreateAndOpenDataChannel;
                final String transport = rtpDataChannelCreateAndOpenDataChannel.getTransport();
                final RtpDataChannel rtpDataChannel = this.dataChannel;
                this.playbackThreadHandler.post(new Runnable() { // from class: com.google.android.exoplayer2.source.rtsp.RtpDataLoadable$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.m158x79854435(transport, rtpDataChannel);
                    }
                });
                this.extractorInput = new DefaultExtractorInput((DataReader) Assertions.checkNotNull(this.dataChannel), 0L, -1L);
                RtpExtractor rtpExtractor = new RtpExtractor(this.rtspMediaTrack.payloadFormat, this.trackId);
                this.extractor = rtpExtractor;
                rtpExtractor.init(this.output);
            }
            while (!this.loadCancelled) {
                if (this.pendingSeekPositionUs != C.TIME_UNSET) {
                    ((RtpExtractor) Assertions.checkNotNull(this.extractor)).seek(this.nextRtpTimestamp, this.pendingSeekPositionUs);
                    this.pendingSeekPositionUs = C.TIME_UNSET;
                }
                if (((RtpExtractor) Assertions.checkNotNull(this.extractor)).read((ExtractorInput) Assertions.checkNotNull(this.extractorInput), new PositionHolder()) == -1) {
                    break;
                }
            }
            this.loadCancelled = false;
        } finally {
            if (((RtpDataChannel) Assertions.checkNotNull(this.dataChannel)).needsClosingOnLoadCompletion()) {
                DataSourceUtil.closeQuietly(this.dataChannel);
                this.dataChannel = null;
            }
        }
    }

    /* JADX INFO: renamed from: lambda$load$0$com-google-android-exoplayer2-source-rtsp-RtpDataLoadable, reason: not valid java name */
    /* synthetic */ void m158x79854435(String str, RtpDataChannel rtpDataChannel) {
        this.eventListener.onTransportReady(str, rtpDataChannel);
    }

    public void resetForSeek() {
        ((RtpExtractor) Assertions.checkNotNull(this.extractor)).preSeek();
    }
}
