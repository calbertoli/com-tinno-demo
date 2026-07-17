package com.google.android.exoplayer2.util;

import android.graphics.Bitmap;
import android.net.Uri;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.common.util.concurrent.ListenableFuture;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface BitmapLoader {
    ListenableFuture<Bitmap> decodeBitmap(byte[] bArr);

    ListenableFuture<Bitmap> loadBitmap(Uri uri);

    default ListenableFuture<Bitmap> loadBitmapFromMetadata(MediaMetadata mediaMetadata) {
        if (mediaMetadata.artworkData != null) {
            return decodeBitmap(mediaMetadata.artworkData);
        }
        if (mediaMetadata.artworkUri != null) {
            return loadBitmap(mediaMetadata.artworkUri);
        }
        return null;
    }
}
