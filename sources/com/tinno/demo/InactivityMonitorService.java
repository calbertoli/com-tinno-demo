package com.tinno.demo;

import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.IOnTouchListener;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.camera2.CameraManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityEvent;
import androidx.core.app.ServiceCompat;
import com.google.android.exoplayer2.upstream.CmcdHeadersFactory;
import com.tinno.demo.util.CountDownTimerUtils;
import com.tinno.demo.util.LogUtils;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public class InactivityMonitorService extends AccessibilityService {
    private static final String CHANNEL_ID = "inactivity_monitor_channel";
    private static final int NOTIFICATION_ID = 1001;
    private static final String TAG = "InactivityMonitorService";
    private long mBackgroundInactivityMillis;
    private CountDownTimer mInactivityTimer;
    private boolean mIsCalling = false;
    private boolean mIsPhotographing = false;
    private BroadcastReceiver mPhoneCallReceiver;
    private long mScreenOffInactivityMillis;
    private CountDownTimer mScreenOffTimer;
    private BroadcastReceiver mScreenStateReceiver;

    @Override // android.accessibilityservice.AccessibilityService
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
    }

    @Override // android.accessibilityservice.AccessibilityService
    public void onInterrupt() {
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        return 1;
    }

    @Override // android.app.Service
    public void onCreate() {
        Log.i(TAG, "start InactivityMonitorService");
        super.onCreate();
        this.mBackgroundInactivityMillis = getResources().getInteger(R.integer.background_inactivity_millisecond);
        this.mScreenOffInactivityMillis = getResources().getInteger(R.integer.screen_off_inactivity_millisecond);
        createNotificationChannel();
        ServiceCompat.startForeground(this, 1001, createNotification(), 1);
        initTimer();
        registerBroadcast();
        try {
            WindowManagerGlobal.getWindowManagerService().registerIOnTouchListener(new IOnTouchListener.Stub() { // from class: com.tinno.demo.InactivityMonitorService.1
                public void onTouchEvent(int i) {
                    if (i != 0 || InactivityMonitorService.this.mInactivityTimer == null) {
                        return;
                    }
                    Log.d(InactivityMonitorService.TAG, "interaction from touch");
                    InactivityMonitorService.this.mInactivityTimer.cancel();
                    InactivityMonitorService.this.mInactivityTimer.start();
                    ((DemoApplication) InactivityMonitorService.this.getApplication()).receiveInactivity();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        initCameraListener();
    }

    @Override // android.accessibilityservice.AccessibilityService
    protected boolean onKeyEvent(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() != 26) {
            CountDownTimerUtils.startSafely(this.mInactivityTimer);
            ((DemoApplication) getApplication()).receiveInactivity();
        }
        return super.onKeyEvent(keyEvent);
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i(TAG, "inactivity monitor service has stopped");
        startAllTimer();
        this.mInactivityTimer = null;
        this.mScreenOffTimer = null;
        unregisterReceiver(this.mScreenStateReceiver);
        unregisterReceiver(this.mPhoneCallReceiver);
    }

    private void initTimer() {
        CountDownTimer countDownTimer = new CountDownTimer(this.mBackgroundInactivityMillis, 1000L) { // from class: com.tinno.demo.InactivityMonitorService.2
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                LogUtils.d(InactivityMonitorService.TAG, "inactivity:" + (j / 1000) + CmcdHeadersFactory.STREAMING_FORMAT_SS);
            }

            @Override // android.os.CountDownTimer
            public void onFinish() {
                InactivityMonitorService.this.startHomeActivity();
                start();
            }
        };
        this.mInactivityTimer = countDownTimer;
        countDownTimer.start();
        this.mScreenOffTimer = new CountDownTimer(this.mScreenOffInactivityMillis, 1000L) { // from class: com.tinno.demo.InactivityMonitorService.3
            @Override // android.os.CountDownTimer
            public void onTick(long j) {
                LogUtils.d(InactivityMonitorService.TAG, "screen off:" + (j / 1000) + CmcdHeadersFactory.STREAMING_FORMAT_SS);
            }

            @Override // android.os.CountDownTimer
            public void onFinish() {
                InactivityMonitorService.this.startHomeActivity();
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startHomeActivity() {
        PowerManager powerManager = (PowerManager) getSystemService("power");
        if ((isForeground() && powerManager.isInteractive()) || this.mIsCalling || this.mIsPhotographing) {
            return;
        }
        Intent intent = new Intent(this, (Class<?>) HomeActivity.class);
        intent.addFlags(268435456);
        startActivity(intent);
    }

    private boolean isForeground() {
        List<ActivityManager.RunningTaskInfo> runningTasks = ((ActivityManager) getSystemService("activity")).getRunningTasks(1);
        if (runningTasks.isEmpty()) {
            return false;
        }
        return runningTasks.get(0).topActivity.getPackageName().equals("com.tinno.demo");
    }

    private void registerBroadcast() {
        this.mScreenStateReceiver = new BroadcastReceiver() { // from class: com.tinno.demo.InactivityMonitorService.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                InactivityMonitorService.this.startAllTimer();
            }
        };
        IntentFilter intentFilter = new IntentFilter("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        registerReceiver(this.mScreenStateReceiver, intentFilter);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.tinno.demo.InactivityMonitorService.5
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if ("android.intent.action.PHONE_STATE".equals(intent.getAction())) {
                    String stringExtra = intent.getStringExtra("state");
                    InactivityMonitorService.this.mIsCalling = !TelephonyManager.EXTRA_STATE_IDLE.equals(stringExtra);
                    if (InactivityMonitorService.this.mIsCalling) {
                        InactivityMonitorService.this.stopAllTimer();
                    } else {
                        InactivityMonitorService.this.startAllTimer();
                    }
                }
            }
        };
        this.mPhoneCallReceiver = broadcastReceiver;
        registerReceiver(broadcastReceiver, new IntentFilter("android.intent.action.PHONE_STATE"));
    }

    private void initCameraListener() {
        try {
            ((CameraManager) getSystemService("camera")).registerAvailabilityCallback(new CameraManager.AvailabilityCallback() { // from class: com.tinno.demo.InactivityMonitorService.6
                @Override // android.hardware.camera2.CameraManager.AvailabilityCallback
                public void onCameraAvailable(String str) {
                    LogUtils.d(InactivityMonitorService.TAG, "Camera free: " + str);
                    InactivityMonitorService.this.mIsPhotographing = false;
                    InactivityMonitorService.this.startAllTimer();
                }

                @Override // android.hardware.camera2.CameraManager.AvailabilityCallback
                public void onCameraUnavailable(String str) {
                    LogUtils.d(InactivityMonitorService.TAG, "Camera in use: " + str);
                    InactivityMonitorService.this.mIsPhotographing = true;
                    InactivityMonitorService.this.stopAllTimer();
                }
            }, (Handler) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, getString(R.string.app_name), 2);
        notificationChannel.setDescription(getString(R.string.notification_content));
        ((NotificationManager) getSystemService(NotificationManager.class)).createNotificationChannel(notificationChannel);
    }

    private Notification createNotification() {
        return new Notification.Builder(this, CHANNEL_ID).setContentTitle(getString(R.string.app_name)).setContentText(getString(R.string.notification_content)).setSmallIcon(R.drawable.ic_app).setPriority(-1).build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopAllTimer() {
        CountDownTimerUtils.cancelSafely(this.mInactivityTimer, this.mScreenOffTimer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startAllTimer() {
        if (((PowerManager) getSystemService("power")).isInteractive()) {
            CountDownTimerUtils.startSafely(this.mInactivityTimer);
            CountDownTimerUtils.cancelSafely(this.mScreenOffTimer);
        } else {
            CountDownTimerUtils.startSafely(this.mScreenOffTimer);
            CountDownTimerUtils.cancelSafely(this.mInactivityTimer);
        }
    }
}
