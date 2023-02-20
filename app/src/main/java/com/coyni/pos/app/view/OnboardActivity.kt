package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityOnboardBinding

class OnboardActivity : BaseActivity() {

    private lateinit var binding: ActivityOnboardBinding
    private var lastClickTime = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOnboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvButton.setOnClickListener {
            if (SystemClock.elapsedRealtime() - lastClickTime < 2000)
                return@setOnClickListener
            lastClickTime = SystemClock.elapsedRealtime()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}