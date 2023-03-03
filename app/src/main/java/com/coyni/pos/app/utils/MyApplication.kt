package com.coyni.pos.app.utils

import android.app.Application
import com.coyni.pos.app.baseclass.OnClickListener

class MyApplication : Application() {
    val mCurrentUserData: UserData = UserData()
    var listener: OnClickListener? = null


    override fun onCreate() {
        super.onCreate()
    }

}