package com.coyni.pos.app.model.login


class LoginData(
    var terminalKey: Int? = null,
    val terminalName: String? = null,
    val terminalId: String? = null,
    val merchantId: Int? = null,
    val jwtToken: String? = null,
    val status: String? = null,
    val passwordFailedAttempts: Int? = null,
    val message: Any? = null,
    val dbaName: String? = null,
    val companyName: String? = null,
    val image: Any? = null
)
