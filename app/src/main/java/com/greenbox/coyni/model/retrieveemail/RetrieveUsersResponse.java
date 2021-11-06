package com.greenbox.coyni.model.retrieveemail;

import com.greenbox.coyni.model.Error;

import java.util.List;

public class RetrieveUsersResponse {
    private String status;
    private String timestamp;
    private List<RetUserResData> data;
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

    public List<RetUserResData> getData() {
        return data;
    }

    public void setData(List<RetUserResData> data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
