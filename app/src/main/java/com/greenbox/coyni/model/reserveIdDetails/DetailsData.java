package com.greenbox.coyni.model.reserveIdDetails;

public class DetailsData {

    public String status;

    public double amount;

    public String payoutDate;

    public String payoutReferenceId;

    public String reserveWalletId;

    public String batchId;

    public String tokenAccount;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayoutDate() {
        return payoutDate;
    }

    public void setPayoutDate(String payoutDate) {
        this.payoutDate = payoutDate;
    }

    public String getPayoutReferenceId() {
        return payoutReferenceId;
    }

    public void setPayoutReferenceId(String payoutReferenceId) {
        this.payoutReferenceId = payoutReferenceId;
    }

    public String getReserveWalletId() {
        return reserveWalletId;
    }

    public void setReserveWalletId(String reserveWalletId) {
        this.reserveWalletId = reserveWalletId;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getTokenAccount() {
        return tokenAccount;
    }

    public void setTokenAccount(String tokenAccount) {
        this.tokenAccount = tokenAccount;
    }
}
