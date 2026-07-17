package com.google.android.exoplayer2.source.rtsp;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.audio.AacUtil;
import com.google.android.exoplayer2.extractor.ts.PsExtractor;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.CodecSpecificDataUtil;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.NalUnitUtil;
import com.google.android.exoplayer2.util.ParsableBitArray;
import com.google.android.exoplayer2.util.Util;
import com.google.common.base.Ascii;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
final class RtspMediaTrack {
    private static final String AAC_CODECS_PREFIX = "mp4a.40.";
    private static final int DEFAULT_H263_HEIGHT = 288;
    private static final int DEFAULT_H263_WIDTH = 352;
    private static final int DEFAULT_MP4V_HEIGHT = 288;
    private static final int DEFAULT_MP4V_WIDTH = 352;
    private static final int DEFAULT_VP8_HEIGHT = 240;
    private static final int DEFAULT_VP8_WIDTH = 320;
    private static final int DEFAULT_VP9_HEIGHT = 240;
    private static final int DEFAULT_VP9_WIDTH = 320;
    private static final String GENERIC_CONTROL_ATTR = "*";
    private static final String H264_CODECS_PREFIX = "avc1.";
    private static final String MPEG4_CODECS_PREFIX = "mp4v.";
    private static final int OPUS_CLOCK_RATE = 48000;
    private static final String PARAMETER_AMR_INTERLEAVING = "interleaving";
    private static final String PARAMETER_AMR_OCTET_ALIGN = "octet-align";
    private static final String PARAMETER_H265_SPROP_MAX_DON_DIFF = "sprop-max-don-diff";
    private static final String PARAMETER_H265_SPROP_PPS = "sprop-pps";
    private static final String PARAMETER_H265_SPROP_SPS = "sprop-sps";
    private static final String PARAMETER_H265_SPROP_VPS = "sprop-vps";
    private static final String PARAMETER_MP4A_CONFIG = "config";
    private static final String PARAMETER_MP4A_C_PRESENT = "cpresent";
    private static final String PARAMETER_PROFILE_LEVEL_ID = "profile-level-id";
    private static final String PARAMETER_SPROP_PARAMS = "sprop-parameter-sets";
    public final RtpPayloadFormat payloadFormat;
    public final Uri uri;

    public RtspMediaTrack(RtspHeaders rtspHeaders, MediaDescription mediaDescription, Uri uri) {
        Assertions.checkArgument(mediaDescription.attributes.containsKey(SessionDescription.ATTR_CONTROL), "missing attribute control");
        this.payloadFormat = generatePayloadFormat(mediaDescription);
        this.uri = extractTrackUri(rtspHeaders, uri, (String) Util.castNonNull(mediaDescription.attributes.get(SessionDescription.ATTR_CONTROL)));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RtspMediaTrack rtspMediaTrack = (RtspMediaTrack) obj;
        return this.payloadFormat.equals(rtspMediaTrack.payloadFormat) && this.uri.equals(rtspMediaTrack.uri);
    }

    public int hashCode() {
        return ((217 + this.payloadFormat.hashCode()) * 31) + this.uri.hashCode();
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    static RtpPayloadFormat generatePayloadFormat(MediaDescription mediaDescription) {
        int iInferChannelCount;
        byte b;
        Format.Builder builder = new Format.Builder();
        if (mediaDescription.bitrate > 0) {
            builder.setAverageBitrate(mediaDescription.bitrate);
        }
        int i = mediaDescription.rtpMapAttribute.payloadType;
        String str = mediaDescription.rtpMapAttribute.mediaEncoding;
        String mimeTypeFromRtpMediaType = RtpPayloadFormat.getMimeTypeFromRtpMediaType(str);
        builder.setSampleMimeType(mimeTypeFromRtpMediaType);
        int i2 = mediaDescription.rtpMapAttribute.clockRate;
        if ("audio".equals(mediaDescription.mediaType)) {
            iInferChannelCount = inferChannelCount(mediaDescription.rtpMapAttribute.encodingParameters, mimeTypeFromRtpMediaType);
            builder.setSampleRate(i2).setChannelCount(iInferChannelCount);
        } else {
            iInferChannelCount = -1;
        }
        ImmutableMap<String, String> fmtpParametersAsMap = mediaDescription.getFmtpParametersAsMap();
        switch (mimeTypeFromRtpMediaType.hashCode()) {
            case -1664118616:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.VIDEO_H263) ? (byte) -1 : (byte) 5;
                break;
            case -1662541442:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.VIDEO_H265) ? (byte) -1 : (byte) 7;
                break;
            case -1606874997:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.AUDIO_AMR_WB) ? (byte) -1 : (byte) 2;
                break;
            case -53558318:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.AUDIO_AAC) ? (byte) -1 : (byte) 0;
                break;
            case 187078296:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.AUDIO_AC3) ? (byte) -1 : Ascii.VT;
                break;
            case 187094639:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.AUDIO_RAW) ? (byte) -1 : (byte) 10;
                break;
            case 1187890754:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.VIDEO_MP4V) ? (byte) -1 : (byte) 4;
                break;
            case 1331836730:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.VIDEO_H264) ? (byte) -1 : (byte) 6;
                break;
            case 1503095341:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.AUDIO_AMR_NB) ? (byte) -1 : (byte) 1;
                break;
            case 1504891608:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.AUDIO_OPUS) ? (byte) -1 : (byte) 3;
                break;
            case 1599127256:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.VIDEO_VP8) ? (byte) -1 : (byte) 8;
                break;
            case 1599127257:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.VIDEO_VP9) ? (byte) -1 : (byte) 9;
                break;
            case 1903231877:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.AUDIO_ALAW) ? (byte) -1 : Ascii.FF;
                break;
            case 1903589369:
                b = !mimeTypeFromRtpMediaType.equals(MimeTypes.AUDIO_MLAW) ? (byte) -1 : Ascii.CR;
                break;
            default:
                b = -1;
                break;
        }
        switch (b) {
            case 0:
                Assertions.checkArgument(iInferChannelCount != -1);
                Assertions.checkArgument(!fmtpParametersAsMap.isEmpty(), "missing attribute fmtp");
                if (str.equals(RtpPayloadFormat.RTP_MEDIA_MPEG4_LATM_AUDIO)) {
                    Assertions.checkArgument(fmtpParametersAsMap.containsKey(PARAMETER_MP4A_C_PRESENT) && fmtpParametersAsMap.get(PARAMETER_MP4A_C_PRESENT).equals(SessionDescription.SUPPORTED_SDP_VERSION), "Only supports cpresent=0 in AAC audio.");
                    String str2 = fmtpParametersAsMap.get(PARAMETER_MP4A_CONFIG);
                    Assertions.checkNotNull(str2, "AAC audio stream must include config fmtp parameter");
                    Assertions.checkArgument(str2.length() % 2 == 0, "Malformat MPEG4 config: " + str2);
                    AacUtil.Config aacStreamMuxConfig = parseAacStreamMuxConfig(str2);
                    builder.setSampleRate(aacStreamMuxConfig.sampleRateHz).setChannelCount(aacStreamMuxConfig.channelCount).setCodecs(aacStreamMuxConfig.codecs);
                }
                processAacFmtpAttribute(builder, fmtpParametersAsMap, str, iInferChannelCount, i2);
                break;
            case 1:
            case 2:
                Assertions.checkArgument(iInferChannelCount == 1, "Multi channel AMR is not currently supported.");
                Assertions.checkArgument(!fmtpParametersAsMap.isEmpty(), "fmtp parameters must include octet-align.");
                Assertions.checkArgument(fmtpParametersAsMap.containsKey(PARAMETER_AMR_OCTET_ALIGN), "Only octet aligned mode is currently supported.");
                Assertions.checkArgument(!fmtpParametersAsMap.containsKey(PARAMETER_AMR_INTERLEAVING), "Interleaving mode is not currently supported.");
                break;
            case 3:
                Assertions.checkArgument(iInferChannelCount != -1);
                Assertions.checkArgument(i2 == 48000, "Invalid OPUS clock rate.");
                break;
            case 4:
                Assertions.checkArgument(!fmtpParametersAsMap.isEmpty());
                processMPEG4FmtpAttribute(builder, fmtpParametersAsMap);
                break;
            case 5:
                builder.setWidth(352).setHeight(288);
                break;
            case 6:
                Assertions.checkArgument(!fmtpParametersAsMap.isEmpty(), "missing attribute fmtp");
                processH264FmtpAttribute(builder, fmtpParametersAsMap);
                break;
            case 7:
                Assertions.checkArgument(!fmtpParametersAsMap.isEmpty(), "missing attribute fmtp");
                processH265FmtpAttribute(builder, fmtpParametersAsMap);
                break;
            case 8:
                builder.setWidth(320).setHeight(PsExtractor.VIDEO_STREAM_MASK);
                break;
            case 9:
                builder.setWidth(320).setHeight(PsExtractor.VIDEO_STREAM_MASK);
                break;
            case 10:
                builder.setPcmEncoding(RtpPayloadFormat.getRawPcmEncodingType(str));
                break;
        }
        Assertions.checkArgument(i2 > 0);
        return new RtpPayloadFormat(builder.build(), i, i2, fmtpParametersAsMap, str);
    }

    private static int inferChannelCount(int i, String str) {
        return i != -1 ? i : str.equals(MimeTypes.AUDIO_AC3) ? 6 : 1;
    }

    private static void processAacFmtpAttribute(Format.Builder builder, ImmutableMap<String, String> immutableMap, String str, int i, int i2) {
        String str2 = immutableMap.get(PARAMETER_PROFILE_LEVEL_ID);
        if (str2 == null && str.equals(RtpPayloadFormat.RTP_MEDIA_MPEG4_LATM_AUDIO)) {
            str2 = "30";
        }
        Assertions.checkArgument((str2 == null || str2.isEmpty()) ? false : true, "missing profile-level-id param");
        builder.setCodecs(AAC_CODECS_PREFIX + str2);
        builder.setInitializationData(ImmutableList.of(AacUtil.buildAacLcAudioSpecificConfig(i2, i)));
    }

    private static AacUtil.Config parseAacStreamMuxConfig(String str) {
        ParsableBitArray parsableBitArray = new ParsableBitArray(Util.getBytesFromHexString(str));
        Assertions.checkArgument(parsableBitArray.readBits(1) == 0, "Only supports audio mux version 0.");
        Assertions.checkArgument(parsableBitArray.readBits(1) == 1, "Only supports allStreamsSameTimeFraming.");
        parsableBitArray.skipBits(6);
        Assertions.checkArgument(parsableBitArray.readBits(4) == 0, "Only supports one program.");
        Assertions.checkArgument(parsableBitArray.readBits(3) == 0, "Only supports one numLayer.");
        try {
            return AacUtil.parseAudioSpecificConfig(parsableBitArray, false);
        } catch (ParserException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static void processMPEG4FmtpAttribute(Format.Builder builder, ImmutableMap<String, String> immutableMap) {
        String str = immutableMap.get(PARAMETER_MP4A_CONFIG);
        if (str != null) {
            byte[] bytesFromHexString = Util.getBytesFromHexString(str);
            builder.setInitializationData(ImmutableList.of(bytesFromHexString));
            Pair<Integer, Integer> videoResolutionFromMpeg4VideoConfig = CodecSpecificDataUtil.getVideoResolutionFromMpeg4VideoConfig(bytesFromHexString);
            builder.setWidth(((Integer) videoResolutionFromMpeg4VideoConfig.first).intValue()).setHeight(((Integer) videoResolutionFromMpeg4VideoConfig.second).intValue());
        } else {
            builder.setWidth(352).setHeight(288);
        }
        String str2 = immutableMap.get(PARAMETER_PROFILE_LEVEL_ID);
        StringBuilder sb = new StringBuilder(MPEG4_CODECS_PREFIX);
        if (str2 == null) {
            str2 = IcyHeaders.REQUEST_HEADER_ENABLE_METADATA_VALUE;
        }
        builder.setCodecs(sb.append(str2).toString());
    }

    private static byte[] getInitializationDataFromParameterSet(String str) {
        byte[] bArrDecode = Base64.decode(str, 0);
        byte[] bArr = new byte[bArrDecode.length + NalUnitUtil.NAL_START_CODE.length];
        System.arraycopy(NalUnitUtil.NAL_START_CODE, 0, bArr, 0, NalUnitUtil.NAL_START_CODE.length);
        System.arraycopy(bArrDecode, 0, bArr, NalUnitUtil.NAL_START_CODE.length, bArrDecode.length);
        return bArr;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static void processH264FmtpAttribute(Format.Builder builder, ImmutableMap<String, String> immutableMap) {
        Assertions.checkArgument(immutableMap.containsKey(PARAMETER_SPROP_PARAMS), "missing sprop parameter");
        String[] strArrSplit = Util.split((String) Assertions.checkNotNull(immutableMap.get(PARAMETER_SPROP_PARAMS)), ",");
        Assertions.checkArgument(strArrSplit.length == 2, "empty sprop value");
        ImmutableList immutableListOf = ImmutableList.of(getInitializationDataFromParameterSet(strArrSplit[0]), getInitializationDataFromParameterSet(strArrSplit[1]));
        builder.setInitializationData(immutableListOf);
        byte[] bArr = (byte[]) immutableListOf.get(0);
        NalUnitUtil.SpsData spsNalUnit = NalUnitUtil.parseSpsNalUnit(bArr, NalUnitUtil.NAL_START_CODE.length, bArr.length);
        builder.setPixelWidthHeightRatio(spsNalUnit.pixelWidthHeightRatio);
        builder.setHeight(spsNalUnit.height);
        builder.setWidth(spsNalUnit.width);
        String str = immutableMap.get(PARAMETER_PROFILE_LEVEL_ID);
        if (str != null) {
            builder.setCodecs(H264_CODECS_PREFIX + str);
        } else {
            builder.setCodecs(CodecSpecificDataUtil.buildAvcCodecString(spsNalUnit.profileIdc, spsNalUnit.constraintsFlagsAndReservedZero2Bits, spsNalUnit.levelIdc));
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static void processH265FmtpAttribute(Format.Builder builder, ImmutableMap<String, String> immutableMap) {
        if (immutableMap.containsKey(PARAMETER_H265_SPROP_MAX_DON_DIFF)) {
            int i = Integer.parseInt((String) Assertions.checkNotNull(immutableMap.get(PARAMETER_H265_SPROP_MAX_DON_DIFF)));
            Assertions.checkArgument(i == 0, "non-zero sprop-max-don-diff " + i + " is not supported");
        }
        Assertions.checkArgument(immutableMap.containsKey(PARAMETER_H265_SPROP_VPS), "missing sprop-vps parameter");
        String str = (String) Assertions.checkNotNull(immutableMap.get(PARAMETER_H265_SPROP_VPS));
        Assertions.checkArgument(immutableMap.containsKey(PARAMETER_H265_SPROP_SPS), "missing sprop-sps parameter");
        String str2 = (String) Assertions.checkNotNull(immutableMap.get(PARAMETER_H265_SPROP_SPS));
        Assertions.checkArgument(immutableMap.containsKey(PARAMETER_H265_SPROP_PPS), "missing sprop-pps parameter");
        ImmutableList immutableListOf = ImmutableList.of(getInitializationDataFromParameterSet(str), getInitializationDataFromParameterSet(str2), getInitializationDataFromParameterSet((String) Assertions.checkNotNull(immutableMap.get(PARAMETER_H265_SPROP_PPS))));
        builder.setInitializationData(immutableListOf);
        byte[] bArr = (byte[]) immutableListOf.get(1);
        NalUnitUtil.H265SpsData h265SpsNalUnit = NalUnitUtil.parseH265SpsNalUnit(bArr, NalUnitUtil.NAL_START_CODE.length, bArr.length);
        builder.setPixelWidthHeightRatio(h265SpsNalUnit.pixelWidthHeightRatio);
        builder.setHeight(h265SpsNalUnit.height).setWidth(h265SpsNalUnit.width);
        builder.setCodecs(CodecSpecificDataUtil.buildHevcCodecString(h265SpsNalUnit.generalProfileSpace, h265SpsNalUnit.generalTierFlag, h265SpsNalUnit.generalProfileIdc, h265SpsNalUnit.generalProfileCompatibilityFlags, h265SpsNalUnit.constraintBytes, h265SpsNalUnit.generalLevelIdc));
    }

    private static Uri extractTrackUri(RtspHeaders rtspHeaders, Uri uri, String str) {
        Uri uri2 = Uri.parse(str);
        if (uri2.isAbsolute()) {
            return uri2;
        }
        if (!TextUtils.isEmpty(rtspHeaders.get(RtspHeaders.CONTENT_BASE))) {
            uri = Uri.parse(rtspHeaders.get(RtspHeaders.CONTENT_BASE));
        } else if (!TextUtils.isEmpty(rtspHeaders.get("Content-Location"))) {
            uri = Uri.parse(rtspHeaders.get("Content-Location"));
        }
        return str.equals(GENERIC_CONTROL_ATTR) ? uri : uri.buildUpon().appendEncodedPath(str).build();
    }
}
