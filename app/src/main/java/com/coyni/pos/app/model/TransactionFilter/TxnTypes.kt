package com.coyni.pos.app.model.TransactionFilter

data class TxnTypes(
    var txnType: Int? = null,
    var txnSubTypes: ArrayList<Int> = ArrayList()
)
