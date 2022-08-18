package com.coyni.mapp.model.team;

import com.coyni.mapp.model.BaseResponse;

public class TeamGetDataModel extends BaseResponse {

    private TeamData data;

    public TeamData getData() {
        return data;
    }

    public void setData(TeamData data) {
        this.data = data;
    }

}
