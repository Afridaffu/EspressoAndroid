package com.coyni.mapp.model.register;

import com.coyni.mapp.model.login.StateList;

public class EmailResponseData {
    private int userId;
    private String message;
    private String jwtToken;
    private StateList stateList;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public StateList getStateList() {
        return stateList;
    }

    public void setStateList(StateList stateList) {
        this.stateList = stateList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}

