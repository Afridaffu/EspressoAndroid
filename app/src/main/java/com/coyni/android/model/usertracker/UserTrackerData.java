package com.coyni.android.model.usertracker;

public class UserTrackerData {
    private Boolean isProfileVerified;
    private Boolean isAuthyRegistered;
    private Boolean isAddressAvailable;
    private Boolean isPaymentModeAdded;
    private Boolean isPersonIdentified;

    public Boolean getProfileVerified() {
        return isProfileVerified;
    }

    public void setProfileVerified(Boolean profileVerified) {
        isProfileVerified = profileVerified;
    }

    public Boolean getAuthyRegistered() {
        return isAuthyRegistered;
    }

    public void setAuthyRegistered(Boolean authyRegistered) {
        isAuthyRegistered = authyRegistered;
    }

    public Boolean getAddressAvailable() {
        return isAddressAvailable;
    }

    public void setAddressAvailable(Boolean addressAvailable) {
        isAddressAvailable = addressAvailable;
    }

    public Boolean getPaymentModeAdded() {
        return isPaymentModeAdded;
    }

    public void setPaymentModeAdded(Boolean paymentModeAdded) {
        isPaymentModeAdded = paymentModeAdded;
    }

    public Boolean getPersonIdentified() {
        return isPersonIdentified;
    }

    public void setPersonIdentified(Boolean personIdentified) {
        isPersonIdentified = personIdentified;
    }
}
