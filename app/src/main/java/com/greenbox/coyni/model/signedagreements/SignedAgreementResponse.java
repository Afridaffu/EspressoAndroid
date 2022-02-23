package com.greenbox.coyni.model.signedagreements;
import com.greenbox.coyni.model.Error;

public class SignedAgreementResponse {
    private String status;
    private String timestamp;
    private SignedAgreementResponceData data;
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

    public SignedAgreementResponceData getData() {
        return data;
    }

    public void setData(SignedAgreementResponceData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
