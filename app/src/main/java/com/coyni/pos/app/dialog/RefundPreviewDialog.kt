package com.coyni.pos.app.dialog

import android.content.Context
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.databinding.RefundPreviewBinding
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.utils.keyboards.CustomKeyboard


class RefundPreviewDialog(context: Context) : BaseDialog(context) {
    private lateinit var dialogBinding: RefundPreviewBinding
    override fun getLayoutId() = R.layout.refund_preview

    override fun initViews() {
        dialogBinding = RefundPreviewBinding.bind(findViewById(R.id.root))
//        dialogBinding.slideToConfirmRL.setOnClickListener {
//            getOnDialogClickListener()?.onDialogClicked(Utils.SWIPE, "")
//        }
    }
}