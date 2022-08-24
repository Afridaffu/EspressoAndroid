package com.coyni.mapp.model.team;

public class TeamRequest {

    private String firstName;

    private String lastName;

    private Integer roleId;

    private String roleName;

    private String emailAddress;

    private PhoneNumberTeam phoneNumber;

    public PhoneNumberTeam getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(PhoneNumberTeam phoneNumber) {
        this.phoneNumber = phoneNumber;
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

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
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
}