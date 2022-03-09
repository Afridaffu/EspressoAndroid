package com.greenbox.coyni.model.wallet;

public class UserDetailsData {
    private String walletId;
    private String firstName;
    private String lastName;
    private String fullName;
    private int userId;
    private String email;
    private String walletCatagory;
    private String image;
    private String walletType;
    private int accountType;


    public String getImage() {
        return image;
    }
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

    public void setImage(String image) {
        this.image = image;
    }

    public String getWalletType() {
        return walletType;
    }

    public void setWalletType(String walletType) {
        this.walletType = walletType;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }
}
