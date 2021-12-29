package com.greenbox.coyni.model.identity_verification;


import com.greenbox.coyni.model.Error;

public class GetIdentityResponse {
    private String status;
    private String timestamp;
    private Daata data;
    private Error error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Daata getData() {
        return data;
    }

    public void setData(Daata data) {
        this.data = data;
    }

    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }

    public class Daata{
        private String firstName;
        private String lastName;
        private String email;
        private String phoneNumber;
        private String ssn;
        private String dateOfBirth;
        private AddressObj useraddress;
        private PhotoIDEntityObject photoIDEntityObject;

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

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }

        public String getSsn() {
            return ssn;
        }

        public void setSsn(String ssn) {
            this.ssn = ssn;
        }

        public String getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(String dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public AddressObj getUseraddress() {
            return useraddress;
        }

        public void setUseraddress(AddressObj useraddress) {
            this.useraddress = useraddress;
        }

        public PhotoIDEntityObject getPhotoIDEntityObject() {
            return photoIDEntityObject;
        }

        public void setPhotoIDEntityObject(PhotoIDEntityObject photoIDEntityObject) {
            this.photoIDEntityObject = photoIDEntityObject;
        }
    }

}

