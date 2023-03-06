package com.coyni.pos.app.model.TransactionFilter

data class TransactionResponseData(
    private val items: ArrayList<Int>? = null,
    private val currentPageNo: Int? = null,
    private val pageSize: Int? = null,
    private val totalItems: Int? = null,
    private val totalPages: Int? = null
)
