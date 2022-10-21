package com.coyni.mapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SignAgreementData {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("status")
    @Expose
    private int status;

    @SerializedName("version")
    @Expose
    private String version;

    @SerializedName("createdBy")
    @Expose
    private String createdBy;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    @SerializedName("startDate")
    @Expose
    private String startDate;

    @SerializedName("endDate")
    @Expose
    private String endDate;

    @SerializedName("effectiveDate")
    @Expose
    private String effectiveDate;

    @SerializedName("materialNote")
    @Expose
    private String materialNote;

    @SerializedName("statusDescription")
    @Expose
    private String statusDescription;

    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;

    @SerializedName("updatedBy")
    @Expose
    private String updatedBy;

    @SerializedName("materialType")
    @Expose
    private String materialType;

    @SerializedName("agreementType")
    @Expose
    private int agreementType;

    @SerializedName("signedAgreementTracker")
    @Expose
    private boolean signedAgreementTracker;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(String effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getMaterialNote() {
        return materialNote;
    }

    public void setMaterialNote(String materialNote) {
        this.materialNote = materialNote;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public int getAgreementType() {
        return agreementType;
    }

    public void setAgreementType(int agreementType) {
        this.agreementType = agreementType;
    }

    public boolean isSignedAgreementTracker() {
        return signedAgreementTracker;
    }

    public void setSignedAgreementTracker(boolean signedAgreementTracker) {
        this.signedAgreementTracker = signedAgreementTracker;
    }
}
