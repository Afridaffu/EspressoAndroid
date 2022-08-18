package com.coyni.mapp.model.bank;

import java.io.Serializable;

public class SignOnData implements Serializable {
    private String acct_id;
    private String invocation_mode;
    private String resolveUrl;
    private String login_acct_id;
    private String url;

    public String getAcct_id() {
        return acct_id;
    }

    public void setAcct_id(String acct_id) {
        this.acct_id = acct_id;
    }

    public String getInvocation_mode() {
        return invocation_mode;
    }

    public void setInvocation_mode(String invocation_mode) {
        this.invocation_mode = invocation_mode;
    }

    public String getLogin_acct_id() {
        return login_acct_id;
    }

    public void setLogin_acct_id(String login_acct_id) {
        this.login_acct_id = login_acct_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getResolveUrl() {
        return resolveUrl;
    }

    public void setResolveUrl(String resolveUrl) {
        this.resolveUrl = resolveUrl;
    }
}

