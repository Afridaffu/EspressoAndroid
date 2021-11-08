package com.greenbox.coyni.model.login;

public class BiometricLoginRequest {
    private String deviceId;
    private Boolean enableBiometic;
    private String mobileToken;

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Boolean getEnableBiometic() {
        return enableBiometic;
    }

    public void setEnableBiometic(Boolean enableBiometic) {
        this.enableBiometic = enableBiometic;
    }

    public String getMobileToken() {
        return mobileToken;
    }

    public void setMobileToken(String mobileToken) {
        this.mobileToken = mobileToken;
    }
}
