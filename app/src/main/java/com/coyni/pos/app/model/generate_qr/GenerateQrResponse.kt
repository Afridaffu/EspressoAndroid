package com.coyni.pos.app.model.generate_qr

import com.coyni.pos.app.model.Error
import com.coyni.pos.app.model.pin.ValidateResponseData

data class GenerateQrResponse(
    var status: String? = null,
    val timestamp: String? = null,
    var data: GenerateQrResponseData? = null,
    val error: Error? = null
)
