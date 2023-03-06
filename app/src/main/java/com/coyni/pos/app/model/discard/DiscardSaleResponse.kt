package com.coyni.pos.app.model.discard

import com.coyni.pos.app.model.BaseResponse
import com.coyni.pos.app.model.Error

data class DiscardSaleResponse(
    var data: DiscardSaleResponseData? = null,
) : BaseResponse()
