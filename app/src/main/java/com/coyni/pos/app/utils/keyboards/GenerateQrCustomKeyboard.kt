package com.coyni.pos.app.utils.keyboards

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import com.coyni.pos.app.R
import com.coyni.pos.app.databinding.GenerateQrKeyboardBinding
import com.coyni.pos.app.utils.Utils
import java.lang.Exception

class GenerateQrCustomKeyboard : LinearLayout, View.OnClickListener {

    private val keyValues = SparseArray<String>()
    lateinit var listener: OnSuccessListener
    var enteredText: String = ""
    lateinit var binding: GenerateQrKeyboardBinding
    private lateinit var inputConnection: InputConnection

    @RequiresApi(Build.VERSION_CODES.M)
    constructor(context: Context) : super(context) {
//        context = context
        initView(context)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(context)
    }

    interface OnSuccessListener {
        fun onKeyboardClick(action: String, value: String?);
    }

    fun setOnKeyboardClickListener(listener: OnSuccessListener) {
        this.listener = listener
    }

    private fun initView(context: Context) {
//        inflate(getContext(), R.layout.custom_keyboard, this)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = GenerateQrKeyboardBinding.inflate(inflater, this, true)
        clickListners()
        setupKeyvalues();
    }

    private fun clickListners() {
        binding.keyOneTV.setOnClickListener(this)
        binding.keyTwoTV.setOnClickListener(this)
        binding.keyThreeTV.setOnClickListener(this)
        binding.keyFourTV.setOnClickListener(this)
        binding.keyFiveTV.setOnClickListener(this)
        binding.keySixTV.setOnClickListener(this)
        binding.keySevenTV.setOnClickListener(this)
        binding.keyEightTV.setOnClickListener(this)
        binding.keyNineTV.setOnClickListener(this)
        binding.keyZeroTV.setOnClickListener(this)
        binding.keyDotTV.setOnClickListener(this)
        binding.keyActionCV.setOnClickListener(OnClickListener {
            if (listener != null) {
                listener.onKeyboardClick(Utils.BUTTON_CLICK, "")
            }
        })
        binding.keyBackLL.setOnClickListener(View.OnClickListener {
            val charSet = inputConnection.getSelectedText(0)
            try {
                inputConnection.deleteSurroundingText(1, 0)
                if (enteredText.length > 1) {
                    enteredText = enteredText.substring(0, enteredText.length - 1)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        })
    }

    private fun setupKeyvalues() {
        keyValues.put(R.id.keyZeroTV, "0")
        keyValues.put(R.id.keyOneTV, "1")
        keyValues.put(R.id.keyTwoTV, "2")
        keyValues.put(R.id.keyThreeTV, "3")
        keyValues.put(R.id.keyFourTV, "4")
        keyValues.put(R.id.keyFiveTV, "5")
        keyValues.put(R.id.keySixTV, "6")
        keyValues.put(R.id.keySevenTV, "7")
        keyValues.put(R.id.keyEightTV, "8")
        keyValues.put(R.id.keyNineTV, "9")
        keyValues.put(R.id.keyDotTV, ".")
        keyValues.put(R.id.keyActionCV, "")
    }

    override fun onClick(v: View?) {
        if (inputConnection == null) {
            val selectedText: CharSequence = inputConnection.getSelectedText(0)
            if (TextUtils.isEmpty(selectedText)) {
                inputConnection.deleteSurroundingText(1, 0)
            } else {
                inputConnection.commitText("", 1)
            }
        }
        if (enteredText.length < 8) {
            val value: String = keyValues.get(v!!.id)
            if ((enteredText == "" || enteredText.contains(".")) && value == ".") {
            } else {
                println(enteredText)
                enteredText = enteredText + value
                inputConnection.commitText(value, 1)
            }
        }
    }

    fun setInputConnection(ic: InputConnection) {
        inputConnection = ic
    }

    fun setKeyAction(actionName: String?, context: Context?) {
        binding.keyActionTV.text = actionName;
    }


    fun setScreenName(screenName: String) {
//        strScreen = screenName
    }

    fun enableButton() {
        binding.keyActionCV.setCardBackgroundColor(resources.getColor(R.color.primary_green))
        binding.keyActionCV.setEnabled(true)
    }

    fun disableButton() {
        binding.keyActionCV.setCardBackgroundColor(resources.getColor(R.color.inactive_green))
        binding.keyActionCV.setEnabled(false)
    }

    fun clearData() {
        enteredText = ""
    }

    fun setText(strText: String) {
        enteredText = strText
    }

    @JvmName("setEnteredText1")
    fun setEnteredText(text: String) {
        enteredText = text.trim { it <= ' ' }
    }
}

