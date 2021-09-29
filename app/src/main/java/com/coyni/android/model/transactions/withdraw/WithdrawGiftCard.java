package com.coyni.android.model.transactions.withdraw;

import com.coyni.android.model.Error;

public class WithdrawGiftCard {
    private String status;
    private String timestamp;
    private WithdrawGiftCardData data;
    private Error error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public WithdrawGiftCardData getData() {
        return data;
    }

    public void setData(WithdrawGiftCardData data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
