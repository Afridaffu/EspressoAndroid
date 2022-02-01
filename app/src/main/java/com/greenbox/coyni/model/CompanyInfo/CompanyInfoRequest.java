package com.greenbox.coyni.model.CompanyInfo;

import com.greenbox.coyni.model.register.PhNoWithCountryCode;

public class CompanyInfoRequest {

    //Address Info
    private String addressLine1 = "";
    private String addressLine2 = "";
    private String city = "";
    private String country = "";
    private String state = "";
    private String zipCode = "";

    //Basin Info
    private String name = "";
    private String email = "";
    private PhNoWithCountryCode phoneNumberDto = new PhNoWithCountryCode();
    private String businessEntity = "";
    private String ssnOrEin = "";
    private int identificationType; // Here Identification Type is SSN(11) or EIN/TIN(10)


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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(int identificationType) {
        this.identificationType = identificationType;
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
}
