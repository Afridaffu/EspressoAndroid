package com.coyni.pos.app.dialog

import android.content.Context
import android.view.WindowManager
import android.widget.LinearLayout
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.databinding.DiscardSaleBinding
import com.coyni.pos.app.databinding.TransactionFilterDialogBinding
import com.coyni.pos.app.utils.Utils

class TransactionFilterDialog(context: Context) : BaseDialog(context, 60.00) {

    private lateinit var binding: TransactionFilterDialogBinding
    override fun getLayoutId() = R.layout.transaction_filter_dialog

    override fun initViews() {
        binding = TransactionFilterDialogBinding.bind(findViewById(R.id.filterLL))

    }
}