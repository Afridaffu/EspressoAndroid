package com.coyni.pos.app.network

import com.coyni.pos.app.model.BatchAmount.BatchAmountRequest
import com.coyni.pos.app.model.BatchAmount.BatchAmountResponse
import com.coyni.pos.app.model.TransactionData
import com.coyni.pos.app.model.TransactionFilter.TransactionResponse
import com.coyni.pos.app.model.TransactionFilter.TransactionListReq
import com.coyni.pos.app.model.appupdate.AppUpdateResp
import com.coyni.pos.app.model.discard.DiscardSaleRequest
import com.coyni.pos.app.model.discard.DiscardSaleResponse
import com.coyni.pos.app.model.downloadurl.DownloadUrlRequest
import com.coyni.pos.app.model.downloadurl.DownloadUrlResponse
import com.coyni.pos.app.model.generate_qr.GenerateQrRequest
import com.coyni.pos.app.model.generate_qr.GenerateQrResponse
import com.coyni.pos.app.model.login.LoginRequest
import com.coyni.pos.app.model.logout.LogoutResponse
import com.coyni.pos.app.model.login.LoginResponse
import com.coyni.pos.app.model.pin.ValidateRequest
import com.coyni.pos.app.model.pin.ValidateResponse
import com.coyni.pos.app.model.refund.RefundProcessRequest
import com.coyni.pos.app.model.refund.RefundResponse
import com.coyni.pos.app.model.refund.RefundVerifyRequest
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @POST("api/v2/m-pos/login")
    fun login(@Body loginRequest: LoginRequest?): Call<LoginResponse>

    @POST("api/v2/m-pos/logout")
    fun logout(): Call<LogoutResponse>

    @POST("api/v2/profile/download-url")
    fun downloadUrl(@Body downloadUrlRequest: List<DownloadUrlRequest?>): Call<DownloadUrlResponse>

    @GET("api/v2/app-version/retrieve")
    fun getAppUpdate(@Query("osType") osType: String?): Call<AppUpdateResp?>?

    @POST("api/v2/m-pos/generate-qrcode")
    fun generateQR(@Body generateQrRequest: GenerateQrRequest?): Call<GenerateQrResponse>?

    @POST("api/v2/node/mpos/refund/verify")
    fun refundVerify(@Body refundVerifyRequest: RefundVerifyRequest?): Call<RefundResponse>?

    @POST("api/v2/m-pos/discard-sale")
    fun discardSale(@Body discardSaleRequest: DiscardSaleRequest?): Call<DiscardSaleResponse>?

    @POST("api/v2/node/mpos/refund/process")
    fun refundProcess(@Body refundProcessRequest: RefundProcessRequest?): Call<RefundResponse>?

    @POST("api/v2/m-pos/validate-pin")
    fun validateCoyniPin(@Body request: ValidateRequest?): Call<ValidateResponse>?

    @POST("api/v2/m-pos/transaction/summary")
    fun transactionList(@Body request: TransactionListReq?): Call<TransactionResponse>?

    @POST("api/v2/m-pos/exit-sale")
    fun exitSale(@Query("token") token: String?): Call<DiscardSaleResponse?>?

    @POST("api/v2/m-pos/today-batch")
    fun batchAmount(@Body request: BatchAmountRequest?): Call<BatchAmountResponse>?

    @GET("/api/v2/m-pos/token/info/{gbxTxnId}/{txnType}/{txnSubType}")
    fun transactionDetails(@Path("gbxTxnId") gbxTxnId: String,
                           @Path("txnType") txnType: Int,
                           @Path("txnSubType") txnSubType: Int) : Call<TransactionData>?
}