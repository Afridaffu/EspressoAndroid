package com.coyni.pos.app.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.coyni.pos.app.databinding.FragmentDashboardFragBinding

class dashboard_frag : Fragment() {

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

//        binding.todayBatchCV?.setOnClickListener{
//            startActivity(Intent(context, MposSuccesScreen::class.java))
//            val intent = Intent(view.context, TransactionFailedFragment::class.java)
//            view.context.startActivity(intent)
//            activity?.finish()

//            val transaction = fragmentManager?.beginTransaction()
//            transaction?.replace(R.id.FirstFragment, sale_fragment())
//            transaction?.addToBackStack(null)
//            transaction?.commit()

//        }

        binding.startSaleLL?.setOnClickListener {


        }


    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            dashboard_frag().apply {
                arguments = Bundle().apply {

                }
            }
    }
}