package com.coyni.android.model.userrequest;

public class UserRequest {
    private Double amount;
    private String content;
    private String portalType;
    private String remarks;
    private String requestType;
    private int requestedToUserId;
    private String requesterWalletId;
    private String subject;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPortalType() {
        return portalType;
    }

    public void setPortalType(String portalType) {
        this.portalType = portalType;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public int getRequestedToUserId() {
        return requestedToUserId;
    }

    public void setRequestedToUserId(int requestedToUserId) {
        this.requestedToUserId = requestedToUserId;
    }

    public String getRequesterWalletId() {
        return requesterWalletId;
    }

    public void setRequesterWalletId(String requesterWalletId) {
        this.requesterWalletId = requesterWalletId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
