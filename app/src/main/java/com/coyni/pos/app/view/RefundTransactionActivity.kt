package com.coyni.pos.app.view

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityRefundTransactionBinding
import com.coyni.pos.app.dialog.AddNoteDialog
import com.coyni.pos.app.dialog.OnDialogClickListener
import com.coyni.pos.app.dialog.RefundPreviewDialog
import com.coyni.pos.app.model.TransactionData
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.utils.keyboards.CustomKeyboard

class RefundTransactionActivity : BaseActivity(), TextWatcher {
    private lateinit var binding: ActivityRefundTransactionBinding
    val transactionData: TransactionData? = null
    private var grossAmount: Double = 0.00
    private var enteredAmount: Double = 0.00
    private var halfAmount: Double = 0.00
    private lateinit var fullAmount: String
    private lateinit var action_type: String
    private var reason: String = ""
    var isPayClickable: Boolean = false
    var isrefundClickable: Boolean = false
    var ishalfamount: Boolean = false
    var isfullamount: Boolean = false
    var fontSize: Float = 0.0f;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_refund_transaction)
        binding = ActivityRefundTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inItFields()
    }

    private fun inItFields() {
        grossAmount = 500.00
        fontSize = binding.refundAmountET.textSize
        binding.refundAmountET.showSoftInputOnFocus = false
        binding.refundAmountET.textDirection = View.TEXT_DIRECTION_RTL
        binding.refundAmountET.addTextChangedListener(this)
        binding.RefundbackIV.setOnClickListener {
            finish()
        }
//        if (!transactionData?.grossAmount?.isEmpty()!!) {
//            grossAmount = Utils.doubleParsing(
//                Utils.convertTwoDecimal(
//                    transactionData.grossAmount.replace(
//                        "CYN",
//                        ""
//                    ).trim()
//                )
//            )
//            binding.refundCurrencyTV.setText(
//                "" + Utils.convertTwoDecimal(
//                    transactionData.grossAmount.replace(
//                        "CYN",
//                        ""
//                    ).trim()
//                )
//            )
//        }
        binding.refundAmountET.setAccessibilityDelegate(object : View.AccessibilityDelegate() {
            override fun sendAccessibilityEvent(host: View, eventType: Int) {
                super.sendAccessibilityEvent(host, eventType)
                if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                    binding.refundAmountET.setSelection(binding.refundAmountET.text.toString().length)
                }
            }
        })
        binding.fullAmountTV.setOnClickListener {
            if (grossAmount != null) {
                binding.refundAmountET.setText(Utils.convertTwoDecimal(grossAmount.toString()))
                binding.refundAmountET.setSelection(binding.refundAmountET.text.length)
                binding.fullAmountTV.setBackgroundResource(R.drawable.button_bg_light_green_core)
                binding.fullAmountTV.setTextColor(getColor(R.color.primary_green))
                binding.halfAmountTV.setBackgroundResource(R.drawable.button_bg_lightgray_core)
                binding.halfAmountTV.setTextColor(getColor(R.color.dark_gray))
                isfullamount = true
                ishalfamount = false
                binding.refundCKB.setEnteredText(
                    binding.refundAmountET.text.toString().trim { it <= ' ' })
            } else {
                ishalfamount = false
                isfullamount = false
            }
        }
        binding.halfAmountTV.setOnClickListener {
            if (grossAmount != null) {
                halfAmount = grossAmount / 2;
                binding.refundAmountET.setText(Utils.convertTwoDecimal(halfAmount.toString()))
                binding.refundAmountET.setSelection(binding.refundAmountET.text.length)
                binding.halfAmountTV.setBackgroundResource(R.drawable.button_bg_light_green_core)
                binding.halfAmountTV.setTextColor(getColor(R.color.primary_green))
                binding.fullAmountTV.setBackgroundResource(R.drawable.button_bg_lightgray_core)
                binding.fullAmountTV.setTextColor(getColor(R.color.dark_gray))
                isfullamount = false
                ishalfamount = true
                binding.refundCKB.setEnteredText(
                    binding.refundAmountET.text.toString().trim { it <= ' ' })
            } else {
                ishalfamount = false
                isfullamount = false
            }
        }

        inItKeyboard()
        binding.remarksLL.setOnClickListener {
            val addNoteDialog = AddNoteDialog(this,reason)
            addNoteDialog.show()
            addNoteDialog.setOnDialogClickListener(object : OnDialogClickListener {
                override fun onDialogClicked(action: String?, value: Any?) {
                    reason = value.toString()
                    if (action == Utils.DONE) {
                        binding.eTremarks.setText(reason)
                    }
                }
            })
        }
    }

    private fun inItKeyboard() {
        val ic: InputConnection = binding.refundAmountET.onCreateInputConnection(EditorInfo())
        binding.refundCKB.setInputConnection(ic)
        binding.refundCKB.setKeyAction("Refund", this)
        binding.refundCKB.setScreenName(Utils.REFUND)
        binding.refundCKB.setOnKeybackClickListener(object :
            CustomKeyboard.OnKeyBackSuccessListener {
            override fun onKeybackClick(action: String, value: String?) {
                if (action == Utils.REFUND) {
                    clearAmountCards()
                }
            }
        })

        binding.refundCKB.setOnKeyboardClickListener(object :
            CustomKeyboard.OnSuccessListener {
            override fun onKeyboardClick(action: String, value: String?) {
                if (action == Utils.BUTTON_CLICK) {
                    if (isrefundClickable) {
                        refundPreviewDialog()
                    }
                }
            }
        })
    }

    private fun refundPreviewDialog() {
        val refundPreviewDialog = RefundPreviewDialog(this)
        refundPreviewDialog.show()
        refundPreviewDialog.setOnDialogClickListener(object :
            OnDialogClickListener {
            override fun onDialogClicked(action: String?, value: Any?) {
                if (action == Utils.SWIPE) {
                    action_type = Utils.REFUND
                    launchPinActivity()
//                                    val intent = Intent(applicationContext, PinActivity::class.java)
//                                    startActivity(intent)
                }
            }
        })
    }

    private fun launchPinActivity() {
        val refundPin = Intent(this, PinActivity::class.java)
        refundPin.putExtra(Utils.ACTION_TYPE, action_type)
        pinActivityResultLauncher.launch(refundPin)
    }

    var pinActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.getResultCode() === RESULT_OK) {
            //Call API Here
            val token: String? = result.data?.getStringExtra(Utils.TRANSACTION_TOKEN)
            refundApiCall(token)
        }
    }

    private fun refundApiCall(token: String?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        if (start == 0 && after == 0) {
            binding.refundAmountET.textSize = Utils.pixelsToSp(applicationContext, fontSize)
        }
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        if (s != null) {
        }
    }

    override fun afterTextChanged(editable: Editable) {
        if (editable === binding.refundAmountET.editableText) {
            try {
                if (editable.length > 0 && editable.toString() != "." && editable.toString() != ".00") {
                    binding.refundAmountET.hint = ""
                    if (editable.length > 8) {
                        binding.refundAmountET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 33F)
                    } else if (editable.length > 5) {
                        binding.refundAmountET.setTextSize(TypedValue.COMPLEX_UNIT_SP, 43F)
                    } else {
                        binding.refundAmountET.textSize =
                            Utils.pixelsToSp(applicationContext, fontSize)
                    }
                    if (Utils.doubleParsing(editable.toString().replace(",", "")) > 0) {
//                        disableButtons(false)
                    } else {
//                        disableButtons(true)
                    }
//                    if (validation()) {
//                        disableButtons(false)
//                    } else {
//                        disableButtons(true)
//                    }
                    binding.refundAmountET.setSelection(binding.refundAmountET.text.length)
                    binding.refundAmountET.textDirection = View.TEXT_DIRECTION_LTR
                } else if (editable.toString() == ".") {
                    binding.refundAmountET.setText("")
//                    disableButtons(true)
                } else if (editable.length == 0) {
                    binding.refundAmountET.hint = "0.00"
                    binding.refundAmountET.textDirection = View.TEXT_DIRECTION_RTL
//                    disableButtons(true)
                    binding.refundCKB.clearData()
                } else {
                    binding.refundAmountET.setText("")
//                    disableButtons(true)
                    binding.refundCKB.clearData()
                }
                enableRefund()
            } catch (_: Exception) {
            }
        }
    }

    private fun enableRefund() {
        try {
            enteredAmount =
                Utils.doubleParsing(Utils.convertBigDecimal(binding.refundAmountET.text.toString()))
            if (enteredAmount >= 0.006 && enteredAmount <= grossAmount) {
                binding.refundCKB.enableButton()
                isrefundClickable = true
            } else {
                binding.refundCKB.disableButton()
                isrefundClickable = false
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    fun clearAmountCards() {
        try {
            binding.fullAmountTV.setBackgroundResource(R.drawable.button_bg_lightgray_core)
            binding.fullAmountTV.setTextColor(getColor(R.color.dark_gray))
            binding.halfAmountTV.setBackgroundResource(R.drawable.button_bg_lightgray_core)
            binding.halfAmountTV.setTextColor(getColor(R.color.dark_gray))
            isfullamount = true
            ishalfamount = false
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
        }
    }
    //    private fun disableButtons(value: Boolean) {
//        try {
//            if (value) {
//                binding.refundCKB.disableButton()
//                isPayClickable = false
//            } else {
//                binding.refundCKB.enableButton()
//                isPayClickable = true
//            }
//        } catch (_: Exception) {
//        }
//    }
//        binding.refundAmountET.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
//            if (!hasFocus) {
//                if (binding.refundAmountET.text.toString().isNotEmpty()) {
//                    val FilterArray = arrayOfNulls<InputFilter>(1)
//                    FilterArray[0] = LengthFilter(getString(R.string.maxlendecimal).toInt())
//                    binding.refundAmountET.setFilters(FilterArray)
//                    USFormat(binding.refundAmountET)
//                }
//            } else {
//                val FilterArray = arrayOfNulls<InputFilter>(1)
//                FilterArray[0] = LengthFilter(getString(R.string.maxlength).toInt())
//                binding.refundAmountET.setFilters(FilterArray)
//            }
//        })

}