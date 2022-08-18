package com.coyni.mapp.model.business_id_verification;


import com.coyni.mapp.model.Error;

public class BusinessTrackerResponse {
    private String status;
    private String timestamp;
    private Daata data;
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

    public Daata getData() {
        return data;
    }

    public void setData(Daata data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Daata {
        private boolean isProfileVerified = false;
        private boolean isCompanyInfo = false;
        private boolean isDbaInfo = false;
        private boolean isBeneficialOwners = false;
        private boolean isBankAccount = false;
        private boolean isAgreementSigned = false;
        private boolean isApplicationSummary = false;
        private boolean isFirstLogin = false;
        private boolean isProcessingFees = false;


        public boolean isProfileVerified() {
            return isProfileVerified;
        }

        public void setProfileVerified(boolean profileVerified) {
            isProfileVerified = profileVerified;
        }

        public boolean isCompanyInfo() {
            return isCompanyInfo;
        }

        public void setCompanyInfo(boolean companyInfo) {
            isCompanyInfo = companyInfo;
        }

        public boolean isDbaInfo() {
            return isDbaInfo;
        }

        public void setDbaInfo(boolean dbaInfo) {
            isDbaInfo = dbaInfo;
        }

        public boolean isBeneficialOwners() {
            return isBeneficialOwners;
        }

        public void setBeneficialOwners(boolean beneficialOwners) {
            isBeneficialOwners = beneficialOwners;
        }

        public boolean isIsbankAccount() {
            return isBankAccount;
        }

        public void setIsbankAccount(boolean isbankAccount) {
            this.isBankAccount = isbankAccount;
        }

        public boolean isAgreementSigned() {
            return isAgreementSigned;
        }

        public void setAgreementSigned(boolean agreementSigned) {
            isAgreementSigned = agreementSigned;
        }

        public boolean isApplicationSummary() {
            return isApplicationSummary;
        }

        public void setApplicationSummary(boolean applicationSummary) {
            isApplicationSummary = applicationSummary;
        }

        public boolean isFirstLogin() {
            return isFirstLogin;
        }

        public void setFirstLogin(boolean firstLogin) {
            isFirstLogin = firstLogin;
        }

        public boolean isProcessingFees() {
            return isProcessingFees;
        }

        public void setProcessingFees(boolean processingFees) {
            isProcessingFees = processingFees;
        }

        @Override
        public String toString() {
            return "Daata{" +
                    "isProfileVerified=" + isProfileVerified +
                    ", isCompanyInfo=" + isCompanyInfo +
                    ", isDbaInfo=" + isDbaInfo +
                    ", isBeneficialOwners=" + isBeneficialOwners +
                    ", isBankAccount=" + isBankAccount +
                    ", isAgreementSigned=" + isAgreementSigned +
                    ", isApplicationSummary=" + isApplicationSummary +
                    ", isFirstLogin=" + isFirstLogin +
                    ", isProcessingFees=" + isProcessingFees +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BusinessTrackerResponse{" +
                "status='" + status + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", data=" + data +
                ", error=" + error +
                '}';
    }
}

