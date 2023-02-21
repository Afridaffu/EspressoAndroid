package com.coyni.pos.app.baseclass

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.coyni.pos.app.R
import com.coyni.pos.app.utils.LogUtils.Companion.d
import com.vt.kotlinexamples.retrofit_network.viewmodel.CommonViewModel
import com.vt.kotlinexamples.retrofit_network.viewmodel.CommonViewModel.Companion.getInstance

abstract class BaseActivity : AppCompatActivity() {
    val TAG = javaClass.name
    private var dialog: Dialog? = null
    var commonViewModel: CommonViewModel? = null
    var isBaseBiometric = false
    var isAccess = false
    var isMerchantHide = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            d(TAG, javaClass.name)
            commonViewModel = getInstance(this)

//            runOnUiThread(() -> {
            commonViewModel!!.appUpdateRespMutableLiveData.observe(this,
                Observer { appUpdateResp ->
                    try {
                        if (appUpdateResp == null) {
                            return@Observer
                        }
                        if (appUpdateResp.data != null) {
                            val version = packageManager.getPackageInfo(
                                this@BaseActivity.packageName,
                                0
                            ).versionName
                            val versionCode =
                                packageManager.getPackageInfo(packageName, 0).versionCode
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
    }

    open fun fragmentNavigation(action: String?, value: String?) {}

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
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
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
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
}