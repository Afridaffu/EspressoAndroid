package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
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

class TransactionListActivity : BaseActivity(), TextWatcher {

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
    private var currentPage = 0
    private var total: Int = 0
    private var strStartAmount = ""
    private var strEndAmount: String? = ""
    private var strFromDate: String? = ""
    private var strToDate: String? = ""
    var transactions: MutableList<TransactionItem> = ArrayList<TransactionItem>()
    var isSwiped = false

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

        binding.txnRefresh.setColorSchemeColors(resources.getColor(R.color.primary_green, null))

        if (myApplication.mCurrentUserData.loginData!!.terminalName != null || myApplication.mCurrentUserData.loginData!!.terminalId != null) {
            binding.terminalNameTV.setText(myApplication.mCurrentUserData.loginData!!.terminalName)
            binding.terminalID.setText((myApplication.mCurrentUserData.loginData!!.terminalId))
        }

        binding.ivBack.setOnClickListener {
//            onBackPressed()
            finish()
        }
        binding.searchET.addTextChangedListener(this)

        // Employee role changes
        empRole = myApplication.mCurrentUserData.validateResponseData?.empRole

        if (empRole.equals(Utils.EMPROLE)) {
            binding.batchCV.visibility = GONE
            binding.searchCV.visibility = GONE
            binding.recentTxnLL.visibility = VISIBLE
            binding.txnRefresh.isEnabled = false
            binding.transactionsNSV.isEnabled = false
        } else {
            binding.batchCV.visibility = VISIBLE
            binding.searchCV.visibility = VISIBLE
            binding.recentTxnLL.visibility = GONE
            binding.txnRefresh.isEnabled = true
            binding.transactionsNSV.isEnabled = true
            batchAPI()
        }

        binding.transactionsNSV.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                try {
                    if (total - 1 > currentPage) {
                        binding.loadLL.visibility = VISIBLE
                        binding.noMoreTransactions.visibility = GONE
                        currentPage = currentPage + 1
                        val transactionListRequest = TransactionListReq()
                        transactionListRequest.params.pageNo = currentPage.toString()
                        transactionListRequest.params.pageSize =
                            java.lang.String.valueOf(Utils.pageSize)
                        transactionListRequest.requestToken =
                            myApplication.mCurrentUserData.validateResponseData?.token

                        if (request != null && request!!.isFilters) {
                            if (request!!.txnTypes != null) {
                                transactionListRequest.txnTypes = request!!.txnTypes
                            }
                            if (request!!.status != null) {
                                transactionListRequest.status = request!!.status
                            }
                            if (request!!.fromAmount.toString().trim { it <= ' ' } != "") {
                                transactionListRequest.fromAmount = (
                                        request!!.fromAmount.toString().replace(
                                            ",",
                                            ""
                                        )
                                        )
                            }
                            if (request!!.toAmount.toString().trim { it <= ' ' } != "") {
                                transactionListRequest.toAmount = (
                                        request!!.toAmount.toString().replace(
                                            ",",
                                            ""
                                        )
                                        )
                            }

                            if (request!!.fromDate != "") {
                                transactionListRequest.fromDate =
                                    Utils.exportDate(request!!.fromDate.toString(), "")

                            }
                            if (request!!.toDate != "") {
                                transactionListRequest.toDate =
                                    Utils.exportDate(
                                        request!!.toDate.toString(), ""
                                    )
                            }
                        }

                        transactionsAPI(transactionListRequest)
                        myApplication.mCurrentUserData.initializeTransactionSearch()
                        myApplication.mCurrentUserData.transactionListReq = transactionListRequest
                        binding.noMoreTransactions.setVisibility(View.GONE)
                    } else {
                        binding.noMoreTransactions.visibility = VISIBLE
                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        })

        binding.txnRefresh.setOnRefreshListener(OnRefreshListener {
            try {
                isSwiped = true
                loadData()
            } catch (ex: Exception) {
                ex.printStackTrace()
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
            val filterDialog =
                TransactionFilterDialog(this@TransactionListActivity, request, myApplication)
            filterDialog.show()

            filterDialog.setOnDialogClickListener(object : OnDialogClickListener {
                override fun onDialogClicked(action: String?, value: Any?) {
                    when (action) {
                        Utils.applyFilter -> {
                            dismissDialog()
                            request = value as TransactionListReq
                            //                        if (request?.txnTypes?.txnType == null)
                            if (request != null && request!!.isFilters == true) {
                                binding.ivFilterIcon.setImageResource(R.drawable.ic_filter_enabled)
                            } else {
                                binding.ivFilterIcon.setImageResource(R.drawable.ic_filter_icon)
                            }
                            request!!.requestToken =
                                myApplication.mCurrentUserData.validateResponseData!!.token
                            transactions.clear()
                            transactionsAPI(request!!)
                            //                        transactionViewModel!!.filterTransactionsList(request!!)
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

            filterDialog.setOnDismissListener { dialogInterface -> }
        } catch (e: Exception) {
        }
    }

    private fun loadData() {
        request = null
        transactionType.clear();
        transactionSubType.clear();
        txnStatus.clear();
        currentPage = 0;
        strFromDate = "";
        strToDate = "";
        strStartAmount = "";
        strEndAmount = "";
        binding.ivFilterIcon.setImageDrawable(getDrawable(R.drawable.ic_filter_icon));

        val transactionListRequest = TransactionListReq();
        transactionListRequest.requestToken =
            myApplication.mCurrentUserData.validateResponseData?.token
        transactionListRequest.params.pageNo = currentPage.toString()
        transactionListRequest.params.pageSize =
            java.lang.String.valueOf(Utils.pageSize)
        transactionsAPI(transactionListRequest);
        myApplication.mCurrentUserData.initializeTransactionSearch()
        myApplication.mCurrentUserData.transactionListReq = transactionListRequest
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
                        binding.txnRefresh.setRefreshing(false)
                        binding.loadLL.visibility = GONE
                        myApplication.mCurrentUserData.transactionResponse =
                            recentTransactionResponse.data
                        total = recentTransactionResponse.data?.totalPages!!
                        if (isSwiped) {
                            transactions =
                                (recentTransactionResponse.data!!.items as MutableList<TransactionItem>?)!!
                            isSwiped = false
                        } else
                            transactions.addAll(recentTransactionResponse.data!!.items!!)

                        if (transactions.size > 0) {
                            binding.txnListRV.visibility = VISIBLE
                            binding.noTransactions.visibility = GONE
                            if (currentPage > 0) {
                                val myPos: Int =
                                    transactions.size - recentTransactionResponse.data!!.items!!.size
                                binding.txnListRV.scrollToPosition(myPos)
                                binding.noMoreTransactions.setVisibility(GONE)
                            } else {
                                binding.txnListRV.scrollToPosition(0)
                                if (empRole.equals(Utils.EMPROLE))
                                    binding.noMoreTransactions.setVisibility(GONE)
                                else
                                    binding.noMoreTransactions.setVisibility(VISIBLE)
                            }
                            prepareListData(transactions)
                        } else {
//                            if (!empRole.equals(Utils.EMPROLE)) {
//                                if (!isFirstAPICalled) {
//                                    isFirstAPICalled = true
//                                    binding.searchListCV.visibility = GONE
//                                    binding.txnRefresh.isEnabled = false
//                                } else {
//                                    binding.searchListCV.visibility = VISIBLE
//                                }
//                            }

                            binding.txnListRV.visibility = GONE
                            binding.noTransactions.visibility = VISIBLE
                            binding.noMoreTransactions.visibility = GONE
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
                        myApplication.mCurrentUserData.batchResponse =
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

            binding.txnListRV.layoutManager =
                LinearLayoutManager(this@TransactionListActivity)
            binding.txnListRV.adapter = adapter
            if (!empRole.equals(Utils.EMPROLE)) {
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

    override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
    }

    override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (charSequence!!.length > 30) {
            transactions.clear()
            if (request != null && request!!.isFilters) {
                request!!.requestToken =
                    myApplication.mCurrentUserData.validateResponseData?.token
                request!!.searchKey = charSequence.toString()
                transactionsAPI(request!!)
            } else {
                val transactionListRequest = TransactionListReq()
                transactionListRequest.searchKey = charSequence.toString()
                transactionListRequest.requestToken =
                    myApplication.mCurrentUserData.validateResponseData?.token
                transactionsAPI(transactionListRequest)
            }
        } else if (charSequence.length > 0 && charSequence.length < 30) {
            binding.txnListRV.visibility = GONE
            binding.noTransactions.visibility = VISIBLE
            binding.noMoreTransactions.visibility = GONE
        } else if (charSequence.toString().trim { it <= ' ' }.length == 0) {
            transactions.clear()
            if (request != null && request!!.isFilters) {
                request!!.requestToken =
                    myApplication.mCurrentUserData.validateResponseData?.token
                request!!.searchKey = charSequence.toString()
                transactionsAPI(request!!)
            } else {
                myApplication.mCurrentUserData.transactionListReq!!.params.pageNo = "0"
                myApplication.mCurrentUserData.transactionListReq!!.requestToken =
                    myApplication.mCurrentUserData.validateResponseData?.token
                transactionsAPI(myApplication.mCurrentUserData.transactionListReq!!)
            }
        }
    }

    override fun afterTextChanged(charSequence: Editable?) {
    }

//    override fun onBackPressed() {
//        finish()
//    }
}
