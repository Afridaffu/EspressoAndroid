package com.greenbox.coyni.model.team;

import com.greenbox.coyni.model.BaseResponse;

public class TeamListResponse extends BaseResponse {

    private TeamListData data = null;

    public TeamListData getData() {
        return data;
    }

    public void setData(TeamListData data) {
        this.data = data;
    }


}


