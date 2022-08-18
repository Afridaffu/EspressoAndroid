package com.coyni.mapp.model.cards;

public class CardResponseData {
    private String processor_response_text;
    private String response;
    private String processor_response_code;
    private String type;
    private int userId;
    private String descriptorName;
    private String transactionId;
    private String status;

    public String getProcessor_response_text() {
        return processor_response_text;
    }

    public void setProcessor_response_text(String processor_response_text) {
        this.processor_response_text = processor_response_text;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getProcessor_response_code() {
        return processor_response_code;
    }

    public void setProcessor_response_code(String processor_response_code) {
        this.processor_response_code = processor_response_code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDescriptorName() {
        return descriptorName;
    }

    public void setDescriptorName(String descriptorName) {
        this.descriptorName = descriptorName;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
