package com.vt.kotlinexamples.retrofit_network.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.coyni.pos.app.model.appupdate.AppUpdateResp
import com.coyni.pos.app.model.login.BiometricSignIn
import com.coyni.pos.app.model.login.LoginRequest
import com.coyni.pos.app.network.ApiClient
import com.coyni.pos.app.network.ApiClient.Companion.getInstance
import com.coyni.pos.app.network.ApiService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommonViewModel(application: Application) : AndroidViewModel(application) {
    // Follow below structure for creating API calls
    // App Update Section
    val appUpdateRespMutableLiveData = MutableLiveData<AppUpdateResp?>()
    val loginNewLiveData = MutableLiveData<BiometricSignIn?>()

    fun getAppUpdate(osType: String?) {
        try {
            val apiService = getInstance().create(
                ApiService::class.java
            )
            val mCall = apiService.getAppUpdate(osType)
            mCall!!.enqueue(object : Callback<AppUpdateResp?> {
                override fun onResponse(
                    call: Call<AppUpdateResp?>,
                    response: Response<AppUpdateResp?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val obj = response.body()
                            appUpdateRespMutableLiveData.setValue(obj)
                        } else {
                            val gson = Gson()
                            val type = object : TypeToken<AppUpdateResp?>() {}.type
                            var errorResponse: AppUpdateResp? = null
                            try {
                                errorResponse = gson.fromJson(response.errorBody()!!.string(), type)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            appUpdateRespMutableLiveData.setValue(errorResponse)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<AppUpdateResp?>, t: Throwable) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show()
                    appUpdateRespMutableLiveData.value = null
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    } // App Update Section

//    fun loginNew(loginRequest: LoginRequest?) {
//        try {
//            val apiService: ApiService = ApiClient.getInstance().create(ApiService::class.java)
//            val mCall: Call<BiometricSignIn> = apiService.loginNew(loginRequest)
//            mCall.enqueue(object : Callback<BiometricSignIn?> {
//                override fun onResponse(
//                    call: Call<BiometricSignIn?>,
//                    response: Response<BiometricSignIn?>
//                ) {
//                    try {
//                        var strResponse = ""
//                        if (response.isSuccessful()) {
//                            val obj: BiometricSignIn? = response.body()
//                            loginNewLiveData.setValue(obj)
//                        } else if (response.code() == 500) {
//                            strResponse = response.errorBody()!!.string()
//                            val gson = Gson()
//                            val type = object : TypeToken<BiometricSignIn?>() {}.type
//                            val errorResponse: BiometricSignIn = gson.fromJson(
//                                response.errorBody()!!.charStream(), type
//                            )
//
//                        } else {
//
//                        }
//                    } catch (ex: java.lang.Exception) {
//                        ex.printStackTrace()
//                    }
//                }
//
//                override fun onFailure(call: Call<BiometricSignIn?>, t: Throwable) {
//                    loginNewLiveData.setValue(null)
//                }
//            })
//        } catch (ex: java.lang.Exception) {
//            ex.printStackTrace()
//        }
//    }

//    companion object {
//        private var sInstance: CommonViewModel? = null
//
//        @Synchronized
//        fun getInstance(context: Context): CommonViewModel? {
//            if (sInstance == null) {
//                sInstance = CommonViewModel(context)
//            }
//            return sInstance
//        }
//    }
}