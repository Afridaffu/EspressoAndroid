package com.coyni.mapp.model.profile;

import com.coyni.mapp.model.BaseResponse;

public class DownloadDocumentResponse extends BaseResponse {

    private DownloadDocumentData data;

    public DownloadDocumentData getData() {
        return data;
    }

    public void setData(DownloadDocumentData data) {
        this.data = data;
    }
}
