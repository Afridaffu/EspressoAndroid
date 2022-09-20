package com.coyni.mapp.model.signin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class BusinessTrackerData {

    @SerializedName("isProfileVerified")
    @Expose
    private boolean isProfileVerified;
    @SerializedName("isCompanyInfo")
    @Expose
    private boolean isCompanyInfo;
    @SerializedName("isDbaInfo")
    @Expose
    private boolean isDbaInfo;
    @SerializedName("isBeneficialOwners")
    @Expose
    private boolean isBeneficialOwners;
    @SerializedName("isBankAccount")
    @Expose
    private boolean isBankAccount;
    @SerializedName("isAgreementSigned")
    @Expose
    private boolean isAgreementSigned;
    @SerializedName("isApplicationSummary")
    @Expose
    private boolean isApplicationSummary;
    @SerializedName("isFirstLogin")
    @Expose
    private boolean isFirstLogin;
    @SerializedName("isProcessingFees")
    @Expose
    private boolean isProcessingFees;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    public boolean isIsProfileVerified() {
        return isProfileVerified;
    }

    public void setIsProfileVerified(boolean isProfileVerified) {
        this.isProfileVerified = isProfileVerified;
    }

    public boolean isIsCompanyInfo() {
        return isCompanyInfo;
    }

    public void setIsCompanyInfo(boolean isCompanyInfo) {
        this.isCompanyInfo = isCompanyInfo;
    }

    public boolean isIsDbaInfo() {
        return isDbaInfo;
    }

    public void setIsDbaInfo(boolean isDbaInfo) {
        this.isDbaInfo = isDbaInfo;
    }

    public boolean isIsBeneficialOwners() {
        return isBeneficialOwners;
    }

    public void setIsBeneficialOwners(boolean isBeneficialOwners) {
        this.isBeneficialOwners = isBeneficialOwners;
    }

    public boolean isIsBankAccount() {
        return isBankAccount;
    }

    public void setIsBankAccount(boolean isBankAccount) {
        this.isBankAccount = isBankAccount;
    }

    public boolean isIsAgreementSigned() {
        return isAgreementSigned;
    }

    public void setIsAgreementSigned(boolean isAgreementSigned) {
        this.isAgreementSigned = isAgreementSigned;
    }

    public boolean isIsApplicationSummary() {
        return isApplicationSummary;
    }

    public void setIsApplicationSummary(boolean isApplicationSummary) {
        this.isApplicationSummary = isApplicationSummary;
    }

    public boolean isIsFirstLogin() {
        return isFirstLogin;
    }

    public void setIsFirstLogin(boolean isFirstLogin) {
        this.isFirstLogin = isFirstLogin;
    }

    public boolean isIsProcessingFees() {
        return isProcessingFees;
    }

    public void setIsProcessingFees(boolean isProcessingFees) {
        this.isProcessingFees = isProcessingFees;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


}
