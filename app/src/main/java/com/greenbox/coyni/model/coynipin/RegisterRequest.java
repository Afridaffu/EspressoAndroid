package com.greenbox.coyni.model.coynipin;

public class RegisterRequest {
    private String pin;
    private int userId;

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
