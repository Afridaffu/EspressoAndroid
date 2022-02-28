package com.greenbox.coyni.model.team;

import java.util.List;

public class TeamDeleteModel {

    private String status;

    private String timestamp;
    private List<TeamData> data = null;

    private Object error;

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

    public List<TeamData> getData() {
        return data;
    }

    public void setData(List<TeamData> data) {
        this.data = data;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }
}
