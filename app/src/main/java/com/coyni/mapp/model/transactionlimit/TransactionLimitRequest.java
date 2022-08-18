package com.coyni.mapp.model.transactionlimit;

public class TransactionLimitRequest {
    private int transactionType;
    private int transactionSubType;

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public int getTransactionSubType() {
        return transactionSubType;
    }

    public void setTransactionSubType(int transactionSubType) {
        this.transactionSubType = transactionSubType;
    }
}
