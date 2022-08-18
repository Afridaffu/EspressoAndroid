package com.coyni.mapp.model.paidorder;

public class PaidOrderRequest {
    private String recipientWalletId;
    private String requestToken;
    private double tokensAmount;

    public void setRecipientWalletId(String recipientWalletId) {
        this.recipientWalletId = recipientWalletId;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public void setTokensAmount(double tokensAmount) {
        this.tokensAmount = tokensAmount;
    }
}
