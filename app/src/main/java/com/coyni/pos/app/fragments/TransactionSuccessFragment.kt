package com.coyni.pos.app.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseFragment
import com.coyni.pos.app.databinding.TransactionSuccessBinding
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.view.DashboardActivity
import com.coyni.pos.app.view.TransactionDetailsActivity

class TransactionSuccessFragment : BaseFragment() {
    private lateinit var binding: TransactionSuccessBinding
    private lateinit var screen: String
    private lateinit var status: String
    private lateinit var amount: String
    var lastClickTime = 0L
    private var myApplication: MyApplication? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate the layout and bind to the _binding
        binding = TransactionSuccessBinding.inflate(layoutInflater, container, false)
        inItFields()
        return binding.root
    }

    private fun inItFields() {
        myApplication = requireActivity().application as MyApplication
        getValues()
        when (screen) {
            Utils.REFUND -> refundSuccess()
        }
    }

    private fun refundSuccess() {
        binding.refundedTV.text = "Transaction Refunded"

        setCustomerName(
            myApplication?.mCurrentUserData?.transactionData?.customerName.toString()
        )
//        binding.customerName.text =
//            "to" + myApplication?.mCurrentUserData?.transactionData?.customerName.toString()
        binding.refundedAMountTV.text = Utils.convertTwoDecimal(amount).replace("$", "")

        binding.doneCV.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < Utils.lastClickDelay)
                return@setOnClickListener
            lastClickTime = SystemClock.elapsedRealtime()
            try {
                val intent = Intent(requireContext(), TransactionDetailsActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                intent.putExtra(
                    Utils.gbxTxnId,
                    myApplication?.mCurrentUserData?.transactionData?.referenceId
                )
                intent.putExtra(Utils.txnType, myApplication?.mCurrentUserData?.transactionData?.transactionType)
                intent.putExtra(Utils.txnSubType, myApplication?.mCurrentUserData?.transactionData?.transactionSubtype)
//                intent.putExtra(Utils.txnId, myApplication?.mCurrentUserData?.transactionData.)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.backIV.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < Utils.lastClickDelay)
                return@setOnClickListener
            lastClickTime = SystemClock.elapsedRealtime()
//            onBackPressed()
            try {
                val intent = Intent(requireContext(), DashboardActivity::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        binding.viewTxnTV.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < Utils.lastClickDelay)
                return@setOnClickListener
            lastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(requireContext(), TransactionDetailsActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra(
                Utils.gbxTxnId,
                myApplication?.mCurrentUserData?.refundResponseData?.referenceId
            )
            intent.putExtra(Utils.txnType, Utils.Refund)
            intent.putExtra(Utils.txnSubType, Utils.SENT)
            startActivity(intent)
        }
    }

    fun setCustomerName(customerName: String) {

        val ss = SpannableString( "to " + customerName)
        ss.setSpan(
            StyleSpan(Typeface.BOLD), 3,
            ss.toString().length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        ss.setSpan(
            ForegroundColorSpan(requireContext().getColor(R.color.black)), 3,
            ss.toString().length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.customerName.setText(ss)
        binding.customerName.setMovementMethod(LinkMovementMethod.getInstance())
        binding.customerName.setHighlightColor(Color.TRANSPARENT)
    }


    override fun onBackPressed() {
//        super.onBackPressed()
    }

    private fun getValues() {
        if (arguments == null) {
            return
        }
        if (requireArguments()[Utils.SCREEN] != null) {
            screen = java.lang.String.valueOf(requireArguments()[Utils.SCREEN])
        }
        if (requireArguments()[Utils.STATUS] != null) {
            status = java.lang.String.valueOf(requireArguments()[Utils.STATUS])
        }
        if (requireArguments()[Utils.REFUNDED_AMOUNT] != null) {
            amount = java.lang.String.valueOf(requireArguments()[Utils.REFUNDED_AMOUNT])
        }
    }
}