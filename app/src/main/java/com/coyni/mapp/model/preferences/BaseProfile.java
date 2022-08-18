package com.coyni.mapp.model.preferences;

import java.util.Objects;

public class BaseProfile {
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
    private int dbaCount = 0;

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

    public int getDbaCount() {
        return dbaCount;
    }

    public void setDbaCount(int dbaCount) {
        this.dbaCount = dbaCount;
    }

    @Override
    public String toString() {
        return "Profiles{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", accountType='" + accountType + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", image='" + image + '\'' +
                ", companyName='" + companyName + '\'' +
                ", dbaName='" + dbaName + '\'' +
                ", accountStatus='" + accountStatus + '\'' +
                ", dbaOwner='" + dbaOwner + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseProfile profiles = (BaseProfile) o;
        return getId() == profiles.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
