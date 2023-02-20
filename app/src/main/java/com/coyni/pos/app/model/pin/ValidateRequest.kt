package com.coyni.pos.app.model.pin

data class ValidateRequest(
    var actionType: String? = null,
    var pin: String? = null
)
