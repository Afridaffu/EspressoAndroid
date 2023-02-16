package com.coyni.pos.app.baseclass

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.coyni.pos.app.R
import com.coyni.pos.app.dialog.OnDialogClickListener

abstract class BaseDialog(context: Context) : Dialog(context, R.style.Theme_Dialog) {
    var listener: OnDialogClickListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultProperties()
        setContentView(getLayoutId())
        initViews()
    }

    open fun setOnDialogClickListener(listener: OnDialogClickListener?) {
        this.listener = listener
    }

    open fun getOnDialogClickListener(): OnDialogClickListener? {
        return listener
    }

    private fun setDefaultProperties() {
        val window = this.window
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE)
            val wlp: WindowManager.LayoutParams = window.attributes
            wlp.gravity = Gravity.BOTTOM
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND
            window.setBackgroundDrawableResource(android.R.color.transparent)
            window.attributes = wlp
            window.attributes.windowAnimations = R.style.DialogAnimation
            setCanceledOnTouchOutside(true)
        }
    }

    abstract fun getLayoutId(): Int

    abstract fun initViews()

}