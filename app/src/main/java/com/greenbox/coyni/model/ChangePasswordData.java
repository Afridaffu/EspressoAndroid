package com.greenbox.coyni.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangePasswordData {

        @SerializedName("userId")
        @Expose
        private int userId;
        @SerializedName("message")
        @Expose
        private String message;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
}
