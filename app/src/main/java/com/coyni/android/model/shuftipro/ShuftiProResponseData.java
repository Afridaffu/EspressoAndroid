package com.coyni.android.model.shuftipro;

import com.google.gson.annotations.SerializedName;

public class ShuftiProResponseData {
    private String reference;
    @SerializedName("event")
    private String strEvent;
    private String declined_reason;

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

    public String getDeclined_reason() {
        return declined_reason;
    }

    public void setDeclined_reason(String declined_reason) {
        this.declined_reason = declined_reason;
    }
}
