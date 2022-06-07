package com.greenbox.coyni.model.identity_verification;

import java.util.ArrayList;

public class LatestTransactionsRequest {
    private int userId;
    private ArrayList<Integer> transactionType;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ArrayList<Integer> getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(ArrayList<Integer> transactionType) {
        this.transactionType = transactionType;
    }
}
