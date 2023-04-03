package com.coyni.pos.app.model.login

data class LoginData(
    var terminalKey: Int? = null,
    var timeZone: Int? = null,
    var terminalName: String? = null,
    var terminalId: String? = null,
    var merchantId: Int? = null,
    var jwtToken: String? = null,
    var status: String? = null,
    var passwordFailedAttempts: Int? = null,
    var message: Any? = null,
    var dbaName: String? = null,
    var companyName: String? = null,
    var image: Any? = null
)
