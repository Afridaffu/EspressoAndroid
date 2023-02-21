package com.coyni.pos.app.model.refund

data class RefundReferenceRequest(
    var gbxTransactionId: String? = null,
    val refundReason: String? = null,
    val refundAmount: Double? = null,
    val walletType: Int? = null,
    val requestToken: String? = null,
)
