package com.greenbox.coyni.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AgreementType {
    @SerializedName("agreementType")
    @Expose
    private int agreementType;
    @SerializedName("version")
    @Expose
    private String version;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("startDate")
    @Expose
    private String startDate;
    @SerializedName("changeSummary")
    @Expose
    private String changeSummary;

    public int getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(int agreementType) {
        this.agreementType = agreementType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }
}
