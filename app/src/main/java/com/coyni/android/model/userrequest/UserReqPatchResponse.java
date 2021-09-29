package com.coyni.android.model.userrequest;

import com.coyni.android.model.Error;

public class UserReqPatchResponse {
    private String status;
    private String timestamp;
    private PatchResponseData data;
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

    public PatchResponseData getData() {
        return data;
    }

    public void setData(PatchResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
