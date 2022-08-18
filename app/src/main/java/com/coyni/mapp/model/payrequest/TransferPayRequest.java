package com.coyni.mapp.model.payrequest;

public class TransferPayRequest {
    private String remarks;
    private String recipientWalletId;
    private String tokens;
    private String sourceWalletId;
    private String requestToken;

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRecipientWalletId() {
        return recipientWalletId;
    }

    public void setRecipientWalletId(String recipientWalletId) {
        this.recipientWalletId = recipientWalletId;
    }

    public String getTokens() {
        return tokens;
    }

    public void setTokens(String tokens) {
        this.tokens = tokens;
    }

    public String getSourceWalletId() {
        return sourceWalletId;
    }

    public void setSourceWalletId(String sourceWalletId) {
        this.sourceWalletId = sourceWalletId;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }
}

