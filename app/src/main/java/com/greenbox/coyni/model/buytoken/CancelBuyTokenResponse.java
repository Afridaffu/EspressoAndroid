package com.greenbox.coyni.model.buytoken;

import com.greenbox.coyni.model.Error;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public class CancelBuyTokenResponse {
    private String status;
    private String timestamp;
    private String data;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}