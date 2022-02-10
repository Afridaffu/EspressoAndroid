package com.greenbox.coyni.model.fee;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FeeData {

    @SerializedName("id")
    @Expose
    public int id;
    @SerializedName("feeStructureCategoryId")
    @Expose
    public int feeStructureCategoryId;
    @SerializedName("feeStructureCategoryName")
    @Expose
    public String feeStructureCategoryName;
    @SerializedName("startDate")
    @Expose
    public String startDate;
    @SerializedName("withdrawalGiftcardFeeInDollar")
    @Expose
    public String withdrawalGiftcardFeeInDollar;
    @SerializedName("withdrawalGiftcardFeeInPercent")
    @Expose
    public String withdrawalGiftcardFeeInPercent;
    @SerializedName("withdrawalInstantFeeInDollar")
    @Expose
    public String withdrawalInstantFeeInDollar;
    @SerializedName("withdrawalInstantFeeInPercent")
    @Expose
    public String withdrawalInstantFeeInPercent;
    @SerializedName("withdrawalBankFeeInDollar")
    @Expose
    public String withdrawalBankFeeInDollar;
    @SerializedName("withdrawalBankFeeInPercent")
    @Expose
    public String withdrawalBankFeeInPercent;
    @SerializedName("withdrawalFailedBankFeeInDollar")
    @Expose
    public String withdrawalFailedBankFeeInDollar;
    @SerializedName("withdrawalFailedBankFeeInPercent")
    @Expose
    public String withdrawalFailedBankFeeInPercent;
    @SerializedName("sendTokenFeeInDollar")
    @Expose
    public String sendTokenFeeInDollar;
    @SerializedName("sendTokenFeeInPercent")
    @Expose
    public String sendTokenFeeInPercent;
    @SerializedName("buyTokenCardFeeInDollar")
    @Expose
    public String buyTokenCardFeeInDollar;
    @SerializedName("buyTokenCardFeeInPercent")
    @Expose
    public String buyTokenCardFeeInPercent;
    @SerializedName("buyTokenBankFeeInDollar")
    @Expose
    public String buyTokenBankFeeInDollar;
    @SerializedName("buyTokenBankFeeInPercent")
    @Expose
    public String buyTokenBankFeeInPercent;
    @SerializedName("withdrawalSignetFeeInDollar")
    @Expose
    public String withdrawalSignetFeeInDollar;
    @SerializedName("withdrawalSignetFeeInPercent")
    @Expose
    public String withdrawalSignetFeeInPercent;
    @SerializedName("buyTokenSignetFeeInDollar")
    @Expose
    public String buyTokenSignetFeeInDollar;
    @SerializedName("buyTokenSignetFeeInPercent")
    @Expose
    public String buyTokenSignetFeeInPercent;
    @SerializedName("monthlyServiceFeeInDollar")
    @Expose
    public String monthlyServiceFeeInDollar;
    @SerializedName("monthlyServiceFeeInPercent")
    @Expose
    public String monthlyServiceFeeInPercent;
    @SerializedName("transactionSaleOrderTokenFeeInDollar")
    @Expose
    public String transactionSaleOrderTokenFeeInDollar;
    @SerializedName("transactionSaleOrderTokenFeeInPercent")
    @Expose
    public String transactionSaleOrderTokenFeeInPercent;
    @SerializedName("transactionRefundFeeInDollar")
    @Expose
    public String transactionRefundFeeInDollar;
    @SerializedName("transactionRefundFeeInPercent")
    @Expose
    public String transactionRefundFeeInPercent;
    @SerializedName("disputeChargebackFeeInDollar")
    @Expose
    public String disputeChargebackFeeInDollar;
    @SerializedName("disputeChargebackFeeInPercent")
    @Expose
    public String disputeChargebackFeeInPercent;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFeeStructureCategoryId() {
        return feeStructureCategoryId;
    }

    public void setFeeStructureCategoryId(int feeStructureCategoryId) {
        this.feeStructureCategoryId = feeStructureCategoryId;
    }

    public String getFeeStructureCategoryName() {
        return feeStructureCategoryName;
    }

    public void setFeeStructureCategoryName(String feeStructureCategoryName) {
        this.feeStructureCategoryName = feeStructureCategoryName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getWithdrawalGiftcardFeeInDollar() {
        return withdrawalGiftcardFeeInDollar;
    }

    public void setWithdrawalGiftcardFeeInDollar(String withdrawalGiftcardFeeInDollar) {
        this.withdrawalGiftcardFeeInDollar = withdrawalGiftcardFeeInDollar;
    }

    public String getWithdrawalGiftcardFeeInPercent() {
        return withdrawalGiftcardFeeInPercent;
    }

    public void setWithdrawalGiftcardFeeInPercent(String withdrawalGiftcardFeeInPercent) {
        this.withdrawalGiftcardFeeInPercent = withdrawalGiftcardFeeInPercent;
    }

    public String getWithdrawalInstantFeeInDollar() {
        return withdrawalInstantFeeInDollar;
    }

    public void setWithdrawalInstantFeeInDollar(String withdrawalInstantFeeInDollar) {
        this.withdrawalInstantFeeInDollar = withdrawalInstantFeeInDollar;
    }

    public String getWithdrawalInstantFeeInPercent() {
        return withdrawalInstantFeeInPercent;
    }

    public void setWithdrawalInstantFeeInPercent(String withdrawalInstantFeeInPercent) {
        this.withdrawalInstantFeeInPercent = withdrawalInstantFeeInPercent;
    }

    public String getWithdrawalBankFeeInDollar() {
        return withdrawalBankFeeInDollar;
    }

    public void setWithdrawalBankFeeInDollar(String withdrawalBankFeeInDollar) {
        this.withdrawalBankFeeInDollar = withdrawalBankFeeInDollar;
    }

    public String getWithdrawalBankFeeInPercent() {
        return withdrawalBankFeeInPercent;
    }

    public void setWithdrawalBankFeeInPercent(String withdrawalBankFeeInPercent) {
        this.withdrawalBankFeeInPercent = withdrawalBankFeeInPercent;
    }

    public String getWithdrawalFailedBankFeeInDollar() {
        return withdrawalFailedBankFeeInDollar;
    }

    public void setWithdrawalFailedBankFeeInDollar(String withdrawalFailedBankFeeInDollar) {
        this.withdrawalFailedBankFeeInDollar = withdrawalFailedBankFeeInDollar;
    }

    public String getWithdrawalFailedBankFeeInPercent() {
        return withdrawalFailedBankFeeInPercent;
    }

    public void setWithdrawalFailedBankFeeInPercent(String withdrawalFailedBankFeeInPercent) {
        this.withdrawalFailedBankFeeInPercent = withdrawalFailedBankFeeInPercent;
    }

    public String getSendTokenFeeInDollar() {
        return sendTokenFeeInDollar;
    }

    public void setSendTokenFeeInDollar(String sendTokenFeeInDollar) {
        this.sendTokenFeeInDollar = sendTokenFeeInDollar;
    }

    public String getSendTokenFeeInPercent() {
        return sendTokenFeeInPercent;
    }

    public void setSendTokenFeeInPercent(String sendTokenFeeInPercent) {
        this.sendTokenFeeInPercent = sendTokenFeeInPercent;
    }

    public String getBuyTokenCardFeeInDollar() {
        return buyTokenCardFeeInDollar;
    }

    public void setBuyTokenCardFeeInDollar(String buyTokenCardFeeInDollar) {
        this.buyTokenCardFeeInDollar = buyTokenCardFeeInDollar;
    }

    public String getBuyTokenCardFeeInPercent() {
        return buyTokenCardFeeInPercent;
    }

    public void setBuyTokenCardFeeInPercent(String buyTokenCardFeeInPercent) {
        this.buyTokenCardFeeInPercent = buyTokenCardFeeInPercent;
    }

    public String getBuyTokenBankFeeInDollar() {
        return buyTokenBankFeeInDollar;
    }

    public void setBuyTokenBankFeeInDollar(String buyTokenBankFeeInDollar) {
        this.buyTokenBankFeeInDollar = buyTokenBankFeeInDollar;
    }

    public String getBuyTokenBankFeeInPercent() {
        return buyTokenBankFeeInPercent;
    }

    public void setBuyTokenBankFeeInPercent(String buyTokenBankFeeInPercent) {
        this.buyTokenBankFeeInPercent = buyTokenBankFeeInPercent;
    }

    public String getWithdrawalSignetFeeInDollar() {
        return withdrawalSignetFeeInDollar;
    }

    public void setWithdrawalSignetFeeInDollar(String withdrawalSignetFeeInDollar) {
        this.withdrawalSignetFeeInDollar = withdrawalSignetFeeInDollar;
    }

    public String getWithdrawalSignetFeeInPercent() {
        return withdrawalSignetFeeInPercent;
    }

    public void setWithdrawalSignetFeeInPercent(String withdrawalSignetFeeInPercent) {
        this.withdrawalSignetFeeInPercent = withdrawalSignetFeeInPercent;
    }

    public String getBuyTokenSignetFeeInDollar() {
        return buyTokenSignetFeeInDollar;
    }

    public void setBuyTokenSignetFeeInDollar(String buyTokenSignetFeeInDollar) {
        this.buyTokenSignetFeeInDollar = buyTokenSignetFeeInDollar;
    }

    public String getBuyTokenSignetFeeInPercent() {
        return buyTokenSignetFeeInPercent;
    }

    public void setBuyTokenSignetFeeInPercent(String buyTokenSignetFeeInPercent) {
        this.buyTokenSignetFeeInPercent = buyTokenSignetFeeInPercent;
    }

    public String getMonthlyServiceFeeInDollar() {
        return monthlyServiceFeeInDollar;
    }

    public void setMonthlyServiceFeeInDollar(String monthlyServiceFeeInDollar) {
        this.monthlyServiceFeeInDollar = monthlyServiceFeeInDollar;
    }

    public String getMonthlyServiceFeeInPercent() {
        return monthlyServiceFeeInPercent;
    }

    public void setMonthlyServiceFeeInPercent(String monthlyServiceFeeInPercent) {
        this.monthlyServiceFeeInPercent = monthlyServiceFeeInPercent;
    }

    public String getTransactionSaleOrderTokenFeeInDollar() {
        return transactionSaleOrderTokenFeeInDollar;
    }

    public void setTransactionSaleOrderTokenFeeInDollar(String transactionSaleOrderTokenFeeInDollar) {
        this.transactionSaleOrderTokenFeeInDollar = transactionSaleOrderTokenFeeInDollar;
    }

    public String getTransactionSaleOrderTokenFeeInPercent() {
        return transactionSaleOrderTokenFeeInPercent;
    }

    public void setTransactionSaleOrderTokenFeeInPercent(String transactionSaleOrderTokenFeeInPercent) {
        this.transactionSaleOrderTokenFeeInPercent = transactionSaleOrderTokenFeeInPercent;
    }

    public String getTransactionRefundFeeInDollar() {
        return transactionRefundFeeInDollar;
    }

    public void setTransactionRefundFeeInDollar(String transactionRefundFeeInDollar) {
        this.transactionRefundFeeInDollar = transactionRefundFeeInDollar;
    }

    public String getTransactionRefundFeeInPercent() {
        return transactionRefundFeeInPercent;
    }

    public void setTransactionRefundFeeInPercent(String transactionRefundFeeInPercent) {
        this.transactionRefundFeeInPercent = transactionRefundFeeInPercent;
    }

    public String getDisputeChargebackFeeInDollar() {
        return disputeChargebackFeeInDollar;
    }

    public void setDisputeChargebackFeeInDollar(String disputeChargebackFeeInDollar) {
        this.disputeChargebackFeeInDollar = disputeChargebackFeeInDollar;
    }

    public String getDisputeChargebackFeeInPercent() {
        return disputeChargebackFeeInPercent;
    }

    public void setDisputeChargebackFeeInPercent(String disputeChargebackFeeInPercent) {
        this.disputeChargebackFeeInPercent = disputeChargebackFeeInPercent;
    }
}
