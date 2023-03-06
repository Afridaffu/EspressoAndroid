package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.coyni.pos.app.R
import com.coyni.pos.app.adapter.RecentTransactionsListAdapter
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.baseclass.OnItemClickListener
import com.coyni.pos.app.databinding.ActivityTransactionHistoryBinding
import com.coyni.pos.app.dialog.TransactionFilterDialog
import com.coyni.pos.app.model.ListItem
import com.coyni.pos.app.model.TransactionFilter.TransactionResponse
import com.coyni.pos.app.model.TransactionFilter.TransactionFilterRequest
import com.coyni.pos.app.model.TransactionFilter.TransactionListReq
import com.coyni.pos.app.model.refund.RefundVerifyRequest
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.viewmodel.TransactionsViewModel
import java.util.*

class TransactionListActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionHistoryBinding
    private var adapter: RecentTransactionsListAdapter? = null
    private var request: TransactionFilterRequest? = null

    private val globalData: List<ListItem> = ArrayList<ListItem>()
    private val transactionType = ArrayList<Int>()
    private val transactionSubType = ArrayList<Int>()
    private val txnStatus = ArrayList<Int>()
    var recentTxns: TransactionResponse? = null
    private lateinit var myApplication: MyApplication
    private var empRole: String? = ""
    private var transactionViewModel: TransactionsViewModel? = null
    private val txnRefresh: SwipeRefreshLayout? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myApplication = applicationContext as MyApplication
        initView()
        filterDialogCalls()
        initObservers()
    }


    private fun initView() {

        transactionViewModel =
            ViewModelProvider(this@TransactionListActivity).get(TransactionsViewModel::class.java)

        txnRefresh?.setColorSchemeColors(resources.getColor(R.color.primary_green, null))

        if (myApplication.mCurrentUserData.loginData!!.terminalName != null || myApplication.mCurrentUserData.loginData!!.terminalId != null) {
            binding.terminalNameTV.setText(myApplication.mCurrentUserData.loginData!!.terminalName)
            binding.terminalID.setText((myApplication.mCurrentUserData.loginData!!.terminalId))
        }

        binding.ivBack.setOnClickListener { onBackPressed(); }

        empRole = myApplication.mCurrentUserData.validateResponseData?.empRole

        if (empRole.equals(Utils.EMPROLE)) {
            binding.llRecentTxn.visibility = View.VISIBLE
            binding.SearchLL.visibility = View.GONE
        } else {
            binding.llRecentTxn.visibility = View.GONE
            binding.SearchLL.visibility = View.VISIBLE
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.itemAnimator = DefaultItemAnimator()
        adapter = RecentTransactionsListAdapter(applicationContext, recentTxns!!)
        binding.recyclerView.adapter = adapter

        binding.listRecyclerRV.layoutManager = LinearLayoutManager(this)
        binding.listRecyclerRV.itemAnimator = DefaultItemAnimator()
        binding.listRecyclerRV.adapter = adapter

        adapter?.setOnItemClickListener(
            object : OnItemClickListener {

                override fun onItemClick(position: Int?, value: Any?) {

                    startActivity(Intent(applicationContext, TransactionDetailsActivity::class.java)

                    )

                }

                override fun onChildClicked(s: String?) {
                    TODO("Not yet implemented")
                }
            })
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
                request = value as TransactionFilterRequest
//                if (request!!.transactionType == null
//                    || request!!.transactionType?.size === 0
//                ) {
//                    request!!.isManualUpdate = true
//                }
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

    private fun getTransactions(transactionListRequest: TransactionListReq) {
        showProgressDialog()
        transactionViewModel!!.allTransactionsList(transactionListRequest)
    }

    fun initObservers() {

        transactionViewModel?.transactionResponse?.observe(this@TransactionListActivity)
        { recentTransactionResponse ->
            try {
                if (recentTransactionResponse != null) {
                    if (recentTransactionResponse.status == Utils.SUCCESS) {

                    } else {
                        Utils.displayAlertNew(
                            recentTransactionResponse.error?.errorDescription.toString(),
                            this,
                            ""
                        )
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun trasnsactionListAPI() {
        val listVerifyRequest = TransactionListReq()
        listVerifyRequest.fromAmount = 10
        listVerifyRequest.toAmount = 100
        listVerifyRequest.requestToken = myApplication.mCurrentUserData.validateResponseData?.token
        listVerifyRequest.status = "4"
        listVerifyRequest.toDate = "21/02/2023"
        listVerifyRequest.data?.txnType = 10
        listVerifyRequest.data?.txnSubTypes = 13
        transactionViewModel!!.allTransactionsList(listVerifyRequest)

    }

    override fun onResume() {
        super.onResume()
        trasnsactionListAPI()
    }

}
