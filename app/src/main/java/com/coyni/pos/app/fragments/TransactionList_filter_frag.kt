package com.coyni.pos.app.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseFragment
import com.coyni.pos.app.databinding.FragmentTransactionListFilterFragBinding
import com.coyni.pos.app.view.TransactionDetailsActivity

class TransactionList_filter_frag : BaseFragment() {

    private lateinit var binding: FragmentTransactionListFilterFragBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransactionListFilterFragBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivFilterIcon.setOnClickListener {

        }

        binding.batchMoneyTV.setOnClickListener {
            startActivity(Intent(context, TransactionDetailsActivity::class.java))
        }
    }
}