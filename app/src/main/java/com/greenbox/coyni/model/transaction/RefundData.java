package com.greenbox.coyni.model.transaction;

import com.greenbox.coyni.model.BaseResponse;

public class RefundData {

    private Boolean insufficientMerchantBalance;
    private Boolean insufficientTokenBalance;
    private String walletBalance;
    private Double processingFee;
    private String referenceId;
    private Integer walletType;

    public Boolean getInsufficientMerchantBalance() {
        return insufficientMerchantBalance;
    }

    public void setInsufficientMerchantBalance(Boolean insufficientMerchantBalance) {
        this.insufficientMerchantBalance = insufficientMerchantBalance;
    }

    public Boolean getInsufficientTokenBalance() {
        return insufficientTokenBalance;
    }

    public void setInsufficientTokenBalance(Boolean insufficientTokenBalance) {
        this.insufficientTokenBalance = insufficientTokenBalance;
    }

    public String getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(String walletBalance) {
        this.walletBalance = walletBalance;
    }

    public Double getProcessingFee() {
        return processingFee;
    }

    public void setProcessingFee(Double processingFee) {
        this.processingFee = processingFee;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public Integer getWalletType() {
        return walletType;
    }

    public void setWalletType(Integer walletType) {
        this.walletType = walletType;
    }

}
