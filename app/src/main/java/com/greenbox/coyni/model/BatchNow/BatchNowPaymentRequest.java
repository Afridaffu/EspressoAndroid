package com.greenbox.coyni.model.BatchNow;

public class BatchNowPaymentRequest {
     private String payoutId;
     private String requestToken;

    public String getPayoutId() {
        return payoutId;
    }

    public void setPayoutId(String payoutId) {
        this.payoutId = payoutId;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }
}
