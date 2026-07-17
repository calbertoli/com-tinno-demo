package com.tinno.demo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import com.tinno.demo.util.LogUtils;

/* JADX INFO: loaded from: classes2.dex */
public class BatteryReceiver extends BroadcastReceiver {
    private static final String TAG = "BatteryReceiver";
    private BatteryChangeCallback mCallback;
    private final float POWER_SAVE_THRESHOLD = 20.0f;
    private boolean mPowerSavedMode = false;

    public interface BatteryChangeCallback {
        default void onBatteryChanged(float f, boolean z) {
        }

        void onPowerSaveModeChange(boolean z);
    }

    public boolean isPowerSavedMode() {
        return this.mPowerSavedMode;
    }

    public void setBatteryChangeCallback(BatteryChangeCallback batteryChangeCallback) {
        this.mCallback = batteryChangeCallback;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BATTERY_CHANGED".equals(intent.getAction())) {
            int intExtra = intent.getIntExtra("level", -1);
            int intExtra2 = intent.getIntExtra("scale", -1);
            int intExtra3 = intent.getIntExtra(NotificationCompat.CATEGORY_STATUS, -1);
            boolean z = false;
            boolean z2 = intExtra3 == 2 || intExtra3 == 5;
            float f = (intExtra / intExtra2) * 100.0f;
            LogUtils.d(TAG, "Battery Level: " + f + "%");
            LogUtils.d(TAG, "Is Charging: " + z2);
            if (f < 20.0f && !z2) {
                z = true;
            }
            if (z != this.mPowerSavedMode) {
                this.mPowerSavedMode = z;
                if (this.mCallback != null) {
                    LogUtils.d(TAG, "onPowerSaveModeChange");
                    this.mCallback.onPowerSaveModeChange(this.mPowerSavedMode);
                }
            }
            LogUtils.d(TAG, "power save mode: " + this.mPowerSavedMode);
            BatteryChangeCallback batteryChangeCallback = this.mCallback;
            if (batteryChangeCallback != null) {
                batteryChangeCallback.onBatteryChanged(f, z2);
            }
        }
    }
}
