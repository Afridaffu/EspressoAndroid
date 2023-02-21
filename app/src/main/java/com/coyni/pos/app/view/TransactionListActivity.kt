package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.coyni.pos.app.R
import com.coyni.pos.app.adapter.RecentTransactionsListAdapter
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.baseclass.OnItemClickListener
import com.coyni.pos.app.databinding.ActivityTransactionHistoryBinding
import com.coyni.pos.app.dialog.TransactionFilterDialog
import com.coyni.pos.app.model.ListItem
import com.coyni.pos.app.model.TransactionFilter.TransactionListRequest
import com.coyni.pos.app.utils.Utils
import java.util.*

class TransactionListActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionHistoryBinding
    private var adapter: RecentTransactionsListAdapter? = null
    private var request: TransactionListRequest? = null

    private val globalData: List<ListItem> = ArrayList<ListItem>()
    private val transactionType = ArrayList<Int>()
    private val transactionSubType = ArrayList<Int>()
    private val txnStatus = ArrayList<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        filterDialogCalls()

    }

    private fun initView() {

        binding.ivBack.setOnClickListener { onBackPressed(); }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        adapter = RecentTransactionsListAdapter(applicationContext)
        binding.recyclerView.adapter = adapter


        binding.recentTV.setOnClickListener {
            binding.llRecentTxn.visibility = View.GONE
            binding.SearchLL.visibility = View.VISIBLE

            binding.listRecyclerRV.layoutManager = LinearLayoutManager(this)
            binding.listRecyclerRV.itemAnimator = DefaultItemAnimator()
            binding.listRecyclerRV.adapter = adapter

            adapter?.setOnItemClickListener(object : OnItemClickListener {

                override fun onItemClick(position: Int?, value: Any?) {

                    startActivity(
                        Intent(
                            applicationContext,
                            TransactionDetailsActivity::class.java
                        )
                    )

                }
                override fun onChildClicked(s: String?) {
                    TODO("Not yet implemented")
                }
            })

        }
    }

    private fun filterDialogCalls() {
        binding.ivFilterIcon.setOnClickListener { view ->
            filterDialog()
        }
    }

    private fun filterDialog() {
        try {
            val filterDialog = TransactionFilterDialog(this@TransactionListActivity)
            filterDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onFilterDialogListenerCall(action: String, value: Any) {
        when (action) {
            Utils.applyFilter -> {
                dismissDialog()
//                globalData.clear
                request = value as TransactionListRequest
                if (request!!.transactionType == null
                    || request!!.transactionType?.size === 0
                ) {
                    request!!.isManualUpdate = true
                }
                if (request != null && request!!.isFilters) {
                    binding.ivFilterIcon.setImageResource(R.drawable.ic_filter_icon)
                } else {
                    binding.ivFilterIcon.setImageResource(R.drawable.ic_filter_icon)
                }
            }
            Utils.resetFilter -> {
                request = null
                loadData()
                dismissDialog()
            }
        }
    }



    private fun loadData() {
        TODO("Not yet implemented")
    }

    private fun getDefaultTransactionTypes(): ArrayList<Int>? {
        val transactionType = ArrayList<Int>()
        transactionType.add(Utils.filter_saleorder)
        transactionType.add(Utils.filter_Refund)
        return transactionType
    }

}
