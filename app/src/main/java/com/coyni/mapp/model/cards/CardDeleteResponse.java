package com.coyni.mapp.model.cards;

import com.coyni.mapp.model.Error;

public class CardDeleteResponse {
    private String status;
    private String timestamp;
    private CardDeleteResponseData data;
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

    public CardDeleteResponseData getData() {
        return data;
    }

    public void setData(CardDeleteResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

