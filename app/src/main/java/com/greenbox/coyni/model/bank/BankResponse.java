package com.greenbox.coyni.model.bank;

import com.greenbox.coyni.model.Error;

import java.util.List;

public class BankResponse {
    private String status;
    private String timestamp;
//    private BankResponseData data;
    private List<BankItem> data;
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

//    public BankResponseData getData() {
//        return data;
//    }
//
//    public void setData(BankResponseData data) {
//        this.data = data;
//    }


    public List<BankItem> getData() {
        return data;
    }

    public void setData(List<BankItem> data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
