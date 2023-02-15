package com.coyni.pos.app.model.login;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BiometricSignInData {

        @SerializedName("userId")
        @Expose
        private long userId;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("phoneNumber")
        @Expose
        private String phoneNumber;
        @SerializedName("firstName")
        @Expose
        private String firstName;
        @SerializedName("lastName")
        @Expose
        private String lastName;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("kycStatus")
        @Expose
        private String kycStatus;
        @SerializedName("riskFlag")
        @Expose
        private boolean riskFlag;
        @SerializedName("accountStatus")
        @Expose
        private String accountStatus;
        @SerializedName("accountType")
        @Expose
        private int accountType;
        @SerializedName("companyName")
        @Expose
        private String companyName;
        @SerializedName("dbaName")
        @Expose
        private String dbaName;
        @SerializedName("dbaOwnerId")
        @Expose
        private long dbaOwnerId;
        @SerializedName("businessUserId")
        @Expose
        private long businessUserId;
        @SerializedName("jwtToken")
        @Expose
        private String jwtToken;
        @SerializedName("statusChangeReasonType")
        @Expose
        private String statusChangeReasonType;
//        @SerializedName("tracker")
//        @Expose
//        private TrackerResponseData tracker;
//        @SerializedName("businessTracker")
//        @Expose
//        private BusinessTrackerData businessTracker;
        @SerializedName("authorities")
        @Expose
        private List<Object> authorities = null;
//        @SerializedName("stateList")
//        @Expose
//        private StateList stateList;
        @SerializedName("ownerDetails")
        @Expose
        private BiometricSignInData ownerDetails;
        @SerializedName("coyniPin")
        @Expose
        private boolean coyniPin;
        @SerializedName("biometricEnabled")
        @Expose
        private boolean biometricEnabled;
        @SerializedName("authyRegistered")
        @Expose
        private boolean authyRegistered;
        @SerializedName("reserveEnabled")
        @Expose
        private boolean reserveEnabled;
        @SerializedName("passwordExpired")
        @Expose
        private boolean passwordExpired;
        @SerializedName("agreementsSigned")
        @Expose
        private boolean agreementsSigned;
        @SerializedName("ownerImage")
        @Expose
        private String ownerImage;

        private String message;
        private String requestToken;
        private boolean oldSessionExist;
        private long passwordFailedAttempts;



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequestToken() {
        return requestToken;
    }

    public void setRequestToken(String requestToken) {
        this.requestToken = requestToken;
    }

    public boolean isOldSessionExist() {
        return oldSessionExist;
    }

    public void setOldSessionExist(boolean oldSessionExist) {
        this.oldSessionExist = oldSessionExist;
    }

    public long getPasswordFailedAttempts() {
        return passwordFailedAttempts;
    }

    public void setPasswordFailedAttempts(long passwordFailedAttempts) {
        this.passwordFailedAttempts = passwordFailedAttempts;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKycStatus() {
        return kycStatus;
    }

    public void setKycStatus(String kycStatus) {
        this.kycStatus = kycStatus;
    }

    public boolean isRiskFlag() {
        return riskFlag;
    }

    public void setRiskFlag(boolean riskFlag) {
        this.riskFlag = riskFlag;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
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

    public long getDbaOwnerId() {
        return dbaOwnerId;
    }

    public void setDbaOwnerId(long dbaOwnerId) {
        this.dbaOwnerId = dbaOwnerId;
    }

    public long getBusinessUserId() {
        return businessUserId;
    }

    public void setBusinessUserId(long businessUserId) {
        this.businessUserId = businessUserId;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getStatusChangeReasonType() {
        return statusChangeReasonType;
    }

    public void setStatusChangeReasonType(String statusChangeReasonType) {
        this.statusChangeReasonType = statusChangeReasonType;
    }

//    public TrackerResponseData getTracker() {
//        return tracker;
//    }
//
//    public void setTracker(TrackerResponseData tracker) {
//        this.tracker = tracker;
//    }
//
//    public BusinessTrackerData getBusinessTracker() {
//        return businessTracker;
//    }
//
//    public void setBusinessTracker(BusinessTrackerData businessTracker) {
//        this.businessTracker = businessTracker;
//    }

    public List<Object> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Object> authorities) {
        this.authorities = authorities;
    }

//    public StateList getStateList() {
//        return stateList;
//    }
//
//    public void setStateList(StateList stateList) {
//        this.stateList = stateList;
//    }

    public BiometricSignInData getOwnerDetails() {
        return ownerDetails;
    }

    public void setOwnerDetails(BiometricSignInData ownerDetails) {
        this.ownerDetails = ownerDetails;
    }

    public boolean isCoyniPin() {
        return coyniPin;
    }

    public void setCoyniPin(boolean coyniPin) {
        this.coyniPin = coyniPin;
    }

    public boolean isBiometricEnabled() {
        return biometricEnabled;
    }

    public void setBiometricEnabled(boolean biometricEnabled) {
        this.biometricEnabled = biometricEnabled;
    }

    public boolean isAuthyRegistered() {
        return authyRegistered;
    }

    public void setAuthyRegistered(boolean authyRegistered) {
        this.authyRegistered = authyRegistered;
    }

    public boolean isReserveEnabled() {
        return reserveEnabled;
    }

    public void setReserveEnabled(boolean reserveEnabled) {
        this.reserveEnabled = reserveEnabled;
    }

    public boolean isPasswordExpired() {
        return passwordExpired;
    }

    public void setPasswordExpired(boolean passwordExpired) {
        this.passwordExpired = passwordExpired;
    }

    public String getOwnerImage() {
        return ownerImage;
    }

    public void setOwnerImage(String ownerImage) {
        this.ownerImage = ownerImage;
    }

    public boolean isAgreementsSigned() {
        return agreementsSigned;
    }

    public void setAgreementsSigned(boolean agreementsSigned) {
        this.agreementsSigned = agreementsSigned;
    }
}
