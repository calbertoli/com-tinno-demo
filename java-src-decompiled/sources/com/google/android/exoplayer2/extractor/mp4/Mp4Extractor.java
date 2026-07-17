package com.google.android.exoplayer2.extractor.mp4;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.audio.Ac4Util;
import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorInput;
import com.google.android.exoplayer2.extractor.ExtractorOutput;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.GaplessInfoHolder;
import com.google.android.exoplayer2.extractor.PositionHolder;
import com.google.android.exoplayer2.extractor.SeekMap;
import com.google.android.exoplayer2.extractor.TrackOutput;
import com.google.android.exoplayer2.extractor.TrueHdSampleRechunker;
import com.google.android.exoplayer2.extractor.mp4.Atom;
import com.google.android.exoplayer2.extractor.mp4.AtomParsers;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.mp4.MotionPhotoMetadata;
import com.google.android.exoplayer2.upstream.DataReader;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import com.google.common.base.Function;
import java.io.IOException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class Mp4Extractor implements Extractor, SeekMap {
    public static final ExtractorsFactory FACTORY = new ExtractorsFactory() { // from class: com.google.android.exoplayer2.extractor.mp4.Mp4Extractor$$ExternalSyntheticLambda0
        @Override // com.google.android.exoplayer2.extractor.ExtractorsFactory
        public final Extractor[] createExtractors() {
            return Mp4Extractor.lambda$static$0();
        }
    };
    private static final int FILE_TYPE_HEIC = 2;
    private static final int FILE_TYPE_MP4 = 0;
    private static final int FILE_TYPE_QUICKTIME = 1;
    public static final int FLAG_READ_MOTION_PHOTO_METADATA = 2;
    public static final int FLAG_READ_SEF_DATA = 4;
    public static final int FLAG_WORKAROUND_IGNORE_EDIT_LISTS = 1;
    private static final long MAXIMUM_READ_AHEAD_BYTES_STREAM = 10485760;
    private static final long RELOAD_MINIMUM_SEEK_DISTANCE = 262144;
    private static final int STATE_READING_ATOM_HEADER = 0;
    private static final int STATE_READING_ATOM_PAYLOAD = 1;
    private static final int STATE_READING_SAMPLE = 2;
    private static final int STATE_READING_SEF = 3;
    private long[][] accumulatedSampleSizes;
    private ParsableByteArray atomData;
    private final ParsableByteArray atomHeader;
    private int atomHeaderBytesRead;
    private long atomSize;
    private int atomType;
    private final ArrayDeque<Atom.ContainerAtom> containerAtoms;
    private long durationUs;
    private ExtractorOutput extractorOutput;
    private int fileType;
    private int firstVideoTrackIndex;
    private final int flags;
    private MotionPhotoMetadata motionPhotoMetadata;
    private final ParsableByteArray nalLength;
    private final ParsableByteArray nalStartCode;
    private int parserState;
    private int sampleBytesRead;
    private int sampleBytesWritten;
    private int sampleCurrentNalBytesRemaining;
    private int sampleTrackIndex;
    private final ParsableByteArray scratch;
    private final SefReader sefReader;
    private final List<Metadata.Entry> slowMotionMetadataEntries;
    private Mp4Track[] tracks;

    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    private static int brandToFileType(int i) {
        if (i != 1751476579) {
            return i != 1903435808 ? 0 : 1;
        }
        return 2;
    }

    private void enterReadingAtomHeaderState() {
        this.parserState = 0;
        this.atomHeaderBytesRead = 0;
    }

    static /* synthetic */ Track lambda$processMoovAtom$1(Track track) {
        return track;
    }

    private static boolean shouldParseContainerAtom(int i) {
        return i == 1836019574 || i == 1953653099 || i == 1835297121 || i == 1835626086 || i == 1937007212 || i == 1701082227 || i == 1835365473;
    }

    private static boolean shouldParseLeafAtom(int i) {
        return i == 1835296868 || i == 1836476516 || i == 1751411826 || i == 1937011556 || i == 1937011827 || i == 1937011571 || i == 1668576371 || i == 1701606260 || i == 1937011555 || i == 1937011578 || i == 1937013298 || i == 1937007471 || i == 1668232756 || i == 1953196132 || i == 1718909296 || i == 1969517665 || i == 1801812339 || i == 1768715124;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public long getDurationUs() {
        return this.durationUs;
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public boolean isSeekable() {
        return true;
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void release() {
    }

    static /* synthetic */ Extractor[] lambda$static$0() {
        return new Extractor[]{new Mp4Extractor()};
    }

    public Mp4Extractor() {
        this(0);
    }

    public Mp4Extractor(int i) {
        this.flags = i;
        this.parserState = (i & 4) != 0 ? 3 : 0;
        this.sefReader = new SefReader();
        this.slowMotionMetadataEntries = new ArrayList();
        this.atomHeader = new ParsableByteArray(16);
        this.containerAtoms = new ArrayDeque<>();
        this.nalStartCode = new ParsableByteArray(NalUnitUtil.NAL_START_CODE);
        this.nalLength = new ParsableByteArray(4);
        this.scratch = new ParsableByteArray();
        this.sampleTrackIndex = -1;
        this.extractorOutput = ExtractorOutput.PLACEHOLDER;
        this.tracks = new Mp4Track[0];
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public boolean sniff(ExtractorInput extractorInput) throws IOException {
        return Sniffer.sniffUnfragmented(extractorInput, (this.flags & 2) != 0);
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public void seek(long j, long j2) {
        this.containerAtoms.clear();
        this.atomHeaderBytesRead = 0;
        this.sampleTrackIndex = -1;
        this.sampleBytesRead = 0;
        this.sampleBytesWritten = 0;
        this.sampleCurrentNalBytesRemaining = 0;
        if (j == 0) {
            if (this.parserState != 3) {
                enterReadingAtomHeaderState();
                return;
            } else {
                this.sefReader.reset();
                this.slowMotionMetadataEntries.clear();
                return;
            }
        }
        for (Mp4Track mp4Track : this.tracks) {
            updateSampleIndex(mp4Track, j2);
            if (mp4Track.trueHdSampleRechunker != null) {
                mp4Track.trueHdSampleRechunker.reset();
            }
        }
    }

    @Override // com.google.android.exoplayer2.extractor.Extractor
    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException {
        while (true) {
            int i = this.parserState;
            if (i != 0) {
                if (i != 1) {
                    if (i == 2) {
                        return readSample(extractorInput, positionHolder);
                    }
                    if (i == 3) {
                        return readSefData(extractorInput, positionHolder);
                    }
                    throw new IllegalStateException();
                }
                if (readAtomPayload(extractorInput, positionHolder)) {
                    return 1;
                }
            } else if (!readAtomHeader(extractorInput)) {
                return -1;
            }
        }
    }

    @Override // com.google.android.exoplayer2.extractor.SeekMap
    public SeekMap.SeekPoints getSeekPoints(long j) {
        return getSeekPoints(j, -1);
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0062  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0088  */
    /* JADX WARN: Removed duplicated region for block: B:40:0x008e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public com.google.android.exoplayer2.extractor.SeekMap.SeekPoints getSeekPoints(long r17, int r19) {
        /*
            r16 = this;
            r0 = r16
            r1 = r17
            r3 = r19
            com.google.android.exoplayer2.extractor.mp4.Mp4Extractor$Mp4Track[] r4 = r0.tracks
            int r5 = r4.length
            if (r5 != 0) goto L13
            com.google.android.exoplayer2.extractor.SeekMap$SeekPoints r1 = new com.google.android.exoplayer2.extractor.SeekMap$SeekPoints
            com.google.android.exoplayer2.extractor.SeekPoint r2 = com.google.android.exoplayer2.extractor.SeekPoint.START
            r1.<init>(r2)
            return r1
        L13:
            r5 = -1
            if (r3 == r5) goto L18
            r6 = r3
            goto L1a
        L18:
            int r6 = r0.firstVideoTrackIndex
        L1a:
            r7 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            r9 = -1
            if (r6 == r5) goto L58
            r4 = r4[r6]
            com.google.android.exoplayer2.extractor.mp4.TrackSampleTable r4 = r4.sampleTable
            int r6 = getSynchronizationSampleIndex(r4, r1)
            if (r6 != r5) goto L35
            com.google.android.exoplayer2.extractor.SeekMap$SeekPoints r1 = new com.google.android.exoplayer2.extractor.SeekMap$SeekPoints
            com.google.android.exoplayer2.extractor.SeekPoint r2 = com.google.android.exoplayer2.extractor.SeekPoint.START
            r1.<init>(r2)
            return r1
        L35:
            long[] r11 = r4.timestampsUs
            r12 = r11[r6]
            long[] r11 = r4.offsets
            r14 = r11[r6]
            int r11 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r11 >= 0) goto L5e
            int r11 = r4.sampleCount
            int r11 = r11 + (-1)
            if (r6 >= r11) goto L5e
            int r1 = r4.getIndexOfLaterOrEqualSynchronizationSample(r1)
            if (r1 == r5) goto L5e
            if (r1 == r6) goto L5e
            long[] r2 = r4.timestampsUs
            r9 = r2[r1]
            long[] r2 = r4.offsets
            r1 = r2[r1]
            goto L60
        L58:
            r14 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r12 = r1
        L5e:
            r1 = r9
            r9 = r7
        L60:
            if (r3 != r5) goto L7f
            r3 = 0
        L63:
            com.google.android.exoplayer2.extractor.mp4.Mp4Extractor$Mp4Track[] r4 = r0.tracks
            int r5 = r4.length
            if (r3 >= r5) goto L7f
            int r5 = r0.firstVideoTrackIndex
            if (r3 == r5) goto L7c
            r4 = r4[r3]
            com.google.android.exoplayer2.extractor.mp4.TrackSampleTable r4 = r4.sampleTable
            long r14 = maybeAdjustSeekOffset(r4, r12, r14)
            int r5 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r5 == 0) goto L7c
            long r1 = maybeAdjustSeekOffset(r4, r9, r1)
        L7c:
            int r3 = r3 + 1
            goto L63
        L7f:
            com.google.android.exoplayer2.extractor.SeekPoint r3 = new com.google.android.exoplayer2.extractor.SeekPoint
            r3.<init>(r12, r14)
            int r4 = (r9 > r7 ? 1 : (r9 == r7 ? 0 : -1))
            if (r4 != 0) goto L8e
            com.google.android.exoplayer2.extractor.SeekMap$SeekPoints r1 = new com.google.android.exoplayer2.extractor.SeekMap$SeekPoints
            r1.<init>(r3)
            return r1
        L8e:
            com.google.android.exoplayer2.extractor.SeekPoint r4 = new com.google.android.exoplayer2.extractor.SeekPoint
            r4.<init>(r9, r1)
            com.google.android.exoplayer2.extractor.SeekMap$SeekPoints r1 = new com.google.android.exoplayer2.extractor.SeekMap$SeekPoints
            r1.<init>(r3, r4)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.extractor.mp4.Mp4Extractor.getSeekPoints(long, int):com.google.android.exoplayer2.extractor.SeekMap$SeekPoints");
    }

    private boolean readAtomHeader(ExtractorInput extractorInput) throws IOException {
        Atom.ContainerAtom containerAtomPeek;
        if (this.atomHeaderBytesRead == 0) {
            if (!extractorInput.readFully(this.atomHeader.getData(), 0, 8, true)) {
                processEndOfStreamReadingAtomHeader();
                return false;
            }
            this.atomHeaderBytesRead = 8;
            this.atomHeader.setPosition(0);
            this.atomSize = this.atomHeader.readUnsignedInt();
            this.atomType = this.atomHeader.readInt();
        }
        long j = this.atomSize;
        if (j == 1) {
            extractorInput.readFully(this.atomHeader.getData(), 8, 8);
            this.atomHeaderBytesRead += 8;
            this.atomSize = this.atomHeader.readUnsignedLongToLong();
        } else if (j == 0) {
            long length = extractorInput.getLength();
            if (length == -1 && (containerAtomPeek = this.containerAtoms.peek()) != null) {
                length = containerAtomPeek.endPosition;
            }
            if (length != -1) {
                this.atomSize = (length - extractorInput.getPosition()) + ((long) this.atomHeaderBytesRead);
            }
        }
        if (this.atomSize < this.atomHeaderBytesRead) {
            throw ParserException.createForUnsupportedContainerFeature("Atom size less than header length (unsupported).");
        }
        if (shouldParseContainerAtom(this.atomType)) {
            long position = extractorInput.getPosition();
            long j2 = this.atomSize;
            int i = this.atomHeaderBytesRead;
            long j3 = (position + j2) - ((long) i);
            if (j2 != i && this.atomType == 1835365473) {
                maybeSkipRemainingMetaAtomHeaderBytes(extractorInput);
            }
            this.containerAtoms.push(new Atom.ContainerAtom(this.atomType, j3));
            if (this.atomSize == this.atomHeaderBytesRead) {
                processAtomEnded(j3);
            } else {
                enterReadingAtomHeaderState();
            }
        } else if (shouldParseLeafAtom(this.atomType)) {
            Assertions.checkState(this.atomHeaderBytesRead == 8);
            Assertions.checkState(this.atomSize <= 2147483647L);
            ParsableByteArray parsableByteArray = new ParsableByteArray((int) this.atomSize);
            System.arraycopy(this.atomHeader.getData(), 0, parsableByteArray.getData(), 0, 8);
            this.atomData = parsableByteArray;
            this.parserState = 1;
        } else {
            processUnparsedAtom(extractorInput.getPosition() - ((long) this.atomHeaderBytesRead));
            this.atomData = null;
            this.parserState = 1;
        }
        return true;
    }

    private boolean readAtomPayload(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException {
        boolean z;
        long j = this.atomSize - ((long) this.atomHeaderBytesRead);
        long position = extractorInput.getPosition() + j;
        ParsableByteArray parsableByteArray = this.atomData;
        if (parsableByteArray != null) {
            extractorInput.readFully(parsableByteArray.getData(), this.atomHeaderBytesRead, (int) j);
            if (this.atomType == 1718909296) {
                this.fileType = processFtypAtom(parsableByteArray);
            } else if (!this.containerAtoms.isEmpty()) {
                this.containerAtoms.peek().add(new Atom.LeafAtom(this.atomType, parsableByteArray));
            }
        } else if (j < 262144) {
            extractorInput.skipFully((int) j);
        } else {
            positionHolder.position = extractorInput.getPosition() + j;
            z = true;
            processAtomEnded(position);
            return (z || this.parserState == 2) ? false : true;
        }
        z = false;
        processAtomEnded(position);
        if (z) {
        }
    }

    private int readSefData(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException {
        int i = this.sefReader.read(extractorInput, positionHolder, this.slowMotionMetadataEntries);
        if (i == 1 && positionHolder.position == 0) {
            enterReadingAtomHeaderState();
        }
        return i;
    }

    private void processAtomEnded(long j) throws ParserException {
        while (!this.containerAtoms.isEmpty() && this.containerAtoms.peek().endPosition == j) {
            Atom.ContainerAtom containerAtomPop = this.containerAtoms.pop();
            if (containerAtomPop.type == 1836019574) {
                processMoovAtom(containerAtomPop);
                this.containerAtoms.clear();
                this.parserState = 2;
            } else if (!this.containerAtoms.isEmpty()) {
                this.containerAtoms.peek().add(containerAtomPop);
            }
        }
        if (this.parserState != 2) {
            enterReadingAtomHeaderState();
        }
    }

    private void processMoovAtom(Atom.ContainerAtom containerAtom) throws ParserException {
        Metadata metadata;
        Metadata metadata2;
        Metadata metadata3;
        List<TrackSampleTable> list;
        int i;
        int i2;
        ArrayList arrayList = new ArrayList();
        boolean z = this.fileType == 1;
        GaplessInfoHolder gaplessInfoHolder = new GaplessInfoHolder();
        Atom.LeafAtom leafAtomOfType = containerAtom.getLeafAtomOfType(Atom.TYPE_udta);
        if (leafAtomOfType != null) {
            AtomParsers.UdtaInfo udta = AtomParsers.parseUdta(leafAtomOfType);
            Metadata metadata4 = udta.metaMetadata;
            Metadata metadata5 = udta.smtaMetadata;
            Metadata metadata6 = udta.xyzMetadata;
            if (metadata4 != null) {
                gaplessInfoHolder.setFromMetadata(metadata4);
            }
            metadata = metadata6;
            metadata2 = metadata4;
            metadata3 = metadata5;
        } else {
            metadata = null;
            metadata2 = null;
            metadata3 = null;
        }
        Atom.ContainerAtom containerAtomOfType = containerAtom.getContainerAtomOfType(Atom.TYPE_meta);
        Metadata mdtaFromMeta = containerAtomOfType != null ? AtomParsers.parseMdtaFromMeta(containerAtomOfType) : null;
        Metadata metadata7 = AtomParsers.parseMvhd(((Atom.LeafAtom) Assertions.checkNotNull(containerAtom.getLeafAtomOfType(Atom.TYPE_mvhd))).data).metadata;
        Metadata metadata8 = mdtaFromMeta;
        List<TrackSampleTable> traks = AtomParsers.parseTraks(containerAtom, gaplessInfoHolder, C.TIME_UNSET, null, (this.flags & 1) != 0, z, new Function() { // from class: com.google.android.exoplayer2.extractor.mp4.Mp4Extractor$$ExternalSyntheticLambda1
            @Override // com.google.common.base.Function
            public final Object apply(Object obj) {
                return Mp4Extractor.lambda$processMoovAtom$1((Track) obj);
            }
        });
        int size = traks.size();
        long j = C.TIME_UNSET;
        long j2 = -9223372036854775807L;
        int i3 = 0;
        int size2 = -1;
        while (i3 < size) {
            TrackSampleTable trackSampleTable = traks.get(i3);
            if (trackSampleTable.sampleCount == 0) {
                list = traks;
                i = size;
            } else {
                Track track = trackSampleTable.track;
                list = traks;
                i = size;
                long j3 = track.durationUs != j ? track.durationUs : trackSampleTable.durationUs;
                long jMax = Math.max(j2, j3);
                Mp4Track mp4Track = new Mp4Track(track, trackSampleTable, this.extractorOutput.track(i3, track.type));
                if (MimeTypes.AUDIO_TRUEHD.equals(track.format.sampleMimeType)) {
                    i2 = trackSampleTable.maximumSize * 16;
                } else {
                    i2 = trackSampleTable.maximumSize + 30;
                }
                Format.Builder builderBuildUpon = track.format.buildUpon();
                builderBuildUpon.setMaxInputSize(i2);
                if (track.type == 2 && j3 > 0 && trackSampleTable.sampleCount > 1) {
                    builderBuildUpon.setFrameRate(trackSampleTable.sampleCount / (j3 / 1000000.0f));
                }
                MetadataUtil.setFormatGaplessInfo(track.type, gaplessInfoHolder, builderBuildUpon);
                int i4 = track.type;
                Metadata[] metadataArr = new Metadata[4];
                metadataArr[0] = metadata3;
                metadataArr[1] = this.slowMotionMetadataEntries.isEmpty() ? null : new Metadata(this.slowMotionMetadataEntries);
                metadataArr[2] = metadata;
                metadataArr[3] = metadata7;
                MetadataUtil.setFormatMetadata(i4, metadata2, metadata8, builderBuildUpon, metadataArr);
                mp4Track.trackOutput.format(builderBuildUpon.build());
                if (track.type == 2 && size2 == -1) {
                    size2 = arrayList.size();
                }
                arrayList.add(mp4Track);
                j2 = jMax;
            }
            i3++;
            traks = list;
            size = i;
            j = C.TIME_UNSET;
        }
        this.firstVideoTrackIndex = size2;
        this.durationUs = j2;
        Mp4Track[] mp4TrackArr = (Mp4Track[]) arrayList.toArray(new Mp4Track[0]);
        this.tracks = mp4TrackArr;
        this.accumulatedSampleSizes = calculateAccumulatedSampleSizes(mp4TrackArr);
        this.extractorOutput.endTracks();
        this.extractorOutput.seekMap(this);
    }

    private int readSample(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException {
        int i;
        PositionHolder positionHolder2;
        int i2;
        long position = extractorInput.getPosition();
        if (this.sampleTrackIndex == -1) {
            int trackIndexOfNextReadSample = getTrackIndexOfNextReadSample(position);
            this.sampleTrackIndex = trackIndexOfNextReadSample;
            if (trackIndexOfNextReadSample == -1) {
                return -1;
            }
        }
        Mp4Track mp4Track = this.tracks[this.sampleTrackIndex];
        TrackOutput trackOutput = mp4Track.trackOutput;
        int i3 = mp4Track.sampleIndex;
        long j = mp4Track.sampleTable.offsets[i3];
        int i4 = mp4Track.sampleTable.sizes[i3];
        TrueHdSampleRechunker trueHdSampleRechunker = mp4Track.trueHdSampleRechunker;
        long j2 = (j - position) + ((long) this.sampleBytesRead);
        if (j2 < 0) {
            i = 1;
            positionHolder2 = positionHolder;
        } else {
            if (j2 < 262144) {
                if (mp4Track.track.sampleTransformation == 1) {
                    j2 += 8;
                    i4 -= 8;
                }
                extractorInput.skipFully((int) j2);
                if (mp4Track.track.nalUnitLengthFieldLength != 0) {
                    byte[] data = this.nalLength.getData();
                    data[0] = 0;
                    data[1] = 0;
                    data[2] = 0;
                    int i5 = mp4Track.track.nalUnitLengthFieldLength;
                    int i6 = 4;
                    int i7 = 4 - mp4Track.track.nalUnitLengthFieldLength;
                    while (this.sampleBytesWritten < i4) {
                        int i8 = this.sampleCurrentNalBytesRemaining;
                        if (i8 == 0) {
                            extractorInput.readFully(data, i7, i5);
                            this.sampleBytesRead += i5;
                            this.nalLength.setPosition(0);
                            int i9 = this.nalLength.readInt();
                            if (i9 < 0) {
                                throw ParserException.createForMalformedContainer("Invalid NAL length", null);
                            }
                            this.sampleCurrentNalBytesRemaining = i9;
                            this.nalStartCode.setPosition(0);
                            trackOutput.sampleData(this.nalStartCode, i6);
                            this.sampleBytesWritten += i6;
                            i4 += i7;
                        } else {
                            int iSampleData = trackOutput.sampleData((DataReader) extractorInput, i8, false);
                            this.sampleBytesRead += iSampleData;
                            this.sampleBytesWritten += iSampleData;
                            this.sampleCurrentNalBytesRemaining -= iSampleData;
                            i6 = 4;
                        }
                    }
                } else {
                    if (MimeTypes.AUDIO_AC4.equals(mp4Track.track.format.sampleMimeType)) {
                        if (this.sampleBytesWritten == 0) {
                            Ac4Util.getAc4SampleHeader(i4, this.scratch);
                            trackOutput.sampleData(this.scratch, 7);
                            this.sampleBytesWritten += 7;
                        }
                        i4 += 7;
                    } else if (trueHdSampleRechunker != null) {
                        trueHdSampleRechunker.startSample(extractorInput);
                    }
                    while (true) {
                        int i10 = this.sampleBytesWritten;
                        if (i10 >= i4) {
                            break;
                        }
                        int iSampleData2 = trackOutput.sampleData((DataReader) extractorInput, i4 - i10, false);
                        this.sampleBytesRead += iSampleData2;
                        this.sampleBytesWritten += iSampleData2;
                        this.sampleCurrentNalBytesRemaining -= iSampleData2;
                    }
                }
                int i11 = i4;
                long j3 = mp4Track.sampleTable.timestampsUs[i3];
                int i12 = mp4Track.sampleTable.flags[i3];
                if (trueHdSampleRechunker != null) {
                    trueHdSampleRechunker.sampleMetadata(trackOutput, j3, i12, i11, 0, null);
                    if (i3 + 1 == mp4Track.sampleTable.sampleCount) {
                        trueHdSampleRechunker.outputPendingSampleMetadata(trackOutput, null);
                    }
                    i2 = 0;
                } else {
                    i2 = 0;
                    trackOutput.sampleMetadata(j3, i12, i11, 0, null);
                }
                mp4Track.sampleIndex++;
                this.sampleTrackIndex = -1;
                this.sampleBytesRead = i2;
                this.sampleBytesWritten = i2;
                this.sampleCurrentNalBytesRemaining = i2;
                return i2;
            }
            positionHolder2 = positionHolder;
            i = 1;
        }
        positionHolder2.position = j;
        return i;
    }

    private int getTrackIndexOfNextReadSample(long j) {
        int i = -1;
        int i2 = -1;
        int i3 = 0;
        long j2 = Long.MAX_VALUE;
        boolean z = true;
        long j3 = Long.MAX_VALUE;
        boolean z2 = true;
        long j4 = Long.MAX_VALUE;
        while (true) {
            Mp4Track[] mp4TrackArr = this.tracks;
            if (i3 >= mp4TrackArr.length) {
                break;
            }
            Mp4Track mp4Track = mp4TrackArr[i3];
            int i4 = mp4Track.sampleIndex;
            if (i4 != mp4Track.sampleTable.sampleCount) {
                long j5 = mp4Track.sampleTable.offsets[i4];
                long j6 = ((long[][]) Util.castNonNull(this.accumulatedSampleSizes))[i3][i4];
                long j7 = j5 - j;
                boolean z3 = j7 < 0 || j7 >= 262144;
                if ((!z3 && z2) || (z3 == z2 && j7 < j4)) {
                    z2 = z3;
                    j4 = j7;
                    i2 = i3;
                    j3 = j6;
                }
                if (j6 < j2) {
                    z = z3;
                    i = i3;
                    j2 = j6;
                }
            }
            i3++;
        }
        return (j2 == Long.MAX_VALUE || !z || j3 < j2 + MAXIMUM_READ_AHEAD_BYTES_STREAM) ? i2 : i;
    }

    private void updateSampleIndex(Mp4Track mp4Track, long j) {
        TrackSampleTable trackSampleTable = mp4Track.sampleTable;
        int indexOfEarlierOrEqualSynchronizationSample = trackSampleTable.getIndexOfEarlierOrEqualSynchronizationSample(j);
        if (indexOfEarlierOrEqualSynchronizationSample == -1) {
            indexOfEarlierOrEqualSynchronizationSample = trackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(j);
        }
        mp4Track.sampleIndex = indexOfEarlierOrEqualSynchronizationSample;
    }

    private void processEndOfStreamReadingAtomHeader() {
        if (this.fileType != 2 || (this.flags & 2) == 0) {
            return;
        }
        this.extractorOutput.track(0, 4).format(new Format.Builder().setMetadata(this.motionPhotoMetadata == null ? null : new Metadata(this.motionPhotoMetadata)).build());
        this.extractorOutput.endTracks();
        this.extractorOutput.seekMap(new SeekMap.Unseekable(C.TIME_UNSET));
    }

    private void maybeSkipRemainingMetaAtomHeaderBytes(ExtractorInput extractorInput) throws IOException {
        this.scratch.reset(8);
        extractorInput.peekFully(this.scratch.getData(), 0, 8);
        AtomParsers.maybeSkipRemainingMetaAtomHeaderBytes(this.scratch);
        extractorInput.skipFully(this.scratch.getPosition());
        extractorInput.resetPeekPosition();
    }

    private void processUnparsedAtom(long j) {
        if (this.atomType == 1836086884) {
            int i = this.atomHeaderBytesRead;
            this.motionPhotoMetadata = new MotionPhotoMetadata(0L, j, C.TIME_UNSET, j + ((long) i), this.atomSize - ((long) i));
        }
    }

    private static long[][] calculateAccumulatedSampleSizes(Mp4Track[] mp4TrackArr) {
        long[][] jArr = new long[mp4TrackArr.length][];
        int[] iArr = new int[mp4TrackArr.length];
        long[] jArr2 = new long[mp4TrackArr.length];
        boolean[] zArr = new boolean[mp4TrackArr.length];
        for (int i = 0; i < mp4TrackArr.length; i++) {
            jArr[i] = new long[mp4TrackArr[i].sampleTable.sampleCount];
            jArr2[i] = mp4TrackArr[i].sampleTable.timestampsUs[0];
        }
        long j = 0;
        int i2 = 0;
        while (i2 < mp4TrackArr.length) {
            long j2 = Long.MAX_VALUE;
            int i3 = -1;
            for (int i4 = 0; i4 < mp4TrackArr.length; i4++) {
                if (!zArr[i4]) {
                    long j3 = jArr2[i4];
                    if (j3 <= j2) {
                        i3 = i4;
                        j2 = j3;
                    }
                }
            }
            int i5 = iArr[i3];
            jArr[i3][i5] = j;
            j += (long) mp4TrackArr[i3].sampleTable.sizes[i5];
            int i6 = i5 + 1;
            iArr[i3] = i6;
            if (i6 < jArr[i3].length) {
                jArr2[i3] = mp4TrackArr[i3].sampleTable.timestampsUs[i6];
            } else {
                zArr[i3] = true;
                i2++;
            }
        }
        return jArr;
    }

    private static long maybeAdjustSeekOffset(TrackSampleTable trackSampleTable, long j, long j2) {
        int synchronizationSampleIndex = getSynchronizationSampleIndex(trackSampleTable, j);
        return synchronizationSampleIndex == -1 ? j2 : Math.min(trackSampleTable.offsets[synchronizationSampleIndex], j2);
    }

    private static int getSynchronizationSampleIndex(TrackSampleTable trackSampleTable, long j) {
        int indexOfEarlierOrEqualSynchronizationSample = trackSampleTable.getIndexOfEarlierOrEqualSynchronizationSample(j);
        return indexOfEarlierOrEqualSynchronizationSample == -1 ? trackSampleTable.getIndexOfLaterOrEqualSynchronizationSample(j) : indexOfEarlierOrEqualSynchronizationSample;
    }

    private static int processFtypAtom(ParsableByteArray parsableByteArray) {
        parsableByteArray.setPosition(8);
        int iBrandToFileType = brandToFileType(parsableByteArray.readInt());
        if (iBrandToFileType != 0) {
            return iBrandToFileType;
        }
        parsableByteArray.skipBytes(4);
        while (parsableByteArray.bytesLeft() > 0) {
            int iBrandToFileType2 = brandToFileType(parsableByteArray.readInt());
            if (iBrandToFileType2 != 0) {
                return iBrandToFileType2;
            }
        }
        return 0;
    }

    private static final class Mp4Track {
        public int sampleIndex;
        public final TrackSampleTable sampleTable;
        public final Track track;
        public final TrackOutput trackOutput;
        public final TrueHdSampleRechunker trueHdSampleRechunker;

        public Mp4Track(Track track, TrackSampleTable trackSampleTable, TrackOutput trackOutput) {
            this.track = track;
            this.sampleTable = trackSampleTable;
            this.trackOutput = trackOutput;
            this.trueHdSampleRechunker = MimeTypes.AUDIO_TRUEHD.equals(track.format.sampleMimeType) ? new TrueHdSampleRechunker() : null;
        }
    }
}
