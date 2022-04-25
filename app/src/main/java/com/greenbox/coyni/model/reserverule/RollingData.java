package com.greenbox.coyni.model.reserverule;

public class RollingData {

    private Integer id;

    private Integer merchantId;

    private Object dbaName;

    private String reserveAmount;

    private Integer reservePeriod;

    private String reserveReason;

    private Boolean isAccepted;

    private String status;

    private Object merchantBalance;

    private Object reserveBalance;

    private String reserveType;

    private Object documentList;

    private Object monthlyProcessingVolume;

    private Object highTicket;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Integer merchantId) {
        this.merchantId = merchantId;
    }

    public Object getDbaName() {
        return dbaName;
    }

    public void setDbaName(Object dbaName) {
        this.dbaName = dbaName;
    }

    public String getReserveAmount() {
        return reserveAmount;
    }

    public void setReserveAmount(String reserveAmount) {
        this.reserveAmount = reserveAmount;
    }

    public Integer getReservePeriod() {
        return reservePeriod;
    }

    public void setReservePeriod(Integer reservePeriod) {
        this.reservePeriod = reservePeriod;
    }

    public String getReserveReason() {
        return reserveReason;
    }

    public void setReserveReason(String reserveReason) {
        this.reserveReason = reserveReason;
    }

    public Boolean getAccepted() {
        return isAccepted;
    }

    public void setAccepted(Boolean accepted) {
        isAccepted = accepted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getMerchantBalance() {
        return merchantBalance;
    }

    public void setMerchantBalance(Object merchantBalance) {
        this.merchantBalance = merchantBalance;
    }

    public Object getReserveBalance() {
        return reserveBalance;
    }

    public void setReserveBalance(Object reserveBalance) {
        this.reserveBalance = reserveBalance;
    }

    public String getReserveType() {
        return reserveType;
    }

    public void setReserveType(String reserveType) {
        this.reserveType = reserveType;
    }

    public Object getDocumentList() {
        return documentList;
    }

    public void setDocumentList(Object documentList) {
        this.documentList = documentList;
    }

    public Object getMonthlyProcessingVolume() {
        return monthlyProcessingVolume;
    }

    public void setMonthlyProcessingVolume(Object monthlyProcessingVolume) {
        this.monthlyProcessingVolume = monthlyProcessingVolume;
    }

    public Object getHighTicket() {
        return highTicket;
    }

    public void setHighTicket(Object highTicket) {
        this.highTicket = highTicket;
    }
}