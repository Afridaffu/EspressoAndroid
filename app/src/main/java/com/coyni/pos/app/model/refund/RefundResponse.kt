package com.coyni.pos.app.model.refund

import com.coyni.pos.app.model.BaseResponse
import com.coyni.pos.app.model.Error
import com.coyni.pos.app.model.pin.ValidateResponseData

data class RefundResponse(
    var data: RefundResponseData? = null,
): BaseResponse()
