package com.coyni.pos.app.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.coyni.pos.app.model.pin.ValidateRequest
import com.coyni.pos.app.model.pin.ValidateResponse
import com.coyni.pos.app.network.ApiService
import com.coyni.pos.app.network.AuthApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PinViewModel (application: Application) : AndroidViewModel(application) {


    val validatePinResponse = MutableLiveData<ValidateResponse?>()

    fun validateCoyniPin(request: ValidateRequest) {
        try {
            val apiService = AuthApiClient.instance.create(ApiService::class.java)
            val mCall = apiService.validateCoyniPin(request)
            mCall!!.enqueue(object : Callback<ValidateResponse?> {
                override fun onResponse(
                    call: Call<ValidateResponse?>,
                    response: Response<ValidateResponse?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val obj = response.body()
                            validatePinResponse.setValue(obj)
                        } else {
                            val gson = Gson()
                            val type = object : TypeToken<ValidateResponse?>() {}.type
                            var errorResponse: ValidateResponse? = null
                            try {
                                errorResponse = gson.fromJson(response.errorBody()!!.string(), type)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            validatePinResponse.setValue(errorResponse)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<ValidateResponse?>, t: Throwable) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show()
                    validatePinResponse.value = null
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}