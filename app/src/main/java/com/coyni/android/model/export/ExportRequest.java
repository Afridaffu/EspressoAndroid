package com.coyni.android.model.export;

import java.util.List;

public class ExportRequest {
    private String eventTypeId;
    private String eventSubTypeId;
    private List<String> exportColumns;
    private FilterColumns filterColumns;

    public String getEventTypeId() {
        return eventTypeId;
    }

    public void setEventTypeId(String eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    public String getEventSubTypeId() {
        return eventSubTypeId;
    }

    public void setEventSubTypeId(String eventSubTypeId) {
        this.eventSubTypeId = eventSubTypeId;
    }

    public List<String> getExportColumns() {
        return exportColumns;
    }

    public void setExportColumns(List<String> exportColumns) {
        this.exportColumns = exportColumns;
    }

    public FilterColumns getFilterColumns() {
        return filterColumns;
    }

    public void setFilterColumns(FilterColumns filterColumns) {
        this.filterColumns = filterColumns;
    }
}
