package com.greenbox.coyni.model.register;

public class CustRegisRequest {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private int accountType;
    private String parentAccount;
    private String entityName;
    private PhNoWithCountryCode phoneNumberWithCountryCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public PhNoWithCountryCode getPhoneNumberWithCountryCode() {
        return phoneNumberWithCountryCode;
    }

    public void setPhoneNumberWithCountryCode(PhNoWithCountryCode phoneNumberWithCountryCode) {
        this.phoneNumberWithCountryCode = phoneNumberWithCountryCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(String parentAccount) {
        this.parentAccount = parentAccount;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}
