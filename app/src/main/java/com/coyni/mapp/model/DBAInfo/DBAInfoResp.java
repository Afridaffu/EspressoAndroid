package com.coyni.mapp.model.DBAInfo;

import com.coyni.mapp.model.Error;
import com.coyni.mapp.model.register.PhNoWithCountryCode;

import java.util.ArrayList;

public class DBAInfoResp {
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
        private int id = -1;
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
        private String businessType;
        private String website;
        private String monthlyProcessingVolume;
        private String highTicket;
        private String averageTicket;
        private int timeZone;
        private ArrayList<RequiredDocumets> requiredDocuments = new ArrayList<>();
        private boolean copyCompanyInfo = false;

        public boolean isCopyCompanyInfo() {
            return copyCompanyInfo;
        }

        public void setCopyCompanyInfo(boolean copyCompanyInfo) {
            this.copyCompanyInfo = copyCompanyInfo;
        }

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

        public String getIdentificationType() {
            return identificationType;
        }

        public void setIdentificationType(String identificationType) {
            this.identificationType = identificationType;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getBusinessType() {
            return businessType;
        }

        public void setBusinessType(String businessType) {
            this.businessType = businessType;
        }

        public String getWebsite() {
            return website;
        }

        public void setWebsite(String website) {
            this.website = website;
        }

        public String getMonthlyProcessingVolume() {
            return monthlyProcessingVolume;
        }

        public void setMonthlyProcessingVolume(String monthlyProcessingVolume) {
            this.monthlyProcessingVolume = monthlyProcessingVolume;
        }

        public String getHighTicket() {
            return highTicket;
        }

        public void setHighTicket(String highTicket) {
            this.highTicket = highTicket;
        }

        public String getAverageTicket() {
            return averageTicket;
        }

        public void setAverageTicket(String avarageTicket) {
            this.averageTicket = avarageTicket;
        }

        public int getTimeZone() {
            return timeZone;
        }

        public void setTimeZone(int timeZone) {
            this.timeZone = timeZone;
        }

        public ArrayList<RequiredDocumets> getRequiredDocuments() {
            return requiredDocuments;
        }

        public void setRequiredDocuments(ArrayList<RequiredDocumets> requiredDocuments) {
            this.requiredDocuments = requiredDocuments;
        }
    }

    public class RequiredDocumets {
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

