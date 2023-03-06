package com.coyni.pos.app.model.login

data class LoginRequest(
    var terminalId: String? = null,
    var password: String? = null
)