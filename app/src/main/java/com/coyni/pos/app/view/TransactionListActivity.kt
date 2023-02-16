package com.coyni.pos.app.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.coyni.pos.app.R
import com.coyni.pos.app.databinding.ActivityTransactionListBinding
import com.coyni.pos.app.fragments.TransactionList_filter_frag
import com.coyni.pos.app.fragments.dashboard_frag

class TransactionListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTransactionListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)
        initFields()
    }

    private fun initFields(){
        showfrag(TransactionList_filter_frag())
    }
    private fun showfrag(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.transactionlistFrameFL, fragment)
        transaction.commit()
    }
}