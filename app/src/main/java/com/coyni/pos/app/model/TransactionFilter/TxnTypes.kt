package com.coyni.pos.app.model.TransactionFilter

import java.util.ArrayList

data class TxnTypes(
    var txnSubTypes: Int? = null,
    var txnType: ArrayList<Int>? = null)
