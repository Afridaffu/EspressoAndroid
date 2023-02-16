package com.coyni.pos.app.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.coyni.pos.app.R
import com.coyni.pos.app.databinding.ActivityTransactionListBinding

class TransactionListActivity : AppCompatActivity() {

    lateinit var binding : ActivityTransactionListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        initFields()
    }

    private fun initFields(){
        binding.backBtn.setOnClickListener {
        }
    }
}