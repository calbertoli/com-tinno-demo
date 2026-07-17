package com.google.android.exoplayer2.text;

import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.text.cea.Cea608Decoder;
import com.google.android.exoplayer2.text.cea.Cea708Decoder;
import com.google.android.exoplayer2.text.dvb.DvbDecoder;
import com.google.android.exoplayer2.text.pgs.PgsDecoder;
import com.google.android.exoplayer2.text.ssa.SsaDecoder;
import com.google.android.exoplayer2.text.subrip.SubripDecoder;
import com.google.android.exoplayer2.text.ttml.TtmlDecoder;
import com.google.android.exoplayer2.text.tx3g.Tx3gDecoder;
import com.google.android.exoplayer2.text.webvtt.Mp4WebvttDecoder;
import com.google.android.exoplayer2.text.webvtt.WebvttDecoder;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.common.base.Ascii;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface SubtitleDecoderFactory {
    public static final SubtitleDecoderFactory DEFAULT = new SubtitleDecoderFactory() { // from class: com.google.android.exoplayer2.text.SubtitleDecoderFactory.1
        @Override // com.google.android.exoplayer2.text.SubtitleDecoderFactory
        public boolean supportsFormat(Format format) {
            String str = format.sampleMimeType;
            return MimeTypes.TEXT_VTT.equals(str) || MimeTypes.TEXT_SSA.equals(str) || MimeTypes.APPLICATION_TTML.equals(str) || MimeTypes.APPLICATION_MP4VTT.equals(str) || MimeTypes.APPLICATION_SUBRIP.equals(str) || MimeTypes.APPLICATION_TX3G.equals(str) || MimeTypes.APPLICATION_CEA608.equals(str) || MimeTypes.APPLICATION_MP4CEA608.equals(str) || MimeTypes.APPLICATION_CEA708.equals(str) || MimeTypes.APPLICATION_DVBSUBS.equals(str) || MimeTypes.APPLICATION_PGS.equals(str) || MimeTypes.TEXT_EXOPLAYER_CUES.equals(str);
        }

        /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
        @Override // com.google.android.exoplayer2.text.SubtitleDecoderFactory
        public SubtitleDecoder createDecoder(Format format) {
            String str = format.sampleMimeType;
            if (str != null) {
                str.hashCode();
                byte b = -1;
                switch (str.hashCode()) {
                    case -1351681404:
                        if (str.equals(MimeTypes.APPLICATION_DVBSUBS)) {
                            b = 0;
                        }
                        break;
                    case -1248334819:
                        if (str.equals(MimeTypes.APPLICATION_PGS)) {
                            b = 1;
                        }
                        break;
                    case -1026075066:
                        if (str.equals(MimeTypes.APPLICATION_MP4VTT)) {
                            b = 2;
                        }
                        break;
                    case -1004728940:
                        if (str.equals(MimeTypes.TEXT_VTT)) {
                            b = 3;
                        }
                        break;
                    case 691401887:
                        if (str.equals(MimeTypes.APPLICATION_TX3G)) {
                            b = 4;
                        }
                        break;
                    case 822864842:
                        if (str.equals(MimeTypes.TEXT_SSA)) {
                            b = 5;
                        }
                        break;
                    case 930165504:
                        if (str.equals(MimeTypes.APPLICATION_MP4CEA608)) {
                            b = 6;
                        }
                        break;
                    case 1201784583:
                        if (str.equals(MimeTypes.TEXT_EXOPLAYER_CUES)) {
                            b = 7;
                        }
                        break;
                    case 1566015601:
                        if (str.equals(MimeTypes.APPLICATION_CEA608)) {
                            b = 8;
                        }
                        break;
                    case 1566016562:
                        if (str.equals(MimeTypes.APPLICATION_CEA708)) {
                            b = 9;
                        }
                        break;
                    case 1668750253:
                        if (str.equals(MimeTypes.APPLICATION_SUBRIP)) {
                            b = 10;
                        }
                        break;
                    case 1693976202:
                        if (str.equals(MimeTypes.APPLICATION_TTML)) {
                            b = Ascii.VT;
                        }
                        break;
                }
                switch (b) {
                    case 0:
                        return new DvbDecoder(format.initializationData);
                    case 1:
                        return new PgsDecoder();
                    case 2:
                        return new Mp4WebvttDecoder();
                    case 3:
                        return new WebvttDecoder();
                    case 4:
                        return new Tx3gDecoder(format.initializationData);
                    case 5:
                        return new SsaDecoder(format.initializationData);
                    case 6:
                    case 8:
                        return new Cea608Decoder(str, format.accessibilityChannel, Cea608Decoder.MIN_DATA_CHANNEL_TIMEOUT_MS);
                    case 7:
                        return new ExoplayerCuesDecoder();
                    case 9:
                        return new Cea708Decoder(format.accessibilityChannel, format.initializationData);
                    case 10:
                        return new SubripDecoder();
                    case 11:
                        return new TtmlDecoder();
                }
            }
            throw new IllegalArgumentException("Attempted to create decoder for unsupported MIME type: " + str);
        }
    };

    SubtitleDecoder createDecoder(Format format);

    boolean supportsFormat(Format format);
}
