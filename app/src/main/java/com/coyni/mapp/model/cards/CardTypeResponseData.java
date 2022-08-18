package com.coyni.mapp.model.cards;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CardTypeResponseData {
    private String country;

    @SerializedName("country-code")
    private String CountryCode;

    @SerializedName("card-brand")
    private String CardBrand;

    @SerializedName("ip-city")
    private String IpCity;

    @SerializedName("ip-blocklists")
    private List<Object> IpBlocklists;

    @SerializedName("ip-country-code3")
    private String IpCountryCode3;

    @SerializedName("is-commercial")
    private Boolean IsCommercial;

    @SerializedName("ip-country")
    private String IpCountry;

    @SerializedName("bin-number")
    private String BinNumber;

    private String issuer;

    @SerializedName("issuer-website")
    private String IssuerWebsite;

    @SerializedName("ip-region")
    private String IpRegion;

    private Boolean valid;

    @SerializedName("card-type")
    private String CardType;

    @SerializedName("is-prepaid")
    private Boolean IsPrepaid;

    @SerializedName("ip-blocklisted")
    private Boolean IpBlocklisted;

    @SerializedName("card-category")
    private String CardCategory;

    @SerializedName("issuer-phone")
    private String IssuerPhone;

    @SerializedName("currency-code")
    private String CurrencyCode;

    @SerializedName("ip-matches-bin")
    private Boolean IpMatchesBin;

    @SerializedName("country-code3")
    private String CountryCode3;

    @SerializedName("ip-country-code")
    private String IpCountryCode;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getCardBrand() {
        return CardBrand;
    }

    public void setCardBrand(String cardBrand) {
        CardBrand = cardBrand;
    }

    public String getIpCity() {
        return IpCity;
    }

    public void setIpCity(String ipCity) {
        IpCity = ipCity;
    }

    public List<Object> getIpBlocklists() {
        return IpBlocklists;
    }

    public void setIpBlocklists(List<Object> ipBlocklists) {
        IpBlocklists = ipBlocklists;
    }

    public String getIpCountryCode3() {
        return IpCountryCode3;
    }

    public void setIpCountryCode3(String ipCountryCode3) {
        IpCountryCode3 = ipCountryCode3;
    }

    public Boolean getCommercial() {
        return IsCommercial;
    }

    public void setCommercial(Boolean commercial) {
        IsCommercial = commercial;
    }

    public String getIpCountry() {
        return IpCountry;
    }

    public void setIpCountry(String ipCountry) {
        IpCountry = ipCountry;
    }

    public String getBinNumber() {
        return BinNumber;
    }

    public void setBinNumber(String binNumber) {
        BinNumber = binNumber;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssuerWebsite() {
        return IssuerWebsite;
    }

    public void setIssuerWebsite(String issuerWebsite) {
        IssuerWebsite = issuerWebsite;
    }

    public String getIpRegion() {
        return IpRegion;
    }

    public void setIpRegion(String ipRegion) {
        IpRegion = ipRegion;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public String getCardType() {
        return CardType;
    }

    public void setCardType(String cardType) {
        CardType = cardType;
    }

    public Boolean getPrepaid() {
        return IsPrepaid;
    }

    public void setPrepaid(Boolean prepaid) {
        IsPrepaid = prepaid;
    }

    public Boolean getIpBlocklisted() {
        return IpBlocklisted;
    }

    public void setIpBlocklisted(Boolean ipBlocklisted) {
        IpBlocklisted = ipBlocklisted;
    }

    public String getCardCategory() {
        return CardCategory;
    }

    public void setCardCategory(String cardCategory) {
        CardCategory = cardCategory;
    }

    public String getIssuerPhone() {
        return IssuerPhone;
    }

    public void setIssuerPhone(String issuerPhone) {
        IssuerPhone = issuerPhone;
    }

    public String getCurrencyCode() {
        return CurrencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        CurrencyCode = currencyCode;
    }

    public Boolean getIpMatchesBin() {
        return IpMatchesBin;
    }

    public void setIpMatchesBin(Boolean ipMatchesBin) {
        IpMatchesBin = ipMatchesBin;
    }

    public String getCountryCode3() {
        return CountryCode3;
    }

    public void setCountryCode3(String countryCode3) {
        CountryCode3 = countryCode3;
    }

    public String getIpCountryCode() {
        return IpCountryCode;
    }

    public void setIpCountryCode(String ipCountryCode) {
        IpCountryCode = ipCountryCode;
    }
}

