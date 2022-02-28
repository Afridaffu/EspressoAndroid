package com.greenbox.coyni.model.team;


import com.greenbox.coyni.model.register.PhNoWithCountryCode;

public class TeamData {

    private String firstName;

    private String lastName;

    private Integer roleId;

    private Object status;

    private String roleName;

    private String emailAddress;

    private PhoneNumberTeam phoneNumber;


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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Object getStatus() {
        return status;
    }

    public void setStatus(Object status) {
        this.status = status;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public PhoneNumberTeam getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumberTeam phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


}
