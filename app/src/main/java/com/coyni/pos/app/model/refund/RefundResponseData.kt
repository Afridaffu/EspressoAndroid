package com.coyni.pos.app.model.refund

class RefundResponseData(
    val insufficientMerchantBalance: Boolean = false,
    val insufficientTokenBalance: Boolean = false,
    val processingFee: Double? = null,
    val walletBalance: String? = null,
    val referenceId: String? = null,
    val walletType: Int? = null
)