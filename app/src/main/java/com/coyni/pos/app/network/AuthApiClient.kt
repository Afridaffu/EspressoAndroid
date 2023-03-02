package com.coyni.pos.app.network

import android.util.Log
import com.google.gson.Gson
import com.coyni.pos.app.BuildConfig
import com.coyni.pos.app.network.ApiClient.*
import com.coyni.pos.app.utils.Utils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLHandshakeException

class AuthApiClient {
    private val TAG = javaClass.simpleName
    private val TIME_OUT = 120
    private val interceptor = HttpLoggingInterceptor().setLevel(
        if (BuildConfig.LOGGING_ENABLED) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    )
    private val tokenInterceptor = TokenInterceptor()
    private val encryptionInterceptor = CustomEncryptionHandler()
    private val client: OkHttpClient = OkHttpClient.Builder().connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
        .readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS).addInterceptor(tokenInterceptor)
        .addInterceptor(encryptionInterceptor).addInterceptor(interceptor).build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.URL_PRODUCTION)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private class TokenInterceptor : Interceptor {
        @Throws(
            NoConnectivityException::class,
            SessionTimeOutException::class,
            ConnectionInterruptedException::class
        )
        override fun intercept(chain: Interceptor.Chain): Response {
            var initialRequest: Request = chain.request()
            val requestBuild: Request.Builder =
                initialRequest.newBuilder()
                                        .addHeader(KEY_AUTHORIZATION, "Bearer " + Utils.strAuth);
            initialRequest = requestBuild.build()
            lateinit var response: Response
            response = try {
                chain.proceed(initialRequest)
            } catch (ste: SocketTimeoutException) {
                throw SessionTimeOutException()
            } catch (she: SSLHandshakeException) {
                throw ConnectionInterruptedException(TYPE_CONNECTION_INTERRUPT)
            } catch (ex: Exception) {
                ex.printStackTrace()
                throw ConnectionInterruptedException(TYPE_SOMETHING_WENT_WRONG)
            }
            Log.e("resp auth", Gson().toJson(response.code))
            return response
        }
    }

    companion object {
        const val TYPE_NO_NETWORK = "NO_NETWORK"
        const val TYPE_CONNECTION_INTERRUPT = "CONNECTION_INTERRUPT"
        const val TYPE_SESSION_TIMEOUT = "SESSION_TIMEOUT"
        private const val TYPE_SOMETHING_WENT_WRONG = "WENT_WRONG"
        private const val KEY_AUTHORIZATION = "Authorization"
        private var apiClient: AuthApiClient? = null
        val instance: Retrofit
            get() {
                if (apiClient == null) apiClient = AuthApiClient()
                return apiClient!!.retrofit
            }
    }
}