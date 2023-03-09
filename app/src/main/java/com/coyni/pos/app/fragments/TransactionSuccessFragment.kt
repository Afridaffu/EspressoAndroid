package com.coyni.pos.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coyni.pos.app.baseclass.BaseFragment
import com.coyni.pos.app.databinding.TransactionSuccessBinding
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils

class TransactionSuccessFragment : BaseFragment() {
    private lateinit var binding: TransactionSuccessBinding
    private lateinit var screen: String
    private lateinit var status: String
    private lateinit var amount: String
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
        binding.customerName.text =
            "to" + myApplication?.mCurrentUserData?.transactionData?.customerName.toString()
        binding.refundedAMountTV.text = Utils.convertTwoDecimal(amount).replace("$", "")
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