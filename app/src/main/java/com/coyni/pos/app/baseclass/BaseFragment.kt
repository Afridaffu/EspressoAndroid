package com.coyni.pos.app.baseclass

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.coyni.pos.app.utils.Utils
import okhttp3.*
import okio.ByteString
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

abstract class BaseFragment : Fragment() {
    val TAG = javaClass.name
    lateinit var mActivity: BaseActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = activity as BaseActivity
    }

    open fun onBackPressed() {}

}