package com.greenbox.coyni.model.withdraw;

import java.util.List;

public class GiftCardWithDrawInfo {
    private String giftCardCurrency;
    private String giftCardName;
    private List<RecipientDetail> recipientDetails;
    private Double totalAmount;
    private String utid;

    public String getGiftCardCurrency() {
        return giftCardCurrency;
    }

    public void setGiftCardCurrency(String giftCardCurrency) {
        this.giftCardCurrency = giftCardCurrency;
    }

    public String getGiftCardName() {
        return giftCardName;
    }

    public void setGiftCardName(String giftCardName) {
        this.giftCardName = giftCardName;
    }

    public List<RecipientDetail> getRecipientDetails() {
        return recipientDetails;
    }

    public void setRecipientDetails(List<RecipientDetail> recipientDetails) {
        this.recipientDetails = recipientDetails;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUtid() {
        return utid;
    }

    public void setUtid(String utid) {
        this.utid = utid;
    }
}

