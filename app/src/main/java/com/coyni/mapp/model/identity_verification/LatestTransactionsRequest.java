package com.coyni.mapp.model.identity_verification;

import java.util.ArrayList;

public class LatestTransactionsRequest {
    private ArrayList<Integer> transactionType;
    private boolean isMerchantTokenTransactions;

    public ArrayList<Integer> getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(ArrayList<Integer> transactionType) {
        this.transactionType = transactionType;
    }

    public boolean isMerchantTokenTransactions() {
        return isMerchantTokenTransactions;
    }

    public void setMerchantTokenTransactions(boolean merchantTokenTransactions) {
        isMerchantTokenTransactions = merchantTokenTransactions;
    }
}
