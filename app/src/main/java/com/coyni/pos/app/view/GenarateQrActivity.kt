package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import androidx.fragment.app.Fragment
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityGenarateQrBinding
import com.coyni.pos.app.fragments.GenenrateQrFragment
import com.coyni.pos.app.fragments.MerchantQrFragment
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils

class GenarateQrActivity : BaseActivity() {
    private lateinit var binding: ActivityGenarateQrBinding
    lateinit var myApplication: MyApplication
    lateinit var terminalName: String
    lateinit var currentEmployee: String
    lateinit var dbaName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGenarateQrBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pushFragment(GenenrateQrFragment(), "", "")

        inItFields();
    }

    private fun inItFields() {
        myApplication = applicationContext as MyApplication
        getLoginResponce()
        generateQr()
//        merchantQr()
        binding.exitLL?.setOnClickListener {
            val dialog = Utils.showProgressDialog(this)
            Handler().postDelayed({
                if (dialog != null) {
                    val intent = Intent(applicationContext, MposDashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }, 3000)
        }

    }

    private fun getLoginResponce() {
//        terminalName = myApplication.mCurrentUserData?.loginData?.terminalName.toString()
//        currentEmployee =
//            myApplication.mCurrentUserData?.validateResponseData?.employeeName.toString()
//        dbaName = myApplication.mCurrentUserData?.validateResponseData?.dbaName.toString()
//        binding.dbaNameTV.setText(dbaName)
//        binding.currentEmployeeTV.setText(currentEmployee)
//        binding.terminalNameTV.setText(terminalName)
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
                binding.arrowButton.setImageResource(R.drawable.menu_bar_ic)
            } else {
                TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
                binding.consLL.setBackgroundResource(R.color.hidden_view_color)
                binding.hiddenView.visibility = View.VISIBLE
                binding.cvProfileSmall.visibility = View.GONE
                binding.dbaNameTV.visibility = View.GONE
                binding.arrowButton.setImageResource(R.drawable.ic_white_close)
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
                binding.arrowButton.setImageResource(R.drawable.menu_bar_ic)
            } else {
                TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
                binding.consLL.setBackgroundResource(R.color.hidden_view_color)
                binding.hiddenView.visibility = View.VISIBLE
                binding.cvProfileSmall.visibility = View.GONE
                binding.dbaNameTV.visibility = View.GONE
                binding.arrowButton.setImageResource(R.drawable.ic_white_close)
            }
        }
    }

}