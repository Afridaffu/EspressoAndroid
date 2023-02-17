package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.coyni.pos.app.adapter.RecentTransactionsListAdapter
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.baseclass.OnItemClickListener
import com.coyni.pos.app.databinding.ActivityTransactionHistoryBinding

class TransactionListActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionHistoryBinding
    private var adapter: RecentTransactionsListAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }

    private fun initView() {

        binding.ivBack.setOnClickListener { onBackPressed(); }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        adapter = RecentTransactionsListAdapter(applicationContext)
        binding.recyclerView.adapter = adapter

        adapter?.setOnItemClickListener(object : OnItemClickListener {

            override fun onItemClick(position: Int?, value: Any?) {

                startActivity(Intent(applicationContext, TerminalDeactivatedActivity::class.java))

            }
        })

        binding.recentTV.setOnClickListener {
            binding.llRecentTxn.visibility = View.GONE
            binding.SearchLL.visibility = View.VISIBLE

            binding.listRecyclerRV.layoutManager = LinearLayoutManager(this)
            binding.listRecyclerRV.itemAnimator = DefaultItemAnimator()
            adapter = RecentTransactionsListAdapter(applicationContext)
            binding.listRecyclerRV.adapter = adapter

        }
    }
}
