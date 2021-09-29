package com.coyni.android.model.withdraw;

public class WithdrawResponseData {
    private String gbxTransactionId;
    private String blockTransactionId;
    private String withdrawTotalAmount;
    private String remainingBalance;
    private String cardId;
    private Double fee;

    public String getGbxTransactionId() {
        return gbxTransactionId;
    }

    public void setGbxTransactionId(String gbxTransactionId) {
        this.gbxTransactionId = gbxTransactionId;
    }

    public String getBlockTransactionId() {
        return blockTransactionId;
    }

    public void setBlockTransactionId(String blockTransactionId) {
        this.blockTransactionId = blockTransactionId;
    }

    public String getWithdrawTotalAmount() {
        return withdrawTotalAmount;
    }

    public void setWithdrawTotalAmount(String withdrawTotalAmount) {
        this.withdrawTotalAmount = withdrawTotalAmount;
    }

    public String getRemainingBalance() {
        return remainingBalance;
    }

    public void setRemainingBalance(String remainingBalance) {
        this.remainingBalance = remainingBalance;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }
}
