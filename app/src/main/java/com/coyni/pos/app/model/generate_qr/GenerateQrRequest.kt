package com.coyni.pos.app.model.generate_qr

data class GenerateQrRequest(
    var amount: Double? = null,
    var isQrCodeEnable: Boolean? = false,
    var requestToken: String? = null,
)
