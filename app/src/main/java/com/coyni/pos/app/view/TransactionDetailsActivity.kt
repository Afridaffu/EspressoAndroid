package com.coyni.pos.app.view

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.coyni.pos.app.R
import com.coyni.pos.app.adapter.ActivityLogAdapter
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.baseclass.OnItemClickListener
import com.coyni.pos.app.databinding.ActivityTransactionDetailsBinding
import com.coyni.pos.app.model.TransactionData
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.viewmodel.TransactionsViewModel

class TransactionDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityTransactionDetailsBinding
    private var transactionViewModel: TransactionsViewModel? = null
    var gbxID: String? = null
    var txnType: Int? = null
    var txnSubType: Int? = null
    private val retail_mobile = "Retail / Mobile"
    private val eCommerce = "eCommerce"
    private val full = "FULL"
    private val partial = "Partial"
    private val Sent = "Sent"
    private val Received = "Received"
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
        try {
            transactionViewModel =
                ViewModelProvider(this).get(TransactionsViewModel::class.java)

            myApplication = applicationContext as MyApplication

            binding.ivBack.setOnClickListener {
//                onBackPressed()
                finish()
            }
            if (intent.getIntExtra(Utils.txnId, 0) != 0) {
                txnId = intent.getIntExtra(Utils.txnId, 0)
//                myApplication!!.mCurrentUserData.transactionId = txnId
            }

            binding.ivRefund.setOnClickListener {
                startActivity(Intent(this, RefundTransactionActivity::class.java))
            }

            if (intent.getStringExtra(Utils.gbxTxnId) != null) {
                gbxID = intent.getStringExtra(Utils.gbxTxnId)!!
            }

            if (intent.getStringExtra(Utils.txnType) != null && !intent.getStringExtra(Utils.txnType)
                    .equals("")
            ) {
                when (intent.getStringExtra(Utils.txnType)!!) {
                    Utils.SALE_ORDER -> txnType = Utils.saleorderType.toInt()
                    Utils.Refund -> txnType = Utils.refundType.toInt()
                    else -> txnType = null
                }
            }

            if (intent.getStringExtra(Utils.txnSubType) != null && !intent.getStringExtra(Utils.txnSubType)
                    .equals("")
            ) {
                when (intent.getStringExtra(Utils.txnSubType)!!) {
                    retail_mobile -> txnSubType = Utils.retailMobileSubType.toInt()
                    eCommerce -> txnSubType = Utils.eCommerceSubType.toInt()
                    Sent -> txnSubType = Utils.sentType.toInt()
                    Received -> txnSubType = Utils.receivedType.toInt()
                    else -> txnSubType = null
                }
            }
            showProgressDialog()
            transactionViewModel!!.transactionDetails(gbxID!!, txnType!!, txnSubType!!)
//            getActivityLogAPICall()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun initObservers() {
        transactionViewModel?.transactionDetailResponse?.observe(this) { transactionDetailsResponse ->
            try {
                if (transactionDetailsResponse != null) {
                    dismissDialog()
                    if (transactionDetailsResponse.status == Utils.SUCCESS) {
                        myApplication?.mCurrentUserData?.transactionData =
                            transactionDetailsResponse.data
                        when (transactionDetailsResponse.data?.transactionType) {
                            Utils.SALE_ORDER -> {
                                showSaleOrderData(transactionDetailsResponse.data)
                                getActivityLogAPICall()
                            }
                            Utils.Refund -> {
                                showRefundData(transactionDetailsResponse.data!!)
                            }
                        }
                    } else {
                        Utils.displayAlert(
                            transactionDetailsResponse.error?.errorDescription!!,
                            this,
                            ""
                        )
                    }
                } else {
                    Utils.displayAlert(
                        transactionDetailsResponse?.error?.errorDescription!!,
                        this,
                        ""
                    )
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        transactionViewModel?.logsResponseMutableLiveData?.observe(this) { activityLogsResponse ->
            try {
                if (activityLogsResponse != null) {
                    if (activityLogsResponse.status == Utils.SUCCESS) {
                        if (activityLogsResponse.data?.size!! > 0) {
                            myApplication?.mCurrentUserData?.activityLogsResponseData =
                                activityLogsResponse.data
                            val activityListAdater =
                                ActivityLogAdapter(
                                    this@TransactionDetailsActivity,
                                    activityLogsResponse.data!!
                                )
                            val mLayoutManager =
                                LinearLayoutManager(this@TransactionDetailsActivity)
                            binding.recyclerView.layoutManager = mLayoutManager
                            binding.recyclerView.itemAnimator = DefaultItemAnimator()
                            binding.recyclerView.adapter = activityListAdater
                            binding.llActivityLog.setVisibility(View.VISIBLE)

                            activityListAdater?.setOnItemClickListener(
                                object : OnItemClickListener {
                                    override fun onItemClick(position: Int?, value: Any?) {
                                        val intent = Intent(
                                            applicationContext,
                                            TransactionDetailsActivity::class.java
                                        )
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                        intent.putExtra(
                                            Utils.gbxTxnId,
                                            myApplication?.mCurrentUserData?.activityLogsResponseData?.get(
                                                position!!
                                            )?.linkText
                                        )
                                        if (myApplication?.mCurrentUserData?.activityLogsResponseData?.get(
                                                position!!
                                            )?.txnType == 9
                                        ) {
                                            intent.putExtra(
                                                Utils.txnType,
                                                Utils.Refund
                                            )
                                        }
                                        if (myApplication?.mCurrentUserData?.activityLogsResponseData?.get(
                                                position!!
                                            )?.txnSubType == 8
                                        ) {
                                            intent.putExtra(
                                                Utils.txnSubType,
                                                Sent
                                            )
                                        }

                                        startActivity(intent)
                                    }

                                    override fun onChildClicked(s: String?) {

                                    }
                                })
                        } else {
                            binding.llActivityLog.setVisibility(View.GONE)
                        }
                    } else {
                        binding.llActivityLog.setVisibility(View.GONE)
                    }
                } else {
                    binding.llActivityLog.setVisibility(View.GONE)
                }

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    private fun showSaleOrderData(data: TransactionData?) {
        binding.transactionDetailsLL.visibility = VISIBLE
        binding.llSaleOrderData.visibility = View.VISIBLE
        binding.tvReason.visibility = View.GONE
        binding.llOriginalInfo.visibility = View.GONE
        binding.llActivityLog.visibility = View.GONE
        binding.llServicePhone.visibility = View.GONE

        if (data != null) {
            if (data.transactionType != null && data.transactionSubtype != null) {
                binding.typeNsubtype.setText(data.transactionType + " - " + data.transactionSubtype)
            }
            if (data.purchaseAmount != null) {
                saleOrderAmount = Utils.convertTwoDecimal(
                    data.purchaseAmount.toString().replace(
                        "CYN",
                        ""
                    ).trim()
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

                when (data.status!!.toLowerCase()) {
                    Utils.transCompleted -> {
                        binding.statusTV.setTextColor(resources.getColor(R.color.true_green))
                        binding.statusTV.setBackgroundResource(R.drawable.txn_completed_bg)
                    }
                    Utils.transInprogress -> {
                        binding.statusTV.setTextColor(resources.getColor(R.color.inprogress_status))
                        binding.statusTV.setBackgroundResource(R.drawable.txn_inprogress_bg)
                    }
                    Utils.refunded -> {
                        binding.statusTV.setTextColor(resources.getColor(R.color.pending_status))
                        binding.statusTV.setBackgroundResource(R.drawable.txn_pending_bg)
                        binding.ivRefund.isEnabled = false
                        binding.ivRefund.setImageResource(R.drawable.refund_disable_icon)
                        binding.RefundTV.setTextColor(getColor(R.color.light_gray))
                    }
                    Utils.partialrefund,
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
                    binding.refID.text = (data.referenceId)?.substring(0, 10) + "..."
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

            binding.saleRefIDcopyLL.setOnClickListener(View.OnClickListener {
                Utils.copyText(
                    data.referenceId, this@TransactionDetailsActivity
                )
            })
            binding.orgRefIDcopyLL.setOnClickListener(View.OnClickListener {
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
            if (txnId != 0)
//                if (myApplication?.mCurrentUserData?.UserType == Utils.BUSINESS)
                transactionViewModel?.activityLogsDetails(txnId!!, Utils.MERCHANT)

        }
    }

    private fun showRefundData(data: TransactionData) {
        binding.transactionDetailsLL.visibility = VISIBLE
        binding.tvReason.visibility = View.VISIBLE
        binding.llSaleOrderData.visibility = View.GONE
        binding.llOriginalInfo.visibility = View.VISIBLE
        binding.llActivityLog.visibility = View.GONE
        binding.llServicePhone.visibility = View.VISIBLE
        binding.tvCustomerInfoHeading.text = "Merchant Information"
        binding.nameTV.text = "Merchant Name"
        binding.emailTV.text = "Customer Service Email"


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
            if (data.remarks != null) {
                binding.tvReason.text = data.remarks.toString()
            }
            if (data.createdDate != null) {
                binding.tvDate.text =
                    (myApplication?.mCurrentUserData?.convertZoneLatestTxndate(data.createdDate))
            }

            if (data.status != null) {
                binding.statusTV.setText(data.status)

                // Activity Logs API
//                getActivityLogAPICall()

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
                    binding.refID.text = (data.referenceId)?.substring(0, 10) + "..."
                else
                    binding.refID.text = (data.referenceId)
            }

            if (data.merchantName != null) {
                binding.custName.text = (data.merchantName)
            }

            if (data.customerServiceEmail != null) {
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
                binding.orgRefId.paintFlags =
                    binding.orgRefId.paintFlags or Paint.UNDERLINE_TEXT_FLAG
                if (data.saleOrderReferenceId!!.length > 10)
                    binding.orgRefId.text = (data.saleOrderReferenceId)?.substring(0, 10) + "..."
                else
                    binding.orgRefId.text = (data.saleOrderReferenceId)
            }
            if (data.transactionAmount != null) {
                binding.tvOrgTxnAmt.text = (data.transactionAmount)
            }

            if (data.tip != null) {
                binding.tvOrgTip.text = (data.tip)
            }

            if (data.total != null) {
                binding.tvOrgTotal.text = (data.total)
            }

            binding.saleRefIDcopyLL.setOnClickListener(View.OnClickListener {
                Utils.copyText(
                    data.referenceId, this@TransactionDetailsActivity
                )
            })

            binding.orgRefIDcopyIV.setOnClickListener(View.OnClickListener {
                Utils.copyText(
                    data.saleOrderReferenceId, this@TransactionDetailsActivity
                )
            })

            binding.orgRefId.setOnClickListener(View.OnClickListener {
                val intent = Intent(
                    applicationContext,
                    TransactionDetailsActivity::class.java
                )
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra(
                    Utils.gbxTxnId, data.saleOrderReferenceId
                )
                if (data.saleOrderType == 10) {
                    intent.putExtra(
                        Utils.txnType,
                        Utils.SALE_ORDER
                    )
                }
                if (data.saleOrderSubType == 13) {
                    intent.putExtra(
                        Utils.txnSubType,
                        retail_mobile
                    )
                }
                intent.putExtra(Utils.txnId, data.saleOrderTransactionId)
                startActivity(intent)
            })

        }

    }

//    override fun onBackPressed() {
//       finish()
//    }

}