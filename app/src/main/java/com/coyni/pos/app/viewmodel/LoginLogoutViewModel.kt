package com.coyni.pos.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.coyni.pos.app.model.downloadurl.DownloadUrlRequest
import com.coyni.pos.app.model.downloadurl.DownloadUrlResponse
import com.coyni.pos.app.model.login.LoginRequest
import com.coyni.pos.app.model.login.LoginResponse
import com.coyni.pos.app.model.logout.LogoutResponse
import com.coyni.pos.app.network.ApiService
import com.coyni.pos.app.network.AuthApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type

class LoginLogoutViewModel(application: Application) : AndroidViewModel(application) {
    val loginResponseMutableLiveData = MutableLiveData<LoginResponse?>()
    val logoutResponseMutableLiveData = MutableLiveData<LogoutResponse?>()
    val downloadUrlResponseMutableLiveData = MutableLiveData<DownloadUrlResponse?>()

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

    fun getLogout() {
        val apiService: ApiService = AuthApiClient.instance.create(ApiService::class.java)
        val call: Call<LogoutResponse> = apiService.logout()
        call.enqueue(object : Callback<LogoutResponse> {

            override fun onResponse(
                call: Call<LogoutResponse>,
                response: Response<LogoutResponse>
            ) {
                if (response.isSuccessful) {
                    val listResponse: LogoutResponse? = response.body()
                    logoutResponseMutableLiveData.value = listResponse
                } else {
                    val gson = Gson()
                    val type: Type = object : TypeToken<LogoutResponse>() {}.type
                    val errorResponse: LogoutResponse =
                        gson.fromJson(response.errorBody()?.string(), type)
                    logoutResponseMutableLiveData.value = errorResponse
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                logoutResponseMutableLiveData.value = null
            }

        })
    }

    fun downloadUrl(request: ArrayList<DownloadUrlRequest>) {
        val apiService: ApiService = AuthApiClient.instance.create(ApiService::class.java)
        val call: Call<DownloadUrlResponse> = apiService.downloadUrl(request)
        call.enqueue(object : Callback<DownloadUrlResponse> {

            override fun onResponse(
                call: Call<DownloadUrlResponse>,
                response: Response<DownloadUrlResponse>
            ) {
                if (response.isSuccessful) {
                    val listResponse: DownloadUrlResponse? = response.body()
                    downloadUrlResponseMutableLiveData.value = listResponse
                } else {
                    val gson = Gson()
                    val type: Type = object : TypeToken<DownloadUrlResponse>() {}.type
                    val errorResponse: DownloadUrlResponse =
                        gson.fromJson(response.errorBody()?.string(), type)
                    downloadUrlResponseMutableLiveData.value = errorResponse
                }
            }

            override fun onFailure(call: Call<DownloadUrlResponse>, t: Throwable) {
                downloadUrlResponseMutableLiveData.value = null
            }

        })
    }

}