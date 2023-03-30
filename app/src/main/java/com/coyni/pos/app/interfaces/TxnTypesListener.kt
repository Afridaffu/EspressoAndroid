package com.coyni.pos.app.interfaces

import com.coyni.pos.app.model.TransactionFilter.TransactionsSubTypeData
import com.coyni.pos.app.model.TransactionFilter.TransactionsTypeData

interface TxnTypesListener {
    fun onCheckBoxClick(transactionsSubTypeData: HashMap<Int, List<TransactionsSubTypeData>>)
}