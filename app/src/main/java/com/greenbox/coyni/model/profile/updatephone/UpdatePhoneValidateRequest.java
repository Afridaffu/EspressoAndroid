package com.greenbox.coyni.model.profile.updatephone;

public class UpdatePhoneValidateRequest {
    private boolean isCurrentNumber;
    private String otp;
    private String trackerId;
    private String countryCode;
    private String phoneNumber;

    public boolean isCurrentNumber() {
        return isCurrentNumber;
    }

    public void setCurrentNumber(boolean currentNumber) {
        isCurrentNumber = currentNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(String trackerId) {
        this.trackerId = trackerId;
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
}
