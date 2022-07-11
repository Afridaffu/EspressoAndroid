package com.greenbox.coyni.model.profile;

public class ProfileData {
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String addressLine1;
    private String addressLine2;
    private String country;
    private String state;
    private String city;
    private String zipCode;
    private String message;
    private int addressType;
    private String image;
    private String accountStatus;
    private String status;
    private String dbaName;
    private String companyName;
    private String identityDocument;
    private String identityUploadDate;
    private String identityDocumentName;
    private String dob;
    private String statusChangeReasonType;
    private boolean isReserveEnabled;
    private int ssn;
    private int accountType;

    public String getIdentityDocument() {
        return identityDocument;
    }

    public void setIdentityDocument(String identityDocument) {
        this.identityDocument = identityDocument;
    }

    public String getIdentityUploadDate() {
        return identityUploadDate;
    }

    public void setIdentityUploadDate(String identityUploadDate) {
        this.identityUploadDate = identityUploadDate;
    }

    public String getIdentityDocumentName() {
        return identityDocumentName;
    }

    public void setIdentityDocumentName(String identityDocumentName) {
        this.identityDocumentName = identityDocumentName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public boolean isReserveEnabled() {
        return isReserveEnabled;
    }

    public void setReserveEnabled(boolean reserveEnabled) {
        isReserveEnabled = reserveEnabled;
    }

    public int getSsn() {
        return ssn;
    }

    public void setSsn(int ssn) {
        this.ssn = ssn;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDbaName() {
        return dbaName;
    }

    public void setDbaName(String dbaName) {
        this.dbaName = dbaName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getStatusChangeReasonType() {
        return statusChangeReasonType;
    }

    public void setStatusChangeReasonType(String statusChangeReasonType) {
        this.statusChangeReasonType = statusChangeReasonType;
    }

    @Override
    public String toString() {
        return "ProfileData{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", addressLine1='" + addressLine1 + '\'' +
                ", addressLine2='" + addressLine2 + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", message='" + message + '\'' +
                ", addressType=" + addressType +
                ", image='" + image + '\'' +
                ", accountStatus='" + accountStatus + '\'' +
                ", status='" + status + '\'' +
                ", dbaName='" + dbaName + '\'' +
                ", companyName='" + companyName + '\'' +
                '}';
    }
}
