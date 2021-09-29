package com.coyni.android.model.user;

import com.coyni.android.model.Error;

public class Agreements {
    private String status;
    private String timestamp;
    /*private List<AgreementsData> data;*/
    private AgreementsData data;
    private Error error;

    public AgreementsData getData() {
        return data;
    }

    public void setData(AgreementsData data) {
        this.data = data;
    }
/*private AgreementsByIdContent dataCOntent;

    public AgreementsByIdContent getDataCOntent() {
        return dataCOntent;
    }

    public void setDataCOntent(AgreementsByIdContent dataCOntent) {
        this.dataCOntent = dataCOntent;
    }*/



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



    public Error getError() {
        return error;
    }

    public void setError(Error error) {
        this.error = error;
    }
}
