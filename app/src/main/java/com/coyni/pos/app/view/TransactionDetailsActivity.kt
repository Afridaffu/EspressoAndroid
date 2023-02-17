package com.coyni.pos.app.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityTransactionDetailsBinding
import com.coyni.pos.app.databinding.RefundTransactionDetailsBinding
import com.coyni.pos.app.utils.Utils

class TransactionDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initField()
    }

    private fun initField() {

    }
}