package com.coyni.pos.app.model.refund

import com.coyni.pos.app.model.Error
import com.coyni.pos.app.model.pin.ValidateResponseData

data class RefundResponse(
    var status: String? = null,
    val timestamp: String? = null,
    var data: RefundResponseData? = null,
    val error: Error? = null
)
