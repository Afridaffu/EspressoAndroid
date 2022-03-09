package com.greenbox.coyni.model.summary;

import java.util.List;

public class DbaInfo {

    private Integer id;

    private String name;

    private String email;

    private PhoneNumberDto1 phoneNumberDto;

    private String addressLine1;

    private String addressLine2;

    private String city;

    private String state;

    private String zipCode;

    private String country;

    private Integer identificationType;

    private String businessType;

    private String website;

    private Integer monthlyProcessingVolume;

    private Integer highTicket;

    private Integer averageTicket;

    private Integer timeZone;

    private List<RequiredDocument> requiredDocuments = null;

    private Boolean copyCompanyInfo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public PhoneNumberDto1 getPhoneNumberDto() {
        return phoneNumberDto;
    }

    public void setPhoneNumberDto(PhoneNumberDto1 phoneNumberDto) {
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

    public Integer getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(Integer identificationType) {
        this.identificationType = identificationType;
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

    public Integer getMonthlyProcessingVolume() {
        return monthlyProcessingVolume;
    }

    public void setMonthlyProcessingVolume(Integer monthlyProcessingVolume) {
        this.monthlyProcessingVolume = monthlyProcessingVolume;
    }

    public Integer getHighTicket() {
        return highTicket;
    }

    public void setHighTicket(Integer highTicket) {
        this.highTicket = highTicket;
    }

    public Integer getAverageTicket() {
        return averageTicket;
    }

    public void setAverageTicket(Integer averageTicket) {
        this.averageTicket = averageTicket;
    }

    public Integer getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Integer timeZone) {
        this.timeZone = timeZone;
    }

    public List<RequiredDocument> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(List<RequiredDocument> requiredDocuments) {
        this.requiredDocuments = requiredDocuments;
    }

    public Boolean getCopyCompanyInfo() {
        return copyCompanyInfo;
    }

    public void setCopyCompanyInfo(Boolean copyCompanyInfo) {
        this.copyCompanyInfo = copyCompanyInfo;
    }
}
