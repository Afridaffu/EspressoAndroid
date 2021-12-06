package com.greenbox.coyni.model.transaction;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionListPending {

    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("txnTypeDn")
    @Expose
    private String txnTypeDn;
    @SerializedName("txnSubTypeDn")
    @Expose
    private Object txnSubTypeDn;
    @SerializedName("txnStatusDn")
    @Expose
    private String txnStatusDn;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("txnDescription")
    @Expose
    private String txnDescription;
    @SerializedName("walletBalance")
    @Expose
    private String walletBalance;
    @SerializedName("transactionId")
    @Expose
    private Object transactionId;
    @SerializedName("gbxTransactionId")
    @Expose
    private Object gbxTransactionId;
    @SerializedName("updatedAt")
    @Expose
    private Object updatedAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTxnTypeDn() {
        return txnTypeDn;
    }

    public void setTxnTypeDn(String txnTypeDn) {
        this.txnTypeDn = txnTypeDn;
    }

    public Object getTxnSubTypeDn() {
        return txnSubTypeDn;
    }

    public void setTxnSubTypeDn(Object txnSubTypeDn) {
        this.txnSubTypeDn = txnSubTypeDn;
    }

    public String getTxnStatusDn() {
        return txnStatusDn;
    }

    public void setTxnStatusDn(String txnStatusDn) {
        this.txnStatusDn = txnStatusDn;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTxnDescription() {
        return txnDescription;
    }

    public void setTxnDescription(String txnDescription) {
        this.txnDescription = txnDescription;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Object getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Object transactionId) {
        this.transactionId = transactionId;
    }

    public Object getGbxTransactionId() {
        return gbxTransactionId;
    }

    public void setGbxTransactionId(Object gbxTransactionId) {
        this.gbxTransactionId = gbxTransactionId;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }

}
