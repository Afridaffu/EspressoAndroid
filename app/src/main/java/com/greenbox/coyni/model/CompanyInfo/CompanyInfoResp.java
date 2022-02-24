package com.greenbox.coyni.model.CompanyInfo;

import com.greenbox.coyni.model.Error;
import com.greenbox.coyni.model.register.PhNoWithCountryCode;

import java.util.ArrayList;

public class CompanyInfoResp {
    private String status;
    private String timestamp;
    private Data data;
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Data {
        private String name;
        private String email;
        private String identificationType;
        private PhNoWithCountryCode phoneNumberDto;
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String zipCode;
        private String country;
        private String businessEntity;
        private String ssnOrEin;

        private ArrayList<RequiredDocumets> requiredDocumets = new ArrayList<>();
        private ArrayList<RequiredDocumets> requiredDocuments = new ArrayList<>();


        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public PhNoWithCountryCode getPhoneNumberDto() {
            return phoneNumberDto;
        }

        public void setPhoneNumberDto(PhNoWithCountryCode phoneNumberDto) {
            this.phoneNumberDto = phoneNumberDto;
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

        public String getBusinessEntity() {
            return businessEntity;
        }

        public void setBusinessEntity(String businessEntity) {
            this.businessEntity = businessEntity;
        }

        public String getSsnOrEin() {
            return ssnOrEin;
        }

        public void setSsnOrEin(String ssnOrEin) {
            this.ssnOrEin = ssnOrEin;
        }

        public ArrayList<RequiredDocumets> getRequiredDocumets() {
            return requiredDocuments;
        }

        public void setRequiredDocumets(ArrayList<RequiredDocumets> requiredDocumets) {
            this.requiredDocuments = requiredDocumets;
        }

        public String getIdentificationType() {
            return identificationType;
        }

        public void setIdentificationType(String identificationType) {
            this.identificationType = identificationType;
        }
    }

    public static class RequiredDocumets {
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

