package com.coyni.mapp.model.underwriting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WebsiteChangeData {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("iteration")
    @Expose
    private int iteration;

    @SerializedName("header")
    @Expose
    private String header;

    @SerializedName("comment")
    @Expose
    private String comment;

    @SerializedName("documentUrl1")
    @Expose
    private String documentUrl1;

    @SerializedName("documentUrl2")
    @Expose
    private String documentUrl2;

    @SerializedName("documentUrl3")
    @Expose
    private String documentUrl3;

    @SerializedName("isCustomerAccepted")
    @Expose
    private boolean isCustomerAccepted;

    @SerializedName("isAdminAccepted")
    @Expose
    private boolean isAdminAccepted;

    @SerializedName("custom")
    @Expose
    private boolean custom;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIteration() {
        return iteration;
    }

    public void setIteration(int iteration) {
        this.iteration = iteration;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDocumentUrl1() {
        return documentUrl1;
    }

    public void setDocumentUrl1(String documentUrl1) {
        this.documentUrl1 = documentUrl1;
    }

    public String getDocumentUrl2() {
        return documentUrl2;
    }

    public void setDocumentUrl2(String documentUrl2) {
        this.documentUrl2 = documentUrl2;
    }

    public String getDocumentUrl3() {
        return documentUrl3;
    }

    public void setDocumentUrl3(String documentUrl3) {
        this.documentUrl3 = documentUrl3;
    }

    public boolean getIsCustomerAccepted() {
        return isCustomerAccepted;
    }

    public void setIsCustomerAccepted(boolean isCustomerAccepted) {
        this.isCustomerAccepted = isCustomerAccepted;
    }

    public boolean getIsAdminAccepted() {
        return isAdminAccepted;
    }

    public void setIsAdminAccepted(boolean isAdminAccepted) {
        this.isAdminAccepted = isAdminAccepted;
    }

    public boolean getCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }
}
