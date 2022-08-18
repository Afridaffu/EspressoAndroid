package com.coyni.mapp.model.withdraw;

public class WithdrawRequest {
    private Long bankId;
    private Long cardId;
    private GiftCardWithDrawInfo giftCardWithDrawInfo;
    private String remarks = "";
    private String signetWalletId = "";
    private Double tokens;
    private String withdrawType;
    private String requestToken;

    public Long getBankId() {
        return bankId;
    }

    public void setBankId(Long bankId) {
        this.bankId = bankId;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
    }

    public GiftCardWithDrawInfo getGiftCardWithDrawInfo() {
        return giftCardWithDrawInfo;
    }

    public void setGiftCardWithDrawInfo(GiftCardWithDrawInfo giftCardWithDrawInfo) {
        this.giftCardWithDrawInfo = giftCardWithDrawInfo;
    }

    public Double getTokens() {
        return tokens;
    }

    public void setTokens(Double tokens) {
        this.tokens = tokens;
    }

    public String getWithdrawType() {
        return withdrawType;
    }

    public void setWithdrawType(String withdrawType) {
        this.withdrawType = withdrawType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getSignetWalletId() {
        return signetWalletId;
    }

    public void setSignetWalletId(String signetWalletId) {
        this.signetWalletId = signetWalletId;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }
}
