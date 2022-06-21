package com.greenbox.coyni.model.transaction;

import java.util.ArrayList;

public class TransactionListRequest {
    private String walletCategory;
    private String pageSize;
    private String pageNo;
    private boolean isManualUpdate = false;
    private String fromAmount;
    private String fromAmountOperator;

    private String toAmount;
    private String toAmountOperator;

    private String updatedFromDate;
    private String updatedFromDateOperator;

    private String updatedToDate;
    private String updatedToDateOperator;
    private String gbxTransactionId;
    private  boolean isFilters;
    private boolean isMerchantTransactions;
    private boolean isMerchantTokenTransactions ;

    private ArrayList<Integer> transactionType;
    private ArrayList<Integer> transactionSubType;
    private ArrayList<Integer> txnStatus;

    public boolean isMerchantTokenTransactions() {
        return isMerchantTokenTransactions;
    }

    public void setMerchantTokenTransactions(boolean merchantTokenTransactions) {
        isMerchantTokenTransactions = merchantTokenTransactions;
    }

    public String getWalletCategory() {
        return walletCategory;
    }

    public void setWalletCategory(String walletCategory) {
        this.walletCategory = walletCategory;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageNo() {
        return pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public ArrayList<Integer> getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(ArrayList<Integer> transactionType) {
        this.transactionType = transactionType;
    }

    public ArrayList<Integer> getTransactionSubType() {
        return transactionSubType;
    }

    public void setTransactionSubType(ArrayList<Integer> transactionSubType) {
        this.transactionSubType = transactionSubType;
    }

    public ArrayList<Integer> getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(ArrayList<Integer> txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getFromAmount() {
        return fromAmount;
    }

    public void setFromAmount(String fromAmount) {
        this.fromAmount = fromAmount;
    }

    public String getFromAmountOperator() {
        return fromAmountOperator;
    }

    public void setFromAmountOperator(String fromAmountOperator) {
        this.fromAmountOperator = fromAmountOperator;
    }

    public String getToAmount() {
        return toAmount;
    }

    public void setToAmount(String toAmount) {
        this.toAmount = toAmount;
    }

    public String getToAmountOperator() {
        return toAmountOperator;
    }

    public void setToAmountOperator(String toAmountOperator) {
        this.toAmountOperator = toAmountOperator;
    }

    public String getUpdatedFromDate() {
        return updatedFromDate;
    }

    public void setUpdatedFromDate(String updatedFromDate) {
        this.updatedFromDate = updatedFromDate;
    }

    public String getUpdatedFromDateOperator() {
        return updatedFromDateOperator;
    }

    public void setUpdatedFromDateOperator(String updatedFromDateOperator) {
        this.updatedFromDateOperator = updatedFromDateOperator;
    }

    public String getUpdatedToDate() {
        return updatedToDate;
    }

    public void setUpdatedToDate(String updatedToDate) {
        this.updatedToDate = updatedToDate;
    }

    public String getUpdatedToDateOperator() {
        return updatedToDateOperator;
    }

    public void setUpdatedToDateOperator(String updatedToDateOperator) {
        this.updatedToDateOperator = updatedToDateOperator;
    }

    public String getGbxTransactionId() {
        return gbxTransactionId;
    }

    public void setGbxTransactionId(String gbxTransactionId) {
        this.gbxTransactionId = gbxTransactionId;
    }

    public boolean isManualUpdate() {
        return isManualUpdate;
    }

    public void setManualUpdate(boolean manualUpdate) {
        isManualUpdate = manualUpdate;
    }

    public boolean isFilters() {
        return isFilters;
    }

    public void setFilters(boolean filters) {
        isFilters = filters;
    }

    public void setMerchantTransactions(boolean merchantTransactions) {
        isMerchantTransactions = merchantTransactions;
    }

    public boolean isMerchantTransactions() {
        return isMerchantTransactions;
    }
}
