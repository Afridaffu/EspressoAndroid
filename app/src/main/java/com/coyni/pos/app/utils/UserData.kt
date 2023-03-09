package com.coyni.pos.app.utils

import com.coyni.pos.app.model.BatchAmount.BatchAmountResponse
import com.coyni.pos.app.model.BatchAmount.BatchResponseData
import com.coyni.pos.app.model.TransactionFilter.TransactionResponseData
import com.coyni.pos.app.model.generate_qr.GenerateQrResponseData
import com.coyni.pos.app.model.login.LoginData
import com.coyni.pos.app.model.pin.ValidateResponseData
import com.coyni.pos.app.model.refund.RefundResponseData
import org.json.JSONObject

class UserData {
    var strPreference = "PST"
    var validateResponseData: ValidateResponseData? = null
    var generateQrResponseData: GenerateQrResponseData? = null
    var refundResponseData: RefundResponseData? = null
    var loginData: LoginData? = null
    var transactionResponse: TransactionResponseData? = null
    var batchResponse: BatchResponseData? = null
    var webSocketObject: JSONObject? = null
}