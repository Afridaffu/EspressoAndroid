package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.View.VISIBLE
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.ui.AppBarConfiguration
import com.bumptech.glide.Glide
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityGenarateQrBinding
import com.coyni.pos.app.dialog.DiscardSaleDialog
import com.coyni.pos.app.dialog.OnDialogClickListener
import com.coyni.pos.app.fragments.GenenrateQrFragment
import com.coyni.pos.app.fragments.MerchantQrFragment
import com.coyni.pos.app.model.discard.DiscardSaleRequest
import com.coyni.pos.app.utils.DisplayImageUtility
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.viewmodel.GenerateQrViewModel
import java.util.*

class GenarateQrActivity : BaseActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
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
        inItFields()
        inItObservers()
    }

    private fun inItFields() {
        myApplication = applicationContext as MyApplication
        generateQrViewModel = ViewModelProvider(this).get(GenerateQrViewModel::class.java)
        getLoginResponce()
        pushFragment(GenenrateQrFragment(), "", "")
        generateQr()
//        merchantQr()
        binding.exitLL.setOnClickListener {
            try {
                val dialog = Utils.exitSaleModeDialog(this)
                generateQrViewModel.exitSaleRequest(myApplication.mCurrentUserData.validateResponseData?.token)
            } catch (e: Exception) {
            }
        }
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

    override fun onBackPressed() {
        try {
            if (myApplication.mCurrentUserData.generateQrResponseData?.uniqueId == null || myApplication.mCurrentUserData.generateQrResponseData!!.uniqueId == "") {
                if (binding.hiddenView.visibility == VISIBLE) {
                    TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
                    binding.consLL.setBackgroundResource(R.color.hidden_view_color)
                    binding.hiddenView.visibility = View.GONE
                    binding.cvProfileSmall.visibility = View.VISIBLE
                    binding.dbaNameTV.visibility = View.VISIBLE
                    binding.arrowButton.setImageResource(R.drawable.ic_feather_menu)
                    val amount = GenenrateQrFragment.binding.merchantAmountET.text.toString()
                    pushFragment(GenenrateQrFragment(), "", amount)
                } else {
                    generateQrViewModel.exitSaleRequest(myApplication.mCurrentUserData.validateResponseData?.token)
                }
            } else {
                if (binding.hiddenView.visibility == VISIBLE) {
                    TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
                    binding.consLL.setBackgroundResource(R.drawable.dash_bottom_radius)
                    binding.hiddenView.visibility = View.GONE
                    binding.cvProfileSmall.visibility = View.VISIBLE
                    binding.dbaNameTV.visibility = View.VISIBLE
                    binding.arrowButton.setImageResource(R.drawable.ic_feather_menu)
                } else {
                    val discardSaleDialog = DiscardSaleDialog(this)
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
        } catch (e: Exception) {
        }
    }

    private fun disCardSale() {
        try {
            val discardSaleRequest = DiscardSaleRequest()
            discardSaleRequest.requestToken =
                myApplication?.mCurrentUserData?.validateResponseData?.token
            discardSaleRequest.uniqueId =
                myApplication?.mCurrentUserData?.generateQrResponseData?.uniqueId
            showProgressDialog()
            generateQrViewModel.discardSaleRequest(discardSaleRequest)
        } catch (e: Exception) {
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
//                binding.baseCardview.radius = 15f
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

                val amount = GenenrateQrFragment.binding.merchantAmountET.text.toString()
                pushFragment(GenenrateQrFragment(), "", amount)
            } else {
                TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
                binding.consLL.setBackgroundResource(R.color.hidden_view_color)
                binding.hiddenView.visibility = View.VISIBLE
                binding.cvProfileSmall.visibility = View.GONE
                binding.dbaNameTV.visibility = View.GONE
//                binding.baseCardview.radius = 15f
                binding.arrowButton.setImageResource(R.drawable.ic_feather_x)
            }
        }
    }

    private fun inItObservers() {
        generateQrViewModel.exitSaleResponse.observe(this) { discardSaleResponse ->
            try {
                if (discardSaleResponse != null) {
                    if (discardSaleResponse.status == Utils.SUCCESS) {
                        myApplication.mCurrentUserData.generateQrResponseData?.uniqueId = null
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    } else {
                        Utils.displayAlert(
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
        generateQrViewModel.discardSaleResponse.observe(this) { discardSaleResponse ->
            try {
                if (discardSaleResponse != null) {
                    dismissDialog()
                    if (discardSaleResponse.status == Utils.SUCCESS) {
                        myApplication.mCurrentUserData.generateQrResponseData?.uniqueId = null
                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        finish()
                    } else {
                        Utils.displayAlert(
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

    private fun getLoginResponce() {
        terminalName = myApplication.mCurrentUserData.loginData?.terminalName.toString()
        currentEmployee =
            myApplication.mCurrentUserData.validateResponseData?.employeeName.toString()
        dbaName = myApplication.mCurrentUserData.validateResponseData?.dbaName.toString()
        binding.dbaNameTV.setText(dbaName)
        binding.currentEmployeeTV.setText(currentEmployee)
        binding.terminalNameTV.setText(terminalName)

        if (myApplication.mCurrentUserData.loginData?.image != null) {
            val utility: DisplayImageUtility? = DisplayImageUtility.getInstance(applicationContext)
            binding.ivUserProfile.visibility = View.VISIBLE
            utility?.addImage(
                myApplication?.mCurrentUserData?.loginData?.image as String,
                binding.ivUserProfile, R.drawable.dba_img
            )
//            Glide.with(this)
//                .load(myApplication!!.mCurrentUserData.downloadUrlData?.get(0)!!.downloadUrl)
//                .into(binding.ivUserProfile)
        }
//        else {
//            binding.ivUserProfile.visibility = View.VISIBLE
//            binding.ivUserProfile.setImageResource(R.drawable.dba_img)
//        }
    }
}