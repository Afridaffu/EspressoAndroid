package com.coyni.pos.app.dialog

import android.content.Context
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.databinding.DiscardSaleBinding
import com.coyni.pos.app.fragments.MerchantQrFragment
import com.coyni.pos.app.utils.Utils

class DiscardSaleDialog(context: Context) : BaseDialog(context) {
    private lateinit var dialogBinding: DiscardSaleBinding
    override fun getLayoutId() = R.layout.discard_sale

    override fun initViews() {
        dialogBinding = DiscardSaleBinding.bind(findViewById(R.id.root))

        dialogBinding.discardSaleLL.setOnClickListener{
            getOnDialogClickListener()?.onDialogClicked(Utils.DISCARD,"")
        }
        dialogBinding.continueLL.setOnClickListener{
            dismiss()
        }
    }
}