package com.coyni.android.model.reguser;

import com.coyni.android.model.Error;

import java.util.List;

public class RegUsersResponse {
    private String status;
    private String timestamp;
    private List<RegUsersResponseData> data;
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

    public List<RegUsersResponseData> getData() {
        return data;
    }

    public void setData(List<RegUsersResponseData> data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
