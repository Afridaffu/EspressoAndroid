package com.coyni.mapp.model.forgotpassword;

public class SetPasswordRespData {
    private int userId;
    private String message;

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
}

