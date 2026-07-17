package com.tinno.demo.util;

import android.os.SystemProperties;
import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class LogUtils {
    public static final String BUILD_TYPE = "ro.build.type";
    public static final String BUILD_TYPE_ENG = "eng";
    public static final String BUILD_TYPE_USERDEBUG = "userdebug";
    public static final boolean LOG_ENABLE;
    private static final String TAG = "recorder";

    static {
        LOG_ENABLE = SystemProperties.get(BUILD_TYPE).equals(BUILD_TYPE_ENG) || SystemProperties.get(BUILD_TYPE).equals(BUILD_TYPE_USERDEBUG) || Log.isLoggable(TAG, 3);
    }

    public static void i(String str, String str2) {
        if (LOG_ENABLE) {
            Log.i(str, str2);
        }
    }

    public static void v(String str, String str2) {
        if (LOG_ENABLE) {
            Log.v(str, str2);
        }
    }

    public static void d(String str, String str2) {
        if (LOG_ENABLE) {
            Log.d(str, str2);
        }
    }

    public static void w(String str, String str2) {
        if (LOG_ENABLE) {
            Log.w(str, str2);
        }
    }
}
