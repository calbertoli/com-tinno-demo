package com.google.android.exoplayer2.util;

import android.util.Pair;
import java.lang.Throwable;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface ErrorMessageProvider<T extends Throwable> {
    Pair<Integer, String> getErrorMessage(T t);
}
