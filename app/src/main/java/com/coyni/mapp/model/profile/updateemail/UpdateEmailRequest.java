package com.coyni.mapp.model.profile.updateemail;

public class UpdateEmailRequest {
    private String existingEmail;
    private String newEmail;

    public String getExistingEmail() {
        return existingEmail;
    }

    public void setExistingEmail(String existingEmail) {
        this.existingEmail = existingEmail;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
