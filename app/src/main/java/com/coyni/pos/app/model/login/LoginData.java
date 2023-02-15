package com.coyni.pos.app.model.login;

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
//    private StateList stateList;
    private boolean isReserveEnabled;
    private int accountType;
    private int dbaOwnerId;
    private String accountStatus;
    private String companyName;
    private String dbaName;
    private boolean oldSessionExist;
    private boolean isProfileVerified;
    private boolean isPersonIdentified;
    private String firstName;
    private String lastName;
    private String requestToken;
    private String ownerImage;
    private int businessUserId;
    private Object[] authorities;

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public int getBusinessUserId() {
        return businessUserId;
    }

    public void setBusinessUserId(int businessUserId) {
        this.businessUserId = businessUserId;
    }

    public Object[] getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Object[] authorities) {
        this.authorities = authorities;
    }

    public int getDbaOwnerId() {
        return dbaOwnerId;
    }

    public void setDbaOwnerId(int dbaOwnerId) {
        this.dbaOwnerId = dbaOwnerId;
    }

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

    public boolean isReserveEnabled() {
        return isReserveEnabled;
    }

    public void setReserveEnabled(boolean reserveEnabled) {
        isReserveEnabled = reserveEnabled;
    }

//    public StateList getStateList() {
//        return stateList;
//    }
//
//    public void setStateList(StateList stateList) {
//        this.stateList = stateList;
//    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDbaName() {
        return dbaName;
    }

    public void setDbaName(String dbaName) {
        this.dbaName = dbaName;
    }

    public boolean isOldSessionExist() {
        return oldSessionExist;
    }

    public void setOldSessionExist(boolean oldSessionExist) {
        this.oldSessionExist = oldSessionExist;
    }

    public boolean isProfileVerified() {
        return isProfileVerified;
    }

    public void setProfileVerified(boolean profileVerified) {
        isProfileVerified = profileVerified;
    }

    public boolean isPersonIdentified() {
        return isPersonIdentified;
    }

    public void setPersonIdentified(boolean personIdentified) {
        isPersonIdentified = personIdentified;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getOwnerImage() {
        return ownerImage;
    }

    public void setOwnerImage(String ownerImage) {
        this.ownerImage = ownerImage;
    }
}


