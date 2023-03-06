package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityGenarateQrBinding
import com.coyni.pos.app.fragments.GenenrateQrFragment
import com.coyni.pos.app.fragments.MerchantQrFragment
import com.coyni.pos.app.model.discard.DiscardSaleRequest
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.viewmodel.GenerateQrViewModel

class GenarateQrActivity : BaseActivity() {
    private lateinit var binding: ActivityGenarateQrBinding
    lateinit var myApplication: MyApplication
    lateinit var terminalName: String
    lateinit var currentEmployee: String
    lateinit var dbaName: String
    lateinit var generateQrViewModel: GenerateQrViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenarateQrBinding.inflate(layoutInflater)
        setContentView(binding.root)
        pushFragment(GenenrateQrFragment(), "", "")
        inItFields()
        inItObservers()
    }

    private fun inItFields() {
        myApplication = applicationContext as MyApplication
        generateQrViewModel = ViewModelProvider(this).get(GenerateQrViewModel::class.java)
        getLoginResponce()
        generateQr()
//        merchantQr()
        binding.exitLL.setOnClickListener {
            val dialog = Utils.exitSaleModeDialog(this)
            if (myApplication.mCurrentUserData.generateQrResponseData?.uniqueId == null || myApplication.mCurrentUserData.generateQrResponseData?.uniqueId == "") {
                Handler().postDelayed({
                    if (dialog != null) {
                        val intent = Intent(applicationContext, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }, 3000)
            } else {
                disCardSale()
            }
        }

    }

    private fun disCardSale() {
        val discardSaleRequest = DiscardSaleRequest()
        discardSaleRequest.requestToken =
            myApplication.mCurrentUserData.validateResponseData?.token
        discardSaleRequest.uniqueId =
            myApplication.mCurrentUserData.generateQrResponseData?.uniqueId
        generateQrViewModel.discardSaleRequest(discardSaleRequest)
    }

    private fun getLoginResponce() {
        terminalName = myApplication.mCurrentUserData.loginData?.terminalName.toString()
        currentEmployee =
            myApplication.mCurrentUserData.validateResponseData?.employeeName.toString()
        dbaName = myApplication.mCurrentUserData.validateResponseData?.dbaName.toString()
        binding.dbaNameTV.setText(dbaName)
        binding.currentEmployeeTV.setText(currentEmployee)
        binding.terminalNameTV.setText(terminalName)
    }

    fun pushFragment(fragment: Fragment, navigation: String, value: String) {
        val bundle = Bundle()
        bundle.putString(Utils.SCREEN, navigation)
        bundle.putString(Utils.VALUE, value)
        fragment.setArguments(bundle)
        supportFragmentManager.beginTransaction()
            .replace(binding.frameLayout.id, fragment).commit()
    }

    override fun fragmentNavigation(action: String?, value: String?) {
        super.fragmentNavigation(action, value)
        when (action) {
            Utils.MERCHANT_QR -> {
                pushFragment(MerchantQrFragment(), action, value!!)
                merchantQr()
            }
//            Utils.GENERATE_QR -> {
//                pushFragment(GenenrateQrFragment(), action, value!!)
//                generateQr()
//            }
        }
    }

    private fun merchantQr() {
        binding.consLL.setBackgroundResource(R.drawable.dash_bottom_radius)
        binding.arrowButton.setOnClickListener {
            if (binding.hiddenView.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
                binding.consLL.setBackgroundResource(R.drawable.dash_bottom_radius)
                binding.hiddenView.visibility = View.GONE
                binding.cvProfileSmall.visibility = View.VISIBLE
                binding.dbaNameTV.visibility = View.VISIBLE
                binding.arrowButton.setImageResource(R.drawable.ic_feather_menu)
            } else {
                TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
                binding.consLL.setBackgroundResource(R.color.hidden_view_color)
                binding.hiddenView.visibility = View.VISIBLE
                binding.cvProfileSmall.visibility = View.GONE
                binding.dbaNameTV.visibility = View.GONE
                binding.arrowButton.setImageResource(R.drawable.ic_feather_x)
            }
        }
    }

    private fun generateQr() {
        binding.arrowButton.setOnClickListener {
            if (binding.hiddenView.visibility == View.VISIBLE) {
                TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
                binding.consLL.setBackgroundResource(R.color.hidden_view_color)
                binding.hiddenView.visibility = View.GONE
                binding.cvProfileSmall.visibility = View.VISIBLE
                binding.dbaNameTV.visibility = View.VISIBLE
                binding.arrowButton.setImageResource(R.drawable.ic_feather_menu)
            } else {
                TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
                binding.consLL.setBackgroundResource(R.color.hidden_view_color)
                binding.hiddenView.visibility = View.VISIBLE
                binding.cvProfileSmall.visibility = View.GONE
                binding.dbaNameTV.visibility = View.GONE
                binding.arrowButton.setImageResource(R.drawable.ic_feather_x)
            }
        }
    }

    private fun inItObservers() {
        generateQrViewModel.discardSaleResponse.observe(this) { discardSaleResponse ->
            try {
                if (discardSaleResponse != null) {
                    if (discardSaleResponse.status == Utils.SUCCESS) {
                        myApplication.mCurrentUserData.generateQrResponseData?.uniqueId = null
                        val intent = Intent(this, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Utils.displayAlertNew(
                            discardSaleResponse.error?.errorDescription.toString(),
                            this,
                            ""
                        )
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}