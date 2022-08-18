package com.coyni.mapp.model.cards;

public class CardTypeResponse {
    private String status;
    private String timestamp;
    private CardTypeResponseData data;
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

    public CardTypeResponseData getData() {
        return data;
    }

    public void setData(CardTypeResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

