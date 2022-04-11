package com.greenbox.coyni.model.transaction;

public class RefundReferenceRequest {

    private String gbxTransactionId;
    private String refundReason;
    private Double refundAmount;
    private Integer walletType;

    public String getGbxTransactionId() {
        return gbxTransactionId;
    }

    public void setGbxTransactionId(String gbxTransactionId) {
        this.gbxTransactionId = gbxTransactionId;
    }

    public String getRemarks() {
        return refundReason;
    }

    public void setRemarks(String remarks) {
        this.refundReason = remarks;
    }

    public Double getAmount() {
        return refundAmount;
    }

    public void setAmount(Double refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Integer getWalletType() {
        return walletType;
    }

    public void setWalletType(Integer walletType) {
        this.walletType = walletType;
    }


}
