package com.coyni.pos.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity

class RefundTransactionActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_refund_transaction)
    }
}