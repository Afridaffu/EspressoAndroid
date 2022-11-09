package com.coyni.mapp.model.signin;

import com.coyni.mapp.model.login.StateList;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class InitializeResponseData {
    @SerializedName("userId")
    @Expose
    private int userId;
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
    private int dbaOwnerId;
    @SerializedName("businessUserId")
    @Expose
    private String businessUserId;
    @SerializedName("jwtToken")
    @Expose
    private String jwtToken;
    @SerializedName("statusChangeReasonType")
    @Expose
    private String statusChangeReasonType;
    @SerializedName("tracker")
    @Expose
    private TrackerResponseData tracker;
    @SerializedName("businessTracker")
    @Expose
    private BusinessTrackerData businessTracker;
    @SerializedName("authorities")
    @Expose
    private List<Object> authorities = null;
    @SerializedName("stateList")
    @Expose
    private StateList stateList;
    @SerializedName("authyRegistered")
    @Expose
    private boolean authyRegistered;
    @SerializedName("reserveEnabled")
    @Expose
    private boolean reserveEnabled;

    @SerializedName("ownerDetails")
    @Expose
    private InitializeResponseData ownerDetails;

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

    public int getDbaOwnerId() {
        return dbaOwnerId;
    }

    public void setDbaOwnerId(int dbaOwnerId) {
        this.dbaOwnerId = dbaOwnerId;
    }

    public Object getBusinessUserId() {
        return businessUserId;
    }

    public void setBusinessUserId(String businessUserId) {
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

    public TrackerResponseData getTracker() {
        return tracker;
    }

    public void setTracker(TrackerResponseData tracker) {
        this.tracker = tracker;
    }

    public BusinessTrackerData getBusinessTracker() {
        return businessTracker;
    }

    public void setBusinessTracker(BusinessTrackerData businessTracker) {
        this.businessTracker = businessTracker;
    }

    public List<Object> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Object> authorities) {
        this.authorities = authorities;
    }

    public StateList getStateList() {
        return stateList;
    }

    public void setStateList(StateList stateList) {
        this.stateList = stateList;
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

    public InitializeResponseData getOwnerDetails() {
        return ownerDetails;
    }

    public void setOwnerDetails(InitializeResponseData ownerDetails) {
        this.ownerDetails = ownerDetails;
    }
}
