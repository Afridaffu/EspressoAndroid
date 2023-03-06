package com.coyni.pos.app.model.TransactionFilter

import com.coyni.pos.app.model.BaseResponse

data class TransactionResponse(
    var data:TransactionResponseData? = null,
) : BaseResponse()
