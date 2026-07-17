package com.google.android.exoplayer2.scheduler;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface Scheduler {
    boolean cancel();

    Requirements getSupportedRequirements(Requirements requirements);

    boolean schedule(Requirements requirements, String str, String str2);
}
