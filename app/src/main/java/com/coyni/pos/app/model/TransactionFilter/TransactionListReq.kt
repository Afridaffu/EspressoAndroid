package com.coyni.pos.app.model.TransactionFilter

import com.coyni.pos.app.utils.Utils

data class TransactionListReq(
    var fromAmount: String = "",
    var fromDate: String? = null,
//    var fromDate: String? = if (!Utils.employeeRole.equals(
//            Utils.EMPROLE,
//            true
//        )
//    ) Utils.getCurrentFromDate() else null,
    var params: PageParams = PageParams(),
    var requestToken: String? = null,
    var searchKey: String? = null,
    var status: ArrayList<Int>? = null,
    var toAmount: String = "",
    var toDate: String? = null,
//    var toDate: String? = if (!Utils.employeeRole.equals(
//            Utils.EMPROLE,
//            true
//        )
//    ) Utils.getCurrentToDate() else null,
    var txnTypes: ArrayList<TxnTypes> = ArrayList(),
    var isFilters: Boolean = false,
)
