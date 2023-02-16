package com.coyni.pos.app.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityTerminalDeactivatedBinding

class TerminalDeactivatedActivity : BaseActivity() {

    private lateinit var binding: ActivityTerminalDeactivatedBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTerminalDeactivatedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvButton.setOnClickListener { onBackPressed() }
    }
}