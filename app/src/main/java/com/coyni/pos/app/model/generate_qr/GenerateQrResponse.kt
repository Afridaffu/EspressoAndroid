package com.coyni.pos.app.model.generate_qr

import com.coyni.pos.app.model.BaseResponse
import com.coyni.pos.app.model.Error
import com.coyni.pos.app.model.pin.ValidateResponseData

data class GenerateQrResponse(
    var data: GenerateQrResponseData? = null,
): BaseResponse()
