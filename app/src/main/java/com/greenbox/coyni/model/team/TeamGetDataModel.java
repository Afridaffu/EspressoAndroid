package com.greenbox.coyni.model.team;

import com.greenbox.coyni.model.BaseResponse;

public class TeamGetDataModel extends BaseResponse {

    private TeamData data;

    public TeamData getData() {
        return data;
    }

    public void setData(TeamData data) {
        this.data = data;
    }

}
