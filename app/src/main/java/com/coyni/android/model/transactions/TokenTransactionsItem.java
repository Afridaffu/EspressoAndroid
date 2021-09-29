package com.coyni.android.model.transactions;

public class TokenTransactionsItem {
    private String createdAt;
    //    private String txnType;
//    private String txnSubType;
//    private String txnStatus;
    private String txnTypeDn;
    private String txnSubTypeDn;
    private String txnStatusDn;
    private String amount;
    private String txnDescription;
    private String walletBalance;
    private int transactionId;
    private String gbxTransactionId;
    private String updatedAt;

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTxnType() {
        return txnTypeDn;
    }

    public void setTxnType(String txnType) {
        this.txnTypeDn = txnType;
    }

    public String getTxnSubType() {
        return txnSubTypeDn;
    }

    public void setTxnSubType(String txnSubType) {
        this.txnSubTypeDn = txnSubType;
    }

    public String getTxnStatus() {
        return txnStatusDn;
    }

    public void setTxnStatus(String txnStatus) {
        this.txnStatusDn = txnStatus;
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

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
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


}
