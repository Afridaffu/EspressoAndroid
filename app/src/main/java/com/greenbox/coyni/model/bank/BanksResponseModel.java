package com.greenbox.coyni.model.bank;

public class BanksResponseModel {

private String status;

private String timestamp;

private BankAccountsDataModel data;

private Object error;

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

public BankAccountsDataModel getData() {
return data;
}

public void setData(BankAccountsDataModel data) {
this.data = data;
}

public Object getError() {
return error;
}

public void setError(Object error) {
this.error = error;
}

}