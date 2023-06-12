package com.coyni.pos.app.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.AnimationUtils
import androidx.lifecycle.ViewModelProvider
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.databinding.ActivityPinBinding
import com.coyni.pos.app.model.pin.ValidateRequest
import com.coyni.pos.app.model.pin.ValidateResponse
import com.coyni.pos.app.model.pin.ValidateResponseData
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.viewmodel.PinViewModel
import com.google.gson.Gson

class PinActivity : BaseActivity(), View.OnClickListener {
    lateinit var binding: ActivityPinBinding
    var passcode: String = ""
    lateinit var pinViewModel: PinViewModel
    var myApplication: MyApplication? = null
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
        pinViewModel = ViewModelProvider(this).get(PinViewModel::class.java)
        myApplication = applicationContext as MyApplication

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
            R.id.backActionIV -> if (passcode != "") {
                passcode = passcode.substring(0, passcode.length - 1)
                passNumberClear(passcode)
            }
        }
    }

    private fun passNumberClear(passcode: String) {
        when (passcode.length) {
            3 -> {
                binding.circleFourIV.setImageResource(R.drawable.clear_pin_ic)
            }
            2 -> {
                binding.circleThreeIV.setImageResource(R.drawable.clear_pin_ic)
            }
            1 -> {
                binding.circleTwoIV.setImageResource(R.drawable.clear_pin_ic)
            }
            0 -> {
                binding.circleOneIV.setImageResource(R.drawable.clear_pin_ic)
            }
        }
    }


    private fun passNumber(passcode: String) {
        if (passcode.length == 0) {
            clearPasscodes()
        } else {
            when (passcode.length) {
                1 -> binding.circleOneIV.setImageResource(R.drawable.ic_enter_pin)
                2 -> binding.circleTwoIV.setImageResource(R.drawable.ic_enter_pin)
                3 -> binding.circleThreeIV.setImageResource(R.drawable.ic_enter_pin)
                4 -> {
                    binding.circleFourIV.setImageResource(R.drawable.ic_enter_pin)
                    checkAndProceed()
                }
            }
        }
    }

    private fun checkAndProceed() {
        val request = ValidateRequest()
        request.pin = passcode
        pinViewModel.validateCoyniPin(request)
    }

    private fun clearPasscodes() {
        binding.circleOneIV.setImageResource(R.drawable.clear_pin_ic)
        binding.circleTwoIV.setImageResource(R.drawable.clear_pin_ic)
        binding.circleThreeIV.setImageResource(R.drawable.clear_pin_ic)
        binding.circleFourIV.setImageResource(R.drawable.clear_pin_ic)
    }

    private fun setErrorPIN() {
        count = count + 1;

        if (count != 0 && count < 4) {
            binding.invalidTV.visibility = VISIBLE
        } else {
            binding.invalidTV.visibility = VISIBLE
            binding.inValidErrorTV.visibility = VISIBLE
        }
        binding.circleOneIV.setImageResource(R.drawable.error_pin_ic)
        binding.circleTwoIV.setImageResource(R.drawable.error_pin_ic)
        binding.circleThreeIV.setImageResource(R.drawable.error_pin_ic)
        binding.circleFourIV.setImageResource(R.drawable.error_pin_ic)

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
        clearPasscodes()
    }

    fun sendSuccessResult(intent: Intent) {
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun inItObservers() {
        pinViewModel.validatePinResponse.observe(this) { validateResponse ->
            try {
                if (validateResponse != null) {
                    if (validateResponse.status == Utils.SUCCESS) {
                        myApplication?.mCurrentUserData?.validateResponseData =
                            validateResponse.data
                        Utils.employeeRole = validateResponse.data!!.empRole!!
                        Handler().postDelayed({
                            val intent = Intent()
                            intent.putExtra(Utils.ACTION_TYPE, action)
                            intent.putExtra(
                                Utils.TRANSACTION_TOKEN, validateResponse.data?.token
                            )
                            if (action == Utils.REFUND && validateResponse.data!!.empRole == Utils.EMPROLE) {
                                setErrorPIN()
                            } else {
                                sendSuccessResult(intent)
                            }
                        }, 200)
                    } else {
                        setErrorPIN()
                    }
                } else {
                    setErrorPIN()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

}



