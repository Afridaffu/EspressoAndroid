package com.coyni.pos.app.dialog

import android.content.Context
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.databinding.DialogErrorBinding

class ErrorDialog(context: Context) : BaseDialog(context) {

    private lateinit var binding: DialogErrorBinding

    override fun getLayoutId() = R.layout.dialog_error

    override fun initViews() {

        binding = DialogErrorBinding.bind(findViewById(R.id.llDialog))

        binding.tvButton.setOnClickListener { dismiss() }

    }
}