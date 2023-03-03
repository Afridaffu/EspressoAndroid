package com.coyni.pos.app.model.pin

import com.coyni.pos.app.model.BaseResponse
import com.coyni.pos.app.model.Error

data class ValidateResponse(
    var data: ValidateResponseData? = null,
) : BaseResponse()
