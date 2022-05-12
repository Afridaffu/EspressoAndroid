package com.greenbox.coyni.model.profile;

import com.greenbox.coyni.model.BaseResponse;

public class DownloadDocumentResponse extends BaseResponse {

    private DownloadDocumentData data;

    public DownloadDocumentData getData() {
        return data;
    }

    public void setData(DownloadDocumentData data) {
        this.data = data;
    }
}
