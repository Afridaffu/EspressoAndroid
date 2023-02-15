package com.coyni.pos.app.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.coyni.pos.app.R
import com.coyni.pos.app.model.login.LoginRequest
import com.coyni.pos.app.utils.MyApplication
import com.vt.kotlinexamples.retrofit_network.viewmodel.CommonViewModel

class MainActivity : AppCompatActivity() {
    var commonViewModel: CommonViewModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val myApplication: MyApplication = getApplicationContext() as MyApplication

        //View model initialization
        commonViewModel = CommonViewModel.getInstance(this)

        //API call of Checking App Updates
        commonViewModel!!.getAppUpdate(getString(R.string.android_text))

        //Get Response for APP Update
        commonViewModel!!.appUpdateRespMutableLiveData.observe(this,
            { appUpdateResp ->
                try {
                    if (appUpdateResp!!.data != null) {

                    } else {

                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            })

        val loginRequest = LoginRequest()
        loginRequest.setEmail("ttt@gmail.com")
        loginRequest.setPassword("Admin@123")
        commonViewModel!!.loginNew(loginRequest)

        commonViewModel!!.loginNewLiveData.observe(this, { it ->
            try {

            } catch (ex: java.lang.Exception) {

            }
        })
    }
}