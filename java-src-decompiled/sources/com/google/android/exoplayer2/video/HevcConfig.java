package com.google.android.exoplayer2.video;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableByteArray;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class HevcConfig {
    private static final int SPS_NAL_UNIT_TYPE = 33;
    public final String codecs;
    public final int colorRange;
    public final int colorSpace;
    public final int colorTransfer;
    public final int height;
    public final List<byte[]> initializationData;
    public final int nalUnitLengthFieldLength;
    public final float pixelWidthHeightRatio;
    public final int width;

    public static HevcConfig parse(ParsableByteArray parsableByteArray) throws ParserException {
        int i;
        int i2;
        try {
            parsableByteArray.skipBytes(21);
            int unsignedByte = parsableByteArray.readUnsignedByte() & 3;
            int unsignedByte2 = parsableByteArray.readUnsignedByte();
            int position = parsableByteArray.getPosition();
            int i3 = 0;
            for (int i4 = 0; i4 < unsignedByte2; i4++) {
                parsableByteArray.skipBytes(1);
                int unsignedShort = parsableByteArray.readUnsignedShort();
                for (int i5 = 0; i5 < unsignedShort; i5++) {
                    int unsignedShort2 = parsableByteArray.readUnsignedShort();
                    i3 += unsignedShort2 + 4;
                    parsableByteArray.skipBytes(unsignedShort2);
                }
            }
            parsableByteArray.setPosition(position);
            byte[] bArr = new byte[i3];
            int i6 = -1;
            int i7 = -1;
            int i8 = -1;
            int i9 = -1;
            int i10 = -1;
            float f = 1.0f;
            String strBuildHevcCodecString = null;
            int i11 = 0;
            for (int i12 = 0; i12 < unsignedByte2; i12++) {
                int unsignedByte3 = parsableByteArray.readUnsignedByte() & 63;
                int unsignedShort3 = parsableByteArray.readUnsignedShort();
                int i13 = 0;
                while (i13 < unsignedShort3) {
                    int unsignedShort4 = parsableByteArray.readUnsignedShort();
                    int i14 = unsignedByte2;
                    System.arraycopy(NalUnitUtil.NAL_START_CODE, 0, bArr, i11, NalUnitUtil.NAL_START_CODE.length);
                    int length = i11 + NalUnitUtil.NAL_START_CODE.length;
                    System.arraycopy(parsableByteArray.getData(), parsableByteArray.getPosition(), bArr, length, unsignedShort4);
                    if (unsignedByte3 == 33 && i13 == 0) {
                        NalUnitUtil.H265SpsData h265SpsNalUnit = NalUnitUtil.parseH265SpsNalUnit(bArr, length, length + unsignedShort4);
                        int i15 = h265SpsNalUnit.width;
                        i7 = h265SpsNalUnit.height;
                        i8 = h265SpsNalUnit.colorSpace;
                        int i16 = h265SpsNalUnit.colorRange;
                        int i17 = h265SpsNalUnit.colorTransfer;
                        float f2 = h265SpsNalUnit.pixelWidthHeightRatio;
                        i = unsignedByte3;
                        i2 = unsignedShort3;
                        i6 = i15;
                        strBuildHevcCodecString = CodecSpecificDataUtil.buildHevcCodecString(h265SpsNalUnit.generalProfileSpace, h265SpsNalUnit.generalTierFlag, h265SpsNalUnit.generalProfileIdc, h265SpsNalUnit.generalProfileCompatibilityFlags, h265SpsNalUnit.constraintBytes, h265SpsNalUnit.generalLevelIdc);
                        i10 = i17;
                        i9 = i16;
                        f = f2;
                    } else {
                        i = unsignedByte3;
                        i2 = unsignedShort3;
                    }
                    i11 = length + unsignedShort4;
                    parsableByteArray.skipBytes(unsignedShort4);
                    i13++;
                    unsignedByte2 = i14;
                    unsignedByte3 = i;
                    unsignedShort3 = i2;
                }
            }
            return new HevcConfig(i3 == 0 ? Collections.emptyList() : Collections.singletonList(bArr), unsignedByte + 1, i6, i7, i8, i9, i10, f, strBuildHevcCodecString);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw ParserException.createForMalformedContainer("Error parsing HEVC config", e);
        }
    }

    private HevcConfig(List<byte[]> list, int i, int i2, int i3, int i4, int i5, int i6, float f, String str) {
        this.initializationData = list;
        this.nalUnitLengthFieldLength = i;
        this.width = i2;
        this.height = i3;
        this.colorSpace = i4;
        this.colorRange = i5;
        this.colorTransfer = i6;
        this.pixelWidthHeightRatio = f;
        this.codecs = str;
    }
}
