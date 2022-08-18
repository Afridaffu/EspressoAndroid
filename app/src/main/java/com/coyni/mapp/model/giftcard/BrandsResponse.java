package com.coyni.mapp.model.giftcard;

import com.coyni.mapp.model.Error;

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
