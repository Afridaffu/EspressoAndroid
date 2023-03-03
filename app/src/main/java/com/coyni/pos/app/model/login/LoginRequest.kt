package com.coyni.pos.app.model.login

data class LoginRequest(
    private val terminalId: String? = null,
    private val password: String? = null
)