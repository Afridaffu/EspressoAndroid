package com.coyni.mapp.model.users;

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
    private String payRequestTokenMinLimit;
    @SerializedName("payRequestTokenType")
    @Expose
    private int payRequestTokenType;
    @SerializedName("payRequestTokenTxnLimit")
    @Expose
    private String payRequestTokenTxnLimit;
    @SerializedName("withdrawsBankAccountMinLimit")
    @Expose
    private String withdrawsBankAccountMinLimit;
    @SerializedName("withdrawsBankAccountType")
    @Expose
    private int withdrawsBankAccountType;
    @SerializedName("withdrawsBankAccountTxnLimit")
    @Expose
    private String withdrawsBankAccountTxnLimit;
    @SerializedName("withdrawsInstantPayMinLimit")
    @Expose
    private String withdrawsInstantPayMinLimit;
    @SerializedName("withdrawsInstantPayType")
    @Expose
    private int withdrawsInstantPayType;
    @SerializedName("withdrawsInstantPayTxnLimit")
    @Expose
    private String withdrawsInstantPayTxnLimit;
    @SerializedName("withdrawsCogentMinLimit")
    @Expose
    private String withdrawsCogentMinLimit;
    @SerializedName("withdrawsCogentType")
    @Expose
    private int withdrawsCogentType;
    @SerializedName("withdrawsCogentTxnLimit")
    @Expose
    private String withdrawsCogentTxnLimit;

    @SerializedName("withdrawsSignetMinLimit")
    @Expose
    private String withdrawsSignetMinLimit;
    @SerializedName("withdrawsSignetType")
    @Expose
    private int withdrawsSignetType;
    @SerializedName("withdrawsSignetTxnLimit")
    @Expose
    private String withdrawsSignetTxnLimit;

    @SerializedName("withdrawsGiftCardMinLimit")
    @Expose
    private String withdrawsGiftCardMinLimit;
    @SerializedName("withdrawsGiftCardType")
    @Expose
    private int withdrawsGiftCardType;
    @SerializedName("withdrawsGiftCardTxnLimit")
    @Expose
    private String withdrawsGiftCardTxnLimit;
    @SerializedName("buyTokenBankAccountMinLimit")
    @Expose
    private String buyTokenBankAccountMinLimit;
    @SerializedName("buyTokenBankAccountType")
    @Expose
    private int buyTokenBankAccountType;
    @SerializedName("buyTokenBankAccountTxnLimit")
    @Expose
    private String buyTokenBankAccountTxnLimit;
    @SerializedName("buyTokenCardMinLimit")
    @Expose
    private String buyTokenCardMinLimit;
    @SerializedName("buyTokenCardType")
    @Expose
    private int buyTokenCardType;
    @SerializedName("buyTokenCardTxnLimit")
    @Expose
    private String buyTokenCardTxnLimit;
    @SerializedName("buyTokenCogentMinLimit")
    @Expose
    private String buyTokenCogentMinLimit;
    @SerializedName("buyTokenCogentType")
    @Expose
    private int buyTokenCogentType;
    @SerializedName("buyTokenCogentTxnLimit")
    @Expose
    private String buyTokenCogentTxnLimit;

   @SerializedName("buyTokenSignetMinLimit")
    @Expose
    private String buyTokenSignetMinLimit;
    @SerializedName("buyTokenSignetType")
    @Expose
    private int buyTokenSignetType;
    @SerializedName("buyTokenSignetTxnLimit")
    @Expose
    private String buyTokenSignetTxnLimit;

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("transactionHighTicketMinLimit")
    @Expose
    private String transactionHighTicketMinLimit;
    @SerializedName("transactionHighTicketType")
    @Expose
    private int transactionHighTicketType;
    @SerializedName("transactionHighTicketTxnLimit")
    @Expose
    private String transactionHighTicketTxnLimit;
    @SerializedName("transactionSaleOrderTokenMinLimit")
    @Expose
    private String transactionSaleOrderTokenMinLimit;
    @SerializedName("transactionSaleOrderTokenType")
    @Expose
    private int transactionSaleOrderTokenType;
    @SerializedName("transactionSaleOrderTokenTxnLimit")
    @Expose
    private String transactionSaleOrderTokenTxnLimit;

    public AccountLimitsData() {
    }

    public String getTransactionSaleOrderTokenMinLimit() {
        return transactionSaleOrderTokenMinLimit;
    }

    public int getTransactionSaleOrderTokenType() {
        return transactionSaleOrderTokenType;
    }

    public String getTransactionSaleOrderTokenTxnLimit() {
        return transactionSaleOrderTokenTxnLimit;
    }

    public String getTransactionHighTicketMinLimit() {
        return transactionHighTicketMinLimit;
    }

    public int getTransactionHighTicketType() {
        return transactionHighTicketType;
    }

    public String getTransactionHighTicketTxnLimit() {
        return transactionHighTicketTxnLimit;
    }

    public int getUserType() {
        return userType;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getPayRequestTokenMinLimit() {
        return payRequestTokenMinLimit;
    }

    public int getPayRequestTokenType() {
        return payRequestTokenType;
    }

    public String getPayRequestTokenTxnLimit() {
        return payRequestTokenTxnLimit;
    }

    public String getWithdrawsBankAccountMinLimit() {
        return withdrawsBankAccountMinLimit;
    }

    public int getWithdrawsBankAccountType() {
        return withdrawsBankAccountType;
    }

    public String getWithdrawsBankAccountTxnLimit() {
        return withdrawsBankAccountTxnLimit;
    }

    public String getWithdrawsInstantPayMinLimit() {
        return withdrawsInstantPayMinLimit;
    }

    public int getWithdrawsInstantPayType() {
        return withdrawsInstantPayType;
    }

    public String getWithdrawsInstantPayTxnLimit() {
        return withdrawsInstantPayTxnLimit;
    }

    public String getWithdrawsCogentMinLimit() {
        return withdrawsCogentMinLimit;
    }

    public int getWithdrawsCogentType() {
        return withdrawsCogentType;
    }

    public String getWithdrawsCogentTxnLimit() {
        return withdrawsCogentTxnLimit;
    }

    public String getWithdrawsGiftCardMinLimit() {
        return withdrawsGiftCardMinLimit;
    }

    public int getWithdrawsGiftCardType() {
        return withdrawsGiftCardType;
    }

    public String getWithdrawsGiftCardTxnLimit() {
        return withdrawsGiftCardTxnLimit;
    }

    public String getBuyTokenBankAccountMinLimit() {
        return buyTokenBankAccountMinLimit;
    }

    public int getBuyTokenBankAccountType() {
        return buyTokenBankAccountType;
    }

    public String getBuyTokenBankAccountTxnLimit() {
        return buyTokenBankAccountTxnLimit;
    }

    public String getBuyTokenCardMinLimit() {
        return buyTokenCardMinLimit;
    }

    public int getBuyTokenCardType() {
        return buyTokenCardType;
    }

    public String getBuyTokenCardTxnLimit() {
        return buyTokenCardTxnLimit;
    }

    public String getBuyTokenCogentMinLimit() {
        return buyTokenCogentMinLimit;
    }

    public int getBuyTokenCogentType() {
        return buyTokenCogentType;
    }

    public String getBuyTokenCogentTxnLimit() {
        return buyTokenCogentTxnLimit;
    }

    public String getStatus() {
        return status;
    }

    public String getWithdrawsSignetMinLimit() {
        return withdrawsSignetMinLimit;
    }

    public void setWithdrawsSignetMinLimit(String withdrawsSignetMinLimit) {
        this.withdrawsSignetMinLimit = withdrawsSignetMinLimit;
    }

    public int getWithdrawsSignetType() {
        return withdrawsSignetType;
    }

    public void setWithdrawsSignetType(int withdrawsSignetType) {
        this.withdrawsSignetType = withdrawsSignetType;
    }

    public String getWithdrawsSignetTxnLimit() {
        return withdrawsSignetTxnLimit;
    }

    public void setWithdrawsSignetTxnLimit(String withdrawsSignetTxnLimit) {
        this.withdrawsSignetTxnLimit = withdrawsSignetTxnLimit;
    }

    public String getBuyTokenSignetMinLimit() {
        return buyTokenSignetMinLimit;
    }

    public void setBuyTokenSignetMinLimit(String buyTokenSignetMinLimit) {
        this.buyTokenSignetMinLimit = buyTokenSignetMinLimit;
    }

    public int getBuyTokenSignetType() {
        return buyTokenSignetType;
    }

    public void setBuyTokenSignetType(int buyTokenSignetType) {
        this.buyTokenSignetType = buyTokenSignetType;
    }

    public String getBuyTokenSignetTxnLimit() {
        return buyTokenSignetTxnLimit;
    }

    public void setBuyTokenSignetTxnLimit(String buyTokenSignetTxnLimit) {
        this.buyTokenSignetTxnLimit = buyTokenSignetTxnLimit;
    }
}

