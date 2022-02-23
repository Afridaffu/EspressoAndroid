package com.greenbox.coyni.model.preferences;

import com.greenbox.coyni.model.Error;

import java.util.List;

public class ProfilesResponse {
    private String status;
    private String timestamp;
    private List<Profiles> data;
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

    public List<Profiles> getData() {
        return data;
    }

    public void setData(List<Profiles> data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Profiles {
        private int id;
        private String fullName;
        private String accountType;
        private String countryCode;
        private String phoneNumber;
        private String email;
        private String image;
        private String companyName;
        private String dbaName;
        private String accountStatus;
        private String dbaOwner;
        private boolean isSelected = false;


        public boolean isSelected() {
            return isSelected;
        }

        public void setSelected(boolean selected) {
            isSelected = selected;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getAccountType() {
            return accountType;
        }

        public void setAccountType(String accountType) {
            this.accountType = accountType;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
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

        public String getAccountStatus() {
            return accountStatus;
        }

        public void setAccountStatus(String accountStatus) {
            this.accountStatus = accountStatus;
        }

        public String getDbaOwner() {
            return dbaOwner;
        }

        public void setDbaOwner(String dbaOwner) {
            this.dbaOwner = dbaOwner;
        }
    }
}
