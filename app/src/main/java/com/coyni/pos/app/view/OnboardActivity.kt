package com.coyni.pos.app.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityOnboardBinding
import com.coyni.pos.app.utils.Utils

class OnboardActivity : BaseActivity() {

    private lateinit var binding: ActivityOnboardBinding
    private var lastClickTime = 0L
    private var isClick = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun initView() {
        binding.ICONClick.setOnClickListener {
//            if (isClick) {
//                isClick = false
//                binding.ICONClick.setImageDrawable(getDrawable(R.drawable.mpos_full))
//            } else {
//                isClick = true
//                binding.ICONClick.setImageDrawable(getDrawable(R.drawable.ic_onboard_mpos))
//            }
        }

        binding.tvButton.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < Utils.lastClickDelay)
                return@setOnClickListener
            lastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}