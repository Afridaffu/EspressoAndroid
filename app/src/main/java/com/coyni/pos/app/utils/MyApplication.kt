package com.coyni.pos.app.utils

import android.app.Application

class MyApplication : Application() {
    val mCurrentUserData: UserData? = null

    override fun onCreate() {
        super.onCreate()
    }

}