package com.coyni.pos.app.model.pin
import com.coyni.pos.app.model.Error

data class ValidateResponse(
    var status: String? = null,
    val timestamp: String? = null,
    var data: ValidateResponseData? = null,
    val error: Error? = null
)
