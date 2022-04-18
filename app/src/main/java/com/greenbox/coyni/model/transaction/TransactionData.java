package com.greenbox.coyni.model.transaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class TransactionData implements Serializable {
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
    private String walletId;
    private String nameOnBank;
    private String payoutId;
    private String payoutDate;
    private String totalTransactions;
    private String failedReason;
    private String achReferenceId;


    //    MerchantTransaction newly added fields

    private String fees;
    private String netAmount;
    private String senderEmail;
    private String reserve;
    private String grossAmount;
    private String dateAndTime;
    private String refundAmount;
    private String depositTo;
    private String amountSent;
    private String saleOrderGrossAmount;
    private String saleOrderNetAmount;
    private String saleOrderReferenceId;
    private String saleOrderDateAndTime;
    private String saleOrderReserve;


    public String getWithdrawId() {
        return withdrawId;
    }

    public String getCardExpiryDate() {
        return cardExpiryDate;
    }

    public void setCardExpiryDate(String cardExpiryDate) {
        this.cardExpiryDate = cardExpiryDate;
    }

    public String getGiftCardFee() {
        return giftCardFee;
    }

    public void setGiftCardFee(String giftCardFee) {
        this.giftCardFee = giftCardFee;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(String receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public String getWithdrawalId() {
        return withdrawalId;
    }

    public void setWithdrawalId(String withdrawalId) {
        this.withdrawalId = withdrawalId;
    }

    public String getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(String withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }

    public String getTotalPaidAmount() {
        return totalPaidAmount;
    }

    public void setTotalPaidAmount(String totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public String getGiftCardName() {
        return giftCardName;
    }

    public void setGiftCardName(String giftCardName) {
        this.giftCardName = giftCardName;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpdate() {
        return expdate;
    }

    public void setExpdate(String expdate) {
        this.expdate = expdate;
    }

    public void setDepositid(String depositId) {
        this.depositId = depositId;
    }

    public String getDepositid() {
        return depositId;
    }

    public String getPurchaseAmount() {
        return purchaseAmount;
    }

    public void setPurchaseAmount(String purchaseAmount) {
        this.purchaseAmount = purchaseAmount;
    }

    public String getSenderWalletAddress() {
        return senderWalletAddress;
    }

    public void setSenderWalletAddress(String senderWalletAddress) {
        this.senderWalletAddress = senderWalletAddress;
    }

    public String getAmountReceived() {
        return amountReceived;
    }

    public void setAmountReceived(String amountReceived) {
        this.amountReceived = amountReceived;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderMessage() {
        return senderMessage;
    }

    public void setSenderMessage(String senderMessage) {
        this.senderMessage = senderMessage;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getRecipientWalletAddress() {
        return recipientWalletAddress;
    }

    public void setRecipientWalletAddress(String recipientWalletAddress) {
        this.recipientWalletAddress = recipientWalletAddress;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getYouPay() {
        return youPay;
    }

    public void setYouPay(String youPay) {
        this.youPay = youPay;
    }

    public String getTransactionSubtype() {
        return transactionSubtype;
    }

    public void setTransactionSubtype(String transactionSubtype) {
        this.transactionSubtype = transactionSubtype;
    }

    public String getNameOnBankAccount() {
        return nameOnBankAccount;
    }

    public void setNameOnBankAccount(String nameOnBankAccount) {
        this.nameOnBankAccount = nameOnBankAccount;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(String processingFee) {
        this.processingFee = processingFee;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getYouGet() {
        return youGet;
    }

    public void setYouGet(String youGet) {
        this.youGet = youGet;
    }

    public String getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(String accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getCardBrand() {
        return cardBrand;
    }

    public void setCardBrand(String cardBrand) {
        this.cardBrand = cardBrand;
    }

    public String getDescriptorName() {
        return descriptorName;
    }

    public void setDescriptorName(String descriptorName) {
        this.descriptorName = descriptorName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDepositId() {
        return depositId;
    }

    public void setDepositId(String depositId) {
        this.depositId = depositId;
    }

    public void setWithdrawId(String withdrawId) {
        this.withdrawId = withdrawId;
    }

    public String getGiftCardAmount() {
        return giftCardAmount;
    }

    public void setGiftCardAmount(String giftCardAmount) {
        this.giftCardAmount = giftCardAmount;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getWalletId() {
        return walletId;
    }

    public String getNameOnBank() {
        return nameOnBank;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public void setNameOnBank(String nameOnBank) {
        this.nameOnBank = nameOnBank;
    }

    public String getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(String payoutId) {
        this.payoutId = payoutId;
    }

    public String getPayoutDate() {
        return payoutDate;
    }

    public void setPayoutDate(String payoutDate) {
        this.payoutDate = payoutDate;
    }

    public String getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(String totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public String getDepositTo() {
        return depositTo;
    }

    public void setDepositTo(String depositTo) {
        this.depositTo = depositTo;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public String getAchReferenceId() {
        return achReferenceId;
    }

    public void setAchReferenceId(String achReferenceId) {
        this.achReferenceId = achReferenceId;
    }

    //    MerchantTransaction newly added fields

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount = netAmount;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public String getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(String grossAmount) {
        this.grossAmount = grossAmount;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(String refundAmount) {
        this.refundAmount = refundAmount;
    }

    public String getAmountSent() {
        return amountSent;
    }

    public void setAmountSent(String amountSent) {
        this.amountSent = amountSent;
    }


    public String getSaleOrderGrossAmount() {
        return saleOrderGrossAmount;
    }

    public void setSaleOrderGrossAmount(String saleOrderGrossAmount) {
        this.saleOrderGrossAmount = saleOrderGrossAmount;
    }


    public String getSaleOrderNetAmount() {
        return saleOrderNetAmount;
    }

    public void setSaleOrderNetAmount(String saleOrderNetAmount) {
        this.saleOrderNetAmount = saleOrderNetAmount;
    }


    public String getSaleOrderReferenceId() {
        return saleOrderReferenceId;
    }

    public void setSaleOrderReferenceId(String saleOrderReferenceId) {
        this.saleOrderReferenceId = saleOrderReferenceId;
    }


    public String getSaleOrderDateAndTime() {
        return saleOrderDateAndTime;
    }

    public void setSaleOrderDateAndTime(String saleOrderDateAndTime) {
        this.saleOrderDateAndTime = saleOrderDateAndTime;
    }


    public String getSaleOrderReserve() {
        return saleOrderReserve;
    }

    public void setSaleOrderReserve(String saleOrderReserve) {
        this.saleOrderReserve = saleOrderReserve;
    }


}