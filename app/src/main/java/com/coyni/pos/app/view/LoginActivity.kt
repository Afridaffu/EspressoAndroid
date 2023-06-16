package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.ViewModelProvider
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityLoginBinding
import com.coyni.pos.app.dialog.ErrorDialog
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
    private var status: String = ""
    private var loinViewModel: LoginLogoutViewModel? = null
    private lateinit var myApplication: MyApplication
    private var lastClick: Long = 0L

    override fun onResume() {
        super.onResume()
        binding.tidET.requestFocus()
        binding.tidET.setText("")
        binding.passwordET.setText("")
        binding.passwordET.hint = ""
        Utils.upperHintColor(
            binding.passwordTIL, this, R.color.light_gray
        )
        if (!getKeyboardVisible()!!) {
            Utils.shwForcedKeypad(this@LoginActivity, binding.tidET)
        }
        //Static data remove later
//        setLoginData()
        //Static data remove later
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

        binding.passwordTIL.endIconMode = END_ICON_CUSTOM
        binding.tvButton.isEnabled = false
        binding.passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(this))

        binding.passwordET.transformationMethod = PasswordTransformationMethod.getInstance()

        binding.ivBack.setOnClickListener {
            if (Utils.isKeyboardVisible) Utils.hideKeypad(this@LoginActivity)
            onBackPressed()
        }

        binding.tvButton.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick < Utils.lastClickDelay) return@setOnClickListener
            lastClick = SystemClock.elapsedRealtime()
            showProgressDialog()
            loinViewModel?.getLoginData(LoginRequest(terminalId, password))
        }

        binding.endIconIV.setOnClickListener {
            if (!isIconEnable) {
                isIconEnable = true
                binding.endIconIV.setImageResource(R.drawable.ic_eyeopen)
                binding.passwordET.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                isIconEnable = false
                binding.endIconIV.setImageResource(R.drawable.ic_eyeclose)
                binding.passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding.passwordET.setSelection(binding.passwordET.text.toString().length)

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
                if (binding.tidET.text.toString().length == 0) {
                    binding.tvUpperHint.visibility = View.VISIBLE
                    binding.tvUpperHint.setTextColor(getColor(R.color.error_red))
                    binding.llTIDError.visibility = View.VISIBLE
                    binding.tvTIDError.text = "Field Required"
                    binding.llBoxStroke.background = getDrawable(R.drawable.outline_box_error)
                } else if (binding.tidET.text.toString().length < 10) {
                    binding.tvUpperHint.visibility = View.VISIBLE
                    binding.tvUpperHint.setTextColor(getColor(R.color.error_red))
                    binding.llTIDError.visibility = View.VISIBLE
                    binding.tvTIDError.text = "Minimum 10 Characters"
                    binding.llBoxStroke.background = getDrawable(R.drawable.outline_box_error)
                } else if (binding.tidET.text.toString().length == 10) {
                    binding.tvUpperHint.visibility = View.VISIBLE
                    binding.tvUpperHint.setTextColor(getColor(R.color.primary_black))
                    binding.llTIDError.visibility = View.GONE
                    binding.llBoxStroke.background = getDrawable(R.drawable.outline_box_unfocused)
                } else {
                    binding.tidET.hint = "Terminal ID"
                    binding.tvUpperHint.visibility = View.GONE
                    binding.llTIDError.visibility = View.GONE
                    binding.llBoxStroke.background = getDrawable(R.drawable.outline_box_unfocused)
                }
            }
        }
        binding.passwordET.setOnFocusChangeListener { view, b ->
            if (!b) {
                if (binding.passwordET.text.toString()
                        .trim().length in 1..7
                ) {
                    binding.passwordTIL.setBoxStrokeColorStateList(
                        Utils.getErrorColorState(
                            this@LoginActivity
                        )
                    )
                    Utils.upperHintColor(binding.passwordTIL, this@LoginActivity, R.color.error_red)
                    binding.passwordErrorLL.setVisibility(View.VISIBLE)
                    binding.passwordErrorTV.text = "Please enter a valid Password"
                } else if (binding.passwordET.text.toString().trim().length == 0) {
                    binding.passwordTIL.setBoxStrokeColorStateList(
                        Utils.getErrorColorState(
                            this@LoginActivity
                        )
                    )
                    Utils.upperHintColor(
                        binding.passwordTIL, this, R.color.light_gray
                    )
                    binding.passwordET.hint = ""
                    binding.passwordErrorLL.setVisibility(View.VISIBLE)
                    binding.passwordErrorTV.text = "Field Required"
                } else if (binding.passwordET.text.toString().trim().length >= 8) {
                    binding.passwordTIL.setBoxStrokeColorStateList(
                        Utils.getNormalColorState(
                            this@LoginActivity
                        )
                    )
                    Utils.upperHintColor(
                        binding.passwordTIL, this, R.color.primary_black
                    )
                    binding.passwordErrorLL.setVisibility(View.GONE)
                }
            } else {
                binding.passwordTIL.setBoxStrokeColor(getColor(R.color.primary_green))
                Utils.upperHintColor(
                    binding.passwordTIL, this, R.color.primary_green
                )
                binding.passwordErrorLL.setVisibility(View.GONE)
                binding.passwordET.hint =
                    "\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605"
                if (!getKeyboardVisible()!!) Utils.shwForcedKeypad(this, binding.passwordET)
            }
        }
    }

    private fun textWatchers() {

        binding.tidET.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.tidET.text.toString().length == 10) {
                    isId = true
                    terminalId = binding.tidET.text.toString()
                    enableButton()
                } else {
                    isId = false
                    enableButton()
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        binding.passwordET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//                TODO("Not yet implemented")
            }

            override fun onTextChanged(s: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (s!!.length > 0 && s.toString().trim { it <= ' ' }.length == 0) {
                    binding.passwordET.setText("")
                } else if (s.length > 0 && s.toString().contains(" ")) {
                    binding.passwordET.setText(s.toString().trim { it <= ' ' })
                    binding.passwordET.setSelection(s.toString().trim { it <= ' ' }.length)
                }
                if (binding.passwordET.text.toString().length >= 8) {
                    isPassword = true
                    password = binding.passwordET.text.toString()
                    enableButton()
                } else {
                    isPassword = false
                    enableButton()
                }
            }

            override fun afterTextChanged(p0: Editable?) {
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
                Log.e("strPreference", response.data!!.timeZone.toString())
                if (response.data!!.timeZone != null) {
                    myApplication.mCurrentUserData.strPreference =
                        Utils.getTimeZone(response.data!!.timeZone!!)
                    Log.e("strPreference", myApplication.mCurrentUserData.strPreference)
                }
                Utils.hideKeypad(this@LoginActivity)
                status = response.data!!.status.toString()
                if (status == Utils.DEACTIVATED || status == Utils.INACTIVE) {
                    showTerminalScreen(status)
                } else {
                    startActivity(
                        Intent(applicationContext, DashboardActivity::class.java).setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        )
                    )
                }

            } else {
                showDialog()
            }
        }
    }

    private fun showTerminalScreen(status: String) {
        startActivity(
            Intent(
                this@LoginActivity, StatusFailedActivity::class.java
            ).putExtra(Utils.STATUS, status)
        )
    }

    private fun showDialog() {
        val errorDialog = ErrorDialog(this@LoginActivity)
        errorDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (Utils.onBoard) {
            startActivity(
                Intent(applicationContext, OnboardActivity::class.java).setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
            )
        } else {
//                onBackPressed()
        }
    }

    override fun onPause() {
        super.onPause();
        hideAndClearFocus();
    }

    fun hideAndClearFocus() {
        Handler().postDelayed({
            binding.tidET.clearFocus()
            binding.passwordET.clearFocus()
        }, 500)
    }

}
