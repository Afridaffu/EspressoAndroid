package com.coyni.pos.app.model.discard

import com.coyni.pos.app.model.Error

data class DiscardSaleResponse(
    var status: String? = null,
    val timestamp: String? = null,
    var data: DiscardSaleResponseData? = null,
    val error: Error? = null
)
