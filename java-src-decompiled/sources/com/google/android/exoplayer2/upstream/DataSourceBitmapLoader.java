package com.google.android.exoplayer2.upstream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.BitmapLoader;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public final class DataSourceBitmapLoader implements BitmapLoader {
    public static final Supplier<ListeningExecutorService> DEFAULT_EXECUTOR_SERVICE = Suppliers.memoize(new Supplier() { // from class: com.google.android.exoplayer2.upstream.DataSourceBitmapLoader$$ExternalSyntheticLambda1
        @Override // com.google.common.base.Supplier
        public final Object get() {
            return MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor());
        }
    });
    private final DataSource.Factory dataSourceFactory;
    private final ListeningExecutorService listeningExecutorService;

    public DataSourceBitmapLoader(Context context) {
        this((ListeningExecutorService) Assertions.checkStateNotNull(DEFAULT_EXECUTOR_SERVICE.get()), new DefaultDataSource.Factory(context));
    }

    public DataSourceBitmapLoader(ListeningExecutorService listeningExecutorService, DataSource.Factory factory) {
        this.listeningExecutorService = listeningExecutorService;
        this.dataSourceFactory = factory;
    }

    @Override // com.google.android.exoplayer2.util.BitmapLoader
    public ListenableFuture<Bitmap> decodeBitmap(final byte[] bArr) {
        return this.listeningExecutorService.submit(new Callable() { // from class: com.google.android.exoplayer2.upstream.DataSourceBitmapLoader$$ExternalSyntheticLambda2
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return DataSourceBitmapLoader.decode(bArr);
            }
        });
    }

    /* JADX INFO: renamed from: lambda$loadBitmap$2$com-google-android-exoplayer2-upstream-DataSourceBitmapLoader, reason: not valid java name */
    /* synthetic */ Bitmap m180xfab27783(Uri uri) throws Exception {
        return load(this.dataSourceFactory.createDataSource(), uri);
    }

    @Override // com.google.android.exoplayer2.util.BitmapLoader
    public ListenableFuture<Bitmap> loadBitmap(final Uri uri) {
        return this.listeningExecutorService.submit(new Callable() { // from class: com.google.android.exoplayer2.upstream.DataSourceBitmapLoader$$ExternalSyntheticLambda0
            @Override // java.util.concurrent.Callable
            public final Object call() {
                return this.f$0.m180xfab27783(uri);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Bitmap decode(byte[] bArr) {
        Bitmap bitmapDecodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length);
        Assertions.checkArgument(bitmapDecodeByteArray != null, "Could not decode image data");
        return bitmapDecodeByteArray;
    }

    private static Bitmap load(DataSource dataSource, Uri uri) throws IOException {
        dataSource.open(new DataSpec(uri));
        return decode(DataSourceUtil.readToEnd(dataSource));
    }
}
