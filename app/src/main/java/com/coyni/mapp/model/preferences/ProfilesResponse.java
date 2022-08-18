package com.coyni.mapp.model.preferences;

import com.coyni.mapp.model.Error;

import java.util.List;

public class ProfilesResponse {
    private String status;
    private String timestamp;
    private List<Profiles> data;
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

    public List<Profiles> getData() {
        return data;
    }

    public void setData(List<Profiles> data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Profiles extends BaseProfile {


    }

    @Override
    public String toString() {
        return "ProfilesResponse{" +
                "status='" + status + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", data=" + data +
                ", error=" + error +
                '}';
    }
}
