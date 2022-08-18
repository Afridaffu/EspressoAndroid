package com.coyni.mapp.model.featurecontrols;

import com.coyni.mapp.model.Error;

public class FeatureDataByUser {
    private String status;
    private String timestamp;
    private FeatureData data;
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

    public FeatureData getData() {
        return data;
    }

    public void setData(FeatureData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
