package com.coyni.pos.app.view

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityLoginBinding
import com.coyni.pos.app.dialog.ErrorDialog
import com.coyni.pos.app.utils.Utils

class LoginActivity : BaseActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var isId = false
    private var isPassword = false
    private var isIconEnable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        focusListeners()
        textWatchers()
    }

    private fun initView() {

        binding.tvButton.isEnabled = false
        binding.passwordTIL.setBoxStrokeColorStateList(Utils.getNormalColorState(this))
//        binding.tidET.requestFocus()

        binding.passwordTIL.setEndIconOnClickListener {
            if (!isIconEnable) {
                isIconEnable = true
                binding.passwordET.setSelection(binding.passwordET.text.toString().length)
                binding.passwordTIL.endIconDrawable =
                    AppCompatResources.getDrawable(this, R.drawable.ic_eyeopen)
                binding.passwordET.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                isIconEnable = false
                binding.passwordET.setSelection(binding.passwordET.text.toString().length)
                binding.passwordTIL.endIconDrawable =
                    AppCompatResources.getDrawable(this, R.drawable.ic_eyeclose)
                binding.passwordET.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.tvButton.setOnClickListener {
            showDialog()
//            startActivity(Intent(applicationContext, DashboardActivity::class.java))
        }
    }

    private fun focusListeners() {

        binding.tidET.setOnFocusChangeListener { view, b ->
            if (b) {
                if (binding.tidET.text.toString().isNotEmpty())
                    binding.tidET.setSelection(binding.tidET.text.toString().length)
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

        binding.passwordET.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                if (binding.passwordET.text.toString().isNotEmpty())
                    binding.passwordET.setSelection(binding.passwordET.text.toString().length)
//                Utils.upperHintColor(binding.passwordTIL, R.color.primary_green)
                binding.passwordErrorLL.visibility = View.GONE
                binding.passwordTIL.setBoxStrokeColorStateList(Utils.getFocusedColorState(this))
                binding.passwordET.hint =
                    "\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605\u2605"

            } else {
                if (binding.passwordET.text.toString().length in 1..7) {
                    binding.passwordErrorTV.text = "Please enter a valid Password"
//                    Utils.upperHintColor(binding.passwordTIL, R.color.error)
                    binding.passwordErrorLL.visibility = View.VISIBLE
                    binding.passwordTIL.setBoxStrokeColorStateList(Utils.getErrorColorState(this))
                } else {
//                    Utils.upperHintColor(binding.passwordTIL, R.color.light_gray)
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

    private fun showDialog() {
        val errorDialog = ErrorDialog(this@LoginActivity)
        errorDialog.show()
    }
}