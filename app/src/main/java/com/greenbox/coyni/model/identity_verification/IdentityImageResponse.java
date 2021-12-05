package com.greenbox.coyni.model.identity_verification;


import com.greenbox.coyni.model.Error;
import com.greenbox.coyni.model.login.LoginData;

public class IdentityImageResponse {
    private String status;
    private String timestamp;
    private LoginData data;
    private Error error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public LoginData getData() {
        return data;
    }

    public void setData(LoginData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

