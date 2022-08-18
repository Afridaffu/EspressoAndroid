package com.coyni.mapp.model.forgotpassword;

import com.coyni.mapp.model.Error;

public class ManagePasswordResponse {
    private String status;
    private String timestamp;
    private ManagePasswordData data;
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

    public ManagePasswordData getData() {
        return data;
    }

    public void setData(ManagePasswordData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
