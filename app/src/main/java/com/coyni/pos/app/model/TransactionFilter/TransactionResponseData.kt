package com.coyni.pos.app.model.TransactionFilter

data class TransactionResponseData(
    var items: List<TransactionItem>? = null,
    var currentPageNo: Int? = null,
    var pageSize: Int? = null,
    var totalItems: Int? = null,
    var totalPages: Int? = null
)
