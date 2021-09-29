package com.coyni.android.model.templates;

import com.coyni.android.model.Error;

public class TemplateResponse {
    private String status;
    private String timestamp;
    private TemplateResponseData data;
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

    public TemplateResponseData getData() {
        return data;
    }

    public void setData(TemplateResponseData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
