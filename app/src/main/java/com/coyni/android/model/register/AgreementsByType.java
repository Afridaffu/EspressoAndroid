package com.coyni.android.model.register;

import com.coyni.android.model.Error;
public class AgreementsByType {
    private String status;
    private String timestamp;
    private Error error;
    private AgreementsByTypeContent data;

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

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public AgreementsByTypeContent getData() {
        return data;
    }

    public void setData(AgreementsByTypeContent data) {
        this.data = data;
    }
}
