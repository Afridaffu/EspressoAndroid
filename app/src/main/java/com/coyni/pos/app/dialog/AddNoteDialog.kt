package com.coyni.pos.app.dialog

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.databinding.AddNoteBinding
import com.coyni.pos.app.utils.Utils

class AddNoteDialog(context: Context, private val reason: String) : BaseDialog(context),
    TextWatcher {
    private lateinit var dialogBinding: AddNoteBinding
    override fun getLayoutId() = R.layout.add_note

    override fun initViews() {
        dialogBinding = AddNoteBinding.bind(findViewById(R.id.root))
        if (reason != null) {
            dialogBinding.addNoteET.setText(reason)
        }
        dialogBinding.cancelBtn.setOnClickListener {
            dismiss()
        }
        dialogBinding.doneBtn.setOnClickListener {
            getOnDialogClickListener()?.onDialogClicked(
                Utils.DONE,
                dialogBinding.addNoteET.text.toString()
            )
            dismiss()

        }
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
    override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        dialogBinding.addNoteTIL.isCounterEnabled = charSequence.length != 0
    }

    override fun afterTextChanged(editable: Editable) {
        try {
            val str: String = dialogBinding.addNoteET.text.toString()
            if (str.length > 0 && str.substring(0, 1) == " ") {
                dialogBinding.addNoteET.setText("")
                dialogBinding.addNoteET.setSelection(dialogBinding.addNoteET.text!!.length)
            } else if (str.length > 0 && str.contains(".")) {
                dialogBinding.addNoteET.setText(
                    dialogBinding.addNoteET.text.toString().replace("\\.".toRegex(), "")
                )
                dialogBinding.addNoteET.setSelection(dialogBinding.addNoteET.text!!.length)
            } else if (str.length > 0 && str.contains("http") || str.length > 0 && str.contains(
                    "https"
                )
            ) {
                dialogBinding.addNoteET.setText("")
                dialogBinding.addNoteET.setSelection(dialogBinding.addNoteET.text!!.length)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}