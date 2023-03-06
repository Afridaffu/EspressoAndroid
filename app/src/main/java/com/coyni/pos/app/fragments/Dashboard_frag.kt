package com.coyni.pos.app.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.coyni.pos.app.databinding.FragmentDashboardFragBinding
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.view.GenarateQrActivity
import com.coyni.pos.app.view.PinActivity
import com.coyni.pos.app.view.TransactionListActivity

class Dashboard_frag : Fragment() {

    private lateinit var binding: FragmentDashboardFragBinding
    lateinit var action_type: String
    private var myApplication: MyApplication? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardFragBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myApplication = context?.applicationContext as MyApplication?
        binding.tvTerminalName.text = myApplication?.mCurrentUserData?.loginData?.terminalName
        binding.terminalID.text = "TID - " + myApplication?.mCurrentUserData?.loginData?.terminalId

        binding.todayBatchCV.setOnClickListener {
            action_type = Utils.VIEW_BATCH
            launchPinActivity()
        }

        binding.startSaleLL.setOnClickListener {
            action_type = Utils.START_NEW_SALE
            launchPinActivity()
        }
    }

    private fun launchPinActivity() {
        val pin = Intent(requireContext(), PinActivity::class.java)
        pin.putExtra(Utils.ACTION_TYPE, action_type)
        pinActivityResultLauncher.launch(pin)
    }

    var pinActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.getResultCode() === AppCompatActivity.RESULT_OK) {
            val token: String? = result.data?.getStringExtra(Utils.TRANSACTION_TOKEN)
            val screen: String? = result.data?.getStringExtra(Utils.ACTION_TYPE)
            screenlauncher(screen)
        }
    }

    private fun screenlauncher(screen: String?) {
        when (screen) {
            Utils.START_NEW_SALE -> {
                val intent = Intent(requireContext(), GenarateQrActivity::class.java)
                startActivity(intent)
            }
            Utils.VIEW_BATCH -> {
                val intent = Intent(requireContext(), TransactionListActivity::class.java)
                startActivity(intent)
            }
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