package com.google.android.exoplayer2.drm;

import android.media.DeniedByServerException;
import android.media.MediaCrypto;
import android.media.MediaCryptoException;
import android.media.MediaDrm;
import android.media.MediaDrmException;
import android.media.NotProvisionedException;
import android.media.UnsupportedSchemeException;
import android.media.metrics.LogSessionId;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.TextUtils;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.analytics.PlayerId;
import com.google.android.exoplayer2.drm.DrmInitData;
import com.google.android.exoplayer2.drm.ExoMediaDrm;
import com.google.android.exoplayer2.extractor.mp4.PsshAtomUtil;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.ParsableByteArray;
import com.google.android.exoplayer2.util.Util;
import com.google.common.base.Charsets;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class FrameworkMediaDrm implements ExoMediaDrm {
    private static final String CENC_SCHEME_MIME_TYPE = "cenc";
    public static final ExoMediaDrm.Provider DEFAULT_PROVIDER = new ExoMediaDrm.Provider() { // from class: com.google.android.exoplayer2.drm.FrameworkMediaDrm$$ExternalSyntheticLambda2
        @Override // com.google.android.exoplayer2.drm.ExoMediaDrm.Provider
        public final ExoMediaDrm acquireExoMediaDrm(UUID uuid) {
            return FrameworkMediaDrm.lambda$static$0(uuid);
        }
    };
    private static final String MOCK_LA_URL = "<LA_URL>https://x</LA_URL>";
    private static final String MOCK_LA_URL_VALUE = "https://x";
    private static final String TAG = "FrameworkMediaDrm";
    private static final int UTF_16_BYTES_PER_CHARACTER = 2;
    private final MediaDrm mediaDrm;
    private int referenceCount;
    private final UUID uuid;

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public int getCryptoType() {
        return 2;
    }

    static /* synthetic */ ExoMediaDrm lambda$static$0(UUID uuid) {
        try {
            return newInstance(uuid);
        } catch (UnsupportedDrmException unused) {
            Log.e(TAG, "Failed to instantiate a FrameworkMediaDrm for uuid: " + uuid + ".");
            return new DummyExoMediaDrm();
        }
    }

    public static boolean isCryptoSchemeSupported(UUID uuid) {
        return MediaDrm.isCryptoSchemeSupported(adjustUuid(uuid));
    }

    public static FrameworkMediaDrm newInstance(UUID uuid) throws UnsupportedDrmException {
        try {
            return new FrameworkMediaDrm(uuid);
        } catch (UnsupportedSchemeException e) {
            throw new UnsupportedDrmException(1, e);
        } catch (Exception e2) {
            throw new UnsupportedDrmException(2, e2);
        }
    }

    private FrameworkMediaDrm(UUID uuid) throws UnsupportedSchemeException {
        Assertions.checkNotNull(uuid);
        Assertions.checkArgument(!C.COMMON_PSSH_UUID.equals(uuid), "Use C.CLEARKEY_UUID instead");
        this.uuid = uuid;
        MediaDrm mediaDrm = new MediaDrm(adjustUuid(uuid));
        this.mediaDrm = mediaDrm;
        this.referenceCount = 1;
        if (C.WIDEVINE_UUID.equals(uuid) && needsForceWidevineL3Workaround()) {
            forceWidevineL3(mediaDrm);
        }
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void setOnEventListener(final ExoMediaDrm.OnEventListener onEventListener) {
        this.mediaDrm.setOnEventListener(onEventListener == null ? null : new MediaDrm.OnEventListener() { // from class: com.google.android.exoplayer2.drm.FrameworkMediaDrm$$ExternalSyntheticLambda1
            @Override // android.media.MediaDrm.OnEventListener
            public final void onEvent(MediaDrm mediaDrm, byte[] bArr, int i, int i2, byte[] bArr2) {
                this.f$0.m118x6bb92dcc(onEventListener, mediaDrm, bArr, i, i2, bArr2);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$setOnEventListener$1$com-google-android-exoplayer2-drm-FrameworkMediaDrm, reason: not valid java name */
    /* synthetic */ void m118x6bb92dcc(ExoMediaDrm.OnEventListener onEventListener, MediaDrm mediaDrm, byte[] bArr, int i, int i2, byte[] bArr2) {
        onEventListener.onEvent(this, bArr, i, i2, bArr2);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void setOnKeyStatusChangeListener(final ExoMediaDrm.OnKeyStatusChangeListener onKeyStatusChangeListener) {
        if (Util.SDK_INT < 23) {
            throw new UnsupportedOperationException();
        }
        this.mediaDrm.setOnKeyStatusChangeListener(onKeyStatusChangeListener == null ? null : new MediaDrm.OnKeyStatusChangeListener() { // from class: com.google.android.exoplayer2.drm.FrameworkMediaDrm$$ExternalSyntheticLambda3
            @Override // android.media.MediaDrm.OnKeyStatusChangeListener
            public final void onKeyStatusChange(MediaDrm mediaDrm, byte[] bArr, List list, boolean z) {
                this.f$0.m120x8be3cdb4(onKeyStatusChangeListener, mediaDrm, bArr, list, z);
            }
        }, (Handler) null);
    }

    /* JADX INFO: renamed from: lambda$setOnKeyStatusChangeListener$2$com-google-android-exoplayer2-drm-FrameworkMediaDrm, reason: not valid java name */
    /* synthetic */ void m120x8be3cdb4(ExoMediaDrm.OnKeyStatusChangeListener onKeyStatusChangeListener, MediaDrm mediaDrm, byte[] bArr, List list, boolean z) {
        ArrayList arrayList = new ArrayList();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            MediaDrm.KeyStatus keyStatus = (MediaDrm.KeyStatus) it.next();
            arrayList.add(new ExoMediaDrm.KeyStatus(keyStatus.getStatusCode(), keyStatus.getKeyId()));
        }
        onKeyStatusChangeListener.onKeyStatusChange(this, bArr, arrayList, z);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void setOnExpirationUpdateListener(final ExoMediaDrm.OnExpirationUpdateListener onExpirationUpdateListener) {
        if (Util.SDK_INT < 23) {
            throw new UnsupportedOperationException();
        }
        this.mediaDrm.setOnExpirationUpdateListener(onExpirationUpdateListener == null ? null : new MediaDrm.OnExpirationUpdateListener() { // from class: com.google.android.exoplayer2.drm.FrameworkMediaDrm$$ExternalSyntheticLambda0
            @Override // android.media.MediaDrm.OnExpirationUpdateListener
            public final void onExpirationUpdate(MediaDrm mediaDrm, byte[] bArr, long j) {
                this.f$0.m119x78401754(onExpirationUpdateListener, mediaDrm, bArr, j);
            }
        }, (Handler) null);
    }

    /* JADX INFO: renamed from: lambda$setOnExpirationUpdateListener$3$com-google-android-exoplayer2-drm-FrameworkMediaDrm, reason: not valid java name */
    /* synthetic */ void m119x78401754(ExoMediaDrm.OnExpirationUpdateListener onExpirationUpdateListener, MediaDrm mediaDrm, byte[] bArr, long j) {
        onExpirationUpdateListener.onExpirationUpdate(this, bArr, j);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public byte[] openSession() throws MediaDrmException {
        return this.mediaDrm.openSession();
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void closeSession(byte[] bArr) {
        this.mediaDrm.closeSession(bArr);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void setPlayerIdForSession(byte[] bArr, PlayerId playerId) {
        if (Util.SDK_INT >= 31) {
            try {
                Api31.setLogSessionIdOnMediaDrmSession(this.mediaDrm, bArr, playerId);
            } catch (UnsupportedOperationException unused) {
                Log.w(TAG, "setLogSessionId failed.");
            }
        }
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public ExoMediaDrm.KeyRequest getKeyRequest(byte[] bArr, List<DrmInitData.SchemeData> list, int i, HashMap<String, String> map) throws NotProvisionedException {
        DrmInitData.SchemeData schemeData;
        byte[] bArrAdjustRequestInitData;
        String strAdjustRequestMimeType;
        if (list != null) {
            schemeData = getSchemeData(this.uuid, list);
            bArrAdjustRequestInitData = adjustRequestInitData(this.uuid, (byte[]) Assertions.checkNotNull(schemeData.data));
            strAdjustRequestMimeType = adjustRequestMimeType(this.uuid, schemeData.mimeType);
        } else {
            schemeData = null;
            bArrAdjustRequestInitData = null;
            strAdjustRequestMimeType = null;
        }
        MediaDrm.KeyRequest keyRequest = this.mediaDrm.getKeyRequest(bArr, bArrAdjustRequestInitData, strAdjustRequestMimeType, i, map);
        byte[] bArrAdjustRequestData = adjustRequestData(this.uuid, keyRequest.getData());
        String strAdjustLicenseServerUrl = adjustLicenseServerUrl(keyRequest.getDefaultUrl());
        if (TextUtils.isEmpty(strAdjustLicenseServerUrl) && schemeData != null && !TextUtils.isEmpty(schemeData.licenseServerUrl)) {
            strAdjustLicenseServerUrl = schemeData.licenseServerUrl;
        }
        return new ExoMediaDrm.KeyRequest(bArrAdjustRequestData, strAdjustLicenseServerUrl, Util.SDK_INT >= 23 ? keyRequest.getRequestType() : Integer.MIN_VALUE);
    }

    private static String adjustLicenseServerUrl(String str) {
        return MOCK_LA_URL.equals(str) ? "" : (Util.SDK_INT == 33 && "https://default.url".equals(str)) ? "" : str;
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public byte[] provideKeyResponse(byte[] bArr, byte[] bArr2) throws DeniedByServerException, NotProvisionedException {
        if (C.CLEARKEY_UUID.equals(this.uuid)) {
            bArr2 = ClearKeyUtil.adjustResponseData(bArr2);
        }
        return this.mediaDrm.provideKeyResponse(bArr, bArr2);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public ExoMediaDrm.ProvisionRequest getProvisionRequest() {
        MediaDrm.ProvisionRequest provisionRequest = this.mediaDrm.getProvisionRequest();
        return new ExoMediaDrm.ProvisionRequest(provisionRequest.getData(), provisionRequest.getDefaultUrl());
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void provideProvisionResponse(byte[] bArr) throws DeniedByServerException {
        this.mediaDrm.provideProvisionResponse(bArr);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public Map<String, String> queryKeyStatus(byte[] bArr) {
        return this.mediaDrm.queryKeyStatus(bArr);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public boolean requiresSecureDecoder(byte[] bArr, String str) {
        if (Util.SDK_INT >= 31) {
            return Api31.requiresSecureDecoder(this.mediaDrm, str);
        }
        try {
            MediaCrypto mediaCrypto = new MediaCrypto(this.uuid, bArr);
            try {
                return mediaCrypto.requiresSecureDecoderComponent(str);
            } finally {
                mediaCrypto.release();
            }
        } catch (MediaCryptoException unused) {
            return true;
        }
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public synchronized void acquire() {
        Assertions.checkState(this.referenceCount > 0);
        this.referenceCount++;
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public synchronized void release() {
        int i = this.referenceCount - 1;
        this.referenceCount = i;
        if (i == 0) {
            this.mediaDrm.release();
        }
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void restoreKeys(byte[] bArr, byte[] bArr2) {
        this.mediaDrm.restoreKeys(bArr, bArr2);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public PersistableBundle getMetrics() {
        if (Util.SDK_INT < 28) {
            return null;
        }
        return this.mediaDrm.getMetrics();
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public String getPropertyString(String str) {
        return this.mediaDrm.getPropertyString(str);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public byte[] getPropertyByteArray(String str) {
        return this.mediaDrm.getPropertyByteArray(str);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void setPropertyString(String str, String str2) {
        this.mediaDrm.setPropertyString(str, str2);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public void setPropertyByteArray(String str, byte[] bArr) {
        this.mediaDrm.setPropertyByteArray(str, bArr);
    }

    @Override // com.google.android.exoplayer2.drm.ExoMediaDrm
    public FrameworkCryptoConfig createCryptoConfig(byte[] bArr) throws MediaCryptoException {
        return new FrameworkCryptoConfig(adjustUuid(this.uuid), bArr, Util.SDK_INT < 21 && C.WIDEVINE_UUID.equals(this.uuid) && "L3".equals(getPropertyString("securityLevel")));
    }

    private static DrmInitData.SchemeData getSchemeData(UUID uuid, List<DrmInitData.SchemeData> list) {
        if (!C.WIDEVINE_UUID.equals(uuid)) {
            return list.get(0);
        }
        if (Util.SDK_INT >= 28 && list.size() > 1) {
            DrmInitData.SchemeData schemeData = list.get(0);
            int length = 0;
            for (int i = 0; i < list.size(); i++) {
                DrmInitData.SchemeData schemeData2 = list.get(i);
                byte[] bArr = (byte[]) Assertions.checkNotNull(schemeData2.data);
                if (Util.areEqual(schemeData2.mimeType, schemeData.mimeType) && Util.areEqual(schemeData2.licenseServerUrl, schemeData.licenseServerUrl) && PsshAtomUtil.isPsshAtom(bArr)) {
                    length += bArr.length;
                }
            }
            byte[] bArr2 = new byte[length];
            int i2 = 0;
            for (int i3 = 0; i3 < list.size(); i3++) {
                byte[] bArr3 = (byte[]) Assertions.checkNotNull(list.get(i3).data);
                int length2 = bArr3.length;
                System.arraycopy(bArr3, 0, bArr2, i2, length2);
                i2 += length2;
            }
            return schemeData.copyWithData(bArr2);
        }
        for (int i4 = 0; i4 < list.size(); i4++) {
            DrmInitData.SchemeData schemeData3 = list.get(i4);
            int version = PsshAtomUtil.parseVersion((byte[]) Assertions.checkNotNull(schemeData3.data));
            if (Util.SDK_INT < 23 && version == 0) {
                return schemeData3;
            }
            if (Util.SDK_INT >= 23 && version == 1) {
                return schemeData3;
            }
        }
        return list.get(0);
    }

    private static UUID adjustUuid(UUID uuid) {
        return (Util.SDK_INT >= 27 || !C.CLEARKEY_UUID.equals(uuid)) ? uuid : C.COMMON_PSSH_UUID;
    }

    private static byte[] adjustRequestInitData(UUID uuid, byte[] bArr) {
        byte[] schemeSpecificData;
        if (C.PLAYREADY_UUID.equals(uuid)) {
            byte[] schemeSpecificData2 = PsshAtomUtil.parseSchemeSpecificData(bArr, uuid);
            if (schemeSpecificData2 != null) {
                bArr = schemeSpecificData2;
            }
            bArr = PsshAtomUtil.buildPsshAtom(C.PLAYREADY_UUID, addLaUrlAttributeIfMissing(bArr));
        }
        return (((Util.SDK_INT >= 23 || !C.WIDEVINE_UUID.equals(uuid)) && !(C.PLAYREADY_UUID.equals(uuid) && "Amazon".equals(Util.MANUFACTURER) && ("AFTB".equals(Util.MODEL) || "AFTS".equals(Util.MODEL) || "AFTM".equals(Util.MODEL) || "AFTT".equals(Util.MODEL)))) || (schemeSpecificData = PsshAtomUtil.parseSchemeSpecificData(bArr, uuid)) == null) ? bArr : schemeSpecificData;
    }

    private static String adjustRequestMimeType(UUID uuid, String str) {
        return (Util.SDK_INT < 26 && C.CLEARKEY_UUID.equals(uuid) && (MimeTypes.VIDEO_MP4.equals(str) || MimeTypes.AUDIO_MP4.equals(str))) ? "cenc" : str;
    }

    private static byte[] adjustRequestData(UUID uuid, byte[] bArr) {
        return C.CLEARKEY_UUID.equals(uuid) ? ClearKeyUtil.adjustRequestData(bArr) : bArr;
    }

    private static void forceWidevineL3(MediaDrm mediaDrm) {
        mediaDrm.setPropertyString("securityLevel", "L3");
    }

    private static boolean needsForceWidevineL3Workaround() {
        return "ASUS_Z00AD".equals(Util.MODEL);
    }

    private static byte[] addLaUrlAttributeIfMissing(byte[] bArr) {
        ParsableByteArray parsableByteArray = new ParsableByteArray(bArr);
        int littleEndianInt = parsableByteArray.readLittleEndianInt();
        short littleEndianShort = parsableByteArray.readLittleEndianShort();
        short littleEndianShort2 = parsableByteArray.readLittleEndianShort();
        if (littleEndianShort != 1 || littleEndianShort2 != 1) {
            Log.i(TAG, "Unexpected record count or type. Skipping LA_URL workaround.");
            return bArr;
        }
        String string = parsableByteArray.readString(parsableByteArray.readLittleEndianShort(), Charsets.UTF_16LE);
        if (string.contains("<LA_URL>")) {
            return bArr;
        }
        int iIndexOf = string.indexOf("</DATA>");
        if (iIndexOf == -1) {
            Log.w(TAG, "Could not find the </DATA> tag. Skipping LA_URL workaround.");
        }
        String str = string.substring(0, iIndexOf) + MOCK_LA_URL + string.substring(iIndexOf);
        int i = littleEndianInt + 52;
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(i);
        byteBufferAllocate.order(ByteOrder.LITTLE_ENDIAN);
        byteBufferAllocate.putInt(i);
        byteBufferAllocate.putShort(littleEndianShort);
        byteBufferAllocate.putShort(littleEndianShort2);
        byteBufferAllocate.putShort((short) (str.length() * 2));
        byteBufferAllocate.put(str.getBytes(Charsets.UTF_16LE));
        return byteBufferAllocate.array();
    }

    private static class Api31 {
        private Api31() {
        }

        public static boolean requiresSecureDecoder(MediaDrm mediaDrm, String str) {
            return mediaDrm.requiresSecureDecoder(str);
        }

        public static void setLogSessionIdOnMediaDrmSession(MediaDrm mediaDrm, byte[] bArr, PlayerId playerId) {
            LogSessionId logSessionId = playerId.getLogSessionId();
            if (logSessionId.equals(LogSessionId.LOG_SESSION_ID_NONE)) {
                return;
            }
            ((MediaDrm.PlaybackComponent) Assertions.checkNotNull(mediaDrm.getPlaybackComponent(bArr))).setLogSessionId(logSessionId);
        }
    }
}
