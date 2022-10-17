package com.coyni.mapp.model.websocket;

public class WebSocketUrlResponseData {
    private String websocketUrl;
    private String checkoutUrl;
    private String isCogentEnabled = "true";
    private String isSignetEnabled = "false";

    public String getWebsocketUrl() {
        return websocketUrl;
    }

    public void setWebsocketUrl(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public String getIsCogentEnabled() {
        return isCogentEnabled;
    }

    public void setIsCogentEnabled(String isCogentEnabled) {
        this.isCogentEnabled = isCogentEnabled;
    }

    public String getIsSignetEnabled() {
        return isSignetEnabled;
    }

    public void setIsSignetEnabled(String isSignetEnabled) {
        this.isSignetEnabled = isSignetEnabled;
    }
}

