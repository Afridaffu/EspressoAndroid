package com.coyni.mapp.model.summary;

public class Item1 {

    private Integer id;

    private Integer signatureType;

    private Integer refId;

    private Integer userId;

    private String signature;

    private String signedOn;

    private String ipAddress;

    private String documentVersion;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSignatureType() {
        return signatureType;
    }

    public void setSignatureType(Integer signatureType) {
        this.signatureType = signatureType;
    }

    public Integer getRefId() {
        return refId;
    }

    public void setRefId(Integer refId) {
        this.refId = refId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
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
}