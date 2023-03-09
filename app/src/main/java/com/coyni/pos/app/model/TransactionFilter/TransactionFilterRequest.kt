package com.coyni.pos.app.model.TransactionFilter

data class TransactionFilterRequest(

    var fromAmount: Int? = null,
    var fromDate: String? = null,
    var requestToken: String? = null,
    var status: String? = null,
    var toAmount: Int? = null,
    var toDate: String? = null,
    var data: TxnTypes? = null,
    var isFilters: Boolean = false,

//    var walletCategory: String? = null,
//    var pageSize: String? = null,
//    var pageNo: String? = null,
//    var isManualUpdate: Boolean = false,
//    var fromAmountOperator: String? = null,
//    var toAmountOperator: String? = null,
//    var updatedFromDate: String? = null,
//    var updatedFromDateOperator: String? = null,
//    var updatedToDate: String? = null,
//    var updatedToDateOperator: String? = null,
//    var gbxTransactionId: String? = null,
//    var isMerchantTransactions: Boolean = false,
//    var isMerchantTokenTransactions: Boolean = false,
//    var transactionType: ArrayList<Int>? = null,
//    var transactionSubType: ArrayList<Int>? = null,
//    var txnStatus: ArrayList<Int>? = null,
)
