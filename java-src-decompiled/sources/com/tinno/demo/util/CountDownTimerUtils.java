package com.tinno.demo.util;

import android.os.CountDownTimer;

/* JADX INFO: loaded from: classes2.dex */
public class CountDownTimerUtils {
    public static boolean cancelSafely(CountDownTimer countDownTimer) {
        if (countDownTimer == null) {
            return false;
        }
        countDownTimer.cancel();
        return true;
    }

    public static boolean cancelSafely(CountDownTimer... countDownTimerArr) {
        for (CountDownTimer countDownTimer : countDownTimerArr) {
            if (countDownTimer == null) {
                return false;
            }
            countDownTimer.cancel();
        }
        return true;
    }

    public static boolean startSafely(CountDownTimer countDownTimer) {
        if (countDownTimer == null) {
            return false;
        }
        countDownTimer.cancel();
        countDownTimer.start();
        return true;
    }
}
