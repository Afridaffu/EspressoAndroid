package com.coyni.pos.app.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat.getSystemService
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.databinding.AddNoteBinding
import com.coyni.pos.app.utils.Utils

class AddNoteDialog(context: Context, private val reason: String) : BaseDialog(context) {
    private lateinit var dialogBinding: AddNoteBinding
    override fun getLayoutId() = R.layout.add_note

    override fun initViews() {
        dialogBinding = AddNoteBinding.bind(findViewById(R.id.root))
        dialogBinding.addNoteET.requestFocus()
//        if (!Utils.isKeyboardVisible) {
//            dialogBinding.addNoteET.requestFocus()
//            Utils.shwForcedKeypad(context.applicationContext, dialogBinding.addNoteET)
//        }
        if (reason != null || reason != "") {
            dialogBinding.addNoteET.setText(reason)
            if (reason.length > 0) {
                dialogBinding.addNoteTIL.setCounterEnabled(true)
                dialogBinding.addNoteET.setSelection(dialogBinding.addNoteET.text!!.length)
            } else {
                dialogBinding.addNoteTIL.setCounterEnabled(false)
            }
        }
        dialogBinding.cancelBtn.setOnClickListener {
            dismiss()
        }
        dialogBinding.doneBtn.setOnClickListener {
            getOnDialogClickListener()?.onDialogClicked(Utils.DONE, dialogBinding.addNoteET.text.toString())
            dismiss()
        }

        dialogBinding.addNoteET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length == 0 || charSequence.toString() == "") {
                    dialogBinding.addNoteTIL.setCounterEnabled(false)
                } else {
                    dialogBinding.addNoteTIL.setCounterEnabled(true)
                }
                if (dialogBinding.addNoteET.text.toString().contains("  ")) {
                    dialogBinding.addNoteET.setText(
                        dialogBinding.addNoteET.text.toString().replace("  ", " ")
                    )
                    dialogBinding.addNoteET.setSelection(dialogBinding.addNoteET.text!!.length)
                }
            }

            override fun afterTextChanged(editable: Editable) {
                try {
                    val str: String = dialogBinding.addNoteET.getText().toString()
                    if (str.length > 0 && str.substring(0, 1) == " ") {
                        dialogBinding.addNoteET.setText("")
                        dialogBinding.addNoteET.setSelection(dialogBinding.addNoteET.getText()!!.length)
                    } else if (str.length > 0 && str.contains(".")) {
                        dialogBinding.addNoteET.setText(
                            dialogBinding.addNoteET.getText().toString()
                                .replace("\\.".toRegex(), "")
                        )
                        dialogBinding.addNoteET.setSelection(dialogBinding.addNoteET.getText()!!.length)
                    } else if (str.length > 0 && str.contains("http") || str.length > 0 && str.contains(
                            "https"
                        )
                    ) {
                        dialogBinding.addNoteET.setText("")
                        dialogBinding.addNoteET.setSelection(dialogBinding.addNoteET.getText()!!.length)
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        })
    }
}