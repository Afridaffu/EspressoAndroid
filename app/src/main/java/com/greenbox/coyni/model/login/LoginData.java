package com.greenbox.coyni.model.login;

public class LoginData {
    private int userId;
    private String email;
    private String phoneNumber;
    private String jwtToken;
    private String message;
    private Boolean authyRegistered;
    private Boolean passwordExpired;
    private int passwordFailedAttempts;
    private int updatedAgreementId;
    private Boolean agreementsSigned;
    private Boolean kycStatus;
    private Boolean riskFlag;
    private Boolean coyniPin;
    private Boolean paymentAdded;
    private Boolean biometricEnabled;
    private StateList stateList;
    private int accountType;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getAuthyRegistered() {
        return authyRegistered;
    }

    public void setAuthyRegistered(Boolean authyRegistered) {
        this.authyRegistered = authyRegistered;
    }

    public Boolean getPasswordExpired() {
        return passwordExpired;
    }

    public void setPasswordExpired(Boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    public int getPasswordFailedAttempts() {
        return passwordFailedAttempts;
    }

    public void setPasswordFailedAttempts(int passwordFailedAttempts) {
        this.passwordFailedAttempts = passwordFailedAttempts;
    }

    public int getUpdatedAgreementId() {
        return updatedAgreementId;
    }

    public void setUpdatedAgreementId(int updatedAgreementId) {
        this.updatedAgreementId = updatedAgreementId;
    }

    public Boolean getAgreementsSigned() {
        return agreementsSigned;
    }

    public void setAgreementsSigned(Boolean agreementsSigned) {
        this.agreementsSigned = agreementsSigned;
    }

    public Boolean getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(Boolean kycStatus) {
        this.kycStatus = kycStatus;
    }

    public Boolean getRiskFlag() {
        return riskFlag;
    }

    public void setRiskFlag(Boolean riskFlag) {
        this.riskFlag = riskFlag;
    }

    public Boolean getCoyniPin() {
        return coyniPin;
    }

    public void setCoyniPin(Boolean coyniPin) {
        this.coyniPin = coyniPin;
    }

    public Boolean getPaymentAdded() {
        return paymentAdded;
    }

    public void setPaymentAdded(Boolean paymentAdded) {
        this.paymentAdded = paymentAdded;
    }

    public Boolean getBiometricEnabled() {
        return biometricEnabled;
    }

    public void setBiometricEnabled(Boolean biometricEnabled) {
        this.biometricEnabled = biometricEnabled;
    }

    public StateList getStateList() {
        return stateList;
    }

    public void setStateList(StateList stateList) {
        this.stateList = stateList;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }
}


