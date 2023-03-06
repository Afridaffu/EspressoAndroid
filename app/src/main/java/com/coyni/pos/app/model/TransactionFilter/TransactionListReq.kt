package com.coyni.pos.app.model.TransactionFilter

data class TransactionListReq(
    var fromAmount: Int? = null,
    var fromDate: String? = null,
    var requestToken: String? = null,
    var status: String? = null,
    var toAmount: Int? = null,
    var toDate: String? = null,
    var data: TxnTypes? = null
)
