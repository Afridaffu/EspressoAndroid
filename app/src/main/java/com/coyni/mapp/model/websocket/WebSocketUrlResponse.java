package com.coyni.mapp.model.websocket;

import com.coyni.mapp.model.BaseResponse;

public class WebSocketUrlResponse extends BaseResponse {
    private WebSocketUrlResponseData data;

    public WebSocketUrlResponseData getData() {
        return data;
    }

    public void setData(WebSocketUrlResponseData data) {
        this.data = data;
    }
}


