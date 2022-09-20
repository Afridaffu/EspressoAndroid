package com.coyni.mapp.model.register;

import com.coyni.mapp.model.login.StateList;

public class SignAgreementResponseData {
    private int userId;
    private String message;
    private String jwtToken;
    private int accountType;
    private String email;
    private String fullName;
    private String phoneNumber;
    private StateList stateList;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StateList getStateList() {
        return stateList;
    }

    public void setStateList(StateList stateList) {
        this.stateList = stateList;
    }
}
