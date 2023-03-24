package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.baseclass.OnClickListener
import com.coyni.pos.app.databinding.ActivityStatusFailedBinding
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils

class StatusFailedActivity : BaseActivity() {

    private lateinit var binding: ActivityStatusFailedBinding
    private var status = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatusFailedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {

        val myApplication = applicationContext as MyApplication
        status = intent.getStringExtra(Utils.STATUS).toString()

        if (status.equals(Utils.DEACTIVATED, true)) {
            binding.ivBack.visibility = View.GONE
            binding.tvButton.text = getString(R.string.okay)

            binding.tvHeader.text = getString(R.string.terminal_deactivated)
            binding.tvDescription.text =
                getString(R.string.this_terminal_has_been_deactivated_and_is_no_longer_accessible)

        } else if (status.equals(Utils.FAILED, true) || status.equals(Utils.CANCELED, true)) {
            binding.ivBack.visibility = View.VISIBLE
            binding.tvButton.text = getString(R.string.try_again)

            if (status.equals(Utils.FAILED, true)) {
                binding.tvHeader.text = getString(R.string.payment_failed)
                binding.tvDescription.text = getString(R.string.payment_failed_description)
            } else if (status.equals(Utils.CANCELED, true)) {
                binding.tvHeader.text = getString(R.string.payment_canceled)
                binding.tvDescription.text = getString(R.string.customer_canceled_description)
            }
        }


        binding.tvButton.setOnClickListener {
//            if (status.equals(Utils.DEACTIVATED, true)) {
            finish()
//            } else if (status.equals(Utils.FAILED, true) || status.equals(Utils.CANCELED, true)) {
//
//            }
        }
        binding.ivBack.setOnClickListener {
            myApplication.mCurrentUserData.generateQrResponseData?.uniqueId = null
            startActivity(
                Intent(applicationContext, DashboardActivity::class.java).setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
            )
        }
    }

    override fun onBackPressed() {

    }
}