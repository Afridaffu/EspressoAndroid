package com.coyni.mapp.model.businesswallet;

public class WalletInfo {
    private String createdAt;
    private String walletId;
    private String walletName;
    private String availableBalance;
    private double availabilityToUse;
    private double exchangeRate;
    private String exchangeVariation;
    private double exchangeAmount;
    private String status;
    private String walletCategory;
    private String type;
    private String subType;
    private String walletDescription;
    private String amount;
    private String balance;
    private String updatedAt;
    private String walletType;


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

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

    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    public double getAvailabilityToUse() {
        return availabilityToUse;
    }

    public void setAvailabilityToUse(double availabilityToUse) {
        this.availabilityToUse = availabilityToUse;
    }

    public double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getExchangeVariation() {
        return exchangeVariation;
    }

    public void setExchangeVariation(String exchangeVariation) {
        this.exchangeVariation = exchangeVariation;
    }

    public double getExchangeAmount() {
        return exchangeAmount;
    }

    public void setExchangeAmount(double exchangeAmount) {
        this.exchangeAmount = exchangeAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWalletCategory() {
        return walletCategory;
    }

    public void setWalletCategory(String walletCategory) {
        this.walletCategory = walletCategory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public String getWalletDescription() {
        return walletDescription;
    }

    public void setWalletDescription(String walletDescription) {
        this.walletDescription = walletDescription;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    @Override
    public String toString() {
        return "WalletInfo{" +
                "createdAt='" + createdAt + '\'' +
                ", walletId='" + walletId + '\'' +
                ", walletName='" + walletName + '\'' +
                ", availableBalance='" + availableBalance + '\'' +
                ", availabilityToUse=" + availabilityToUse +
                ", exchangeRate=" + exchangeRate +
                ", exchangeVariation='" + exchangeVariation + '\'' +
                ", exchangeAmount=" + exchangeAmount +
                ", status='" + status + '\'' +
                ", walletCategory='" + walletCategory + '\'' +
                ", type='" + type + '\'' +
                ", subType='" + subType + '\'' +
                ", walletDescription='" + walletDescription + '\'' +
                ", amount='" + amount + '\'' +
                ", balance='" + balance + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", walletType='" + walletType + '\'' +
                '}';
    }
}