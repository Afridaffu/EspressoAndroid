package com.greenbox.coyni.model.CompanyInfo;

import com.greenbox.coyni.model.register.PhNoWithCountryCode;

public class ContactInfoRequest {

    private String email;

    private Integer id;

    private PhNoWithCountryCode phoneNumberDto;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PhNoWithCountryCode getPhoneNumberDto() {
        return phoneNumberDto;
    }

    public void setPhoneNumberDto(PhNoWithCountryCode phoneNumberDto) {
        this.phoneNumberDto = phoneNumberDto;
    }
}
