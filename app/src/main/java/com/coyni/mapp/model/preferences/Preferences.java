package com.coyni.mapp.model.preferences;

import com.coyni.mapp.model.Error;

public class Preferences {
    private String status;
    private String timestamp;
    private PreferencesResponse data;
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

    public PreferencesResponse getData() {
        return data;
    }

    public void setData(PreferencesResponse data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}