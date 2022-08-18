package com.coyni.mapp.model.profile;

import com.coyni.mapp.model.BaseResponse;

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
