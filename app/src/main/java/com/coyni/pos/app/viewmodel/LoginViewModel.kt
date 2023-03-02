package com.coyni.pos.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.coyni.pos.app.model.login.LoginResponse
import com.coyni.pos.app.model.login.LoginRequest
import com.coyni.pos.app.network.ApiService
import com.coyni.pos.app.network.AuthApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    val loginResponseMutableLiveData = MutableLiveData<LoginResponse?>()

    fun getLoginData(request: LoginRequest?) {
        val apiService: ApiService = AuthApiClient.instance.create(ApiService::class.java)
        val call: Call<LoginResponse> = apiService.login(request)
        call.enqueue(object : Callback<LoginResponse?> {
            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                if (response.isSuccessful) {
                    val listResponse: LoginResponse? = response.body()
                    loginResponseMutableLiveData.value = listResponse
                } else {
                    val gson = Gson()
                    val type: Type = object : TypeToken<LoginResponse?>() {}.type
                    val errorResponse: LoginResponse? =
                        gson.fromJson(response.errorBody()?.string(), type)
                    loginResponseMutableLiveData.value = errorResponse
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                loginResponseMutableLiveData.value = null
            }

        })
    }
}