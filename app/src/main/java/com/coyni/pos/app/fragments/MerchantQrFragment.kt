package com.coyni.pos.app.fragments

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseFragment
import com.coyni.pos.app.databinding.MerchantQrBinding
import com.coyni.pos.app.dialog.DiscardSaleDialog
import com.coyni.pos.app.dialog.OnDialogClickListener
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.view.MposDashboardActivity
import com.coyni.pos.app.view.SucessFlowActivity

class MerchantQrFragment : BaseFragment() {
    lateinit var binding: MerchantQrBinding
    var fontSize: Float = 0.0f;
    private var isPayClickable: Boolean = false
    private lateinit var screen: String
    var rotate: Animation? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate the layout and bind to the _binding
        binding = MerchantQrBinding.inflate(layoutInflater, container, false)

        inItFields()
        return binding.root
    }

    private fun inItFields() {

//        binding.lottieAnimV.loop(false)

        Handler().postDelayed({
            rotate = AnimationUtils.loadAnimation(context, R.anim.rotate)
            binding.lottieAnimV.startAnimation(rotate)

            binding.qrLL.visibility = View.VISIBLE
            binding.animationRL.visibility = View.GONE

            Handler().postDelayed({
                rotate = AnimationUtils.loadAnimation(context, R.anim.rotate)
                binding.lottieAnimV.startAnimation(rotate)
                binding.qrLL.visibility = View.GONE
                binding.animationRL.visibility = View.VISIBLE
                binding.waitingText.visibility = View.VISIBLE

            }, 3000)
        }, 3000)


        binding.discardSaleLL.setOnClickListener {
            val discardSaleDialog = DiscardSaleDialog(requireContext())
            discardSaleDialog.show()
            discardSaleDialog.setOnDialogClickListener(object : OnDialogClickListener {
                override fun onDialogClicked(action: String?, value: Any?) {
                    if (action == Utils.DISCARD) {
                        val intent = Intent(requireContext(), MposDashboardActivity::class.java)
                        startActivity(intent)
                    }
                }
            })
        }

        binding.amountTV.setOnClickListener {
            startActivity(Intent(context, SucessFlowActivity::class.java))
        }
    }
}