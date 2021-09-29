package com.coyni.android.model.userdetails;

public class UserDetailsData {
    private String walletId;
    private String firstName;
    private String lastName;
    private String fullName;
    private int userId;
    private String email;
    private String walletCatagory;

    public String getWalletId() {
        return walletId;
    }

    public void setWalletId(String walletId) {
        this.walletId = walletId;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWalletCatagory() {
        return walletCatagory;
    }

    public void setWalletCatagory(String walletCatagory) {
        this.walletCatagory = walletCatagory;
    }
}
