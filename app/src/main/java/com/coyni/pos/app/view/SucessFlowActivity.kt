package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.airbnb.lottie.LottieAnimationView
import com.coyni.pos.app.R
import com.coyni.pos.app.databinding.ActivityPaymentSuccessFlowBinding

class SucessFlowActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentSuccessFlowBinding
    var animSlideUp: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSuccessFlowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initfields();
    }

    private fun initfields() {

        binding.animationView.loop(false)

        Handler().postDelayed({
            animSlideUp = AnimationUtils.loadAnimation(applicationContext, R.anim.slide_up)
            binding.animationView.startAnimation(animSlideUp)
            binding.animRL.visibility = View.GONE
            binding.backIV.visibility = View.VISIBLE
            binding.mainLayout.visibility = View.VISIBLE
        }, 3000)

//        binding.validateCV.setOnClickListener {
//            startActivity(Intent(applicationContext, MposDashboardActivity::class.java).
//            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("screen", "qr"))
//        }
    }

}