package com.greenbox.coyni.model.UpdateSignAgree;

import com.google.gson.annotations.SerializedName;

public class UpdateSignAgreementsResponseData {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
