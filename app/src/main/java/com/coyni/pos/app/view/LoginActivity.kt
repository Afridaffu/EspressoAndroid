package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.baseclass.OnClickListener
import com.coyni.pos.app.databinding.ActivityLoginBinding
import com.coyni.pos.app.dialog.ErrorDialog
import com.coyni.pos.app.model.downloadurl.DownloadUrlRequest
import com.coyni.pos.app.model.login.LoginRequest
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.viewmodel.LoginLogoutViewModel
import com.google.android.material.textfield.TextInputLayout.END_ICON_CUSTOM

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isId = false
    private var isPassword = false
    private var isPwdEye = false
    private var isIconEnable = false
    private var terminalId: String = ""
    private var password: String = ""
    private var loinViewModel: LoginLogoutViewModel? = null
    private lateinit var myApplication: MyApplication
    private var lastClick: Long = 0L

    override fun onResume() {
        super.onResume()
        binding.tidET.setText("")
        binding.passwordET.setText("")
        binding.tidET.requestFocus()
        if (!isKeyboardVisible)
            Utils.shwForcedKeypad(this, binding.tidET)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myApplication = applicationContext as MyApplication
        initView()
        focusListeners()
        textWatchers()
        initObserver()
    }

    private fun initView() {

        loinViewModel = ViewModelProvider(this@LoginActivity).get(LoginLogoutViewModel::class.java)

        val myApplication = applicationContext as MyApplication

        binding.passwordTIL.endIconMode = END_ICON_CUSTOM
        binding.tvButton.isEnabled = false
        binding.passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(this))

        binding.passwordET.transformationMethod = PasswordTransformationMethod.getInstance()

        binding.ivBack.setOnClickListener {
            if (Utils.isKeyboardVisible) Utils.hideKeypad(this@LoginActivity)
            onBackPressed()
        }

//        binding.passwordTIL.setEndIconOnClickListener {
//            if (!isIconEnable) {
//                isIconEnable = true
//                binding.passwordTIL.endIconDrawable =
//                    AppCompatResources.getDrawable(this, R.drawable.ic_eyeopen)
//                binding.passwordET.transformationMethod =
//                    HideReturnsTransformationMethod.getInstance()
//            } else {
//                isIconEnable = false
//                binding.passwordTIL.endIconDrawable =
//                    AppCompatResources.getDrawable(this, R.drawable.ic_eyeclose)
//                binding.passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
//            }
//            binding.passwordET.setSelection(binding.passwordET.text.toString().length)
//        }

        binding.tvButton.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick < Utils.lastClickDelay) return@setOnClickListener
            lastClick = SystemClock.elapsedRealtime()
            showProgressDialog()
            loinViewModel?.getLoginData(LoginRequest(terminalId, password))
        }

        binding.endIconIV.setOnClickListener {
            try {
                if (!isPwdEye) {
                    isPwdEye = true
                    binding.endIconIV.setImageResource(R.drawable.ic_eyeopen)
                    binding.passwordET.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                } else {
                    isPwdEye = false
                    binding.endIconIV.setImageResource(R.drawable.ic_eyeclose)
                    binding.passwordET.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                }
                if (binding.passwordET.text.toString().length > 0) {
                    binding.passwordET.setSelection(binding.passwordET.text.toString().length)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun focusListeners() {

        binding.tidET.setOnFocusChangeListener { _, b ->
            if (b) {
                if (binding.tidET.text.toString()
                        .isNotEmpty()
                ) binding.tidET.setSelection(binding.tidET.text.toString().length)
                binding.tidET.hint = ""
                binding.tvUpperHint.visibility = View.VISIBLE
                binding.tvUpperHint.setTextColor(getColor(R.color.primary_green))
                binding.llTIDError.visibility = View.GONE
                binding.llBoxStroke.background = getDrawable(R.drawable.outline_box_focused)
            } else {
                if (binding.tidET.text.toString().length in 5..8) {
                    binding.tvUpperHint.visibility = View.VISIBLE
                    binding.tvUpperHint.setTextColor(getColor(R.color.primary_black))
                    binding.llTIDError.visibility = View.GONE
                    binding.llBoxStroke.background = getDrawable(R.drawable.outline_box_unfocused)

                } else if (binding.tidET.text.toString().length in 1..4) {
                    binding.tvUpperHint.visibility = View.VISIBLE
                    binding.tvUpperHint.setTextColor(getColor(R.color.error_red))
                    binding.llTIDError.visibility = View.VISIBLE
                    binding.tvTIDError.text = "Minimum 5 Characters"
                    binding.llBoxStroke.background = getDrawable(R.drawable.outline_box_error)

                } else {
                    binding.tidET.hint = "Terminal ID"
                    binding.tvUpperHint.visibility = View.GONE
                    binding.llTIDError.visibility = View.GONE
                    binding.llBoxStroke.background = getDrawable(R.drawable.outline_box_unfocused)
                }
            }
        }

        binding.passwordET.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                if (binding.passwordET.text.toString()
                        .isNotEmpty()
                ) binding.passwordET.setSelection(binding.passwordET.text.toString().length)
                Utils.upperHintColor(binding.passwordTIL, this@LoginActivity, R.color.primary_green)
                Log.e("getKeyboardVisible", getKeyboardVisible().toString())
                if (!getKeyboardVisible()!!) {
                    Utils.shwForcedKeypad(this, binding.passwordET)
                }
                if (binding.passwordET.text.toString()
                        .isNotEmpty()
                ) binding.passwordET.setSelection(binding.passwordET.text.toString().length)
                Utils.upperHintColor(binding.passwordTIL, this@LoginActivity, R.color.primary_green)
                binding.passwordErrorLL.visibility = View.GONE
                binding.passwordTIL.setBoxStrokeColorStateList(Utils.getFocusedColorState(this))
                binding.passwordET.hint =
                    "\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605"

                if (binding.passwordET.text.toString().length > 0)
                    binding.passwordET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                else
                    binding.passwordET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            } else {
                binding.passwordET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                if (binding.passwordET.text.toString().length in 1..7) {
                    binding.passwordErrorTV.text = "Please enter a valid Password"
                    Utils.upperHintColor(binding.passwordTIL, this@LoginActivity, R.color.error_red)
                    binding.passwordErrorLL.visibility = View.VISIBLE
                    binding.passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(this))
                } else {
                    if (binding.passwordET.text.toString().length > 7) Utils.upperHintColor(
                        binding.passwordTIL, this@LoginActivity, R.color.primary_black
                    )
                    else Utils.upperHintColor(
                        binding.passwordTIL, this@LoginActivity, R.color.light_gray
                    )
                    binding.passwordET.hint = ""
                    binding.passwordErrorLL.visibility = View.GONE
                    binding.passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(this))
                }
            }
        }

    }

    private fun textWatchers() {

        binding.tidET.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isId = binding.tidET.text.toString().length >= 5
                terminalId = binding.tidET.text.toString()
                enableButton()
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.passwordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                isPassword = binding.passwordET.text?.length!! >= 8
                password = binding.passwordET.text.toString()
                enableButton()

                if (p0!!.length == 0) {
                    // No entered text so will show hint
                    if (binding.passwordET.hasFocus()) binding.passwordET.setTextSize(
                        TypedValue.COMPLEX_UNIT_SP, 12f
                    ) else binding.passwordET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                } else {
                    binding.passwordET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
//                TODO("Not yet implemented")
            }

        })
    }

    private fun enableButton() {
        if (isId && isPassword) {
            binding.tvButton.isEnabled = true
            binding.tvButton.background =
                AppCompatResources.getDrawable(this@LoginActivity, R.drawable.button_bg_green)
        } else {
            binding.tvButton.isEnabled = false
            binding.tvButton.background =
                AppCompatResources.getDrawable(this@LoginActivity, R.drawable.button_bg_inactive)
        }
    }

    private fun initObserver() {
        loinViewModel?.loginResponseMutableLiveData?.observe(this@LoginActivity) { response ->
            dismissDialog()

            if (response != null && response.status.equals(Utils.SUCCESS)) {
                Utils.strAuth = response.data?.jwtToken
                myApplication.mCurrentUserData.loginData = response.data!!
                Utils.hideKeypad(this@LoginActivity)
                if (response.data?.status.equals(Utils.DEACTIVATED, true)) {
                    showTerminalScreen()
                } else {
                    val imgUrl: String = response.data!!.image.toString()
                    val urlList = ArrayList<DownloadUrlRequest>()
                    urlList.add(DownloadUrlRequest(imgUrl))
                    loinViewModel!!.downloadUrl(urlList)
                }

            } else {
                showDialog()
            }
        }
        loinViewModel?.downloadUrlResponseMutableLiveData?.observe(this@LoginActivity) { response ->
            if (response != null && response.status.equals(Utils.SUCCESS)) {
                myApplication.mCurrentUserData.downloadUrlData = response.data
                startActivity(
                    Intent(applicationContext, DashboardActivity::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
                )
            }
        }
    }

    private fun showTerminalScreen() {
        startActivity(
            Intent(
                this@LoginActivity, StatusFailedActivity::class.java
            ).putExtra(Utils.STATUS, Utils.DEACTIVATED)
        )
    }

    private fun showDialog() {
        val errorDialog = ErrorDialog(this@LoginActivity)
        errorDialog.show()
    }
}
