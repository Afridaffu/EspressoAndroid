package com.coyni.pos.app.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coyni.pos.app.databinding.FragmentDashboardFragBinding
import com.coyni.pos.app.view.TransactionListActivity

class Dashboard_frag : Fragment() {

    private lateinit var binding: FragmentDashboardFragBinding

    interface fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDashboardFragBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.todayBatchCV?.setOnClickListener{
            startActivity(Intent(context, TransactionListActivity::class.java))
        }

        binding.startSaleLL?.setOnClickListener {
//            startActivity(Intent(context, PinActivity::class.java))
            startActivity(Intent(context, TransactionListActivity::class.java))
        }


    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Dashboard_frag().apply {
                arguments = Bundle().apply {

                }
            }
    }
}