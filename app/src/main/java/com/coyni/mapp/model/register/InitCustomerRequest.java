package com.coyni.mapp.model.register;

import com.coyni.mapp.model.Error;

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

