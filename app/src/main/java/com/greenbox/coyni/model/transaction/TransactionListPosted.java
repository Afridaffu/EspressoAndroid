package com.greenbox.coyni.model.transaction;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.greenbox.coyni.model.ListItem;

import java.io.Serializable;

public class TransactionListPosted extends ListItem implements Serializable, Comparable<TransactionListPosted> {
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("txnTypeDn")
    @Expose
    private String txnTypeDn;
    @SerializedName("txnSubTypeDn")
    @Expose
    private String txnSubTypeDn;
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
    private String transactionId;
    @SerializedName("gbxTransactionId")
    @Expose
    private String gbxTransactionId;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    private String senderName;
    private String receiveName;

    public String getReceiveName() {
        return receiveName;
    }

    public void setReceiveName(String receiveName) {
        this.receiveName = receiveName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    private int type = ListItem.TYPE_GENERAL;

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

    public String getTxnSubTypeDn() {
        return txnSubTypeDn;
    }

    public void setTxnSubTypeDn(String txnSubTypeDn) {
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

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getGbxTransactionId() {
        return gbxTransactionId;
    }

    public void setGbxTransactionId(String gbxTransactionId) {
        this.gbxTransactionId = gbxTransactionId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int compareTo(TransactionListPosted transactionListPosted) {
        return updatedAt.compareTo(transactionListPosted.updatedAt);
    }
}
