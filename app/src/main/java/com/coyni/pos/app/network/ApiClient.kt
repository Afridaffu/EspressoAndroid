package com.coyni.pos.app.network

import com.google.gson.GsonBuilder
import com.coyni.pos.app.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLHandshakeException

class ApiClient {
    private val interceptor = HttpLoggingInterceptor().setLevel(
        if (BuildConfig.LOGGING_ENABLED) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    )
    private val encryptionInterceptor = CustomEncryptionHandler()
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
        .readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
        .addInterceptor(encryptionInterceptor)
        .addInterceptor(interceptor)
        .followRedirects(false).build()

    var gsonBuilder = GsonBuilder()
    var gson = gsonBuilder.create()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.URL_PRODUCTION)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    class NoConnectivityException : IOException()

    class ConnectionInterruptedException
    internal constructor(reason: String?) : SSLHandshakeException(reason)

    class SessionTimeOutException : SocketTimeoutException()

    companion object {
        val TYPE_NO_NETWORK = "NO_NETWORK"
        val TYPE_CONNECTION_INTERRUPT = "CONNECTION_INTERRUPT"
        val TYPE_SESSION_TIMEOUT = "SESSION_TIMEOUT"
        val TYPE_SOMETHING_WENT_WRONG = "WENT_WRONG"
        val TIME_OUT = 120

        private var apiClient: ApiClient? = null
        fun getInstance(): Retrofit {
            if (apiClient == null) {
                apiClient = ApiClient()
            }
            return apiClient!!.retrofit
        }

    }
}