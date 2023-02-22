package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityPinBinding
import com.coyni.pos.app.model.pin.ValidateRequest
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.viewmodel.PinViewModel

class PinActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityPinBinding
    var passcode: String = ""
    lateinit var pinViewModel: PinViewModel
    lateinit var action: String
    var count: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPinBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.slide_up, 0)
        setContentView(binding.root)
        action = intent.getStringExtra(Utils.ACTION_TYPE).toString()

        inItFields()
        inItObservers()
    }


    private fun inItFields() {
        pinViewModel = PinViewModel.getInstance(this)!!

        binding.qrNavigationTV.setOnClickListener {
            startActivity(Intent(this, GenarateQrActivity::class.java))
        }

        binding.imgBackk.setOnClickListener {
            onBackPressed()
        }
        binding.keyZeroTV.setOnClickListener(this)
        binding.keyOneTV.setOnClickListener(this)
        binding.keyTwoTV.setOnClickListener(this)
        binding.keyThreeTV.setOnClickListener(this)
        binding.keyFourTV.setOnClickListener(this)
        binding.keyFiveTV.setOnClickListener(this)
        binding.keySixTV.setOnClickListener(this)
        binding.keySevenTV.setOnClickListener(this)
        binding.keyEightTV.setOnClickListener(this)
        binding.keyNineTV.setOnClickListener(this)
        binding.backActionIV.setOnClickListener(this)
//        binding.tvForgot.setOnClickListener(this)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(0, R.anim.slide_bottom)
        count = 0
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.keyZeroTV -> if (passcode.length < 4) {
                passcode += "0"
                passNumber(passcode)
            }
            R.id.keyOneTV -> if (passcode.length < 4) {
                passcode += "1"
                passNumber(passcode)
            }
            R.id.keyTwoTV -> if (passcode.length < 4) {
                passcode += "2"
                passNumber(passcode)
            }
            R.id.keyThreeTV -> if (passcode.length < 4) {
                passcode += "3"
                passNumber(passcode)
            }
            R.id.keyFourTV -> if (passcode.length < 4) {
                passcode += "4"
                passNumber(passcode)
            }
            R.id.keyFiveTV -> if (passcode.length < 4) {
                passcode += "5"
                passNumber(passcode)
            }
            R.id.keySixTV -> if (passcode.length < 4) {
                passcode += "6"
                passNumber(passcode)
            }
            R.id.keySevenTV -> if (passcode.length < 4) {
                passcode += "7"
                passNumber(passcode)
            }
            R.id.keyEightTV -> if (passcode.length < 4) {
                passcode += "8"
                passNumber(passcode)
            }
            R.id.keyNineTV -> if (passcode.length < 4) {
                passcode += "9"
                passNumber(passcode)
            }
            R.id.backActionIV ->
                if (passcode != "") {
                    passcode = passcode.substring(0, passcode.length - 1)
                    passNumberClear(passcode)
                }
        }
    }

    private fun passNumberClear(passcode: String) {
        when (passcode.length) {
            3 -> {
                binding.chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle)
                binding.circleFourLL.setBackgroundResource(R.drawable.ic_outline_circle)
            }
            2 -> {
                binding.chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle)
                binding.circleThreeLL.setBackgroundResource(R.drawable.ic_outline_circle)
            }
            1 -> {
                binding.chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle)
                binding.circleTwoLL.setBackgroundResource(R.drawable.ic_outline_circle)
            }
            0 -> {
                binding.chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle)
                binding.circleOneLL.setBackgroundResource(R.drawable.ic_outline_circle)
            }
        }
    }


    private fun passNumber(passcode: String) {
        if (passcode.length == 0) {
            clearPasscodes()
        } else {
            when (passcode.length) {
                1 -> binding.chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle_white)
                2 -> binding.chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle_white)
                3 -> binding.chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle_white)
                4 -> {
                    binding.chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle_white)
                    checkAndProceed()
                }
            }
        }
    }

    private fun checkAndProceed() {
        val request = ValidateRequest()
        request.pin = passcode
        request.actionType = action
        pinViewModel.validateCoyniPin(request)
    }

    private fun clearPasscodes() {
        binding.chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle)
        binding.chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle)
        binding.chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle)
        binding.chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle)
    }

    private fun setErrorPIN() {
        count = count + 1;

        if (count != 0 && count < 4) {
            binding.invalidTV.visibility = VISIBLE
        } else {
            binding.invalidTV.visibility = VISIBLE
            binding.inValidErrorTV.visibility = VISIBLE
        }
        binding.circleOneLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error))
        binding.circleTwoLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error))
        binding.circleThreeLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error))
        binding.circleFourLL.setBackground(getDrawable(R.drawable.ic_outline_circle_error))

        binding.chooseCircleOne.setBackgroundResource(R.drawable.ic_baseline_circle_error)
        binding.chooseCircleTwo.setBackgroundResource(R.drawable.ic_baseline_circle_error)
        binding.chooseCircleThree.setBackgroundResource(R.drawable.ic_baseline_circle_error)
        binding.chooseCircleFour.setBackgroundResource(R.drawable.ic_baseline_circle_error)

//        shakeAnimateLeftRight()
        Handler().postDelayed({
            try {
                clearControls()
                passcode = ""
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }, 2000)
    }

    private fun shakeAnimateLeftRight() {
        binding.pinLL.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake))
    }

    private fun clearControls() {
        binding.invalidTV.visibility = GONE
        binding.inValidErrorTV.visibility = GONE
        binding.circleOneLL.setBackgroundResource(R.drawable.ic_outline_circle)
        binding.circleTwoLL.setBackgroundResource(R.drawable.ic_outline_circle)
        binding.circleThreeLL.setBackgroundResource(R.drawable.ic_outline_circle)
        binding.circleFourLL.setBackgroundResource(R.drawable.ic_outline_circle)
        clearPasscodes()
    }

    fun sendSuccessResult(intent: Intent) {
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun inItObservers() {
        pinViewModel.validatePinResponse.observe(this) { ValidateResponse ->
            try {
                if (ValidateResponse!!.data != null) {
                    if (ValidateResponse.status == Utils.SUCCESS) {
                        Handler().postDelayed({
                            val `in` = Intent()
                            `in`.putExtra(Utils.ACTION_TYPE, action)
                            `in`.putExtra(
                                Utils.TRANSACTION_TOKEN, ValidateResponse.data?.requestToken
                            )
                            sendSuccessResult(`in`)
                        }, 200)
                    } else {
                        setErrorPIN()
                    }
                } else {
                    setErrorPIN()
//                    val intent = Intent(this, RefundTransactionActivity::class.java)
//                    startActivity(intent)
//                    finish()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }
}


