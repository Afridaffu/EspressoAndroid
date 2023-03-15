package com.coyni.pos.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coyni.pos.app.baseclass.BaseFragment
import com.coyni.pos.app.databinding.TransactionFailedBinding
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.view.RefundTransactionActivity
import com.coyni.pos.app.view.TransactionListActivity


class TransactionFailedFragment : BaseFragment() {
    private lateinit var binding: TransactionFailedBinding
    private var myApplication: MyApplication? = null
    lateinit var screen: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate the layout and bind to the _binding
        binding = TransactionFailedBinding.inflate(layoutInflater, container, false)
        inItFields()
        return binding.root
    }

    private fun inItFields() {
        myApplication = requireActivity().application as MyApplication

        if (requireArguments()[Utils.SCREEN] != null) {
            screen = java.lang.String.valueOf(requireArguments()[Utils.SCREEN])
        }
        when (screen) {
            Utils.REFUND -> refundFailed()
        }
    }

    private fun refundFailed() {
        binding.tvErrMessage.text =
            "[" + myApplication?.mCurrentUserData?.refundResponse?.error?.errorCode.toString() + "-" + myApplication?.mCurrentUserData?.refundResponse?.error?.errorDescription.toString() + "]"

        binding.cvTryAgain.setOnClickListener {
            startActivity(
                Intent(requireContext(), RefundTransactionActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
        binding.ivBack.setOnClickListener {
            startActivity(
                Intent(requireContext(), TransactionListActivity::class.java)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            )
        }
    }
}