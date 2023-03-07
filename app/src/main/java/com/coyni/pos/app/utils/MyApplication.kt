package com.coyni.pos.app.utils

import android.app.Application
import com.coyni.pos.app.baseclass.OnClickListener

class MyApplication : Application() {
    var mCurrentUserData: UserData = UserData()
    var listener: OnClickListener? = null

    fun clearUserData() {
        mCurrentUserData = UserData()
    }
}