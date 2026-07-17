package com.google.android.exoplayer2.database;

import android.database.sqlite.SQLiteDatabase;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface DatabaseProvider {
    public static final String TABLE_PREFIX = "ExoPlayer";

    SQLiteDatabase getReadableDatabase();

    SQLiteDatabase getWritableDatabase();
}
