package com.coyni.mapp.model;

import com.coyni.mapp.model.users.AccountLimitsData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FetchAddressResp {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("data")
    @Expose
    private AddressData data;
    @SerializedName("error")
    @Expose
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

    public AddressData getData() {
        return data;
    }

    public void setData(AddressData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class AddressData {
        @SerializedName("addressLine1")
        @Expose
        private String addressLine1;

        @SerializedName("addressLine2")
        @Expose
        private String addressLine2;

        @SerializedName("country")
        @Expose
        private String country;

        @SerializedName("state")
        @Expose
        private String state;

        @SerializedName("city")
        @Expose
        private String city;

        @SerializedName("zipCode")
        @Expose
        private String zipCode;

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

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getZipCode() {
            return zipCode;
        }

        public void setZipCode(String zipCode) {
            this.zipCode = zipCode;
        }
    }
}