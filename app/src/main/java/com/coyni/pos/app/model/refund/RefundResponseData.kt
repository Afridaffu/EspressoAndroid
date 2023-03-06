package com.coyni.pos.app.model.refund

class RefundResponseData(
    var insufficientMerchantBalance: Boolean = false,
    var insufficientTokenBalance: Boolean = false,
    var processingFee: Double? = null,
    var walletBalance: String? = null,
    var referenceId: String? = null,
    var walletType: Int? = null
)