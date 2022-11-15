package com.coyni.mapp.model;

import android.widget.LinearLayout;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DocLayout {
    private LinearLayout linearLayouts;
    private int id;
    private boolean isUploaded = false;

    public LinearLayout getLinearLayouts() {
        return linearLayouts;
    }

    public void setLinearLayouts(LinearLayout linearLayout) {
        this.linearLayouts = linearLayout;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isUploaded() {
        return isUploaded;
    }

    public void setUploaded(boolean uploaded) {
        isUploaded = uploaded;
    }
}

