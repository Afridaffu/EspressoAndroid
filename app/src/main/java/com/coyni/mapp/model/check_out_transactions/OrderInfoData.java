package com.coyni.mapp.model.check_out_transactions;

public class OrderInfoData {

    private String checkoutOrderId;
    private String amount;
    private String merchantName;
    private boolean checkoutUser;
    private String merchantLogo;
    private String orderId;
    private String checkoutCallbackUrl;

    public String getCheckoutOrderId() {
        return checkoutOrderId;
    }

    public void setCheckoutOrderId(String checkoutOrderId) {
        this.checkoutOrderId = checkoutOrderId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    public boolean isCheckoutUser() {
        return checkoutUser;
    }

    public void setCheckoutUser(boolean checkoutUser) {
        this.checkoutUser = checkoutUser;
    }

    public String getMerchantLogo() {
        return merchantLogo;
    }

    public void setMerchantLogo(String merchantLogo) {
        this.merchantLogo = merchantLogo;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCheckoutCallbackUrl() {
        return checkoutCallbackUrl;
    }

    public void setCheckoutCallbackUrl(String checkoutCallbackUrl) {
        this.checkoutCallbackUrl = checkoutCallbackUrl;
    }
}
