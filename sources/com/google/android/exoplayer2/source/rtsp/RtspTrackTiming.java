package com.google.android.exoplayer2.source.rtsp;

import android.net.Uri;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.UriUtil;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
final class RtspTrackTiming {
    public final long rtpTimestamp;
    public final int sequenceNumber;
    public final Uri uri;

    /* JADX WARN: Removed duplicated region for block: B:23:0x0068  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static com.google.common.collect.ImmutableList<com.google.android.exoplayer2.source.rtsp.RtspTrackTiming> parseTrackTiming(java.lang.String r18, android.net.Uri r19) throws com.google.android.exoplayer2.ParserException {
        /*
            com.google.common.collect.ImmutableList$Builder r0 = new com.google.common.collect.ImmutableList$Builder
            r0.<init>()
            java.lang.String r1 = ","
            r2 = r18
            java.lang.String[] r1 = com.google.android.exoplayer2.util.Util.split(r2, r1)
            int r2 = r1.length
            r3 = 0
            r4 = r3
        L10:
            if (r4 >= r2) goto Lc2
            r5 = r1[r4]
            java.lang.String r6 = ";"
            java.lang.String[] r6 = com.google.android.exoplayer2.util.Util.split(r5, r6)
            int r7 = r6.length
            r12 = r3
            r13 = 0
            r14 = -1
            r15 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
        L23:
            if (r12 >= r7) goto L94
            r8 = r6[r12]
            java.lang.String r9 = "="
            java.lang.String[] r9 = com.google.android.exoplayer2.util.Util.splitAtFirst(r8, r9)     // Catch: java.lang.Exception -> L8e
            r11 = r9[r3]     // Catch: java.lang.Exception -> L8e
            r3 = 1
            r9 = r9[r3]     // Catch: java.lang.Exception -> L8e
            int r10 = r11.hashCode()     // Catch: java.lang.Exception -> L8e
            r3 = 113759(0x1bc5f, float:1.5941E-40)
            r17 = r1
            r1 = 2
            if (r10 == r3) goto L5e
            r3 = 116079(0x1c56f, float:1.62661E-40)
            if (r10 == r3) goto L53
            r3 = 1524180539(0x5ad9263b, float:3.0561052E16)
            if (r10 == r3) goto L49
            goto L68
        L49:
            java.lang.String r3 = "rtptime"
            boolean r3 = r11.equals(r3)     // Catch: java.lang.Exception -> L8e
            if (r3 == 0) goto L68
            r3 = r1
            goto L69
        L53:
            java.lang.String r3 = "url"
            boolean r3 = r11.equals(r3)     // Catch: java.lang.Exception -> L8e
            if (r3 == 0) goto L68
            r3 = 0
            goto L69
        L5e:
            java.lang.String r3 = "seq"
            boolean r3 = r11.equals(r3)     // Catch: java.lang.Exception -> L8e
            if (r3 == 0) goto L68
            r3 = 1
            goto L69
        L68:
            r3 = -1
        L69:
            if (r3 == 0) goto L82
            r10 = 1
            if (r3 == r10) goto L7b
            if (r3 != r1) goto L75
            long r15 = java.lang.Long.parseLong(r9)     // Catch: java.lang.Exception -> L8e
            goto L7f
        L75:
            r0 = 0
            com.google.android.exoplayer2.ParserException r0 = com.google.android.exoplayer2.ParserException.createForMalformedManifest(r11, r0)     // Catch: java.lang.Exception -> L8e
            throw r0     // Catch: java.lang.Exception -> L8e
        L7b:
            int r14 = java.lang.Integer.parseInt(r9)     // Catch: java.lang.Exception -> L8e
        L7f:
            r1 = r19
            goto L88
        L82:
            r1 = r19
            android.net.Uri r13 = resolveUri(r9, r1)     // Catch: java.lang.Exception -> L8e
        L88:
            int r12 = r12 + 1
            r1 = r17
            r3 = 0
            goto L23
        L8e:
            r0 = move-exception
            com.google.android.exoplayer2.ParserException r0 = com.google.android.exoplayer2.ParserException.createForMalformedManifest(r8, r0)
            throw r0
        L94:
            r17 = r1
            r1 = r19
            if (r13 == 0) goto Lbc
            java.lang.String r3 = r13.getScheme()
            if (r3 == 0) goto Lbc
            r3 = -1
            r8 = r15
            if (r14 != r3) goto Lad
            r6 = -9223372036854775807(0x8000000000000001, double:-4.9E-324)
            int r3 = (r8 > r6 ? 1 : (r8 == r6 ? 0 : -1))
            if (r3 == 0) goto Lbc
        Lad:
            com.google.android.exoplayer2.source.rtsp.RtspTrackTiming r3 = new com.google.android.exoplayer2.source.rtsp.RtspTrackTiming
            r3.<init>(r8, r14, r13)
            r0.add(r3)
            int r4 = r4 + 1
            r1 = r17
            r3 = 0
            goto L10
        Lbc:
            r0 = 0
            com.google.android.exoplayer2.ParserException r0 = com.google.android.exoplayer2.ParserException.createForMalformedManifest(r5, r0)
            throw r0
        Lc2:
            com.google.common.collect.ImmutableList r0 = r0.build()
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.exoplayer2.source.rtsp.RtspTrackTiming.parseTrackTiming(java.lang.String, android.net.Uri):com.google.common.collect.ImmutableList");
    }

    static Uri resolveUri(String str, Uri uri) {
        Assertions.checkArgument(((String) Assertions.checkNotNull(uri.getScheme())).equals("rtsp"));
        Uri uri2 = Uri.parse(str);
        if (uri2.isAbsolute()) {
            return uri2;
        }
        Uri uri3 = Uri.parse("rtsp://" + str);
        String string = uri.toString();
        if (((String) Assertions.checkNotNull(uri3.getHost())).equals(uri.getHost())) {
            return uri3;
        }
        if (string.endsWith("/")) {
            return UriUtil.resolveToUri(string, str);
        }
        return UriUtil.resolveToUri(string + "/", str);
    }

    private RtspTrackTiming(long j, int i, Uri uri) {
        this.rtpTimestamp = j;
        this.sequenceNumber = i;
        this.uri = uri;
    }
}
