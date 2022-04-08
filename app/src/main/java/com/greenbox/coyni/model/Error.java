package com.greenbox.coyni.model;

import java.util.ArrayList;
import java.util.List;

public class Error {

    private String errorCode;
    private String errorDescription;
    private List<String> fieldErrors = new ArrayList<>();

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public List<String> getFieldErrors() {
        if (fieldErrors == null) {
            fieldErrors = new ArrayList<>();
            fieldErrors.add("");
        }
        return fieldErrors;
    }

    public void setFieldErrors(List<String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}

