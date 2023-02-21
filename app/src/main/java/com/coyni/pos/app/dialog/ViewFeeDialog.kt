package com.coyni.pos.app.dialog

import android.content.Context
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.databinding.RefundPreviewBinding
import com.coyni.pos.app.databinding.ViewFeesDialogBinding

class ViewFeeDialog(context: Context) : BaseDialog(context) {
    private lateinit var dialogBinding: ViewFeesDialogBinding
    override fun getLayoutId() = R.layout.view_fees_dialog

    override fun initViews() {
        dialogBinding = ViewFeesDialogBinding.bind(findViewById(R.id.root))
    }
}