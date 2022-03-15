package com.greenbox.coyni.model.team;

import com.greenbox.coyni.model.Error;

import java.util.List;
public class TeamInfoAddModel {

    private String status;

    private String timestamp;

    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

}


