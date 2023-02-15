package com.coyni.pos.app.utils

import android.util.Log
import com.coyni.pos.app.BuildConfig

class LogUtils {

    companion object {
        var IS_LOGGING_ENABLED = BuildConfig.LOGGING_ENABLED

        fun v(tag: String?, msg: String) {
            if (IS_LOGGING_ENABLED) {
                Log.v(tag, "" + msg)
            }
        }

        fun d(tag: String, msg: String) {
            if (IS_LOGGING_ENABLED) {
                Log.d(tag, "" + msg)
            }
        }

        fun i(tag: String?, msg: String) {
            if (IS_LOGGING_ENABLED) {
                Log.i(tag, "" + msg)
            }
        }

        fun w(tag: String?, msg: String) {
            if (IS_LOGGING_ENABLED) {
                Log.w(tag, "" + msg)
            }
        }

        fun e(tag: String?, msg: String) {
            if (IS_LOGGING_ENABLED) {
                Log.e(tag, "" + msg)
            }
        }

        fun e(tag: String?, msg: String, tr: Throwable?) {
            if (IS_LOGGING_ENABLED) {
                Log.e(tag, "" + msg, tr)
            }
        }
    }

}