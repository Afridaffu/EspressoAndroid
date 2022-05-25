package com.greenbox.coyni.model.profile;

import com.greenbox.coyni.model.Error;

public class AddBusinessUserResponse {

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
        private boolean isReserveEnabled;
        private boolean paymentAdded;
        private boolean passwordExpired;
        private boolean coyniPin;

        public boolean isReserveEnabled() {
            return isReserveEnabled;
        }

        public void setReserveEnabled(boolean reserveEnabled) {
            isReserveEnabled = reserveEnabled;
        }

        public boolean isPaymentAdded() {
            return paymentAdded;
        }

        public void setPaymentAdded(boolean paymentAdded) {
            this.paymentAdded = paymentAdded;
        }

        public boolean isPasswordExpired() {
            return passwordExpired;
        }

        public void setPasswordExpired(boolean passwordExpired) {
            this.passwordExpired = passwordExpired;
        }

        public boolean isCoyniPin() {
            return coyniPin;
        }

        public void setCoyniPin(boolean coyniPin) {
            this.coyniPin = coyniPin;
        }

        public boolean isOldSessionExist() {
            return oldSessionExist;
        }

        public void setOldSessionExist(boolean oldSessionExist) {
            this.oldSessionExist = oldSessionExist;
        }

        public int getBusinessUserId() {
            return businessUserId;
        }

        public void setBusinessUserId(int businessUserId) {
            this.businessUserId = businessUserId;
        }

        public int getUpdatedAgreementId() {
            return updatedAgreementId;
        }

        public void setUpdatedAgreementId(int updatedAgreementId) {
            this.updatedAgreementId = updatedAgreementId;
        }

        public String getStateList() {
            return stateList;
        }

        public void setStateList(String stateList) {
            this.stateList = stateList;
        }

        public String getAuthorities() {
            return authorities;
        }

        public void setAuthorities(String authorities) {
            this.authorities = authorities;
        }

        public String getDbaName() {
            return dbaName;
        }

        public void setDbaName(String dbaName) {
            this.dbaName = dbaName;
        }

        public String getRequestToken() {
            return requestToken;
        }

        public void setRequestToken(String requestToken) {
            this.requestToken = requestToken;
        }

        private boolean oldSessionExist;

        private int accountType;
        private int userId;
        private int dbaOwnerId;
        private String jwtToken;
        private String accountStatus;
        private int businessUserId;
        private int updatedAgreementId;

        private String stateList;
        private String authorities;
        private String dbaName;
        private String requestToken;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getAccountStatus() {
            return accountStatus;
        }

        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        public int getDbaOwnerId() {
            return dbaOwnerId;
        }

        public void setDbaOwnerId(int dbaOwnerId) {
            this.dbaOwnerId = dbaOwnerId;
        }

        public int getAccountType() {
            return accountType;
        }

        public void setAccountType(int accountType) {
            this.accountType = accountType;
        }

        public String getJwtToken() {
            return jwtToken;
        }

        public void setJwtToken(String jwtToken) {
            this.jwtToken = jwtToken;
        }


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

        @Override
        public String toString() {
            return "TrackerData{" +
                    "isProfileVerified=" + isProfileVerified +
                    ", isAuthyRegistered=" + isAuthyRegistered +
                    ", isAddressAvailable=" + isAddressAvailable +
                    ", isPaymentModeAdded=" + isPaymentModeAdded +
                    ", isPersonIdentified=" + isPersonIdentified +
                    ", isAgreementSigned=" + isAgreementSigned +
                    ", accountType=" + accountType +
                    ", jwtToken='" + jwtToken + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "AddBusinessUserResponse{" +
                "status='" + status + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", data=" + data +
                ", error=" + error +
                '}';
    }
}
