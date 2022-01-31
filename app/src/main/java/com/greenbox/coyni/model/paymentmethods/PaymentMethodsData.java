package com.greenbox.coyni.model.paymentmethods;

import java.util.List;

public class PaymentMethodsData {
    private List<PaymentsList> data;
    private int creditCardCount;
    private int debitCardCount;
    private int bankCount;
    private int signetCount;
    private int maxDebitCardsAllowed;
    private int maxCreditCardsAllowed;
    private int maxBankAccountsAllowed;
    private int maxSignetAccountsAllowed;

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

    public int getSignetCount() {
        return signetCount;
    }

    public void setSignetCount(int signetCount) {
        this.signetCount = signetCount;
    }

    public int getMaxSignetAccountsAllowed() {
        return maxSignetAccountsAllowed;
    }

    public void setMaxSignetAccountsAllowed(int maxSignetAccountsAllowed) {
        this.maxSignetAccountsAllowed = maxSignetAccountsAllowed;
    }
}
