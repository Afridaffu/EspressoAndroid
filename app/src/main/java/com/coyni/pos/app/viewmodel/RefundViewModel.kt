package com.coyni.pos.app.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.coyni.pos.app.model.pin.PinRegisterResponse
import com.coyni.pos.app.model.pin.RegisterPinRequest
import com.coyni.pos.app.model.pin.ValidateResponse
import com.coyni.pos.app.model.refund.RefundProcessRequest
import com.coyni.pos.app.model.refund.RefundResponse
import com.coyni.pos.app.model.refund.RefundVerifyRequest
import com.coyni.pos.app.network.ApiClient
import com.coyni.pos.app.network.ApiService
import com.coyni.pos.app.network.AuthApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RefundViewModel(application: Application) : AndroidViewModel(application) {

    val refundVerifyResponse = MutableLiveData<RefundResponse?>()

    val refundProcessResponse = MutableLiveData<RefundResponse?>()


    fun refundVerifyRequest(request: RefundVerifyRequest) {
        try {
            val apiService = AuthApiClient.instance.create(ApiService::class.java)
            val mCall = apiService.refundVerify(request)
            mCall!!.enqueue(object : Callback<RefundResponse?> {
                override fun onResponse(
                    call: Call<RefundResponse?>,
                    response: Response<RefundResponse?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val obj = response.body()
                            refundVerifyResponse.setValue(obj)
                        } else {
                            val gson = Gson()
                            val type = object : TypeToken<RefundResponse?>() {}.type
                            var errorResponse: RefundResponse? = null
                            try {
                                errorResponse = gson.fromJson(response.errorBody()!!.string(), type)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            refundVerifyResponse.setValue(errorResponse)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<RefundResponse?>, t: Throwable) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG)
                        .show()
                    refundVerifyResponse.value = null
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun refundProcessRequest(request: RefundProcessRequest) {
        try {
            val apiService = AuthApiClient.instance.create(ApiService::class.java)
            val mCall = apiService.refundProcess(request)
            mCall!!.enqueue(object : Callback<RefundResponse?> {
                override fun onResponse(
                    call: Call<RefundResponse?>,
                    response: Response<RefundResponse?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val obj = response.body()
                            refundProcessResponse.setValue(obj)
                        } else {
                            val gson = Gson()
                            val type = object : TypeToken<RefundResponse?>() {}.type
                            var errorResponse: RefundResponse? = null
                            try {
                                errorResponse = gson.fromJson(response.errorBody()!!.string(), type)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            refundProcessResponse.setValue(errorResponse)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<RefundResponse?>, t: Throwable) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG)
                        .show()
                    refundProcessResponse.value = null
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}
