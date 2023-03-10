package com.coyni.pos.app.fragments

import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseFragment
import com.coyni.pos.app.databinding.FragmentGenarateQrBinding
import com.coyni.pos.app.model.generate_qr.GenerateQrRequest
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.utils.keyboards.GenerateQrCustomKeyboard
import com.coyni.pos.app.viewmodel.GenerateQrViewModel

class GenenrateQrFragment : BaseFragment(), TextWatcher {
    lateinit var binding: FragmentGenarateQrBinding
    var fontSize: Float = 0.0f;
    private var isPayClickable: Boolean = false
    lateinit var generateQrViewModel: GenerateQrViewModel
    var myApplication: MyApplication? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate the layout and bind to the _binding
        binding = FragmentGenarateQrBinding.inflate(layoutInflater, container, false)

        inItFields()
        inItObservers()
        return binding.root
    }

    private fun inItFields() {
        generateQrViewModel = ViewModelProvider(this).get(GenerateQrViewModel::class.java)
        myApplication = requireActivity().application as MyApplication
        fontSize = binding.merchantAmountET.textSize
        initKeyboard()
        binding.merchantAmountET.showSoftInputOnFocus = false
        binding.merchantAmountET.textDirection = View.TEXT_DIRECTION_RTL
        binding.merchantAmountET.addTextChangedListener(this)
        binding.merchantAmountET.setAccessibilityDelegate(object : View.AccessibilityDelegate() {
            override fun sendAccessibilityEvent(host: View, eventType: Int) {
                super.sendAccessibilityEvent(host, eventType)
                if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                    binding.merchantAmountET.setSelection(
                        binding.merchantAmountET.text.toString().length
                    )
                }
            }
        })
        binding.closeIV.setOnClickListener {
            binding.merchantAmountET.hint = "0.00"
            binding.merchantAmountET.setText("")
            disableButtons(true)
        }
    }

    private fun inItObservers() {
        generateQrViewModel.generateQrResponse.observe(requireActivity()) { generateQrResponse ->
            try {
                if (generateQrResponse != null) {
                    if (generateQrResponse.status == Utils.SUCCESS) {
                        myApplication?.mCurrentUserData?.generateQrResponseData =
                            generateQrResponse.data
                        mActivity.fragmentNavigation(
                            Utils.MERCHANT_QR,
                            Utils.convertTwoDecimal(
                                binding.merchantAmountET.text.toString()
                            )
                        )
                    } else {
                        Utils.displayAlert(
                            generateQrResponse.error?.errorDescription.toString(),
                            requireContext(),
                            ""
                        )
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (start == 0 && after == 0) {
            binding.merchantAmountET.textSize = Utils.pixelsToSp(requireContext(), fontSize)
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s != null) {
        }
    }

    override fun afterTextChanged(editable: Editable) {
        if (editable === binding.merchantAmountET.editableText) {
            try {
                if (editable.length > 0 && editable.toString() != "." && editable.toString() != ".00") {
                    binding.merchantAmountET.hint = ""
                    if (editable.length > 8) {
                        binding.merchantAmountET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33F)
                    } else if (editable.length == 5 || editable.length == 6) {
                        binding.merchantAmountET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 42f)
                    } else if (editable.length == 7 || editable.length == 8) {
                        binding.merchantAmountET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32f)
                    } else if (editable.length >= 9) {
                        binding.merchantAmountET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
                    } else if (editable.length <= 4) {
                        binding.merchantAmountET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 53f)
                        binding.merchantAmountET.textSize =
                            Utils.pixelsToSp(requireContext(), fontSize)
                    }
                    if (Utils.doubleParsing(editable.toString().replace(",", "")) > 0) {
                        disableButtons(false)
                    } else {
                        disableButtons(true)
                    }
                    binding.merchantAmountET.setSelection(binding.merchantAmountET.text.length)
                    binding.merchantAmountET.textDirection = View.TEXT_DIRECTION_LTR
                } else if (editable.toString() == ".") {
                    binding.merchantAmountET.setText("")
                    disableButtons(true)
                } else if (editable.length == 0) {
                    binding.merchantAmountET.hint = "0.00"
                    binding.merchantAmountET.textDirection = View.TEXT_DIRECTION_RTL
                    disableButtons(true)
                    binding.bottomKeyPad.clearData()
                    binding.closeIV.visibility = View.GONE
                } else {
                    binding.merchantAmountET.setText("")
                    disableButtons(true)
                    binding.bottomKeyPad.clearData()
                }
            } catch (_: Exception) {
            }
        }
    }


    private fun initKeyboard() {
        val ic: InputConnection =
            binding.merchantAmountET.onCreateInputConnection(EditorInfo())
        binding.bottomKeyPad.setInputConnection(ic)
        binding.bottomKeyPad.setOnKeyboardClickListener(object :
            GenerateQrCustomKeyboard.OnSuccessListener {
            override fun onKeyboardClick(action: String, value: String?) {
                if (action == Utils.BUTTON_CLICK) {
                    convertDecimal()
                    if (isPayClickable) {
                        generateQR()
                    }
                }
            }
        })
    }

    fun convertDecimal() {
        try {
            val FilterArray = arrayOfNulls<InputFilter>(1)
            FilterArray[0] = InputFilter.LengthFilter(getString(R.string.maxlendecimal).toInt())
            binding.merchantAmountET.setFilters(FilterArray)
            USFormat(binding.merchantAmountET)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    private fun USFormat(etAmount: EditText): String? {
        var strAmount = ""
        var strReturn = ""
        try {
            strAmount = Utils.convertTwoDecimal(etAmount.text.toString().trim { it <= ' ' }
                .replace(",", ""))
            etAmount.removeTextChangedListener(this)
            etAmount.setText(Utils.USNumberFormat(Utils.doubleParsing(strAmount)))
            etAmount.addTextChangedListener(this)
            etAmount.setSelection(etAmount.text.toString().length)
            strReturn = Utils.USNumberFormat(Utils.doubleParsing(strAmount))
            changeTextSize(strReturn)
            setDefaultLength()
            binding.bottomKeyPad.setEnteredText(
                binding.merchantAmountET.text.toString().trim { it <= ' ' })
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
        return strReturn
    }

    private fun changeTextSize(editable: String) {
        try {
            val FilterArray = arrayOfNulls<InputFilter>(1)
            if (editable.length > 12) {
                FilterArray[0] = InputFilter.LengthFilter(getString(R.string.maxlendecimal).toInt())
                binding.merchantAmountET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 28f)
            } else if (editable.length > 8) {
                FilterArray[0] = InputFilter.LengthFilter(getString(R.string.maxlendecimal).toInt())
                binding.merchantAmountET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33f)
            } else if (editable.length > 5) {
                FilterArray[0] = InputFilter.LengthFilter(getString(R.string.maxlendecimal).toInt())
                binding.merchantAmountET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43f)

            } else {
            }
            binding.merchantAmountET.setFilters(FilterArray)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    private fun setDefaultLength() {
        try {
            val FilterArray = arrayOfNulls<InputFilter>(1)
            FilterArray[0] = InputFilter.LengthFilter(getString(R.string.maxlength).toInt())
            binding.merchantAmountET.setFilters(FilterArray)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    private fun generateQR() {
        val generateQrRequest = GenerateQrRequest()
        generateQrRequest.amount =
            Utils.doubleParsing(binding.merchantAmountET.text.toString())
        generateQrRequest.isQrCodeEnable = true
        generateQrRequest.requestToken =
            myApplication?.mCurrentUserData?.validateResponseData?.token
        generateQrViewModel.generateQrRequest(generateQrRequest)
    }

    private fun disableButtons(value: Boolean) {
        try {
            if (value) {
                binding.bottomKeyPad.disableButton()
                isPayClickable = false
                binding.closeIV.visibility = View.GONE
            } else {
                binding.bottomKeyPad.enableButton()
                isPayClickable = true
                binding.closeIV.visibility = View.VISIBLE
            }
        } catch (_: Exception) {
        }
    }
}