package com.coyni.mapp.model.coynipin;

import java.util.List;

public class StepUpOTPResponseData {
    private int userId;
    private String success;
    private String jwtToken;
    private List<Object> authorities;
    private Boolean valid;
    private Boolean oldSessionExist;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public List<Object> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Object> authorities) {
        this.authorities = authorities;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getOldSessionExist() {
        return oldSessionExist;
    }

    public void setOldSessionExist(Boolean oldSessionExist) {
        this.oldSessionExist = oldSessionExist;
    }
}
