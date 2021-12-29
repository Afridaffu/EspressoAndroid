package com.greenbox.coyni.model.users;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccountLimitsData{



    @SerializedName("userType")
    @Expose
    private int userType;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("payRequestTokenMinLimit")
    @Expose
    private int payRequestTokenMinLimit;
    @SerializedName("payRequestTokenType")
    @Expose
    private int payRequestTokenType;
    @SerializedName("payRequestTokenTxnLimit")
    @Expose
    private int payRequestTokenTxnLimit;
    @SerializedName("withdrawsBankAccountMinLimit")
    @Expose
    private int withdrawsBankAccountMinLimit;
    @SerializedName("withdrawsBankAccountType")
    @Expose
    private int withdrawsBankAccountType;
    @SerializedName("withdrawsBankAccountTxnLimit")
    @Expose
    private int withdrawsBankAccountTxnLimit;
    @SerializedName("withdrawsInstantPayMinLimit")
    @Expose
    private int withdrawsInstantPayMinLimit;
    @SerializedName("withdrawsInstantPayType")
    @Expose
    private int withdrawsInstantPayType;
    @SerializedName("withdrawsInstantPayTxnLimit")
    @Expose
    private int withdrawsInstantPayTxnLimit;
    @SerializedName("withdrawsSignetMinLimit")
    @Expose
    private int withdrawsSignetMinLimit;
    @SerializedName("withdrawsSignetType")
    @Expose
    private int withdrawsSignetType;
    @SerializedName("withdrawsSignetTxnLimit")
    @Expose
    private int withdrawsSignetTxnLimit;
    @SerializedName("withdrawsGiftCardMinLimit")
    @Expose
    private int withdrawsGiftCardMinLimit;
    @SerializedName("withdrawsGiftCardType")
    @Expose
    private int withdrawsGiftCardType;
    @SerializedName("withdrawsGiftCardTxnLimit")
    @Expose
    private int withdrawsGiftCardTxnLimit;
    @SerializedName("buyTokenBankAccountMinLimit")
    @Expose
    private int buyTokenBankAccountMinLimit;
    @SerializedName("buyTokenBankAccountType")
    @Expose
    private int buyTokenBankAccountType;
    @SerializedName("buyTokenBankAccountTxnLimit")
    @Expose
    private int buyTokenBankAccountTxnLimit;
    @SerializedName("buyTokenCardMinLimit")
    @Expose
    private int buyTokenCardMinLimit;
    @SerializedName("buyTokenCardType")
    @Expose
    private int buyTokenCardType;
    @SerializedName("buyTokenCardTxnLimit")
    @Expose
    private int buyTokenCardTxnLimit;
    @SerializedName("buyTokenSignetMinLimit")
    @Expose
    private int buyTokenSignetMinLimit;
    @SerializedName("buyTokenSignetType")
    @Expose
    private int buyTokenSignetType;
    @SerializedName("buyTokenSignetTxnLimit")
    @Expose
    private int buyTokenSignetTxnLimit;
    @SerializedName("status")
    @Expose
    private int status;

    public int getUserType() {
        return userType;
    }

    public String getStartDate() {
        return startDate;
    }

    public int getPayRequestTokenMinLimit() {
        return payRequestTokenMinLimit;
    }

    public int getPayRequestTokenType() {
        return payRequestTokenType;
    }

    public int getPayRequestTokenTxnLimit() {
        return payRequestTokenTxnLimit;
    }

    public int getWithdrawsBankAccountMinLimit() {
        return withdrawsBankAccountMinLimit;
    }

    public int getWithdrawsBankAccountType() {
        return withdrawsBankAccountType;
    }

    public int getWithdrawsBankAccountTxnLimit() {
        return withdrawsBankAccountTxnLimit;
    }

    public int getWithdrawsInstantPayMinLimit() {
        return withdrawsInstantPayMinLimit;
    }

    public int getWithdrawsInstantPayType() {
        return withdrawsInstantPayType;
    }

    public int getWithdrawsInstantPayTxnLimit() {
        return withdrawsInstantPayTxnLimit;
    }

    public int getWithdrawsSignetMinLimit() {
        return withdrawsSignetMinLimit;
    }

    public int getWithdrawsSignetType() {
        return withdrawsSignetType;
    }

    public int getWithdrawsSignetTxnLimit() {
        return withdrawsSignetTxnLimit;
    }

    public int getWithdrawsGiftCardMinLimit() {
        return withdrawsGiftCardMinLimit;
    }

    public int getWithdrawsGiftCardType() {
        return withdrawsGiftCardType;
    }

    public int getWithdrawsGiftCardTxnLimit() {
        return withdrawsGiftCardTxnLimit;
    }

    public int getBuyTokenBankAccountMinLimit() {
        return buyTokenBankAccountMinLimit;
    }

    public int getBuyTokenBankAccountType() {
        return buyTokenBankAccountType;
    }

    public int getBuyTokenBankAccountTxnLimit() {
        return buyTokenBankAccountTxnLimit;
    }

    public int getBuyTokenCardMinLimit() {
        return buyTokenCardMinLimit;
    }

    public int getBuyTokenCardType() {
        return buyTokenCardType;
    }

    public int getBuyTokenCardTxnLimit() {
        return buyTokenCardTxnLimit;
    }

    public int getBuyTokenSignetMinLimit() {
        return buyTokenSignetMinLimit;
    }

    public int getBuyTokenSignetType() {
        return buyTokenSignetType;
    }

    public int getBuyTokenSignetTxnLimit() {
        return buyTokenSignetTxnLimit;
    }

    public int getStatus() {
        return status;
    }
}

