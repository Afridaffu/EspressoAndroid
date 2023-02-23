package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityTransactionDetailsBinding

class TransactionDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionDetailsBinding
    private var txnType = "Sale Order"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initField()
    }

    private fun initField() {

        if (txnType == "Sale Order") {
            binding.llSaleOrderData.visibility = View.VISIBLE
            binding.tvReason.visibility = View.GONE
            binding.llOriginalInfo.visibility = View.GONE
            binding.llActivityLog.visibility = View.VISIBLE
            binding.llServicePhone.visibility = View.GONE

        } else {
            binding.tvReason.visibility = View.VISIBLE
            binding.llSaleOrderData.visibility = View.GONE
            binding.llOriginalInfo.visibility = View.VISIBLE
            binding.llActivityLog.visibility = View.GONE
            binding.llServicePhone.visibility = View.VISIBLE
            binding.tvCustomerInfoHeading.text = "Merchant Information"
        }

        binding.ivBack.setOnClickListener { onBackPressed() }
        binding.ivRefund.setOnClickListener {
            startActivity(Intent(this, RefundTransactionActivity::class.java))
        }
    }
}