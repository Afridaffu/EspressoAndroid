package com.coyni.pos.app.model

open class BaseResponse (
    var status: String? = null,
    var timestamp: String? = null,
    var error: Error? = null)
