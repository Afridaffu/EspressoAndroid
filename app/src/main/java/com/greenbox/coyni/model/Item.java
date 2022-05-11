package com.greenbox.coyni.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Item {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("signatureType")
    @Expose
    private int signatureType;
    @SerializedName("refId")
    @Expose
    private String refId;
    @SerializedName("userId")
    @Expose
    private String userId;
    @SerializedName("signature")
    @Expose
    private String signature;
    @SerializedName("signedOn")
    @Expose
    private String signedOn;
    @SerializedName("ipAddress")
    @Expose
    private String ipAddress;
    @SerializedName("documentVersion")
    @Expose
    private String documentVersion;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSignatureType() {
        return signatureType;
    }

    public void setSignatureType(int signatureType) {
        this.signatureType = signatureType;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignedOn() {
        return signedOn;
    }

    public void setSignedOn(String signedOn) {
        this.signedOn = signedOn;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getDocumentVersion() {
        return documentVersion;
    }

    public void setDocumentVersion(String documentVersion) {
        this.documentVersion = documentVersion;
    }

    @Override
    public boolean equals(Object o) {
        Item item = (Item) o;
        return signatureType == (item.signatureType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

