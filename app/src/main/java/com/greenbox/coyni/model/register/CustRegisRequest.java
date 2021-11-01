package com.greenbox.coyni.model.register;

public class CustRegisRequest {

    private int userId;
    private String email;
    private String firstName;
    private String lastName;
    private String confirmPassword;
    private String createPassword;
    private int accountType;
    private int parentAccount;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public int getParentAccount() {
        return parentAccount;
    }

    public void setParentAccount(int parentAccount) {
        this.parentAccount = parentAccount;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getCreatePassword() {
        return createPassword;
    }

    public void setCreatePassword(String createPassword) {
        this.createPassword = createPassword;
    }
}
