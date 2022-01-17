package com.greenbox.coyni.model.payrequest;

public class TransferPayRequest {
    private String remarks;
    private String recipientWalletId;
    private String tokens;

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
}

