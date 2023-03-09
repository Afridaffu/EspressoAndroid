package com.coyni.pos.app.model.TransactionFilter

class TransactionItems (
    var createdAt: String? = null,
    var txnTypeDn: String? = null,
    var txnSubTypeDn: String? = null,
    var txnStatusDn: String? = null,
    var amount: String? = null,
    var txnDescription: String? = null,
    var transactionId: Int? = null,
    var gbxTransactionId: String? = null,
    var updatedAt: String? = null,
    var senderName: String? = null,
    var receiveName: String? = null,
    var userType: String? = null
)
