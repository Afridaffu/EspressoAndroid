package com.coyni.pos.app.model.TransactionFilter

class TransactionResponseData(
    var items: ArrayList<Int>? = null,
    var currentPageNo: Int? = null,
    var pageSize: Int? = null,
    var totalItems: Int? = null,
    var totalPages: Int? = null
)
