package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityTransactionDetailsBinding
import com.coyni.pos.app.model.TransactionData
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.viewmodel.TransactionsViewModel

class TransactionDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionDetailsBinding
    private var txnType = " "
    private var transactionViewModel: TransactionsViewModel? = null
    var gbxID: String? = null
    var txnTypeStr: Int? = null
    var txnSubTypeStr: Int? = null
    private val sale_order = "sale order"
    private val refund = "refund"
    private val retail_mobile = "Retail / Mobile"
    private val eCommerce = "eCommerce"
    private val full = "FULL"
    private val partial = "partial"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initField()
        initObservers()
    }

    private fun initField() {

        transactionViewModel =
            ViewModelProvider(this@TransactionDetailsActivity).get(TransactionsViewModel::class.java)

        binding.ivBack.setOnClickListener { onBackPressed() }
        binding.ivRefund.setOnClickListener {
            startActivity(Intent(this, RefundTransactionActivity::class.java))
        }

        gbxID = intent.getStringExtra(Utils.gbxTxnId)!!

        if (getIntent().getStringExtra(Utils.txnType) != null && !getIntent().getStringExtra(Utils.txnType)
                .equals("")
        ) {
            when (getIntent().getStringExtra(Utils.txnType)!!.toLowerCase()) {
                sale_order -> {
                    txnTypeStr = Utils.filter_saleorder
                }
                refund -> {
                    txnTypeStr = Utils.refund
                }
            }

            if (getIntent().getStringExtra(Utils.txnSubType) != null && !getIntent().getStringExtra(
                    Utils.txnSubType
                )
                    .equals("")
            ) {
                when (getIntent().getStringExtra(Utils.txnSubType)!!.toLowerCase()) {
                    retail_mobile -> {
                        txnTypeStr = Utils.filter_Retail
                    }
                    eCommerce -> {
                        txnTypeStr = Utils.filter_eCommerce
                    }
                    full -> {
                        txnTypeStr = Utils.filter_full
                    }
                    partial -> {
                        txnTypeStr = Utils.partialRefund
                    }
                }

            }

            transactionViewModel!!.transactionDetails(gbxID!!, txnTypeStr!!, txnSubTypeStr!!)

        }
    }

    fun initObservers() {

        transactionViewModel?.transactionDetailResponseMutableLiveData?.observe(this@TransactionDetailsActivity) { transactionDetailResponseMutableLiveData ->
            try {
                if (transactionDetailResponseMutableLiveData != null) {
                    if (transactionDetailResponseMutableLiveData.status.equals(Utils.SUCCESS)) {
                        if (transactionDetailResponseMutableLiveData.transactionType == Utils.SALE_ORDER) {
                            showSaleOrderData(transactionDetailResponseMutableLiveData)
                        } else {
                            showRefundData(transactionDetailResponseMutableLiveData)
                        }

                    }
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    private fun showSaleOrderData(data: TransactionData) {
        binding.llSaleOrderData.visibility = View.VISIBLE
        binding.tvReason.visibility = View.GONE
        binding.llOriginalInfo.visibility = View.GONE
        binding.llActivityLog.visibility = View.VISIBLE
        binding.llServicePhone.visibility = View.GONE

        if (data != null) {
            if (data.transactionType != null && data.transactionSubtype != null) {
                binding.typeNsubtype.setText(data.transactionType + " - " + data.transactionSubtype)
            }
            if (data.purchaseAmount != null) {
                binding.amount.setText(
                    Utils.convertTwoDecimal(
                        data.purchaseAmount!!.replace(
                            "CYN",
                            ""
                        ).trim()
                    )
                )
            }
            if (data.status != null) {
                binding.statusTV.setText(data.status)

                // Activity Logs
                getActivityLogAPICall()

                when (data.status!!.toLowerCase()) {
                    Utils.transCompleted -> {
                        binding.statusTV.setTextColor(resources.getColor(R.color.true_green))
                        binding.statusTV.setBackgroundResource(R.drawable.txn_completed_bg)
                    }
                    Utils.transInprogress -> {
                        binding.statusTV.setTextColor(resources.getColor(R.color.inprogress_status))
                        binding.statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg)
                    }
                    Utils.transPending -> {
                        binding.statusTV.setTextColor(resources.getColor(R.color.pending_status))
                        binding.statusTV.setBackgroundResource(R.drawable.txn_pending_bg)
                    }
                    Utils.transFailed -> {
                        binding.statusTV.setTextColor(resources.getColor(R.color.failed_status))
                        binding.statusTV.setBackgroundResource(R.drawable.txn_failed_bg)
                    }
                }
            }

            if (data.createdDate != null) {
                binding.tvDate.setText(data.createdDate)
            }

            if (data.purchaseAmount != null) {
                binding.tvPurchaseAmt.setText(data.purchaseAmount)
            }

            if (data.tip != null) {
                binding.tvTipAmt.setText(data.tip)
            }
            if (data.totalAmount != null) {
                binding.tvTotalAmt.setText(data.totalAmount)
            }
            if (data.referenceId != null) {
                binding.refID.setText(data.referenceId)
            }
            if (data.customerName != null) {
                binding.custName.setText(data.customerName)
            }
            if (data.customerEmail != null) {
                binding.tvCustomerEmail.setText(data.customerEmail)
            }
            if (data.terminalId != null) {
                binding.terminalID.setText(data.terminalId!!)
            }
            if (data.employeeName != null) {
                binding.tvEmpName.setText(data.employeeName)
            }
            if (data.employeeId != null) {
                binding.tvEmpId.setText(data.employeeId!!)
            }
        }

    }

    private fun getActivityLogAPICall() {
        TODO("Not yet implemented")
    }

    private fun showRefundData(data: TransactionData) {
        binding.tvReason.visibility = View.VISIBLE
        binding.llSaleOrderData.visibility = View.GONE
        binding.llOriginalInfo.visibility = View.VISIBLE
        binding.llActivityLog.visibility = View.GONE
        binding.llServicePhone.visibility = View.VISIBLE
        binding.tvCustomerInfoHeading.text = "Merchant Information"

        if (data != null) {
            if (data.transactionType != null && data.transactionSubtype != null) {
                binding.typeNsubtype.setText(data.transactionType + " - " + data.transactionSubtype)
            }
            if (data.transactionAmount != null) {
                binding.amount.setText(
                    Utils.convertTwoDecimal(
                        data.transactionAmount!!.replace(
                            "CYN",
                            ""
                        ).trim()
                    )
                )
            }
            if (data.status != null) {
                binding.statusTV.setText(data.status)

                // Activity Logs API
                getActivityLogAPICall()

                when (data.status!!.toLowerCase()) {
                    Utils.transCompleted -> {
                        binding.statusTV.setTextColor(resources.getColor(R.color.true_green))
                        binding.statusTV.setBackgroundResource(R.drawable.txn_completed_bg)
                    }
                    Utils.transInprogress -> {
                        binding.statusTV.setTextColor(resources.getColor(R.color.inprogress_status))
                        binding.statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg)
                    }
                    Utils.transPending -> {
                        binding.statusTV.setTextColor(resources.getColor(R.color.pending_status))
                        binding.statusTV.setBackgroundResource(R.drawable.txn_pending_bg)
                    }
                    Utils.transFailed -> {
                        binding.statusTV.setTextColor(resources.getColor(R.color.failed_status))
                        binding.statusTV.setBackgroundResource(R.drawable.txn_failed_bg)
                    }
                }
            }
            if (data.referenceId != null) {
                binding.refID.setText(data.referenceId)
            }

            if (data.merchantName != null) {
                binding.nameTV.setText("Merchant Name")
                binding.custName.setText(data.merchantName)
            }

            if (data.customerServiceEmail != null) {
                binding.emailTV.setText("Customer Service Email")
                binding.tvCustomerEmail.setText(data.customerServiceEmail)
            }

            if (data.customerServicePhone != null) {
                binding.tvCustomerServicePhone.setText(data.customerServicePhone)
            }

            if (data.terminalId != null) {
                binding.terminalID.setText(data.terminalId!!)
            }
            if (data.employeeName != null) {
                binding.tvEmpName.setText(data.employeeName)
            }
            if (data.employeeId != null) {
                binding.tvEmpId.setText(data.employeeId!!)
            }

            if (data.saleOrderDateAndTime != null) {
                binding.tvOrgDate.setText(data.saleOrderDateAndTime)
            }

            if (data.saleOrderReferenceId != null) {
                binding.orgRefId.setText(data.referenceId)
            }
            if (data.transactionAmount != null) {
                binding.tvOrgTxnAmt.setText(data.transactionAmount)
            }

            if (data.tip != null) {
                binding.tvOrgTip.setText(data.tip)
            }

            if (data.totalAmount != null) {
                binding.tvOrgTotal.setText(data.totalAmount)
            }

        }

    }

}