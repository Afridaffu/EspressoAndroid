package com.coyni.pos.app.model

import com.coyni.pos.app.model.Error


open class BaseResponse {
    var status: String? = null
    var timestamp: String? = null
    var error: Error? = null
}