package com.coyni.mapp.model.featurecontrols;

import com.coyni.mapp.model.Error;

public class FeatureControlRespByUser {
    private String status ;
    private String timestamp;
    private FeatureDataByUser data;
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

    public FeatureDataByUser getData() {
        return data;
    }

    public void setData(FeatureDataByUser data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
