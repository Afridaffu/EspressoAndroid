package com.coyni.pos.app.dialog

import android.content.Context
import android.os.Handler
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.databinding.ExitSaleModeLayoutBinding
import com.coyni.pos.app.utils.Utils

class ExitSaleModeDialog(context: Context) : BaseDialog(context) {
    private lateinit var dialogBinding: ExitSaleModeLayoutBinding
    override fun getLayoutId(): Int = R.layout.exit_sale_mode_layout

    override fun initViews() {
        dialogBinding = ExitSaleModeLayoutBinding.bind(findViewById(R.id.root))

        Handler().postDelayed({
            getOnDialogClickListener()?.onDialogClicked(Utils.DONE, "")
        }, 3000)

    }
}