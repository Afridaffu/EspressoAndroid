package com.coyni.android.model.cards;

import com.coyni.android.model.Error;

public class CardType {
    private String status;
    private String timestamp;
    private CardTypeData data;
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

    public CardTypeData getData() {
        return data;
    }

    public void setData(CardTypeData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
