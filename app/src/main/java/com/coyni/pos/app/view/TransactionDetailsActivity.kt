package com.coyni.pos.app.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coyni.pos.app.R
import com.coyni.pos.app.databinding.ActivityTransactionDetailsBinding
import com.coyni.pos.app.databinding.RefundTransactionDetailsBinding
import com.coyni.pos.app.utils.Utils

class TransactionDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTransactionDetailsBinding
    private lateinit var refundBinding: RefundTransactionDetailsBinding

    var strGbxTxnIdType = ""
    var txnType = 0
    var txnSubType: Int? = null
    var txnId = ""

    //Types
    private val saleOrder = "sale order"
    private val refund = "refund"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_details)
    }
}