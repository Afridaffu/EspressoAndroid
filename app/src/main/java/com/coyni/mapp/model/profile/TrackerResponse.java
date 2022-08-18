package com.coyni.mapp.model.profile;

import com.coyni.mapp.model.Error;

public class TrackerResponse {

    private String status;
    private String timestamp;
    private TrackerData data;
    private Error error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public TrackerData getData() {
        return data;
    }

    public void setData(TrackerData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class TrackerData{
        private boolean isProfileVerified;
        private boolean isAuthyRegistered;
        private boolean isAddressAvailable;
        private boolean isPaymentModeAdded;
        private boolean isPersonIdentified;
        private boolean isAgreementSigned;


        public boolean isProfileVerified() {
            return isProfileVerified;
        }

        public void setProfileVerified(boolean profileVerified) {
            isProfileVerified = profileVerified;
        }

        public boolean isAuthyRegistered() {
            return isAuthyRegistered;
        }

        public void setAuthyRegistered(boolean authyRegistered) {
            isAuthyRegistered = authyRegistered;
        }

        public boolean isAddressAvailable() {
            return isAddressAvailable;
        }

        public void setAddressAvailable(boolean addressAvailable) {
            isAddressAvailable = addressAvailable;
        }

        public boolean isPaymentModeAdded() {
            return isPaymentModeAdded;
        }

        public void setPaymentModeAdded(boolean paymentModeAdded) {
            isPaymentModeAdded = paymentModeAdded;
        }

        public boolean isPersonIdentified() {
            return isPersonIdentified;
        }

        public void setPersonIdentified(boolean personIdentified) {
            isPersonIdentified = personIdentified;
        }

        public boolean isAgreementSigned() {
            return isAgreementSigned;
        }

        public void setAgreementSigned(boolean agreementSigned) {
            isAgreementSigned = agreementSigned;
        }
    }
}
