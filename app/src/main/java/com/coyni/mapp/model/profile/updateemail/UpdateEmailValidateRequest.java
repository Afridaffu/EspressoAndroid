package com.coyni.mapp.model.profile.updateemail;

public class UpdateEmailValidateRequest {
    private boolean isOldEmail;
    private String otp;
    private String trackerId;

    public boolean isOldEmail() {
        return isOldEmail;
    }

    public void setOldEmail(boolean oldEmail) {
        isOldEmail = oldEmail;
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
}
