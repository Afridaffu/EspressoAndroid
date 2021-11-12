package com.greenbox.coyni.model.profile.updateemail;

import com.greenbox.coyni.model.Error;
import com.greenbox.coyni.model.profile.ProfileData;

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
