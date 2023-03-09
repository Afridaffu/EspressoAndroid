package com.coyni.pos.app.model

import com.coyni.pos.app.model.refund.RefundResponseData

data class TransactionDetailsResponse(
    var data: TransactionData? = null,
) : BaseResponse()
