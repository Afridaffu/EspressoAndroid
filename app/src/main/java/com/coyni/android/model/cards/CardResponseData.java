package com.coyni.android.model.cards;

public class CardResponseData {
    private String transactionId;
    private int response_code;
    private String processor_response_text;
    private String response;
    private String processor_response_code;
    private String type;
    private int userId;
    private int amount_authorized;
    private String cardNumber;
    private String status;
    private String msg;
    private String descriptorName;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getResponse_code() {
        return response_code;
    }

    public void setResponse_code(int response_code) {
        this.response_code = response_code;
    }

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

    public int getAmount_authorized() {
        return amount_authorized;
    }

    public void setAmount_authorized(int amount_authorized) {
        this.amount_authorized = amount_authorized;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescriptorName() {
        return descriptorName;
    }

    public void setDescriptorName(String descriptorName) {
        this.descriptorName = descriptorName;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
