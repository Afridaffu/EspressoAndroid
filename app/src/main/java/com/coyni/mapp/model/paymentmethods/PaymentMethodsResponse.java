package com.coyni.mapp.model.paymentmethods;

import com.coyni.mapp.model.Error;

public class PaymentMethodsResponse {
    private String status;
    private String timestamp;
    private PaymentMethodsData data;
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

    public PaymentMethodsData getData() {
        return data;
    }

    public void setData(PaymentMethodsData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
