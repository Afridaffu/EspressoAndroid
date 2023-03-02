package com.coyni.pos.app.model.pin

data class ValidateResponseData(
    val employeeId: Int? = null,
    val token: String? = null,
    val employeeName: String? = null,
    val dbaName: String? = null,
    val empRole: String? = null,
)
