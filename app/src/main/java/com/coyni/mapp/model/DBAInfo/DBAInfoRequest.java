package com.coyni.mapp.model.DBAInfo;

import com.coyni.mapp.model.register.PhNoWithCountryCode;

public class DBAInfoRequest {

    //Address Info
    private String addressLine1 = "";
    private String addressLine2 = "";
    private String city = "";
    private String country = "";
    private String state = "";
    private String zipCode = "";

    //Basic Info
    private String name = "";
    private String email = "";
    private PhNoWithCountryCode phoneNumberDto = new PhNoWithCountryCode();
    private String businessType = "";
    private int identificationType = 0; // Here Identification Type is ECOMMERCE (9) or RETAIL(8)
    private int averageTicket ;
    private int highTicket ;
    private int monthlyProcessingVolume ;
    private boolean copyCompanyInfo = false;
    private int timeZone = 0;
    private String website = "";


    public String getAddrexssLine1() {
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

    public int getAverageTicket() {
        return averageTicket;
    }

    public void setAverageTicket(int averageTicket) {
        this.averageTicket = averageTicket;
    }

    public int getHighTicket() {
        return highTicket;
    }

    public void setHighTicket(int highTicket) {
        this.highTicket = highTicket;
    }

    public int getMonthlyProcessingVolume() {
        return monthlyProcessingVolume;
    }

    public void setMonthlyProcessingVolume(int monthlyProcessingVolume) {
        this.monthlyProcessingVolume = monthlyProcessingVolume;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public boolean isCopyCompanyInfo() {
        return copyCompanyInfo;
    }

    public void setCopyCompanyInfo(boolean copyCompanyInfo) {
        this.copyCompanyInfo = copyCompanyInfo;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }
}
