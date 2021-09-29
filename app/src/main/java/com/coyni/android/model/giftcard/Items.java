package com.coyni.android.model.giftcard;

import java.io.Serializable;
import java.util.List;

public class Items implements Serializable {
    private String utid;
    private String rewardName;
    private String currencyCode;
    private String status;
    private String valueType;
    private String rewardType;
    private Boolean isWholeAmountValueRequired;
    private Double faceValue;
    private String createdDate;
    private String lastUpdateDate;
    private List<String> countries;
    private List<String> credentialTypes;
    private String redemptionInstructions;
    private Double minValue;
    private Double maxValue;

    public String getUtid() {
        return utid;
    }

    public void setUtid(String utid) {
        this.utid = utid;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public Boolean getWholeAmountValueRequired() {
        return isWholeAmountValueRequired;
    }

    public void setWholeAmountValueRequired(Boolean wholeAmountValueRequired) {
        isWholeAmountValueRequired = wholeAmountValueRequired;
    }

    public Double getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(Double faceValue) {
        this.faceValue = faceValue;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public List<String> getCountries() {
        return countries;
    }

    public void setCountries(List<String> countries) {
        this.countries = countries;
    }

    public List<String> getCredentialTypes() {
        return credentialTypes;
    }

    public void setCredentialTypes(List<String> credentialTypes) {
        this.credentialTypes = credentialTypes;
    }

    public String getRedemptionInstructions() {
        return redemptionInstructions;
    }

    public void setRedemptionInstructions(String redemptionInstructions) {
        this.redemptionInstructions = redemptionInstructions;
    }

    public Double getMinValue() {
        return minValue;
    }

    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }

    public Double getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }
}
