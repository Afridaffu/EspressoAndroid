package com.coyni.pos.app.view

import android.os.Bundle
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.baseclass.BaseFragment
import com.coyni.pos.app.databinding.ActivityTransactionStatusBinding
import com.coyni.pos.app.fragments.TransactionFailedFragment
import com.coyni.pos.app.fragments.TransactionSuccessFragment
import com.coyni.pos.app.utils.Utils

class TransactionStatusActivity : BaseActivity() {
    private lateinit var binding: ActivityTransactionStatusBinding
    private lateinit var screen: String
    private lateinit var status: String
    lateinit var currentFragment: BaseFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTransactionStatusBinding.inflate(layoutInflater)
        setContentView(binding.root)
        screen = intent.getStringExtra(Utils.SCREEN)!!
        status = intent.getStringExtra(Utils.STATUS)!!
        showStatusFragments(screen, status)
    }

    private fun showStatusFragments(screen: Any, status: Any) {
        if (status == Utils.SUCCESS || status == Utils.IN_PROGRESS) {
            when (screen) {
                Utils.REFUND -> refundSuccess()
            }
        } else if (status == Utils.FAILED) {
            when (screen) {
                Utils.REFUND -> refundFailed()
            }
        }
    }

    private fun refundFailed() {
        val refundFailed = TransactionFailedFragment()
        val bundle = Bundle()
        bundle.putString(Utils.SCREEN, Utils.REFUND)
        refundFailed.setArguments(bundle)
        pushFragment(refundFailed)
    }

    private fun refundSuccess() {
        val refundSuccess = TransactionSuccessFragment()
        val bundle = Bundle()
        bundle.putString(Utils.SCREEN, Utils.REFUND)
        refundSuccess.setArguments(bundle)
        pushFragment(refundSuccess)
    }

    private fun pushFragment(fragment: BaseFragment) {
        currentFragment = fragment
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment, "")
            .commit()
    }
}