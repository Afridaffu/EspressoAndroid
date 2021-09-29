package com.coyni.android.model.export;

import com.coyni.android.model.Error;

import java.util.List;

public class ExportColumns {
    private String status;
    private String timestamp;
    private List<ExportColumnsData> data;
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

    public List<ExportColumnsData> getData() {
        return data;
    }

    public void setData(List<ExportColumnsData> data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
