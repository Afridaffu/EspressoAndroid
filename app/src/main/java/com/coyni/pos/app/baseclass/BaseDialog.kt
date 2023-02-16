package com.coyni.pos.app.baseclass

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.coyni.pos.app.R
import com.coyni.pos.app.dialog.OnDialogClickListener

abstract class BaseDialog : Dialog {
    var onDialogClickListener: OnDialogClickListener? = null
    private var customHeight = 0.0
    private var mContext: Context? = null

    constructor(context: Context) : super(context, R.style.Theme_Dialog) {
        this.mContext = context
    }

    constructor(context: Context, height: Double) : super(context, R.style.Theme_Dialog) {
        this.mContext = context
        customHeight = height
    }

    constructor(context: Context, themeResId: Int) : super(context, themeResId) {}

    fun setDialogClickListener(listener: OnDialogClickListener) {
        onDialogClickListener = listener
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultProperties()
        setContentView(getLayoutId())
        initViews()
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
            if (customHeight != 0.0) {
                val height =
                    (mContext!!.resources.displayMetrics.heightPixels * customHeight).toInt()
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, height)
            }
        }
    }

    abstract fun getLayoutId(): Int

    abstract fun initViews()

}