package com.greenbox.coyni.model.giftcard;

import com.greenbox.coyni.model.Error;
import com.greenbox.coyni.model.identity_verification.GetIdentityResponse;

public class BrandsResponse {

    private String status;
    private String timestamp;
    private GiftCard data;
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

    public GiftCard getData() {
        return data;
    }

    public void setData(GiftCard data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
