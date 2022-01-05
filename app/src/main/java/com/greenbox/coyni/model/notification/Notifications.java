package com.greenbox.coyni.model.notification;

import com.greenbox.coyni.model.Error;

public class Notifications {
    private String status;
    private String timestamp;
    private NotificationsData data;
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

    public NotificationsData getData() {
        return data;
    }

    public void setData(NotificationsData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
