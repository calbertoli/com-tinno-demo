package com.tinno.demo;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import com.google.android.exoplayer2.upstream.CmcdHeadersFactory;
import com.tinno.demo.receiver.BatteryReceiver;
import com.tinno.demo.util.AppForegroundListener;
import com.tinno.demo.util.CountDownTimerUtils;
import com.tinno.demo.util.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class DemoApplication extends Application implements AppForegroundListener.AppStateCallback, BatteryReceiver.BatteryChangeCallback {
    private final String TAG = "DemoApplication";
    private final String VIDEO_ACTIVITY_SIMPLE_NAME = "VideoActivity";
    private AppForegroundListener mAppForegroundListener;
    private BatteryReceiver mBatteryReceiver;
    private long mForegroundInactivityMillis;
    private CountDownTimer mInactivityTimer;

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        this.mForegroundInactivityMillis = getResources().getInteger(R.integer.foreground_inactivity_millisecond);
        initTimer();
        initAppState();
        initBatteryReceiver();
        try {
            startForegroundService(new Intent(this, (Class<?>) InactivityMonitorService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override // com.tinno.demo.util.AppForegroundListener.AppStateCallback
    public void onAppForeground() {
        LogUtils.d("DemoApplication", "onAppForeground");
        CountDownTimerUtils.startSafely(this.mInactivityTimer);
    }

    @Override // com.tinno.demo.util.AppForegroundListener.AppStateCallback
    public void onAppBackground() {
        LogUtils.d("DemoApplication", "onAppBackground");
        CountDownTimerUtils.cancelSafely(this.mInactivityTimer);
    }

    @Override // com.tinno.demo.receiver.BatteryReceiver.BatteryChangeCallback
    public void onPowerSaveModeChange(boolean z) {
        AppForegroundListener appForegroundListener = this.mAppForegroundListener;
        if (appForegroundListener != null) {
            Activity topActivity = appForegroundListener.getTopActivity();
            if (topActivity != null) {
                LogUtils.d("DemoApplication", "top activity:" + topActivity.getClass().getSimpleName());
            }
            if (topActivity != null && "VideoActivity".equals(topActivity.getClass().getSimpleName()) && z) {
                LogUtils.d("DemoApplication", "finish top activity");
                topActivity.finish();
            }
        }
    }

    private void initAppState() {
        AppForegroundListener appForegroundListener = new AppForegroundListener();
        this.mAppForegroundListener = appForegroundListener;
        appForegroundListener.setAppStateCallback(this);
        registerActivityLifecycleCallbacks(this.mAppForegroundListener);
    }

    private void initTimer() {
        if (this.mInactivityTimer != null) {
            return;
        }
        this.mInactivityTimer = new CountDownTimer(this.mForegroundInactivityMillis, 1000L) { // from class: com.tinno.demo.DemoApplication.1
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                LogUtils.d("DemoApplication", "application inactivity:" + (j / 1000) + CmcdHeadersFactory.STREAMING_FORMAT_SS);
            }

            @Override // android.os.CountDownTimer
            public void onFinish() {
                LogUtils.d("DemoApplication", "top activity:" + DemoApplication.this.mAppForegroundListener.getTopActivity().getClass().getSimpleName());
                if (!"VideoActivity".equals(DemoApplication.this.mAppForegroundListener.getTopActivity().getClass().getSimpleName()) && !DemoApplication.this.mBatteryReceiver.isPowerSavedMode() && DemoApplication.this.mAppForegroundListener.isForeground()) {
                    LogUtils.d("DemoApplication", "start video activity");
                    Intent intent = new Intent(DemoApplication.this, (Class<?>) VideoActivity.class);
                    intent.addFlags(268435456);
                    DemoApplication.this.startActivity(intent);
                }
                start();
            }
        };
    }

    public void startTimer() {
        CountDownTimerUtils.startSafely(this.mInactivityTimer);
    }

    public void receiveInactivity() {
        if (this.mAppForegroundListener.isForeground()) {
            startTimer();
        }
    }

    private void initBatteryReceiver() {
        BatteryReceiver batteryReceiver = new BatteryReceiver();
        this.mBatteryReceiver = batteryReceiver;
        batteryReceiver.setBatteryChangeCallback(this);
        registerReceiver(this.mBatteryReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        LogUtils.d("DemoApplication", "BatteryReceiver registered");
    }
}
