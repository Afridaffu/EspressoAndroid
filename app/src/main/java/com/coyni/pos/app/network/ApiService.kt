package com.coyni.pos.app.network

import com.coyni.pos.app.model.appupdate.AppUpdateResp
import com.coyni.pos.app.model.login.BiometricSignIn
import com.coyni.pos.app.model.login.LoginRequest
import com.coyni.pos.app.model.pin.PinRegisterResponse
import com.coyni.pos.app.model.pin.RegisterPinRequest
import com.coyni.pos.app.model.pin.ValidateRequest
import com.coyni.pos.app.model.pin.ValidateResponse
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @GET("api/v2/app-version/retrieve")
    fun getAppUpdate(@Query("osType") osType: String?): Call<AppUpdateResp?>?

    @POST("api/v2/user/signin")
    fun loginNew(@Body loginRequest: LoginRequest?): Call<BiometricSignIn>

    @POST("api/v2/coyni-pin/register")
    fun coyniPinRegister(@Body request: RegisterPinRequest?): Call<PinRegisterResponse?>?

    @PATCH("api/v2/coyni-pin/validate")
    fun validateCoyniPin(@Body request: ValidateRequest?): Call<ValidateResponse?>?
}