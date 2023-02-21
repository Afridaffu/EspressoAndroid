package com.coyni.pos.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coyni.pos.app.baseclass.BaseFragment
import com.coyni.pos.app.databinding.TransactionFailedBinding
import com.coyni.pos.app.utils.Utils


class TransactionFailedFragment : BaseFragment() {
    private lateinit var binding: TransactionFailedBinding
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
        if (requireArguments()[Utils.SCREEN] != null) {
            screen = java.lang.String.valueOf(requireArguments()[Utils.SCREEN])
        }
        when (screen) {
            Utils.REFUND -> refundFailed()
        }
    }

    private fun refundFailed() {

    }
}