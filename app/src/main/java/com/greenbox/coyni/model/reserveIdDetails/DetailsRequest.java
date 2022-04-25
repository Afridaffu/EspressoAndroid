package com.greenbox.coyni.model.reserveIdDetails;

public class DetailsRequest {

    private int txnType;

    private String payoutId;

    public int getTxnType() {
        return txnType;
    }

    public void setTxnType(int txnType) {
        this.txnType = txnType;
    }

    public String getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(String payoutId) {
        this.payoutId = payoutId;
    }
}
