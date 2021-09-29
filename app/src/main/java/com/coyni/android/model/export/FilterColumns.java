package com.coyni.android.model.export;

public class FilterColumns {
    private int walletCategory;
    //    private int transactionType;
    private Integer transactionType;
    private String transactionSubType;
    private String gbxTransactionId;
    //    private int txnStatus;
    private Integer txnStatus;
    private String txnStatusOperator;
    //    private String fromDate;
//    private String fromDateOperator;
//    private String toDate;
//    private String toDateOperator;
    private String updatedFromDate;
    private String updatedFromDateOperator;
    private String updatedToDate;
    private String updatedToDateOperator;
    private String fromAmount;
    private String fromAmountOperator;
    private String toAmount;
    private String toAmountOperator;

    public int getWalletCategory() {
        return walletCategory;
    }

    public void setWalletCategory(int walletCategory) {
        this.walletCategory = walletCategory;
    }

    public Integer getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Integer transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionSubType() {
        return transactionSubType;
    }

    public void setTransactionSubType(String transactionSubType) {
        this.transactionSubType = transactionSubType;
    }

    public String getGbxTransactionId() {
        return gbxTransactionId;
    }

    public void setGbxTransactionId(String gbxTransactionId) {
        this.gbxTransactionId = gbxTransactionId;
    }

    public Integer getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(Integer txnStatus) {
        this.txnStatus = txnStatus;
    }

    public String getTxnStatusOperator() {
        return txnStatusOperator;
    }

    public void setTxnStatusOperator(String txnStatusOperator) {
        this.txnStatusOperator = txnStatusOperator;
    }

    public String getFromDate() {
        return updatedFromDate;
    }

    public void setFromDate(String fromDate) {
        this.updatedFromDate = fromDate;
    }

    public String getFromDateOperator() {
        return updatedFromDateOperator;
    }

    public void setFromDateOperator(String fromDateOperator) {
        this.updatedFromDateOperator = fromDateOperator;
    }

    public String getToDate() {
        return updatedToDate;
    }

    public void setToDate(String toDate) {
        this.updatedToDate = toDate;
    }

    public String getToDateOperator() {
        return updatedToDateOperator;
    }

    public void setToDateOperator(String toDateOperator) {
        this.updatedToDateOperator = toDateOperator;
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
}
