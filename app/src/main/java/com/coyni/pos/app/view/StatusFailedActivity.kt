package com.coyni.pos.app.view

import android.os.Bundle
import android.view.View
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.baseclass.OnClickListener
import com.coyni.pos.app.databinding.ActivityStatusFailedBinding
import com.coyni.pos.app.utils.Utils

class StatusFailedActivity : BaseActivity() {

    private lateinit var binding: ActivityStatusFailedBinding

    companion object {
        private var listener: OnClickListener? = null

        fun setOnClickListener(listener: OnClickListener) {
            this.listener = listener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusFailedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }

    private fun initView() {

        if (intent.getStringExtra(Utils.SCREEN).equals(Utils.LOGIN)) {
            binding.ivBack.visibility = View.GONE
            binding.tvButton.text = getString(R.string.okay)
        } else {
            binding.ivBack.visibility = View.VISIBLE
            binding.tvButton.text = getString(R.string.try_again)
        }

        binding.tvHeader.text = intent.getStringExtra(Utils.HEADER)
        binding.tvDescription.text = intent.getStringExtra(Utils.DESCRIPTION)

        binding.tvButton.setOnClickListener {
            listener?.onButtonClick(true)
            finish()
        }
    }
}