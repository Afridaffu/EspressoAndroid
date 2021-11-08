package com.greenbox.coyni.model.register;

import com.greenbox.coyni.model.Error;

public class InitCustomerRequest {

    private String code ;

    private Error error;

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

