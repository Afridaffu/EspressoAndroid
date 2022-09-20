package com.coyni.mapp.model.signin;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrackerResponseData {

    @SerializedName("isProfileVerified")
    @Expose
    private boolean isProfileVerified;
    @SerializedName("isAuthyRegistered")
    @Expose
    private boolean isAuthyRegistered;
    @SerializedName("isAddressAvailable")
    @Expose
    private boolean isAddressAvailable;
    @SerializedName("isPaymentModeAdded")
    @Expose
    private boolean isPaymentModeAdded;
    @SerializedName("isPersonIdentified")
    @Expose
    private boolean isPersonIdentified;
    @SerializedName("isAgreementSigned")
    @Expose
    private boolean isAgreementSigned;

    public boolean isIsProfileVerified() {
        return isProfileVerified;
    }

    public void setIsProfileVerified(boolean isProfileVerified) {
        this.isProfileVerified = isProfileVerified;
    }

    public boolean isIsAuthyRegistered() {
        return isAuthyRegistered;
    }

    public void setIsAuthyRegistered(boolean isAuthyRegistered) {
        this.isAuthyRegistered = isAuthyRegistered;
    }

    public boolean isIsAddressAvailable() {
        return isAddressAvailable;
    }

    public void setIsAddressAvailable(boolean isAddressAvailable) {
        this.isAddressAvailable = isAddressAvailable;
    }

    public boolean isIsPaymentModeAdded() {
        return isPaymentModeAdded;
    }

    public void setIsPaymentModeAdded(boolean isPaymentModeAdded) {
        this.isPaymentModeAdded = isPaymentModeAdded;
    }

    public boolean isIsPersonIdentified() {
        return isPersonIdentified;
    }

    public void setIsPersonIdentified(boolean isPersonIdentified) {
        this.isPersonIdentified = isPersonIdentified;
    }

    public boolean isIsAgreementSigned() {
        return isAgreementSigned;
    }

    public void setIsAgreementSigned(boolean isAgreementSigned) {
        this.isAgreementSigned = isAgreementSigned;
    }
}
