package com.coyni.pos.app.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.coyni.pos.app.model.BatchAmount.BatchAmountRequest
import com.coyni.pos.app.model.BatchAmount.BatchAmountResponse
import com.coyni.pos.app.model.login.LoginRequest
import com.coyni.pos.app.model.login.LoginResponse
import com.coyni.pos.app.model.pin.ValidateResponse
import com.coyni.pos.app.network.ApiService
import com.coyni.pos.app.network.AuthApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class BatchAmountViewModel(application: Application) : AndroidViewModel(application) {
    val batchResponseMutableLiveData = MutableLiveData<BatchAmountResponse?>()

    fun getBatchAmount(request: BatchAmountRequest?) {
        val apiService: ApiService = AuthApiClient.instance.create(ApiService::class.java)
        val call: Call<BatchAmountResponse> = apiService.batchAmount(request)!!
        call.enqueue(object : Callback<BatchAmountResponse?> {
            override fun onResponse(
                call: Call<BatchAmountResponse?>,
                response: Response<BatchAmountResponse?>
            ) {
                if (response.isSuccessful) {
                    val listResponse: BatchAmountResponse? = response.body()
                    batchResponseMutableLiveData.value = listResponse
                } else {
                    val gson = Gson()
                    val type: Type = object : TypeToken<BatchAmountResponse?>() {}.type
                    val errorResponse: BatchAmountResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    batchResponseMutableLiveData.value = errorResponse
                }
            }

            override fun onFailure(call: Call<BatchAmountResponse?>, t: Throwable) {
                Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show()
                batchResponseMutableLiveData.value = null            }
        })
    }
}