package com.coyni.pos.app.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.baseclass.OnClickListener
import com.coyni.pos.app.databinding.ActivityLoginBinding
import com.coyni.pos.app.dialog.ErrorDialog
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isId = false
    private var isPassword = false
    private var isIconEnable = false

    override fun onResume() {
        super.onResume()
        binding.tidET.requestFocus()
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
        initView()
        focusListeners()
        textWatchers()


        val drawable = ContextCompat.getDrawable(this, R.drawable.cursor_color)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            binding.passwordET.textCursorDrawable = drawable
        } else {
            try {
                val f = TextView::class.java.getDeclaredField("mCursorDrawableRes")
                f.isAccessible = true
                f.set(binding.passwordET, R.drawable.cursor_color)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun initView() {

        val myApplication = applicationContext as MyApplication

        binding.tvButton.isEnabled = false
        binding.passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(this))

        binding.ivBack.setOnClickListener {
            if (Utils.isKeyboardVisible) Utils.hideKeypad(this@LoginActivity)
            onBackPressed()
        }

        binding.passwordTIL.setEndIconOnClickListener {
            if (!isIconEnable) {
                isIconEnable = true
                binding.passwordTIL.endIconDrawable =
                    AppCompatResources.getDrawable(this, R.drawable.ic_eyeopen)
                binding.passwordET.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                isIconEnable = false
                binding.passwordTIL.endIconDrawable =
                    AppCompatResources.getDrawable(this, R.drawable.ic_eyeclose)
                binding.passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            binding.passwordET.setSelection(binding.passwordET.text.toString().length)
        }

        myApplication.listener = object : OnClickListener {
            override fun onButtonClick(click: Boolean) {
                finish()
            }
        }

        binding.tvButton.setOnClickListener {

            Utils.hideKeypad(this@LoginActivity)
            startActivity(
                Intent(applicationContext, MposDashboardActivity::class.java).setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
            )
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

                if (binding.passwordET.getText()
                        .toString().length > 0
                ) binding.passwordET.setTextSize(
                    TypedValue.COMPLEX_UNIT_SP, 16f
                ) else binding.passwordET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
            } else {
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

    private fun showTerminalScreen() {

        startActivity(
            Intent(
                this@LoginActivity, StatusFailedActivity::class.java
            ).putExtra(Utils.SCREEN, Utils.LOGIN)
                .putExtra(Utils.HEADER, getString(R.string.terminal_deactivated)).putExtra(
                    Utils.DESCRIPTION,
                    getString(R.string.this_terminal_has_been_deactivated_and_is_no_longer_accessible)
                )
        )
    }

    private fun showDialog() {
        val errorDialog = ErrorDialog(this@LoginActivity)
        errorDialog.show()
    }
}