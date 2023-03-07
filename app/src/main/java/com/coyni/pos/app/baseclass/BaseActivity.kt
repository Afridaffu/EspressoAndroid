package com.coyni.pos.app.baseclass

import android.app.Dialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.coyni.pos.app.R
import com.coyni.pos.app.utils.LogUtils.Companion.d
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.viewmodel.CommonViewModel

abstract class BaseActivity : AppCompatActivity() {
    val TAG = javaClass.name
    var dialog: Dialog? = null
    var commonViewModel: CommonViewModel? = null
    var isBaseBiometric = false
    var isAccess = false
    var isMerchantHide = false
    var mLastClickTime = 0L
    var isKeyboardVisible: Boolean = false
    lateinit var decorView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            d(TAG, javaClass.name)
            commonViewModel = ViewModelProvider(this).get(CommonViewModel::class.java)

//            runOnUiThread(() -> {
            commonViewModel!!.appUpdateRespMutableLiveData.observe(this, Observer { appUpdateResp ->
                try {
                    if (appUpdateResp == null) {
                        return@Observer
                    }
                    if (appUpdateResp.data != null) {
                        val version = packageManager.getPackageInfo(
                            this@BaseActivity.packageName, 0
                        ).versionName
                        val versionCode = packageManager.getPackageInfo(packageName, 0).versionCode
                        val versionName = version.replace(".", "").toInt()
                    }
                    //                        else if (appUpdateResp.getError() != null && appUpdateResp.getError().getErrorCode().equals(getString(R.string.accessrestrictederrorcode))) {
                    //                            showAccessRestricted();
                    //                        }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            })
            //            });
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initKeyboardListener()
    }

    open fun fragmentNavigation(action: String?, value: String?) {}

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    @JvmOverloads
    fun showProgressDialog(
        message: String? = "Loading",
        isCanceledOnTouchOutside: Boolean = true,
        baseActivity: BaseActivity? = this
    ) {
        if (dialog != null && dialog!!.isShowing) {
            return
        }
        dialog = Dialog(baseActivity!!)
        dialog!!.window!!.requestFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setContentView(R.layout.loader)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        val loaderMsg = dialog!!.findViewById<TextView>(R.id.loaderText)
        loaderMsg.text = message
        val window = dialog!!.window
        window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT
        )
        val wlp = window.attributes
        wlp.gravity = Gravity.CENTER
        wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND
        window.attributes = wlp
        dialog!!.setCanceledOnTouchOutside(isCanceledOnTouchOutside)
        dialog!!.show()
    }

    fun dismissDialog() {
        if (dialog != null) {
            dialog!!.dismiss()
        }
    }

    private fun initKeyboardListener() {
        // Get the current window view
        decorView = window.decorView
        // Set a ViewTreeObserver on the window view
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(OnGlobalLayoutListener {

            // Get the current visible display frame of the window
            val rect = Rect()
            decorView.getWindowVisibleDisplayFrame(rect)

            // Calculate the screen height and keyboard height
            val screenHeight: Int = decorView.getRootView().getHeight()
            val keyboardHeight = screenHeight - rect.bottom

            // Check if the keyboard is visible
            isKeyboardVisible = keyboardHeight > screenHeight * 0.15
            Utils.isKeyboardVisible = isKeyboardVisible
        })
    }

    open fun getKeyboardVisible(): Boolean? {
        return isKeyboardVisible
    }

}