package com.google.android.exoplayer2.source.rtsp;

import com.google.android.exoplayer2.ParserException;
import com.google.android.exoplayer2.source.rtsp.MediaDescription;
import com.google.android.exoplayer2.source.rtsp.SessionDescription;
import com.google.android.exoplayer2.util.Assertions;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
final class SessionDescriptionParser {
    private static final String ATTRIBUTE_TYPE = "a";
    private static final String BANDWIDTH_TYPE = "b";
    private static final String CONNECTION_TYPE = "c";
    private static final String EMAIL_TYPE = "e";
    private static final String INFORMATION_TYPE = "i";
    private static final String KEY_TYPE = "k";
    private static final String MEDIA_TYPE = "m";
    private static final String ORIGIN_TYPE = "o";
    private static final String PHONE_NUMBER_TYPE = "p";
    private static final String REPEAT_TYPE = "r";
    private static final String SESSION_TYPE = "s";
    private static final String TIMING_TYPE = "t";
    private static final String URI_TYPE = "u";
    private static final String VERSION_TYPE = "v";
    private static final String ZONE_TYPE = "z";
    private static final Pattern SDP_LINE_PATTERN = Pattern.compile("([a-z])=\\s?(.+)");
    private static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("([\\x21\\x23-\\x27\\x2a\\x2b\\x2d\\x2e\\x30-\\x39\\x41-\\x5a\\x5e-\\x7e]+)(?::(.*))?");
    private static final Pattern MEDIA_DESCRIPTION_PATTERN = Pattern.compile("(\\S+)\\s(\\S+)\\s(\\S+)\\s(\\S+)");

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:135:0x01b4, code lost:
    
        continue;
     */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /* JADX WARN: Removed duplicated region for block: B:57:0x00ed  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static com.google.android.exoplayer2.source.rtsp.SessionDescription parse(java.lang.String r12) throws com.google.android.exoplayer2.ParserException {
        /*
            Method dump skipped, instruction units count: 564
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.rtsp.SessionDescriptionParser.parse(java.lang.String):com.google.android.exoplayer2.source.rtsp.SessionDescription");
    }

    private static void addMediaDescriptionToSession(SessionDescription.Builder builder, MediaDescription.Builder builder2) throws ParserException {
        try {
            builder.addMediaDescription(builder2.build());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw ParserException.createForMalformedManifest(null, e);
        }
    }

    private static MediaDescription.Builder parseMediaDescriptionLine(String str) throws ParserException {
        Matcher matcher = MEDIA_DESCRIPTION_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw ParserException.createForMalformedManifest("Malformed SDP media description line: " + str, null);
        }
        try {
            return new MediaDescription.Builder((String) Assertions.checkNotNull(matcher.group(1)), Integer.parseInt((String) Assertions.checkNotNull(matcher.group(2))), (String) Assertions.checkNotNull(matcher.group(3)), Integer.parseInt((String) Assertions.checkNotNull(matcher.group(4))));
        } catch (NumberFormatException e) {
            throw ParserException.createForMalformedManifest("Malformed SDP media description line: " + str, e);
        }
    }

    private SessionDescriptionParser() {
    }
}
