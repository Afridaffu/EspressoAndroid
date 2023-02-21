package com.coyni.pos.app.model.TransactionFilter

data class TransactionsTypeData(
    var isSelected: Boolean? = false,
    var groupItem: String? = null,
    var itemId: Int = 0
)
