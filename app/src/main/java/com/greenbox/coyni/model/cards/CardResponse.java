package com.greenbox.coyni.model.cards;

import com.greenbox.coyni.model.Error;

public class CardResponse {
    private String status;
    private String timestamp;
    private CardResponseData data;
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

    public CardResponseData getData() {
        return data;
    }

    public void setData(CardResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
