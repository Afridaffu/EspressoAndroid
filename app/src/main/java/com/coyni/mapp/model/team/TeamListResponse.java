package com.coyni.mapp.model.team;

import com.coyni.mapp.model.BaseResponse;

public class TeamListResponse extends BaseResponse {

    private TeamListData data = null;

    public TeamListData getData() {
        return data;
    }

    public void setData(TeamListData data) {
        this.data = data;
    }


}


