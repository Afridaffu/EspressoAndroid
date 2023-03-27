package com.coyni.pos.app.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityDashboardBinding
import com.coyni.pos.app.fragments.Dashboard_frag
import com.coyni.pos.app.utils.DisplayImageUtility
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.viewmodel.LoginLogoutViewModel
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.CornerSize
import com.google.android.material.shape.ShapeAppearanceModel
import java.util.*

class DashboardActivity : BaseActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private var myApplication: MyApplication? = null
    private var logoutViewModel: LoginLogoutViewModel? = null
    private var lastClick: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFields()
        initObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun initFields() {

        myApplication = applicationContext as MyApplication?
        logoutViewModel =
            ViewModelProvider(this@DashboardActivity)[LoginLogoutViewModel::class.java]
        binding.dbaNameTV.text =
            myApplication?.mCurrentUserData?.loginData?.dbaName
        binding.terminalNameTV.text =
            myApplication?.mCurrentUserData?.loginData?.terminalName
        binding.terminalIDTV.text =
            "TID - " + myApplication?.mCurrentUserData?.loginData?.terminalId
        binding.locationTV.text =
            myApplication?.mCurrentUserData?.loginData?.dbaName
        binding.businessNameTV.text =
            myApplication?.mCurrentUserData?.loginData?.companyName


        val utility: DisplayImageUtility? = DisplayImageUtility.getInstance(applicationContext)
        if (myApplication?.mCurrentUserData?.loginData?.image != null) {
            binding.ivUserProfile.visibility = View.VISIBLE
            binding.tvUserName.visibility = View.GONE

            utility?.addImage(
                myApplication?.mCurrentUserData?.loginData?.image as String,
                binding.ivUserProfile, R.drawable.dba_img
            )

        }
//        else {
//            binding.ivUserProfile.visibility = View.GONE
//            binding.tvUserName.visibility = View.VISIBLE
//            val dbaName = myApplication!!.mCurrentUserData.loginData?.dbaName?.get(0)
//            binding.tvUserName.text = dbaName.toString().capitalize(Locale.ROOT)
//        }

        val screen = intent.getStringExtra("screen")
        if (screen == "qr") {
//                showfrag(sale_fragment())
        } else {
            showfrag(Dashboard_frag())
        }

        binding.arrowButton.setOnClickListener { view: View? ->
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

        binding.logoutLL.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClick < Utils.lastClickDelay) return@setOnClickListener
            lastClick = SystemClock.elapsedRealtime()
            showProgressDialog()
            logoutViewModel!!.getLogout()
        }

    }

    private fun showfrag(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
    }

    private fun initObserver() {
        logoutViewModel?.logoutResponseMutableLiveData?.observe(this@DashboardActivity) { response ->
            dismissDialog()
            if (response != null && response.status == Utils.SUCCESS) {
                myApplication?.clearUserData()
                startActivity(
                    Intent(applicationContext, OnboardActivity::class.java)
                        .setFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        )
                )
            } else {
                Utils.displayAlert(
                    response?.error?.errorDescription.toString(),
                    this@DashboardActivity,
                    ""
                )
            }
        }
    }

    override fun onBackPressed() {
        if (binding.hiddenView.visibility == View.VISIBLE) {
            TransitionManager.beginDelayedTransition(binding.baseCardview, AutoTransition())
            binding.consLL.setBackgroundResource(R.drawable.dash_bottom_radius)
            binding.hiddenView.visibility = View.GONE
            binding.cvProfileSmall.visibility = View.VISIBLE
            binding.dbaNameTV.visibility = View.VISIBLE
            binding.arrowButton.setImageResource(R.drawable.ic_feather_menu)
        } else {
          finish()
        }
    }
}