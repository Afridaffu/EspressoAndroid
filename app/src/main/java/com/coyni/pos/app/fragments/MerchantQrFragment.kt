package com.coyni.pos.app.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidmads.library.qrgenearator.QRGEncoder
import androidx.lifecycle.ViewModelProvider
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseFragment
import com.coyni.pos.app.databinding.MerchantQrBinding
import com.coyni.pos.app.dialog.DiscardSaleDialog
import com.coyni.pos.app.dialog.OnDialogClickListener
import com.coyni.pos.app.model.discard.DiscardSaleRequest
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.view.DashboardActivity
import com.coyni.pos.app.viewmodel.GenerateQrViewModel

class MerchantQrFragment : BaseFragment() {
    lateinit var binding: MerchantQrBinding
    var fontSize: Float = 0.0f;
    private var isPayClickable: Boolean = false
    private lateinit var screen: String
    private lateinit var amount: String
    private lateinit var strWallet: String
    private lateinit var webSocketUrl: String
    lateinit var bitmap: Bitmap
    lateinit var qrgEncoder: QRGEncoder
    var rotate: Animation? = null
    var myApplication: MyApplication? = null
    lateinit var generateQrViewModel: GenerateQrViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate the layout and bind to the _binding
        binding = MerchantQrBinding.inflate(layoutInflater, container, false)
        inItFields()
        inItObservers()
        return binding.root
    }


    private fun inItFields() {
        binding.discardSaleLL.isEnabled = false
        myApplication = requireActivity().application as MyApplication
        generateQrViewModel =
            ViewModelProvider(requireActivity()).get(GenerateQrViewModel::class.java)
        getValues()
        strWallet = myApplication!!.mCurrentUserData.generateQrResponseData?.walletId.toString()
        binding.idIVQrcode.setImageBitmap(Utils.convertBase64ToBitmap(myApplication!!.mCurrentUserData.generateQrResponseData?.image.toString()))
        webSocketUrl =
            myApplication!!.mCurrentUserData.generateQrResponseData?.mposWebsocket.toString()
//        binding.lottieAnimV.loop(false)


        Handler().postDelayed({
            rotate = AnimationUtils.loadAnimation(context, R.anim.rotate)
            binding.lottieAnimV.startAnimation(rotate)

            binding.amountTV.text = amount
            binding.qrLL.visibility = View.VISIBLE
            binding.animationRL.visibility = View.GONE
            binding.discardSaleLL.setBackgroundResource(R.drawable.bg_greencolor_filled)
            binding.discardSaleLL.isEnabled = true

            Handler().postDelayed({
//                rotate = AnimationUtils.loadAnimation(context, R.anim.rotate)
//                binding.lottieAnimV.startAnimation(rotate)
//                binding.qrLL.visibility = View.GONE
//                binding.animationRL.visibility = View.VISIBLE
//                binding.waitingText.visibility = View.VISIBLE

            }, 3000)
        }, 3000)


        binding.discardSaleLL.setOnClickListener {
            val discardSaleDialog = DiscardSaleDialog(requireContext())
            discardSaleDialog.show()
            discardSaleDialog.setOnDialogClickListener(object : OnDialogClickListener {
                override fun onDialogClicked(action: String?, value: Any?) {
                    if (action == Utils.DISCARD) {
                        disCardSale()
                    }
                }
            })
        }
    }

    private fun disCardSale() {
        val discardSaleRequest = DiscardSaleRequest()
        discardSaleRequest.requestToken =
            myApplication?.mCurrentUserData?.validateResponseData?.token
        discardSaleRequest.uniqueId =
            myApplication?.mCurrentUserData?.generateQrResponseData?.uniqueId
        generateQrViewModel.discardSaleRequest(discardSaleRequest)
    }

    private fun inItObservers() {
        generateQrViewModel.discardSaleResponse.observe(requireActivity()) { discardSaleResponse ->
            try {
                if (discardSaleResponse != null) {
                    if (discardSaleResponse.status == Utils.SUCCESS) {
                        myApplication?.mCurrentUserData?.generateQrResponseData?.uniqueId = null
                        val intent = Intent(requireContext(), DashboardActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Utils.displayAlertNew(
                            discardSaleResponse.error?.errorDescription.toString(),
                            requireContext(),
                            ""
                        )
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun getValues() {
        if (arguments == null) {
            return
        }
        if (requireArguments()[Utils.SCREEN] != null) {
            screen = java.lang.String.valueOf(requireArguments()[Utils.SCREEN])
        }
        if (requireArguments()[Utils.VALUE] != null) {
            amount = java.lang.String.valueOf(requireArguments()[Utils.VALUE])
        }
    }
}