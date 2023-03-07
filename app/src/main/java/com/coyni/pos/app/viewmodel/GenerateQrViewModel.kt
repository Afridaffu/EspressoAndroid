package com.coyni.pos.app.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.coyni.pos.app.model.discard.DiscardSaleRequest
import com.coyni.pos.app.model.discard.DiscardSaleResponse
import com.coyni.pos.app.model.generate_qr.GenerateQrRequest
import com.coyni.pos.app.model.generate_qr.GenerateQrResponse
import com.coyni.pos.app.network.ApiService
import com.coyni.pos.app.network.AuthApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GenerateQrViewModel(application: Application) : AndroidViewModel(application) {

    val generateQrResponse = MutableLiveData<GenerateQrResponse?>()
    val discardSaleResponse = MutableLiveData<DiscardSaleResponse?>()
    val exitSaleResponse = MutableLiveData<DiscardSaleResponse?>()

    fun generateQrRequest(request: GenerateQrRequest) {
        try {
            val apiService = AuthApiClient.instance.create(ApiService::class.java)
            val mCall = apiService.generateQR(request)
            mCall!!.enqueue(object : Callback<GenerateQrResponse?> {
                override fun onResponse(
                    call: Call<GenerateQrResponse?>,
                    response: Response<GenerateQrResponse?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val obj = response.body()
                            generateQrResponse.setValue(obj)
                        } else {
                            val gson = Gson()
                            val type = object : TypeToken<GenerateQrResponse?>() {}.type
                            var errorResponse: GenerateQrResponse? = null
                            try {
                                errorResponse = gson.fromJson(response.errorBody()!!.string(), type)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            generateQrResponse.setValue(errorResponse)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<GenerateQrResponse?>, t: Throwable) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG)
                        .show()
                    generateQrResponse.value = null
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun discardSaleRequest(request: DiscardSaleRequest) {
        try {
            val apiService = AuthApiClient.instance.create(ApiService::class.java)
            val mCall = apiService.discardSale(request)
            mCall!!.enqueue(object : Callback<DiscardSaleResponse?> {
                override fun onResponse(
                    call: Call<DiscardSaleResponse?>,
                    response: Response<DiscardSaleResponse?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val obj = response.body()
                            discardSaleResponse.setValue(obj)
                        } else {
                            val gson = Gson()
                            val type = object : TypeToken<DiscardSaleResponse?>() {}.type
                            var errorResponse: DiscardSaleResponse? = null
                            try {
                                errorResponse = gson.fromJson(response.errorBody()!!.string(), type)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            discardSaleResponse.setValue(errorResponse)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<DiscardSaleResponse?>, t: Throwable) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG)
                        .show()
                    discardSaleResponse.value = null
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun exitSaleRequest(token: String?) {
        try {
            val apiService = AuthApiClient.instance.create(ApiService::class.java)
            val mCall = apiService.exitSale(token)
            mCall!!.enqueue(object : Callback<DiscardSaleResponse?> {
                override fun onResponse(
                    call: Call<DiscardSaleResponse?>,
                    response: Response<DiscardSaleResponse?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val obj = response.body()
                            exitSaleResponse.setValue(obj)
                        } else {
                            val gson = Gson()
                            val type = object : TypeToken<DiscardSaleResponse?>() {}.type
                            var errorResponse: DiscardSaleResponse? = null
                            try {
                                errorResponse = gson.fromJson(response.errorBody()!!.string(), type)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            exitSaleResponse.setValue(errorResponse)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<DiscardSaleResponse?>, t: Throwable) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG)
                        .show()
                    exitSaleResponse.value = null
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

}
