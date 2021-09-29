package com.coyni.android.model.coynipin;

import com.coyni.android.model.Error;

public class CoyniPinResponse {
    private String status;
    private String timestamp;
    private CoyniPinResponseData data;
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

    public CoyniPinResponseData getData() {
        return data;
    }

    public void setData(CoyniPinResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
