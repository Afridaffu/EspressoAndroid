package com.coyni.pos.app.model

data class TransactionData(
    // Sale order fileds
    var employeeName: String? = null,
    var transactionSubtype: String? = null,
    var employeeId: Int? = null,
    var terminalId: Int? = null,
    var referenceId: String? = null,
    var customerName: String? = null,
    var purchaseAmount: String? = null,
    var transactionType: String? = null,
    var totalAmount: String? = null,
    var createdDate: String? = null,
    var customerEmail: String? = null,
    var tip: String? = null,
    var status: String? = null,

    //Refund fields
    var merchantName: String? = null,
    var customerServiceEmail: String? = null,
    var saleOrderReferenceId: String? = null,
    var customerServicePhone: String? = null,
    var saleOrderDateAndTime: String? = null,
    var saleOrderSubType: Int? = null,
    var transactionAmount: String? = null,
    var remarks: String? = null,


    )
