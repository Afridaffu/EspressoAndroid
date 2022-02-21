package com.greenbox.coyni.utils;

import android.util.Log;

import com.greenbox.coyni.BuildConfig;

public class LogUtils {

    public static boolean IS_LOGGING_ENABLED = BuildConfig.LOGGING_ENABLED;
    private LogUtils() {
    }

    public static void v(String tag, String msg) {
        if(IS_LOGGING_ENABLED) {
            Log.v(tag, ""+ msg);
        }
    }

    public static void d(String tag, String msg) {
        if(IS_LOGGING_ENABLED) {
            Log.d(tag, ""+msg);
        }
    }

    public static void i(String tag, String msg) {
        if(IS_LOGGING_ENABLED) {
            Log.i(tag, ""+msg);
        }
    }

    public static void w(String tag, String msg) {
        if(IS_LOGGING_ENABLED) {
            Log.w(tag, ""+msg);
        }
    }

    public static void e(String tag, String msg) {
        if(IS_LOGGING_ENABLED) {
            Log.e(tag, ""+msg);
        }
    }

    public static void e(String tag, String msg, Throwable tr) {
        if(IS_LOGGING_ENABLED) {
            Log.e(tag, ""+msg, tr);
        }
    }
}
