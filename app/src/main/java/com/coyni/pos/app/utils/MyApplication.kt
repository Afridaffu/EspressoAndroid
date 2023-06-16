package com.coyni.pos.app.utils

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    var mCurrentUserData: UserData = UserData()

    companion object{
        var context: Context? = null
    }
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    fun clearUserData() {
        mCurrentUserData = UserData()
    }

}