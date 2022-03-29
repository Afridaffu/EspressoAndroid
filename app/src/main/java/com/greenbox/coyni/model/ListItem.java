package com.greenbox.coyni.model;

public abstract class ListItem {
    public static final int TYPE_GROUP = 0;
    public static final int TYPE_GENERAL = 1;

    abstract public int getType();
}
