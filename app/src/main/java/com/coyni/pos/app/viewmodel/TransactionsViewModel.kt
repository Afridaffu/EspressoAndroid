package com.coyni.pos.app.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.coyni.pos.app.model.TransactionFilter.TransactionResponse
import com.coyni.pos.app.model.TransactionFilter.TransactionListReq
import com.coyni.pos.app.network.ApiService
import com.coyni.pos.app.network.AuthApiClient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionsViewModel (application: Application) : AndroidViewModel(application){

    val transactionResponse = MutableLiveData<TransactionResponse?>()

    fun allTransactionsList(request: TransactionListReq) {
        try {
            val apiService = AuthApiClient.instance.create(ApiService::class.java)
            val mCall = apiService.transactionList(request)
            mCall!!.enqueue(object : Callback<TransactionResponse?> {
                override fun onResponse(
                    call: Call<TransactionResponse?>,
                    response: Response<TransactionResponse?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val obj = response.body()
                            transactionResponse.setValue(obj)
                        } else {
                            val gson = Gson()
                            val type = object : TypeToken<TransactionResponse?>() {}.type
                            var errorResponse: TransactionResponse? = null
                            try {
                                errorResponse = gson.fromJson(response.errorBody()!!.string(), type)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            transactionResponse.setValue(errorResponse)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<TransactionResponse?>, t: Throwable) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG)
                        .show()
                    transactionResponse.value = null
                }
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}