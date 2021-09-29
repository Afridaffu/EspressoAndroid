package com.coyni.android.model.user;

import com.coyni.android.model.Error;

public class AgreementsById {
    private String status;
    private String timestamp;
    private Error error;
    private AgreementsByIdContent data;

    public AgreementsByIdContent getData() {
        return data;
    }

    public void setData(AgreementsByIdContent data) {
        this.data = data;
    }

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
}
