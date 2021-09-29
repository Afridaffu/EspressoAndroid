package com.coyni.android.model.cards;

import com.coyni.android.model.Error;

public class Cards {
    private String status;
    private String timestamp;
    private CardsData data;
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

    public CardsData getData() {
        return data;
    }

    public void setData(CardsData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
