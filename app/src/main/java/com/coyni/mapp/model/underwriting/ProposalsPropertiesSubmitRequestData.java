package com.coyni.mapp.model.underwriting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProposalsPropertiesSubmitRequestData {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("isUserAccepted")
    @Expose
    private boolean isUserAccepted;


    @SerializedName("userMessage")
    @Expose
    private String userMessage;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isUserAccepted() {
        return isUserAccepted;
    }

    public void setUserAccepted(boolean userAccepted) {
        isUserAccepted = userAccepted;
    }


    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    @Override
    public String toString() {
        return "ProposalsPropertiesSubmitRequestData{" +
                "name='" + name + '\'' +
                ", isUserAccepted=" + isUserAccepted +
                ", userMessage='" + userMessage + '\'' +
                '}';
    }
}
