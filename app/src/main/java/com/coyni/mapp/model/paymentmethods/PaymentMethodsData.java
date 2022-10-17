package com.coyni.mapp.model.paymentmethods;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentMethodsData {
    private List<PaymentsList> data;
    private int creditCardCount;
    private int debitCardCount;
    private int bankCount;
    private int cogentCount;
    private int maxDebitCardsAllowed;
    private int maxCreditCardsAllowed;
    private int maxBankAccountsAllowed;
    private int maxCogentAccountsAllowed;

    public List<PaymentsList> getData() {
        return data;
    }

    public void setData(List<PaymentsList> data) {
        this.data = data;
    }

    public int getCreditCardCount() {
        return creditCardCount;
    }

    public void setCreditCardCount(int creditCardCount) {
        this.creditCardCount = creditCardCount;
    }

    public int getDebitCardCount() {
        return debitCardCount;
    }

    public void setDebitCardCount(int debitCardCount) {
        this.debitCardCount = debitCardCount;
    }

    public int getBankCount() {
        return bankCount;
    }

    public void setBankCount(int bankCount) {
        this.bankCount = bankCount;
    }

    public int getMaxDebitCardsAllowed() {
        return maxDebitCardsAllowed;
    }

    public void setMaxDebitCardsAllowed(int maxDebitCardsAllowed) {
        this.maxDebitCardsAllowed = maxDebitCardsAllowed;
    }

    public int getMaxCreditCardsAllowed() {
        return maxCreditCardsAllowed;
    }

    public void setMaxCreditCardsAllowed(int maxCreditCardsAllowed) {
        this.maxCreditCardsAllowed = maxCreditCardsAllowed;
    }

    public int getMaxBankAccountsAllowed() {
        return maxBankAccountsAllowed;
    }

    public void setMaxBankAccountsAllowed(int maxBankAccountsAllowed) {
        this.maxBankAccountsAllowed = maxBankAccountsAllowed;
    }

    public int getCogentCount() {
        return cogentCount;
    }

    public void setCogentCount(int cogentCount) {
        this.cogentCount = cogentCount;
    }

    public int getMaxCogentAccountsAllowed() {
        return maxCogentAccountsAllowed;
    }

    public void setMaxCogentAccountsAllowed(int maxCogentAccountsAllowed) {
        this.maxCogentAccountsAllowed = maxCogentAccountsAllowed;
    }
}
