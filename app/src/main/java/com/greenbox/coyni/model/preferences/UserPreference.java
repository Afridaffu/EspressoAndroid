package com.greenbox.coyni.model.preferences;

import com.greenbox.coyni.model.Error;

public class UserPreference {
    private String status;
    private String timestamp;
    private UserPreferenceResponse data;
    private Error error;

    public UserPreferenceResponse getData() {
        return data;
    }

    public void setData(UserPreferenceResponse data) {
        this.data = data;
    }

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


    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
