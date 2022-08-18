package com.coyni.mapp.model.wallet;

import com.coyni.mapp.model.Error;

public class UserDetails {
    private String status;
    private String timestamp;
    private UserDetailsData data;
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

    public UserDetailsData getData() {
        return data;
    }

    public void setData(UserDetailsData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
