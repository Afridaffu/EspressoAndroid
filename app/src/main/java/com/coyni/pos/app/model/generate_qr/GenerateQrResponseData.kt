package com.coyni.pos.app.model.generate_qr

data class GenerateQrResponseData(
    val walletId: String? = null,
    val image: String? = null,
    val employeeName: String? = null,
    val companyName: String? = null,
    val businessName: String? = null,
    val uniqueId: String? = null
)
