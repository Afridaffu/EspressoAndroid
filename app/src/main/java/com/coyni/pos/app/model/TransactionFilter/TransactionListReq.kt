package com.coyni.pos.app.model.TransactionFilter

data class TransactionListReq(
    var fromAmount: String? = null,
    var fromDate: String? = null,
    var params: PageParams = PageParams(),
    var requestToken: String? = null,
    var searchKey: String? = null,
    var status: ArrayList<Int>? = null,
    var toAmount: String? = null,
    var toDate: String? = null,
    var txnTypes: ArrayList<TxnTypes>? = null,
    var isFilters: Boolean = false,
)
