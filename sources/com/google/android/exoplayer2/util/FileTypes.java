package com.google.android.exoplayer2.util;

import android.net.Uri;
import com.google.common.base.Ascii;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class FileTypes {
    public static final int AC3 = 0;
    public static final int AC4 = 1;
    public static final int ADTS = 2;
    public static final int AMR = 3;
    public static final int AVI = 16;
    private static final String EXTENSION_AAC = ".aac";
    private static final String EXTENSION_AC3 = ".ac3";
    private static final String EXTENSION_AC4 = ".ac4";
    private static final String EXTENSION_ADTS = ".adts";
    private static final String EXTENSION_AMR = ".amr";
    private static final String EXTENSION_AVI = ".avi";
    private static final String EXTENSION_EC3 = ".ec3";
    private static final String EXTENSION_FLAC = ".flac";
    private static final String EXTENSION_FLV = ".flv";
    private static final String EXTENSION_JPEG = ".jpeg";
    private static final String EXTENSION_JPG = ".jpg";
    private static final String EXTENSION_M2P = ".m2p";
    private static final String EXTENSION_MID = ".mid";
    private static final String EXTENSION_MIDI = ".midi";
    private static final String EXTENSION_MP3 = ".mp3";
    private static final String EXTENSION_MP4 = ".mp4";
    private static final String EXTENSION_MPEG = ".mpeg";
    private static final String EXTENSION_MPG = ".mpg";
    private static final String EXTENSION_OPUS = ".opus";
    private static final String EXTENSION_PREFIX_CMF = ".cmf";
    private static final String EXTENSION_PREFIX_M4 = ".m4";
    private static final String EXTENSION_PREFIX_MK = ".mk";
    private static final String EXTENSION_PREFIX_MP4 = ".mp4";
    private static final String EXTENSION_PREFIX_OG = ".og";
    private static final String EXTENSION_PREFIX_TS = ".ts";
    private static final String EXTENSION_PS = ".ps";
    private static final String EXTENSION_SMF = ".smf";
    private static final String EXTENSION_TS = ".ts";
    private static final String EXTENSION_VTT = ".vtt";
    private static final String EXTENSION_WAV = ".wav";
    private static final String EXTENSION_WAVE = ".wave";
    private static final String EXTENSION_WEBM = ".webm";
    private static final String EXTENSION_WEBVTT = ".webvtt";
    public static final int FLAC = 4;
    public static final int FLV = 5;
    static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final int JPEG = 14;
    public static final int MATROSKA = 6;
    public static final int MIDI = 15;
    public static final int MP3 = 7;
    public static final int MP4 = 8;
    public static final int OGG = 9;
    public static final int PS = 10;
    public static final int TS = 11;
    public static final int UNKNOWN = -1;
    public static final int WAV = 12;
    public static final int WEBVTT = 13;

    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    private FileTypes() {
    }

    public static int inferFileTypeFromResponseHeaders(Map<String, List<String>> map) {
        List<String> list = map.get("Content-Type");
        return inferFileTypeFromMimeType((list == null || list.isEmpty()) ? null : list.get(0));
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    public static int inferFileTypeFromMimeType(String str) {
        byte b;
        if (str == null) {
            return -1;
        }
        String strNormalizeMimeType = MimeTypes.normalizeMimeType(str);
        strNormalizeMimeType.hashCode();
        switch (strNormalizeMimeType.hashCode()) {
            case -2123537834:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_E_AC3_JOC) ? (byte) -1 : (byte) 0;
                break;
            case -1662384011:
                b = !strNormalizeMimeType.equals(MimeTypes.VIDEO_PS) ? (byte) -1 : (byte) 1;
                break;
            case -1662384007:
                b = !strNormalizeMimeType.equals(MimeTypes.VIDEO_MP2T) ? (byte) -1 : (byte) 2;
                break;
            case -1662095187:
                b = !strNormalizeMimeType.equals(MimeTypes.VIDEO_WEBM) ? (byte) -1 : (byte) 3;
                break;
            case -1606874997:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_AMR_WB) ? (byte) -1 : (byte) 4;
                break;
            case -1487394660:
                b = !strNormalizeMimeType.equals(MimeTypes.IMAGE_JPEG) ? (byte) -1 : (byte) 5;
                break;
            case -1248337486:
                b = !strNormalizeMimeType.equals(MimeTypes.APPLICATION_MP4) ? (byte) -1 : (byte) 6;
                break;
            case -1079884372:
                b = !strNormalizeMimeType.equals(MimeTypes.VIDEO_AVI) ? (byte) -1 : (byte) 7;
                break;
            case -1004728940:
                b = !strNormalizeMimeType.equals(MimeTypes.TEXT_VTT) ? (byte) -1 : (byte) 8;
                break;
            case -387023398:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_MATROSKA) ? (byte) -1 : (byte) 9;
                break;
            case -43467528:
                b = !strNormalizeMimeType.equals(MimeTypes.APPLICATION_WEBM) ? (byte) -1 : (byte) 10;
                break;
            case 13915911:
                b = !strNormalizeMimeType.equals(MimeTypes.VIDEO_FLV) ? (byte) -1 : (byte) 11;
                break;
            case 187078296:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_AC3) ? (byte) -1 : (byte) 12;
                break;
            case 187078297:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_AC4) ? (byte) -1 : (byte) 13;
                break;
            case 187078669:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_AMR) ? (byte) -1 : (byte) 14;
                break;
            case 187090232:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_MP4) ? (byte) -1 : (byte) 15;
                break;
            case 187091926:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_OGG) ? (byte) -1 : (byte) 16;
                break;
            case 187099443:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_WAV) ? (byte) -1 : (byte) 17;
                break;
            case 1331848029:
                b = !strNormalizeMimeType.equals(MimeTypes.VIDEO_MP4) ? (byte) -1 : Ascii.DC2;
                break;
            case 1503095341:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_AMR_NB) ? (byte) -1 : (byte) 19;
                break;
            case 1504578661:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_E_AC3) ? (byte) -1 : Ascii.DC4;
                break;
            case 1504619009:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_FLAC) ? (byte) -1 : Ascii.NAK;
                break;
            case 1504824762:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_MIDI) ? (byte) -1 : Ascii.SYN;
                break;
            case 1504831518:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_MPEG) ? (byte) -1 : Ascii.ETB;
                break;
            case 1505118770:
                b = !strNormalizeMimeType.equals(MimeTypes.AUDIO_WEBM) ? (byte) -1 : Ascii.CAN;
                break;
            case 2039520277:
                b = !strNormalizeMimeType.equals(MimeTypes.VIDEO_MATROSKA) ? (byte) -1 : Ascii.EM;
                break;
            default:
                b = -1;
                break;
        }
        switch (b) {
        }
        return -1;
    }

    public static int inferFileTypeFromUri(Uri uri) {
        String lastPathSegment = uri.getLastPathSegment();
        if (lastPathSegment == null) {
            return -1;
        }
        if (lastPathSegment.endsWith(EXTENSION_AC3) || lastPathSegment.endsWith(EXTENSION_EC3)) {
            return 0;
        }
        if (lastPathSegment.endsWith(EXTENSION_AC4)) {
            return 1;
        }
        if (lastPathSegment.endsWith(EXTENSION_ADTS) || lastPathSegment.endsWith(EXTENSION_AAC)) {
            return 2;
        }
        if (lastPathSegment.endsWith(EXTENSION_AMR)) {
            return 3;
        }
        if (lastPathSegment.endsWith(EXTENSION_FLAC)) {
            return 4;
        }
        if (lastPathSegment.endsWith(EXTENSION_FLV)) {
            return 5;
        }
        if (lastPathSegment.endsWith(EXTENSION_MID) || lastPathSegment.endsWith(EXTENSION_MIDI) || lastPathSegment.endsWith(EXTENSION_SMF)) {
            return 15;
        }
        if (lastPathSegment.startsWith(EXTENSION_PREFIX_MK, lastPathSegment.length() - 4) || lastPathSegment.endsWith(EXTENSION_WEBM)) {
            return 6;
        }
        if (lastPathSegment.endsWith(EXTENSION_MP3)) {
            return 7;
        }
        if (lastPathSegment.endsWith(".mp4") || lastPathSegment.startsWith(EXTENSION_PREFIX_M4, lastPathSegment.length() - 4) || lastPathSegment.startsWith(".mp4", lastPathSegment.length() - 5) || lastPathSegment.startsWith(EXTENSION_PREFIX_CMF, lastPathSegment.length() - 5)) {
            return 8;
        }
        if (lastPathSegment.startsWith(EXTENSION_PREFIX_OG, lastPathSegment.length() - 4) || lastPathSegment.endsWith(EXTENSION_OPUS)) {
            return 9;
        }
        if (lastPathSegment.endsWith(EXTENSION_PS) || lastPathSegment.endsWith(EXTENSION_MPEG) || lastPathSegment.endsWith(EXTENSION_MPG) || lastPathSegment.endsWith(EXTENSION_M2P)) {
            return 10;
        }
        if (lastPathSegment.endsWith(".ts") || lastPathSegment.startsWith(".ts", lastPathSegment.length() - 4)) {
            return 11;
        }
        if (lastPathSegment.endsWith(EXTENSION_WAV) || lastPathSegment.endsWith(EXTENSION_WAVE)) {
            return 12;
        }
        if (lastPathSegment.endsWith(EXTENSION_VTT) || lastPathSegment.endsWith(EXTENSION_WEBVTT)) {
            return 13;
        }
        if (lastPathSegment.endsWith(EXTENSION_JPG) || lastPathSegment.endsWith(EXTENSION_JPEG)) {
            return 14;
        }
        return lastPathSegment.endsWith(EXTENSION_AVI) ? 16 : -1;
    }
}
