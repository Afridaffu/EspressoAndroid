package com.coyni.pos.app.model.pin


data class PinRegisterResponse(
    var status: String?,
    var timestamp: String?,
    var data: Data?,
    var error: Error
) {
    data class Data(
        var message: String?
    )
}




