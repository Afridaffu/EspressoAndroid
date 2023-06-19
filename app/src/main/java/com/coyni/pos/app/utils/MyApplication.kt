package com.coyni.pos.app.utils

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class MyApplication : Application() {
    var mCurrentUserData: UserData = UserData()

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    fun clearUserData() {
        mCurrentUserData = UserData()
    }

}