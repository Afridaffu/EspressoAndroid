package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityTransactionDetailsBinding
import com.coyni.pos.app.model.TransactionData
import com.coyni.pos.app.utils.MyApplication
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
    var myApplication: MyApplication? = null
    var txnId: Int? = null
    lateinit var saleOrderAmount: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initField()
        initObservers()
    }

    private fun initField() {

        transactionViewModel =
            ViewModelProvider(this).get(TransactionsViewModel::class.java)

        myApplication = applicationContext as MyApplication

        binding.ivBack.setOnClickListener {
            startActivity(
                Intent(applicationContext, TransactionListActivity::class.java).setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
            )
        }

        if (intent.getStringExtra("txnId") != null && !intent.getStringExtra("txnId").equals("")) {
//            txnId = intent.getIntExtra(txnId.toString()
        }

        binding.ivRefund.setOnClickListener {
            startActivity(Intent(this, RefundTransactionActivity::class.java))
        }
        if (intent.getStringExtra(Utils.gbxTxnId) != null) {
            gbxID = intent.getStringExtra(Utils.gbxTxnId)!!
        }
        if (getIntent().getStringExtra(Utils.txnType) != null && !getIntent().getStringExtra(
                Utils.txnType
            )
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
                when (getIntent().getStringExtra(Utils.txnSubType)!!) {
                    retail_mobile -> {
                        txnSubTypeStr = Utils.filter_Retail
                    }
                    eCommerce -> {
                        txnSubTypeStr = Utils.filter_eCommerce
                    }
                    Utils.SENT -> {
                        txnSubTypeStr = Utils.sent
                    }

                    full -> {
                        txnSubTypeStr = Utils.filter_full
                    }
                    partial -> {
                        txnSubTypeStr = Utils.partialRefund
                    }
                }

            }
        }
        transactionViewModel!!.transactionDetails(gbxID!!, txnTypeStr!!, txnSubTypeStr!!)

    }

    fun initObservers() {

        transactionViewModel?.transactionDetailResponse?.observe(this) { transactionDetailsResponse ->
            try {
                if (transactionDetailsResponse != null) {
                    if (transactionDetailsResponse.status.equals(Utils.SUCCESS)) {
                        myApplication?.mCurrentUserData?.transactionData =
                            transactionDetailsResponse.data

                        when (transactionDetailsResponse.data?.transactionType) {
                            Utils.SALE_ORDER -> showSaleOrderData(transactionDetailsResponse.data)
                            Utils.REFUND -> showRefundData(transactionDetailsResponse.data)
                        }
                    }
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        transactionViewModel?.logsResponseMutableLiveData?.observe(this) { logsResponseMutableLiveData ->
            try {
                if (logsResponseMutableLiveData != null) {
                    if (logsResponseMutableLiveData.status.equals(Utils.SUCCESS)) {

                    }
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    private fun showSaleOrderData(data: TransactionData?) {
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
                saleOrderAmount = Utils.convertTwoDecimal(
                    data.purchaseAmount.toString().replace(
                        "CYN",
                        "")
                    ).replace(
                    "$",
                    "".trim()
                )
                binding.amount.text = saleOrderAmount
            }
//            if (data.purchaseAmount != null) {
//                binding.amount.text = (
//                        Utils.convertTwoDecimal(
//                            data.purchaseAmount!!.replace(
//                                "CYN",
//                                ""
//                            ).trim()
//                        )
//                        )
//            }
            if (data.status != null) {
                binding.statusTV.text = (data.status)

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

            if (data.purchaseAmount != null) {
                binding.tvPurchaseAmt.text = (data.purchaseAmount)
            }

            if (data.createdDate != null) {
                binding.tvDate.text =
                    (myApplication?.mCurrentUserData?.convertZoneLatestTxndate(data.createdDate))
            }

            if (data.tip != null) {
                binding.tvTipAmt.text = (data.tip)
            }
            if (data.totalAmount != null) {
                binding.tvTotalAmt.text = (data.totalAmount)
            }
            if (data.referenceId != null) {
                if (data.referenceId!!.length > 10)
                    binding.refID.text = (data.referenceId)?.substring(0, 10)
                else
                    binding.refID.text = (data.referenceId)
            }
            if (data.customerName != null) {
                binding.custName.text = (data.customerName)
            }
            if (data.customerEmail != null) {
                binding.tvCustomerEmail.text = (data.customerEmail)
            }
            if (data.employeeName != null) {
                binding.tvEmpName.text = (data.employeeName)
            }
            if (data.employeeId != null) {
                binding.tvEmpId.text = (data.employeeId.toString()!!)
            }
            if (data.terminalId != null) {
                binding.terminalID.text = data.terminalId.toString()
            }

            binding.saleRefIDcopyIV.setOnClickListener(View.OnClickListener {
                Utils.copyText(
                    data.referenceId, this@TransactionDetailsActivity
                )
            })
            binding.orgRefIDcopyIV.setOnClickListener(View.OnClickListener {
                Utils.copyText(
                    data.saleOrderReferenceId, this@TransactionDetailsActivity
                )
            })

        }
        binding.ivRefund.setOnClickListener {
            startActivity(
                Intent(this, RefundTransactionActivity::class.java)
                    .putExtra(Utils.SALE_ORDER_AMOUNT, saleOrderAmount)
                    .putExtra(Utils.GBX_ID, gbxID)
            )
        }

    }

    private fun getActivityLogAPICall() {
        if (Utils.checkInternet(this@TransactionDetailsActivity)) {
            if (txnId != null)
                if (myApplication?.mCurrentUserData?.UserType == Utils.PERSONAL)
                    transactionViewModel?.activityLogsDetails(txnId!!, "c")

        }
    }

    private fun showRefundData(data: TransactionData?) {
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

            if (data.createdDate != null) {
                binding.tvDate.text =
                    (myApplication?.mCurrentUserData?.convertZoneLatestTxndate(data.createdDate))
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
                if (data.referenceId!!.length > 10)
                    binding.refID.text = (data.referenceId)?.substring(0, 10)
                else
                    binding.refID.text = (data.referenceId)
            }

            if (data.merchantName != null) {
                binding.nameTV.text = "Merchant Name"
                binding.custName.text = (data.merchantName)
            }

            if (data.customerServiceEmail != null) {
                binding.emailTV.text = "Customer Service Email"
                binding.tvCustomerEmail.text = (data.customerServiceEmail)
            }

            if (data.customerServicePhone != null) {
                var phone_number = "(" + data.customerServicePhone!!.substring(0, 3)
                    .toString() + ")" + " " + data.customerServicePhone!!
                    .substring(3, 6).toString() + "-" + data.customerServicePhone!!
                    .substring(6, 10)

                binding.tvCustomerServicePhone.text = phone_number
            }

            if (data.employeeName != null) {
                binding.tvEmpName.text = (data.employeeName)
            }
            if (data.employeeId != null) {
                binding.tvEmpId.text = (data.employeeId.toString()!!)
            }
            if (data.terminalId != null) {
                binding.terminalID.text = (data.terminalId.toString()!!)
            }
            if (data.saleOrderDateAndTime != null) {
                binding.tvOrgDate.text =
                    (myApplication?.mCurrentUserData?.convertZoneLatestTxndate(data.saleOrderDateAndTime))
            }

            if (data.saleOrderReferenceId != null) {
                if (data.saleOrderReferenceId!!.length > 10)
                    binding.orgRefId.text = (data.referenceId)?.substring(0, 10)
                else
                    binding.orgRefId.text = (data.referenceId)
            }
            if (data.transactionAmount != null) {
                binding.tvOrgTxnAmt.text = (data.transactionAmount)
            }

            if (data.tip != null) {
                binding.tvOrgTip.text = (data.tip)
            }

            if (data.totalAmount != null) {
                binding.tvOrgTotal.text = (data.totalAmount)
            }

            binding.saleRefIDcopyIV.setOnClickListener(View.OnClickListener {
                Utils.copyText(
                    data.referenceId, this@TransactionDetailsActivity
                )
            })

            binding.orgRefIDcopyIV.setOnClickListener(View.OnClickListener {
                Utils.copyText(
                    data.saleOrderReferenceId, this@TransactionDetailsActivity
                )
            })

        }

    }

}