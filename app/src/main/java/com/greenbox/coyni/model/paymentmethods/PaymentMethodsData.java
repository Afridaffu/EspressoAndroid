package com.greenbox.coyni.model.paymentmethods;

import java.util.List;

public class PaymentMethodsData {
    private List<PaymentsList> data;
    private int creditCardCount;
    private int debitCardCount;
    private int bankCount;

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
}
