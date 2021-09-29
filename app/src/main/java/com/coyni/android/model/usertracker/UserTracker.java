package com.coyni.android.model.usertracker;

public class UserTracker {
    private String status;
    private String timestamp;
    private UserTrackerData data;
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

    public UserTrackerData getData() {
        return data;
    }

    public void setData(UserTrackerData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
