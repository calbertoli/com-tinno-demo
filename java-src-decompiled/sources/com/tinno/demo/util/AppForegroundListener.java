package com.tinno.demo.util;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/* JADX INFO: loaded from: classes2.dex */
public class AppForegroundListener implements Application.ActivityLifecycleCallbacks {
    private int mActivityCount;
    private AppStateCallback mCallback;
    private Activity mTopActivity;

    public interface AppStateCallback {
        void onAppBackground();

        void onAppForeground();
    }

    public Activity getTopActivity() {
        return this.mTopActivity;
    }

    public boolean isForeground() {
        return this.mActivityCount > 0;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityCreated(Activity activity, Bundle bundle) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityDestroyed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityPaused(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityResumed(Activity activity) {
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    public void setAppStateCallback(AppStateCallback appStateCallback) {
        this.mCallback = appStateCallback;
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStarted(Activity activity) {
        int i = this.mActivityCount + 1;
        this.mActivityCount = i;
        this.mTopActivity = activity;
        if (i == 1) {
            this.mCallback.onAppForeground();
        }
    }

    @Override // android.app.Application.ActivityLifecycleCallbacks
    public void onActivityStopped(Activity activity) {
        AppStateCallback appStateCallback;
        int i = this.mActivityCount - 1;
        this.mActivityCount = i;
        if (i > 0 || (appStateCallback = this.mCallback) == null) {
            return;
        }
        appStateCallback.onAppBackground();
        this.mActivityCount = 0;
    }
}
