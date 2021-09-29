package com.coyni.android.model.export;

public class ExportColumnsData {
    private int eventTypeId;
    private String eventTypeName;
    private int eventSubTypeId;
    private String exportColumnName;
    private String exportUIName;
    private String frefix;

    public int getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(int eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public int getEventSubTypeId() {
        return eventSubTypeId;
    }

    public void setEventSubTypeId(int eventSubTypeId) {
        this.eventSubTypeId = eventSubTypeId;
    }

    public String getExportColumnName() {
        return exportColumnName;
    }

    public void setExportColumnName(String exportColumnName) {
        this.exportColumnName = exportColumnName;
    }

    public String getExportUIName() {
        return exportUIName;
    }

    public void setExportUIName(String exportUIName) {
        this.exportUIName = exportUIName;
    }

    public String getFrefix() {
        return frefix;
    }

    public void setFrefix(String frefix) {
        this.frefix = frefix;
    }
}
