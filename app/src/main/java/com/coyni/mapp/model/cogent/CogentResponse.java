package com.coyni.mapp.model.cogent;
import com.coyni.mapp.model.Error;

public class CogentResponse {
    private String status;
    private String timestamp;
    private CogentResponseData data;
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

    public CogentResponseData getData() {
        return data;
    }

    public void setData(CogentResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

