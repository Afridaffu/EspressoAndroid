package com.greenbox.coyni.model.userrequest;

import com.greenbox.coyni.model.Error;

public class UserRequestResponse {
    private String status;
    private String timestamp;
    private UserReqRespData data;
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

    public UserReqRespData getData() {
        return data;
    }

    public void setData(UserReqRespData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
