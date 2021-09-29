package com.coyni.android.model.register;

public class CustRegisRequest {
    private String email;
    private String firstName;
    private String lastName;
    private PhNoWithCountryCode phoneNumberWithCountryCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public PhNoWithCountryCode getPhoneNumberWithCountryCode() {
        return phoneNumberWithCountryCode;
    }

    public void setPhoneNumberWithCountryCode(PhNoWithCountryCode phoneNumberWithCountryCode) {
        this.phoneNumberWithCountryCode = phoneNumberWithCountryCode;
    }
}
