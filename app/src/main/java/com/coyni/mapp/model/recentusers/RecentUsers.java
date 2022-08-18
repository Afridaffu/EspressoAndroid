package com.coyni.mapp.model.recentusers;

import com.coyni.mapp.model.Error;

import java.util.List;

public class RecentUsers {
    private String status;
    private String timestamp;
    private List<RecentUsersData> data;
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

    public List<RecentUsersData> getData() {
        return data;
    }

    public void setData(List<RecentUsersData> data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}

