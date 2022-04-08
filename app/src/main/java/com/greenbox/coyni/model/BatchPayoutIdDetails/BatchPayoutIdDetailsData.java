package com.greenbox.coyni.model.BatchPayoutIdDetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BatchPayoutIdDetailsData {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("payoutDate")
    @Expose
    private String payoutDate;
    @SerializedName("payoutReferenceId")
    @Expose
    private String payoutReferenceId;
    @SerializedName("reserveWalletId")
    @Expose
    private String reserveWalletId;
    @SerializedName("batchId")
    @Expose
    private String batchId;
    @SerializedName("tokenAccount")
    @Expose
    private String tokenAccount;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
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
