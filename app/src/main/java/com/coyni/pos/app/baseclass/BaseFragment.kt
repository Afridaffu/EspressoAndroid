package com.coyni.pos.app.baseclass

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {
    val TAG = javaClass.name
    lateinit var mActivity: BaseActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = activity as BaseActivity
    }

    open fun onBackPressed() {}
}