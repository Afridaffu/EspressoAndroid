package com.coyni.pos.app.model.refund

data class RefundProcessRequest(
    var gbxTransactionId: String? = null,
    var refundReason: String? = null,
    var refundAmount: Double? = null,
    var walletType: Int? = null,
    var requestToken: String? = null,
)
