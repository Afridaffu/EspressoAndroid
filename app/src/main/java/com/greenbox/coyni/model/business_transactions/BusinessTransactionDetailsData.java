package com.greenbox.coyni.model.business_transactions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BusinessTransactionDetailsData {
    @SerializedName("youPay")
    @Expose
    private String youPay;
    @SerializedName("transactionSubtype")
    @Expose
    private String transactionSubtype;
    @SerializedName("nameOnBankAccount")
    @Expose
    private String nameOnBankAccount;
    @SerializedName("bankName")
    @Expose
    private String bankName;
    @SerializedName("processingFee")
    @Expose
    private String processingFee;
    @SerializedName("referenceId")
    @Expose
    private String referenceId;
    @SerializedName("transactionType")
    @Expose
    private String transactionType;
    @SerializedName("createdDate")
    @Expose
    private String createdDate;
    @SerializedName("exchangeRate")
    @Expose
    private String exchangeRate;
    @SerializedName("bankAccountNumber")
    @Expose
    private String bankAccountNumber;
    @SerializedName("youGet")
    @Expose
    private String youGet;
    @SerializedName("accountBalance")
    @Expose
    private String accountBalance;
    @SerializedName("cardBrand")
    @Expose
    private String cardBrand;
    @SerializedName("descriptorName")
    @Expose
    private String descriptorName;
    @SerializedName("status")
    @Expose
    private String status;

    private String amount;
    private String senderMessage;
    private String totalAmount;
    private String recipientWalletAddress;
    private String recipientName;
    private String senderName;
    private String amountReceived;
    private String senderWalletAddress;
    private String purchaseAmount;
    private String depositId;
    private String cardNumber;
    private String expdate;
    private String cardHolderName;
    private String cardExpiryDate;
    private String giftCardName;
    private String totalPaidAmount;
    private String recipientEmail;
    private String receivedAmount;
    private String withdrawalId;
    private String withdrawAmount;
    private String description;
    private String orderId;
    private String giftCardFee;
    private String withdrawId;
    private String giftCardAmount;
    private String subtotal;
    private String remarks;

}
