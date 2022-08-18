package com.coyni.mapp.model.actionRqrd;

import com.coyni.mapp.model.Error;

public class ActionRqrdResponse {
    private String status;
    private String timestamp;
    private ActionRqrdData data;
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

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public ActionRqrdData getData() {
        return data;
    }

    public void setData(ActionRqrdData data) {
        this.data = data;
    }
}

