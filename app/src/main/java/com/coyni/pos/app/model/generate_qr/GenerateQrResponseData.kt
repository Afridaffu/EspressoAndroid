package com.coyni.pos.app.model.generate_qr

data class GenerateQrResponseData(
    var walletId: String? = null,
    var image: String? = null,
    var employeeName: String? = null,
    var companyName: String? = null,
    var businessName: String? = null,
    var uniqueId: String? = null,
    val mposWebsocket: String? = null
)
