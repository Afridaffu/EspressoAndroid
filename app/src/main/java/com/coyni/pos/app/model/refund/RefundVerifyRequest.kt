package com.coyni.pos.app.model.refund

data class RefundVerifyRequest(
    var gbxTransactionId: String? = null,
    var refundAmount: Double? = null,
    var refundReason: String? = null
)
