package com.coyni.mapp.model.profile.updatephone;

public class UpdatePhoneRequest {
    private String currentPhoneNumber;
    private String currentcountryCode;
    private String newPhoneNumber;
    private String newcountryCode;

    public String getCurrentPhoneNumber() {
        return currentPhoneNumber;
    }

    public void setCurrentPhoneNumber(String currentPhoneNumber) {
        this.currentPhoneNumber = currentPhoneNumber;
    }

    public String getCurrentcountryCode() {
        return currentcountryCode;
    }

    public void setCurrentcountryCode(String currentcountryCode) {
        this.currentcountryCode = currentcountryCode;
    }

    public String getNewPhoneNumber() {
        return newPhoneNumber;
    }

    public void setNewPhoneNumber(String newPhoneNumber) {
        this.newPhoneNumber = newPhoneNumber;
    }

    public String getNewcountryCode() {
        return newcountryCode;
    }

    public void setNewcountryCode(String newcountryCode) {
        this.newcountryCode = newcountryCode;
    }
}
