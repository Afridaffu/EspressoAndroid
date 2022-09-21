package com.coyni.mapp.model.underwriting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProposalsPropertiesData {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("displayName")
    @Expose
    private String displayName;

    @SerializedName("originalValue")
    @Expose
    private String originalValue;

    @SerializedName("proposedValue")
    @Expose
    private String proposedValue;

    @SerializedName("isUserAccepted")
    @Expose
    private boolean isUserAccepted;

    @SerializedName("adminMessage")
    @Expose
    private String adminMessage;

    @SerializedName("userMessage")
    @Expose
    private String userMessage;

    @SerializedName("giactResponse")
    @Expose
    private String giactResponse;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(String originalValue) {
        this.originalValue = originalValue;
    }

    public String getProposedValue() {
        return proposedValue;
    }

    public void setProposedValue(String proposedValue) {
        this.proposedValue = proposedValue;
    }

    public boolean isUserAccepted() {
        return isUserAccepted;
    }

    public void setUserAccepted(boolean userAccepted) {
        isUserAccepted = userAccepted;
    }

    public String getAdminMessage() {
        return adminMessage;
    }

    public void setAdminMessage(String adminMessage) {
        this.adminMessage = adminMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public String getGiactResponse() {
        return giactResponse;
    }

    public void setGiactResponse(String giactResponse) {
        this.giactResponse = giactResponse;
    }
}
