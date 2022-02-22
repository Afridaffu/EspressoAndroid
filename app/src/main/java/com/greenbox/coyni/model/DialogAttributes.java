package com.greenbox.coyni.model;

public class DialogAttributes {
    private String title;
    private String message;
    private String positiveBtn;
    private String negativeBtn;

    public DialogAttributes(String title, String message, String positiveBtn, String negativeBtn) {
        this.title = title;
        this.message = message;
        this.positiveBtn = positiveBtn;
        this.negativeBtn = negativeBtn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPositiveBtn() {
        return positiveBtn;
    }

    public void setPositiveBtn(String positiveBtn) {
        this.positiveBtn = positiveBtn;
    }

    public String getNegativeBtn() {
        return negativeBtn;
    }

    public void setNegativeBtn(String negativeBtn) {
        this.negativeBtn = negativeBtn;
    }
}
