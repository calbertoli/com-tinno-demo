package com.google.android.exoplayer2.extractor.ts;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.ts.TsPayloadReader;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.TimestampAdjuster;
import com.google.android.exoplayer2.util.Util;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class TsExtractor implements Extractor {
    private static final long AC3_FORMAT_IDENTIFIER = 1094921523;
    private static final long AC4_FORMAT_IDENTIFIER = 1094921524;
    private static final int BUFFER_SIZE = 9400;
    public static final int DEFAULT_TIMESTAMP_SEARCH_BYTES = 112800;
    private static final long E_AC3_FORMAT_IDENTIFIER = 1161904947;
    public static final ExtractorsFactory FACTORY = new ExtractorsFactory() { // from class: com.google.android.exoplayer2.extractor.ts.TsExtractor$$ExternalSyntheticLambda0
        @Override // com.google.android.exoplayer2.extractor.ExtractorsFactory
        public final Extractor[] createExtractors() {
            return TsExtractor.lambda$static$0();
        }
    };
    private static final long HEVC_FORMAT_IDENTIFIER = 1212503619;
    private static final int MAX_PID_PLUS_ONE = 8192;
    public static final int MODE_HLS = 2;
    public static final int MODE_MULTI_PMT = 0;
    public static final int MODE_SINGLE_PMT = 1;
    private static final int SNIFF_TS_PACKET_COUNT = 5;
    public static final int TS_PACKET_SIZE = 188;
    private static final int TS_PAT_PID = 0;
    public static final int TS_STREAM_TYPE_AAC_ADTS = 15;
    public static final int TS_STREAM_TYPE_AAC_LATM = 17;
    public static final int TS_STREAM_TYPE_AC3 = 129;
    public static final int TS_STREAM_TYPE_AC4 = 172;
    public static final int TS_STREAM_TYPE_AIT = 257;
    public static final int TS_STREAM_TYPE_DC2_H262 = 128;
    public static final int TS_STREAM_TYPE_DTS = 138;
    public static final int TS_STREAM_TYPE_DVBSUBS = 89;
    public static final int TS_STREAM_TYPE_E_AC3 = 135;
    public static final int TS_STREAM_TYPE_H262 = 2;
    public static final int TS_STREAM_TYPE_H263 = 16;
    public static final int TS_STREAM_TYPE_H264 = 27;
    public static final int TS_STREAM_TYPE_H265 = 36;
    public static final int TS_STREAM_TYPE_HDMV_DTS = 130;
    public static final int TS_STREAM_TYPE_ID3 = 21;
    public static final int TS_STREAM_TYPE_MPA = 3;
    public static final int TS_STREAM_TYPE_MPA_LSF = 4;
    public static final int TS_STREAM_TYPE_SPLICE_INFO = 134;
    public static final int TS_SYNC_BYTE = 71;
    private int bytesSinceLastSync;
    private final SparseIntArray continuityCounters;
    private final TsDurationReader durationReader;
    private boolean hasOutputSeekMap;
    private TsPayloadReader id3Reader;
    private final int mode;
    private ExtractorOutput output;
    private final TsPayloadReader.Factory payloadReaderFactory;
    private int pcrPid;
    private boolean pendingSeekToStart;
    private int remainingPmts;
    private final List<TimestampAdjuster> timestampAdjusters;
    private final int timestampSearchBytes;
    private final SparseBooleanArray trackIds;
    private final SparseBooleanArray trackPids;
    private boolean tracksEnded;
    private TsBinarySearchSeeker tsBinarySearchSeeker;
    private final ParsableByteArray tsPacketBuffer;
    private final SparseArray<TsPayloadReader> tsPayloadReaders;

    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void init(ExtractorOutput extractorOutput) {
        this.output = extractorOutput;
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void release() {
    }

    static /* synthetic */ int access$108(TsExtractor tsExtractor) {
        int i = tsExtractor.remainingPmts;
        tsExtractor.remainingPmts = i + 1;
        return i;
    }

    static /* synthetic */ Extractor[] lambda$static$0() {
        return new Extractor[]{new TsExtractor()};
    }

    public TsExtractor() {
        this(0);
    }

    public TsExtractor(int i) {
        this(1, i, DEFAULT_TIMESTAMP_SEARCH_BYTES);
    }

    public TsExtractor(int i, int i2, int i3) {
        this(i, new TimestampAdjuster(0L), new DefaultTsPayloadReaderFactory(i2), i3);
    }

    public TsExtractor(int i, TimestampAdjuster timestampAdjuster, TsPayloadReader.Factory factory) {
        this(i, timestampAdjuster, factory, DEFAULT_TIMESTAMP_SEARCH_BYTES);
    }

    public TsExtractor(int i, TimestampAdjuster timestampAdjuster, TsPayloadReader.Factory factory, int i2) {
        this.payloadReaderFactory = (TsPayloadReader.Factory) Assertions.checkNotNull(factory);
        this.timestampSearchBytes = i2;
        this.mode = i;
        if (i == 1 || i == 2) {
            this.timestampAdjusters = Collections.singletonList(timestampAdjuster);
        } else {
            ArrayList arrayList = new ArrayList();
            this.timestampAdjusters = arrayList;
            arrayList.add(timestampAdjuster);
        }
        this.tsPacketBuffer = new ParsableByteArray(new byte[BUFFER_SIZE], 0);
        this.trackIds = new SparseBooleanArray();
        this.trackPids = new SparseBooleanArray();
        this.tsPayloadReaders = new SparseArray<>();
        this.continuityCounters = new SparseIntArray();
        this.durationReader = new TsDurationReader(i2);
        this.output = ExtractorOutput.PLACEHOLDER;
        this.pcrPid = -1;
        resetPayloadReaders();
    }

    /* JADX WARN: Code restructure failed: missing block: B:10:0x001e, code lost:
    
        r1 = r1 + 1;
     */
    @Override // com.google.android.exoplayer2.extractor.Extractor
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean sniff(com.google.android.exoplayer2.extractor.ExtractorInput r7) throws java.io.IOException {
        /*
            r6 = this;
            com.google.android.exoplayer2.util.ParsableByteArray r0 = r6.tsPacketBuffer
            byte[] r0 = r0.getData()
            r1 = 940(0x3ac, float:1.317E-42)
            r2 = 0
            r7.peekFully(r0, r2, r1)
            r1 = r2
        Ld:
            r3 = 188(0xbc, float:2.63E-43)
            if (r1 >= r3) goto L29
            r3 = r2
        L12:
            r4 = 5
            if (r3 >= r4) goto L24
            int r4 = r3 * 188
            int r4 = r4 + r1
            r4 = r0[r4]
            r5 = 71
            if (r4 == r5) goto L21
            int r1 = r1 + 1
            goto Ld
        L21:
            int r3 = r3 + 1
            goto L12
        L24:
            r7.skipFully(r1)
            r7 = 1
            return r7
        L29:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ts.TsExtractor.sniff(com.google.android.exoplayer2.extractor.ExtractorInput):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0045  */
    @Override // com.google.android.exoplayer2.extractor.Extractor
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void seek(long r10, long r12) {
        /*
            r9 = this;
            int r10 = r9.mode
            r11 = 2
            r0 = 1
            r1 = 0
            if (r10 == r11) goto L9
            r10 = r0
            goto La
        L9:
            r10 = r1
        La:
            com.google.android.exoplayer2.util.Assertions.checkState(r10)
            java.util.List<com.google.android.exoplayer2.util.TimestampAdjuster> r10 = r9.timestampAdjusters
            int r10 = r10.size()
            r11 = r1
        L14:
            r2 = 0
            if (r11 >= r10) goto L4b
            java.util.List<com.google.android.exoplayer2.util.TimestampAdjuster> r4 = r9.timestampAdjusters
            java.lang.Object r4 = r4.get(r11)
            com.google.android.exoplayer2.util.TimestampAdjuster r4 = (com.google.android.exoplayer2.util.TimestampAdjuster) r4
            long r5 = r4.getTimestampOffsetUs()
            r7 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            int r5 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r5 != 0) goto L2f
            r5 = r0
            goto L30
        L2f:
            r5 = r1
        L30:
            if (r5 != 0) goto L43
            long r5 = r4.getFirstSampleTimestampUs()
            int r7 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r7 == 0) goto L48
            int r2 = (r5 > r2 ? 1 : (r5 == r2 ? 0 : -1))
            if (r2 == 0) goto L48
            int r2 = (r5 > r12 ? 1 : (r5 == r12 ? 0 : -1))
            if (r2 == 0) goto L48
            goto L45
        L43:
            if (r5 == 0) goto L48
        L45:
            r4.reset(r12)
        L48:
            int r11 = r11 + 1
            goto L14
        L4b:
            int r10 = (r12 > r2 ? 1 : (r12 == r2 ? 0 : -1))
            if (r10 == 0) goto L56
            com.google.android.exoplayer2.extractor.ts.TsBinarySearchSeeker r10 = r9.tsBinarySearchSeeker
            if (r10 == 0) goto L56
            r10.setSeekTargetUs(r12)
        L56:
            com.google.android.exoplayer2.util.ParsableByteArray r10 = r9.tsPacketBuffer
            r10.reset(r1)
            android.util.SparseIntArray r10 = r9.continuityCounters
            r10.clear()
            r10 = r1
        L61:
            android.util.SparseArray<com.google.android.exoplayer2.extractor.ts.TsPayloadReader> r11 = r9.tsPayloadReaders
            int r11 = r11.size()
            if (r10 >= r11) goto L77
            android.util.SparseArray<com.google.android.exoplayer2.extractor.ts.TsPayloadReader> r11 = r9.tsPayloadReaders
            java.lang.Object r11 = r11.valueAt(r10)
            com.google.android.exoplayer2.extractor.ts.TsPayloadReader r11 = (com.google.android.exoplayer2.extractor.ts.TsPayloadReader) r11
            r11.seek()
            int r10 = r10 + 1
            goto L61
        L77:
            r9.bytesSinceLastSync = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ts.TsExtractor.seek(long, long):void");
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException {
        long length = extractorInput.getLength();
        if (this.tracksEnded) {
            if (length != -1 && this.mode != 2 && !this.durationReader.isDurationReadFinished()) {
                return this.durationReader.readDuration(extractorInput, positionHolder, this.pcrPid);
            }
            maybeOutputSeekMap(length);
            if (this.pendingSeekToStart) {
                this.pendingSeekToStart = false;
                seek(0L, 0L);
                if (extractorInput.getPosition() != 0) {
                    positionHolder.position = 0L;
                    return 1;
                }
            }
            TsBinarySearchSeeker tsBinarySearchSeeker = this.tsBinarySearchSeeker;
            if (tsBinarySearchSeeker != null && tsBinarySearchSeeker.isSeeking()) {
                return this.tsBinarySearchSeeker.handlePendingSeek(extractorInput, positionHolder);
            }
        }
        if (!fillBufferWithAtLeastOnePacket(extractorInput)) {
            return -1;
        }
        int iFindEndOfFirstTsPacketInBuffer = findEndOfFirstTsPacketInBuffer();
        int iLimit = this.tsPacketBuffer.limit();
        if (iFindEndOfFirstTsPacketInBuffer > iLimit) {
            return 0;
        }
        int i = this.tsPacketBuffer.readInt();
        if ((8388608 & i) != 0) {
            this.tsPacketBuffer.setPosition(iFindEndOfFirstTsPacketInBuffer);
            return 0;
        }
        int i2 = (4194304 & i) != 0 ? 1 : 0;
        int i3 = (2096896 & i) >> 8;
        boolean z = (i & 32) != 0;
        TsPayloadReader tsPayloadReader = (i & 16) != 0 ? this.tsPayloadReaders.get(i3) : null;
        if (tsPayloadReader == null) {
            this.tsPacketBuffer.setPosition(iFindEndOfFirstTsPacketInBuffer);
            return 0;
        }
        if (this.mode != 2) {
            int i4 = i & 15;
            int i5 = this.continuityCounters.get(i3, i4 - 1);
            this.continuityCounters.put(i3, i4);
            if (i5 == i4) {
                this.tsPacketBuffer.setPosition(iFindEndOfFirstTsPacketInBuffer);
                return 0;
            }
            if (i4 != ((i5 + 1) & 15)) {
                tsPayloadReader.seek();
            }
        }
        if (z) {
            int unsignedByte = this.tsPacketBuffer.readUnsignedByte();
            i2 |= (this.tsPacketBuffer.readUnsignedByte() & 64) != 0 ? 2 : 0;
            this.tsPacketBuffer.skipBytes(unsignedByte - 1);
        }
        boolean z2 = this.tracksEnded;
        if (shouldConsumePacketPayload(i3)) {
            this.tsPacketBuffer.setLimit(iFindEndOfFirstTsPacketInBuffer);
            tsPayloadReader.consume(this.tsPacketBuffer, i2);
            this.tsPacketBuffer.setLimit(iLimit);
        }
        if (this.mode != 2 && !z2 && this.tracksEnded && length != -1) {
            this.pendingSeekToStart = true;
        }
        this.tsPacketBuffer.setPosition(iFindEndOfFirstTsPacketInBuffer);
        return 0;
    }

    private void maybeOutputSeekMap(long j) {
        if (this.hasOutputSeekMap) {
            return;
        }
        this.hasOutputSeekMap = true;
        if (this.durationReader.getDurationUs() != C.TIME_UNSET) {
            TsBinarySearchSeeker tsBinarySearchSeeker = new TsBinarySearchSeeker(this.durationReader.getPcrTimestampAdjuster(), this.durationReader.getDurationUs(), j, this.pcrPid, this.timestampSearchBytes);
            this.tsBinarySearchSeeker = tsBinarySearchSeeker;
            this.output.seekMap(tsBinarySearchSeeker.getSeekMap());
            return;
        }
        this.output.seekMap(new SeekMap.Unseekable(this.durationReader.getDurationUs()));
    }

    private boolean fillBufferWithAtLeastOnePacket(ExtractorInput extractorInput) throws IOException {
        byte[] data = this.tsPacketBuffer.getData();
        if (9400 - this.tsPacketBuffer.getPosition() < 188) {
            int iBytesLeft = this.tsPacketBuffer.bytesLeft();
            if (iBytesLeft > 0) {
                System.arraycopy(data, this.tsPacketBuffer.getPosition(), data, 0, iBytesLeft);
            }
            this.tsPacketBuffer.reset(data, iBytesLeft);
        }
        while (this.tsPacketBuffer.bytesLeft() < 188) {
            int iLimit = this.tsPacketBuffer.limit();
            int i = extractorInput.read(data, iLimit, 9400 - iLimit);
            if (i == -1) {
                return false;
            }
            this.tsPacketBuffer.setLimit(iLimit + i);
        }
        return true;
    }

    private int findEndOfFirstTsPacketInBuffer() throws ParserException {
        int position = this.tsPacketBuffer.getPosition();
        int iLimit = this.tsPacketBuffer.limit();
        int iFindSyncBytePosition = TsUtil.findSyncBytePosition(this.tsPacketBuffer.getData(), position, iLimit);
        this.tsPacketBuffer.setPosition(iFindSyncBytePosition);
        int i = iFindSyncBytePosition + TS_PACKET_SIZE;
        if (i > iLimit) {
            int i2 = this.bytesSinceLastSync + (iFindSyncBytePosition - position);
            this.bytesSinceLastSync = i2;
            if (this.mode == 2 && i2 > 376) {
                throw ParserException.createForMalformedContainer("Cannot find sync byte. Most likely not a Transport Stream.", null);
            }
        } else {
            this.bytesSinceLastSync = 0;
        }
        return i;
    }

    private boolean shouldConsumePacketPayload(int i) {
        return this.mode == 2 || this.tracksEnded || !this.trackPids.get(i, false);
    }

    private void resetPayloadReaders() {
        this.trackIds.clear();
        this.tsPayloadReaders.clear();
        SparseArray<TsPayloadReader> sparseArrayCreateInitialPayloadReaders = this.payloadReaderFactory.createInitialPayloadReaders();
        int size = sparseArrayCreateInitialPayloadReaders.size();
        for (int i = 0; i < size; i++) {
            this.tsPayloadReaders.put(sparseArrayCreateInitialPayloadReaders.keyAt(i), sparseArrayCreateInitialPayloadReaders.valueAt(i));
        }
        this.tsPayloadReaders.put(0, new SectionReader(new PatReader()));
        this.id3Reader = null;
    }

    private class PatReader implements SectionPayloadReader {
        private final ParsableBitArray patScratch = new ParsableBitArray(new byte[4]);

        @Override // com.google.android.exoplayer2.extractor.ts.SectionPayloadReader
        public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator trackIdGenerator) {
        }

        public PatReader() {
        }

        @Override // com.google.android.exoplayer2.extractor.ts.SectionPayloadReader
        public void consume(ParsableByteArray parsableByteArray) {
            if (parsableByteArray.readUnsignedByte() == 0 && (parsableByteArray.readUnsignedByte() & 128) != 0) {
                parsableByteArray.skipBytes(6);
                int iBytesLeft = parsableByteArray.bytesLeft() / 4;
                for (int i = 0; i < iBytesLeft; i++) {
                    parsableByteArray.readBytes(this.patScratch, 4);
                    int bits = this.patScratch.readBits(16);
                    this.patScratch.skipBits(3);
                    if (bits == 0) {
                        this.patScratch.skipBits(13);
                    } else {
                        int bits2 = this.patScratch.readBits(13);
                        if (TsExtractor.this.tsPayloadReaders.get(bits2) == null) {
                            TsExtractor.this.tsPayloadReaders.put(bits2, new SectionReader(TsExtractor.this.new PmtReader(bits2)));
                            TsExtractor.access$108(TsExtractor.this);
                        }
                    }
                }
                if (TsExtractor.this.mode != 2) {
                    TsExtractor.this.tsPayloadReaders.remove(0);
                }
            }
        }
    }

    private class PmtReader implements SectionPayloadReader {
        private static final int TS_PMT_DESC_AC3 = 106;
        private static final int TS_PMT_DESC_AIT = 111;
        private static final int TS_PMT_DESC_DTS = 123;
        private static final int TS_PMT_DESC_DVBSUBS = 89;
        private static final int TS_PMT_DESC_DVB_EXT = 127;
        private static final int TS_PMT_DESC_DVB_EXT_AC4 = 21;
        private static final int TS_PMT_DESC_EAC3 = 122;
        private static final int TS_PMT_DESC_ISO639_LANG = 10;
        private static final int TS_PMT_DESC_REGISTRATION = 5;
        private final int pid;
        private final ParsableBitArray pmtScratch = new ParsableBitArray(new byte[5]);
        private final SparseArray<TsPayloadReader> trackIdToReaderScratch = new SparseArray<>();
        private final SparseIntArray trackIdToPidScratch = new SparseIntArray();

        @Override // com.google.android.exoplayer2.extractor.ts.SectionPayloadReader
        public void init(TimestampAdjuster timestampAdjuster, ExtractorOutput extractorOutput, TsPayloadReader.TrackIdGenerator trackIdGenerator) {
        }

        public PmtReader(int i) {
            this.pid = i;
        }

        @Override // com.google.android.exoplayer2.extractor.ts.SectionPayloadReader
        public void consume(ParsableByteArray parsableByteArray) {
            TimestampAdjuster timestampAdjuster;
            if (parsableByteArray.readUnsignedByte() != 2) {
                return;
            }
            if (TsExtractor.this.mode == 1 || TsExtractor.this.mode == 2 || TsExtractor.this.remainingPmts == 1) {
                timestampAdjuster = (TimestampAdjuster) TsExtractor.this.timestampAdjusters.get(0);
            } else {
                timestampAdjuster = new TimestampAdjuster(((TimestampAdjuster) TsExtractor.this.timestampAdjusters.get(0)).getFirstSampleTimestampUs());
                TsExtractor.this.timestampAdjusters.add(timestampAdjuster);
            }
            if ((parsableByteArray.readUnsignedByte() & 128) == 0) {
                return;
            }
            parsableByteArray.skipBytes(1);
            int unsignedShort = parsableByteArray.readUnsignedShort();
            int i = 3;
            parsableByteArray.skipBytes(3);
            parsableByteArray.readBytes(this.pmtScratch, 2);
            this.pmtScratch.skipBits(3);
            int i2 = 13;
            TsExtractor.this.pcrPid = this.pmtScratch.readBits(13);
            parsableByteArray.readBytes(this.pmtScratch, 2);
            int i3 = 4;
            this.pmtScratch.skipBits(4);
            parsableByteArray.skipBytes(this.pmtScratch.readBits(12));
            if (TsExtractor.this.mode == 2 && TsExtractor.this.id3Reader == null) {
                TsPayloadReader.EsInfo esInfo = new TsPayloadReader.EsInfo(21, null, null, Util.EMPTY_BYTE_ARRAY);
                TsExtractor tsExtractor = TsExtractor.this;
                tsExtractor.id3Reader = tsExtractor.payloadReaderFactory.createPayloadReader(21, esInfo);
                if (TsExtractor.this.id3Reader != null) {
                    TsExtractor.this.id3Reader.init(timestampAdjuster, TsExtractor.this.output, new TsPayloadReader.TrackIdGenerator(unsignedShort, 21, 8192));
                }
            }
            this.trackIdToReaderScratch.clear();
            this.trackIdToPidScratch.clear();
            int iBytesLeft = parsableByteArray.bytesLeft();
            while (iBytesLeft > 0) {
                parsableByteArray.readBytes(this.pmtScratch, 5);
                int bits = this.pmtScratch.readBits(8);
                this.pmtScratch.skipBits(i);
                int bits2 = this.pmtScratch.readBits(i2);
                this.pmtScratch.skipBits(i3);
                int bits3 = this.pmtScratch.readBits(12);
                TsPayloadReader.EsInfo esInfo2 = readEsInfo(parsableByteArray, bits3);
                if (bits == 6 || bits == 5) {
                    bits = esInfo2.streamType;
                }
                iBytesLeft -= bits3 + 5;
                int i4 = TsExtractor.this.mode == 2 ? bits : bits2;
                if (!TsExtractor.this.trackIds.get(i4)) {
                    TsPayloadReader tsPayloadReaderCreatePayloadReader = (TsExtractor.this.mode == 2 && bits == 21) ? TsExtractor.this.id3Reader : TsExtractor.this.payloadReaderFactory.createPayloadReader(bits, esInfo2);
                    if (TsExtractor.this.mode != 2 || bits2 < this.trackIdToPidScratch.get(i4, 8192)) {
                        this.trackIdToPidScratch.put(i4, bits2);
                        this.trackIdToReaderScratch.put(i4, tsPayloadReaderCreatePayloadReader);
                    }
                }
                i = 3;
                i3 = 4;
                i2 = 13;
            }
            int size = this.trackIdToPidScratch.size();
            for (int i5 = 0; i5 < size; i5++) {
                int iKeyAt = this.trackIdToPidScratch.keyAt(i5);
                int iValueAt = this.trackIdToPidScratch.valueAt(i5);
                TsExtractor.this.trackIds.put(iKeyAt, true);
                TsExtractor.this.trackPids.put(iValueAt, true);
                TsPayloadReader tsPayloadReaderValueAt = this.trackIdToReaderScratch.valueAt(i5);
                if (tsPayloadReaderValueAt != null) {
                    if (tsPayloadReaderValueAt != TsExtractor.this.id3Reader) {
                        tsPayloadReaderValueAt.init(timestampAdjuster, TsExtractor.this.output, new TsPayloadReader.TrackIdGenerator(unsignedShort, iKeyAt, 8192));
                    }
                    TsExtractor.this.tsPayloadReaders.put(iValueAt, tsPayloadReaderValueAt);
                }
            }
            if (TsExtractor.this.mode == 2) {
                if (TsExtractor.this.tracksEnded) {
                    return;
                }
                TsExtractor.this.output.endTracks();
                TsExtractor.this.remainingPmts = 0;
                TsExtractor.this.tracksEnded = true;
                return;
            }
            TsExtractor.this.tsPayloadReaders.remove(this.pid);
            TsExtractor tsExtractor2 = TsExtractor.this;
            tsExtractor2.remainingPmts = tsExtractor2.mode == 1 ? 0 : TsExtractor.this.remainingPmts - 1;
            if (TsExtractor.this.remainingPmts == 0) {
                TsExtractor.this.output.endTracks();
                TsExtractor.this.tracksEnded = true;
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:18:0x0043  */
        /* JADX WARN: Removed duplicated region for block: B:24:0x0055  */
        /* JADX WARN: Removed duplicated region for block: B:27:0x005b  */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        private com.google.android.exoplayer2.extractor.ts.TsPayloadReader.EsInfo readEsInfo(com.google.android.exoplayer2.util.ParsableByteArray r13, int r14) {
            /*
                Method dump skipped, instruction units count: 206
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.ts.TsExtractor.PmtReader.readEsInfo(com.google.android.exoplayer2.util.ParsableByteArray, int):com.google.android.exoplayer2.extractor.ts.TsPayloadReader$EsInfo");
        }
    }
}
