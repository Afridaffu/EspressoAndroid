package com.greenbox.coyni.model.profile;

import com.greenbox.coyni.model.BaseResponse;

public class DownloadImageResponse extends BaseResponse {

    private DownloadImageData data;

    public DownloadImageData getData() {
        return data;
    }

    public void setData(DownloadImageData data) {
        this.data = data;
    }
}
