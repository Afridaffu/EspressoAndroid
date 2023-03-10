package com.coyni.pos.app.model.TransactionFilter

import java.util.ArrayList

data class TxnTypes(
    var txnType: Int? = null,
    var txnSubTypes: ArrayList<Int>? = null
)
