package com.greenbox.coyni.model;

public class AbstractResponse extends BaseResponse {

    Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
