package com.coyni.pos.app.utils

import com.coyni.pos.app.model.TransactionFilter.TransactionResponse
import com.coyni.pos.app.model.generate_qr.GenerateQrResponseData
import com.coyni.pos.app.model.login.LoginData
import com.coyni.pos.app.model.pin.ValidateResponseData
import com.coyni.pos.app.model.refund.RefundResponseData

class UserData {
    var strPreference = "PST"
    var validateResponseData: ValidateResponseData? = null
    var generateQrResponseData: GenerateQrResponseData? = null
    var refundResponseData: RefundResponseData? = null
    var loginData: LoginData? = null
    var transactionResponse: TransactionResponse? = null
}