package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityTransactionDetailsBinding

class TransactionDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initField()
    }

    private fun initField() {
        binding.RefundIV.setOnClickListener {
            val intent = Intent(this, RefundTransactionActivity::class.java)
            startActivity(intent)
        }
    }
}