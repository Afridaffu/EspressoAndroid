package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.coyni.pos.app.R
import com.coyni.pos.app.adapter.RecentTransactionsListAdapter
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.baseclass.OnItemClickListener
import com.coyni.pos.app.databinding.ActivityTransactionHistoryBinding
import com.coyni.pos.app.dialog.OnDialogClickListener
import com.coyni.pos.app.dialog.TransactionFilterDialog
import com.coyni.pos.app.model.BatchAmount.BatchAmountRequest
import com.coyni.pos.app.model.ListItem
import com.coyni.pos.app.model.TransactionFilter.TransactionItem
import com.coyni.pos.app.model.TransactionFilter.TransactionListReq
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.viewmodel.BatchAmountViewModel
import com.coyni.pos.app.viewmodel.TransactionsViewModel
import java.util.*

class TransactionListActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionHistoryBinding
    private var adapter: RecentTransactionsListAdapter? = null
    private var request: TransactionListReq? = null

    private val globalData: List<ListItem> = ArrayList<ListItem>()
    private val transactionType = ArrayList<Int>()
    private val transactionSubType = ArrayList<Int>()
    private val txnStatus = ArrayList<Int>()


    private lateinit var myApplication: MyApplication
    private var empRole: String? = ""
    private var transactionViewModel: TransactionsViewModel? = null
    private var batchAmountViewModel: BatchAmountViewModel? = null
    private val txnRefresh: SwipeRefreshLayout? = null
    private var currentPage = 0
    private var total: Int = 0
    private var strStartAmount = ""
    private var strEndAmount: String? = ""
    private var strFromDate: String? = ""
    private var strToDate: String? = ""
    var transactions: MutableList<TransactionItem> = ArrayList<TransactionItem>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myApplication = applicationContext as MyApplication
        initView()
        loadData()
        filterDialogCalls()
        initObservers()
    }


    private fun initView() {

        transactionViewModel =
            ViewModelProvider(this@TransactionListActivity).get(TransactionsViewModel::class.java)
        batchAmountViewModel =
            ViewModelProvider(this@TransactionListActivity).get(BatchAmountViewModel::class.java)

//        txnRefresh?.setColorSchemeColors(resources.getColor(R.color.primary_green, null))

        if (myApplication.mCurrentUserData.loginData!!.terminalName != null || myApplication.mCurrentUserData.loginData!!.terminalId != null) {
            binding.terminalNameTV.setText(myApplication.mCurrentUserData.loginData!!.terminalName)
            binding.terminalID.setText((myApplication.mCurrentUserData.loginData!!.terminalId))
        }

        binding.ivBack.setOnClickListener {
//            onBackPressed()
            finish()
        }

        // Employee role changes
        empRole = myApplication.mCurrentUserData.validateResponseData?.empRole

        if (empRole.equals(Utils.EMPROLE)) {
            binding.llRecentTxn.visibility = View.VISIBLE
            binding.SearchLL.visibility = View.GONE
        } else {
            binding.llRecentTxn.visibility = View.GONE
            binding.SearchLL.visibility = View.VISIBLE
            batchAPI()
        }
        binding.transactionsNSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                Log.e(
                    "scrollY",
                    scrollY.toString() + "  " + v.getChildAt(0).measuredHeight + " " + v.measuredHeight
                )
                try {
                    Log.e("total abcd", total.toString() + "")
                    Log.e("currentPage acbd", currentPage.toString() + "")
                    if (total - 1 > currentPage) {
                        println("message")
                        binding.progressBarLoadMore.setVisibility(View.VISIBLE)
                        binding.loadMoreTV.setVisibility(View.VISIBLE)
                        currentPage = currentPage + 1
                        Log.e("CurrentPage", currentPage.toString() + "")
                        val transactionListRequest = TransactionListReq()
                        transactionListRequest.params.pageNo = currentPage.toString()
                        transactionListRequest.params.pageSize =
                            java.lang.String.valueOf(Utils.pageSize)
                        transactionListRequest.requestToken =
                            myApplication.mCurrentUserData.validateResponseData?.token
                        transactionsAPI(transactionListRequest)
                        binding.noMoreTransactions.setVisibility(View.GONE)
                    } else {
                        println("value")
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })


    }

    private fun filterDialogCalls() {
        binding.ivFilterIcon.setOnClickListener { view ->
            filterDialog()
        }
    }

    private fun filterDialog() {
        val filterDialog = TransactionFilterDialog(this@TransactionListActivity)
        filterDialog!!.show()

        filterDialog.setOnDialogClickListener(object : OnDialogClickListener {
            override fun onDialogClicked(action: String?, value: Any?) {
                when (action) {
                    Utils.applyFilter -> {
                        dismissDialog()
                        request = value as TransactionListReq
                        if (request?.txnTypes?.txnType == null)
                            if (request != null) {
                                binding.ivFilterIcon.setImageResource(R.drawable.ic_filter_icon)
                            } else {
                                binding.ivFilterIcon.setImageResource(R.drawable.ic_filter_icon)
                            }
                        request!!.requestToken = myApplication.mCurrentUserData.validateResponseData!!.token
                        transactionsAPI(request!!)
                    }
                    Utils.resetFilter -> {

//                        filterIV.setImageResource(R.drawable.ic_filtericon);
                        request = null
                        loadData()
                        dismissDialog()
                    }
                }

            }
        })

        filterDialog!!.setOnDismissListener { dialogInterface -> }
    }

    private fun loadData() {
        transactionType.clear();
        transactionSubType.clear();
        txnStatus.clear();
        currentPage = 0;
        strFromDate = "";
        strToDate = "";
        strStartAmount = "";
        strEndAmount = "";
        binding.ivFilterIcon.setImageDrawable(getDrawable(R.drawable.ic_filter_icon));

        var transactionListRequest = TransactionListReq();
        transactionListRequest.requestToken =
            myApplication.mCurrentUserData.validateResponseData?.token
        transactionsAPI(transactionListRequest);
    }

    private fun getDefaultTransactionTypes(): ArrayList<Int> {
        val transactionType = ArrayList<Int>()
        transactionType.add(Utils.filter_saleorder)
        transactionType.add(Utils.filter_Refund)
        return transactionType
    }

    private fun transactionsAPI(transactionListRequest: TransactionListReq) {
        try {
            transactionViewModel?.allTransactionsList(transactionListRequest)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }

    fun initObservers() {

        transactionViewModel?.transactionResponse?.observe(this@TransactionListActivity) { recentTransactionResponse ->
            try {
                if (recentTransactionResponse != null) {
                    if (recentTransactionResponse.status == Utils.SUCCESS) {
                        binding.progressBarLoadMore.setVisibility(View.GONE)
                        binding.loadMoreTV.setVisibility(View.GONE)
                        myApplication?.mCurrentUserData?.transactionResponse =
                            recentTransactionResponse.data
                        total = recentTransactionResponse.data?.totalPages!!
                        transactions.addAll(recentTransactionResponse.data!!.items!!)
                        if (transactions.size > 0) {
                            binding.noTransactions.visibility = GONE
                            if (currentPage > 0) {
                                val myPos: Int =
                                    transactions.size - recentTransactionResponse.data!!.items!!.size
                                binding.recyclerView.scrollToPosition(myPos)
                                binding.noMoreTransactions.setVisibility(View.VISIBLE)
                            } else {
                                binding.recyclerView.scrollToPosition(0)
                                binding.noMoreTransactions.setVisibility(View.VISIBLE)
                            }
                            prepareListData(transactions)
                        } else {
                            binding.noTransactions.visibility = VISIBLE
                        }
                    } else {
                        Utils.displayAlert(
                            recentTransactionResponse.error?.errorDescription.toString(), this, ""
                        )
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        batchAmountViewModel?.batchResponseMutableLiveData?.observe(this@TransactionListActivity) { batchResponseMutableLiveData ->
            try {
                if (batchResponseMutableLiveData != null) {
                    if (batchResponseMutableLiveData.status == Utils.SUCCESS) {
                        myApplication?.mCurrentUserData?.batchResponse =
                            batchResponseMutableLiveData.data
                        if (batchResponseMutableLiveData.data?.todayBatchAmount != null) {
                            binding.batchMoneyTV.setText(
                                Utils.convertBigDecimalUSDC(batchResponseMutableLiveData.data?.todayBatchAmount!!)
                                    .replace("CYN", "").trim()
                            )
                        } else {
                            binding.batchMoneyTV.setText("0.00")
                        }

                    } else {
//                        Utils.displayAlert(
//                            batchResponseMutableLiveData.error?.errorDescription.toString(),
//                            this,
//                            ""
//                        )
                    }
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    private fun prepareListData(items: List<TransactionItem>?) {
        if (items != null && items.isNotEmpty()) {
            adapter = RecentTransactionsListAdapter(this@TransactionListActivity, items)

            // Recent List View
            if (empRole.equals(Utils.EMPROLE)) {
                binding.recyclerView.layoutManager =
                    LinearLayoutManager(this@TransactionListActivity)
                binding.recyclerView.adapter = adapter
            } else {
                // Manager List View
                binding.listRecyclerRV.layoutManager =
                    LinearLayoutManager(this@TransactionListActivity)
                binding.listRecyclerRV.adapter = adapter

                adapter?.setOnItemClickListener(
                    object : OnItemClickListener {
                        override fun onItemClick(position: Int?, value: Any?) {
                            showTransactionDetails(items.get(position!!))
                        }

                        override fun onChildClicked(s: String?) {

                        }
                    })
            }
        }

    }

    private fun showTransactionDetails(obj: TransactionItem) {
        val i = Intent(this@TransactionListActivity, TransactionDetailsActivity::class.java)
        i.putExtra(Utils.gbxTxnId, obj.gbxTransactionId)
        i.putExtra(Utils.txnType, obj.txnTypeDn)
        i.putExtra(Utils.txnSubType, obj.txnSubTypeDn)
        i.putExtra(Utils.txnId, obj.transactionId)
        startActivity(i)
    }

    private fun batchAPI() {
        val req = BatchAmountRequest()
        req.requestToken = myApplication.mCurrentUserData.validateResponseData?.token
        req.todayDate = Utils.getCurrentDate()
        batchAmountViewModel?.getBatchAmount(req)
    }

//    override fun onBackPressed() {
//        finish()
//    }
}
