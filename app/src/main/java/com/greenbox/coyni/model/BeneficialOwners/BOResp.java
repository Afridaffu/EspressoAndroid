package com.greenbox.coyni.model.BeneficialOwners;

import com.greenbox.coyni.model.Error;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BOResp {
    private String status;
    private String timestamp;
    private List<BeneficialOwner> data;
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

    public List<BeneficialOwner> getData() {
        return data;
    }

    public void setData(List<BeneficialOwner> data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class BeneficialOwner implements Serializable {
        private int id;
        private String firstName = "";
        private String lastName = "";
        private String dob;
        private int ownershipParcentage = -1;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String zipCode;
        private String country;
        private String ssn;
        private boolean isDraft = false;
        private ArrayList<RequiredDocumets> requiredDocuments = new ArrayList<>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getDob() {
            return dob;
        }

        public void setDob(String dob) {
            this.dob = dob;
        }

        public int getOwnershipParcentage() {
            return ownershipParcentage;
        }

        public void setOwnershipParcentage(int ownershipParcentage) {
            this.ownershipParcentage = ownershipParcentage;
        }

        public String getAddressLine1() {
            return addressLine1;
        }

        public void setAddressLine1(String addressLine1) {
            this.addressLine1 = addressLine1;
        }

        public String getAddressLine2() {
            return addressLine2;
        }

        public void setAddressLine2(String addressLine2) {
            this.addressLine2 = addressLine2;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getSsn() {
            return ssn;
        }

        public void setSsn(String ssn) {
            this.ssn = ssn;
        }

        public ArrayList<RequiredDocumets> getRequiredDocuments() {
            return requiredDocuments;
        }

        public void setRequiredDocuments(ArrayList<RequiredDocumets> requiredDocumets) {
            this.requiredDocuments = requiredDocumets;
        }

        public boolean isDraft() {
            return isDraft;
        }

        public void setDraft(boolean draft) {
            isDraft = draft;
        }
    }

    public class RequiredDocumets implements Serializable {
        private String imgSize;
        private String imgName;
        private String imgLink;
        private String updatedAt;
        private int identityId;

        public String getImgSize() {
            return imgSize;
        }

        public void setImgSize(String imgSize) {
            this.imgSize = imgSize;
        }

        public String getImgName() {
            return imgName;
        }

        public void setImgName(String imgName) {
            this.imgName = imgName;
        }

        public String getImgLink() {
            return imgLink;
        }

        public void setImgLink(String imgLink) {
            this.imgLink = imgLink;
        }

        public int getIdentityId() {
            return identityId;
        }

        public void setIdentityId(int identityId) {
            this.identityId = identityId;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}

