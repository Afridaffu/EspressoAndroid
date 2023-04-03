package com.coyni.pos.app.utils

import android.util.Log
import com.coyni.pos.app.model.ActivityLogs.ActivityLogsResponseData
import com.coyni.pos.app.model.BatchAmount.BatchResponseData
import com.coyni.pos.app.model.TransactionData
import com.coyni.pos.app.model.TransactionFilter.TransactionListReq
import com.coyni.pos.app.model.TransactionFilter.TransactionResponseData
import com.coyni.pos.app.model.downloadurl.DownloadUrlData
import com.coyni.pos.app.model.generate_qr.GenerateQrResponseData
import com.coyni.pos.app.model.login.LoginData
import com.coyni.pos.app.model.pin.ValidateResponseData
import com.coyni.pos.app.model.refund.RefundResponse
import com.coyni.pos.app.model.refund.RefundResponseData
import org.json.JSONObject

class UserData {
    var strPreference = "PST"
    var validateResponseData: ValidateResponseData? = null
    var generateQrResponseData: GenerateQrResponseData? = null
    var refundResponseData: RefundResponseData? = null
    var refundResponse: RefundResponse? = null
    var loginData: LoginData? = null
    var downloadUrlData: ArrayList<DownloadUrlData>? = null
    var transactionResponse: TransactionResponseData? = null
    var batchResponse: BatchResponseData? = null
    var webSocketObject: JSONObject? = null
    var transactionData: TransactionData? = null
    var UserType: String? = null
    var activityLogsResponseData : List<ActivityLogsResponseData>? = null
    var transactionId: Int? = null
    var transactionListReq: TransactionListReq? = null


    fun convertZoneLatestTxndate(date: String?): String? {
        return Utils.convertZoneLatestTxn(date, strPreference)
    }

    fun convertZoneDateTime(date: String?, format: String?, requiredFormat: String?): String? {
        return Utils.convertZoneDateTime(date, format, requiredFormat, strPreference)
    }
    fun initializeTransactionSearch() {
        transactionListReq = TransactionListReq()
    }
}