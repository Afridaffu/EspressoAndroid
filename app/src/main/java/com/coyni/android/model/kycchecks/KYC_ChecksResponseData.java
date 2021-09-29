package com.coyni.android.model.kycchecks;

import com.google.gson.annotations.SerializedName;

public class KYC_ChecksResponseData {
    private String reference;
    @SerializedName("event")
    private String strEvent;

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getStrEvent() {
        return strEvent;
    }

    public void setStrEvent(String strEvent) {
        this.strEvent = strEvent;
    }
}
