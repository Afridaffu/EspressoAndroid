package com.greenbox.coyni.model.wallet;

import java.io.Serializable;

public class WalletInfo implements Serializable {
    private String walletId;
    private String walletName;
    private double availabilityToUse;
    private String walletType;
    private double exchangeRate;
    private double exchangeVariation;
    private double exchangeAmount;
    private String walletCategory;

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public double getAvailabilityToUse() {
        return availabilityToUse;
    }

    public void setAvailabilityToUse(double availabilityToUse) {
        this.availabilityToUse = availabilityToUse;
    }

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public double getExchangeVariation() {
        return exchangeVariation;
    }

    public void setExchangeVariation(double exchangeVariation) {
        this.exchangeVariation = exchangeVariation;
    }

    public double getExchangeAmount() {
        return exchangeAmount;
    }

    public void setExchangeAmount(double exchangeAmount) {
        this.exchangeAmount = exchangeAmount;
    }

    public String getWalletCategory() {
        return walletCategory;
    }

    public void setWalletCategory(String walletCategory) {
        this.walletCategory = walletCategory;
    }
}

