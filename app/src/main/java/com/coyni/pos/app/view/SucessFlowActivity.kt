package com.coyni.pos.app.view

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.coyni.pos.app.R
import com.coyni.pos.app.databinding.ActivityPaymentSuccessFlowBinding
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils

class SucessFlowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentSuccessFlowBinding
    var animSlideUp: Animation? = null
    var lastClickTime = 0L
    private lateinit var action_type: String
    lateinit var myApplication: MyApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSuccessFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initfields()
    }

    private fun initfields() {
        myApplication = applicationContext as MyApplication
        binding.animationView.loop(false)
        Handler().postDelayed({
            animSlideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)
            binding.animationView.startAnimation(animSlideUp)
            binding.animRL.visibility = View.GONE
            binding.backIV.visibility = View.VISIBLE
            binding.mainLayout.visibility = View.VISIBLE
        }, 3000)

        binding.backIV.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < Utils.lastClickDelay)
                return@setOnClickListener
            lastClickTime = SystemClock.elapsedRealtime()
            myApplication.mCurrentUserData.generateQrResponseData?.uniqueId = null
            startActivity(
                Intent(this, DashboardActivity::class.java).setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
            )
        }
        binding.validateCV.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < Utils.lastClickDelay)
                return@setOnClickListener
            lastClickTime = SystemClock.elapsedRealtime()
            myApplication.mCurrentUserData.generateQrResponseData?.uniqueId = null
            action_type = Utils.SUCCESS
            launchPinActivity()
        }
        binding.amountTV.text = Utils.convertTwoDecimal(
            myApplication.mCurrentUserData.webSocketObject!!.getString("txnAmount").toString()
        ).toString()

        setCustomerName(
            myApplication.mCurrentUserData.webSocketObject!!.getString("senderName").toString()
        )
    }

    fun setCustomerName(customerName: String) {

        val ss = SpannableString("From " + customerName)
        ss.setSpan(
            StyleSpan(Typeface.BOLD), 5,
            ss.toString().length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )
        ss.setSpan(
            ForegroundColorSpan(getColor(R.color.black)), 5,
            ss.toString().length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.receivedFromTV.setText(ss)
        binding.receivedFromTV.setMovementMethod(LinkMovementMethod.getInstance())
        binding.receivedFromTV.setHighlightColor(Color.TRANSPARENT)
    }

    override fun onBackPressed() {
//        super.onBackPressed()
    }

    private fun launchPinActivity() {
        val successPin = Intent(this, PinActivity::class.java)
        successPin.putExtra(Utils.ACTION_TYPE, action_type)
        pinActivityResultLauncher.launch(successPin)
    }

    var pinActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.getResultCode() === RESULT_OK) {
            //Call API Here
            val token: String? = result.data?.getStringExtra(Utils.TRANSACTION_TOKEN)
            val screen: String? = result.data?.getStringExtra(Utils.ACTION_TYPE)
            screenLauncher(screen)
        }
    }

    private fun screenLauncher(screen: String?) {
        if (screen == Utils.SUCCESS) {
            startActivity(
                Intent(this, GenarateQrActivity::class.java)
                    .setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    )
            )
        }
    }
}