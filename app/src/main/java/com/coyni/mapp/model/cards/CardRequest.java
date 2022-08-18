package com.coyni.mapp.model.cards;

public class CardRequest {
//    private String key;
//    private String payload;
//
//    public String getKey() {
//        return key;
//    }
//
//    public void setKey(String key) {
//        this.key = key;
//    }
//
//    public String getPayload() {
//        return payload;
//    }
//
//    public void setPayload(String payload) {
//        this.payload = payload;
//    }

    private String addressLine1;
    private String addressLine2;
    private String cardNumber;
    private String city;
    private String country;
    private String cvc;
    private Boolean defaultForAllWithDrawals;
    private String expiryDate;
    private String name;
    private String state;
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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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

    public String getCvc() {
        return cvc;
    }

    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public Boolean getDefaultForAllWithDrawals() {
        return defaultForAllWithDrawals;
    }

    public void setDefaultForAllWithDrawals(Boolean defaultForAllWithDrawals) {
        this.defaultForAllWithDrawals = defaultForAllWithDrawals;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
