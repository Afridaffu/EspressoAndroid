package com.greenbox.coyni.model.profile;

import com.greenbox.coyni.model.BaseResponse;

import java.util.List;

public class DownloadImageResponse extends BaseResponse {

    private List<DownloadImageData> data;

    public List<DownloadImageData> getData() {
        return data;
    }

    public void setData(List<DownloadImageData> data) {
        this.data = data;
    }
}
