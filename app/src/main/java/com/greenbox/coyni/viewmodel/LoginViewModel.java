package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.forgotpassword.EmailValidateResponse;
import com.greenbox.coyni.model.login.LoginRequest;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.model.register.CustRegisRequest;
import com.greenbox.coyni.model.register.CustRegisterResponse;
import com.greenbox.coyni.model.register.EmailResendResponse;
import com.greenbox.coyni.model.register.EmailResponse;
import com.greenbox.coyni.model.register.SMSResend;
import com.greenbox.coyni.model.register.SMSResponse;
import com.greenbox.coyni.model.register.SMSValidate;
import com.greenbox.coyni.model.register.SmsRequest;
import com.greenbox.coyni.network.ApiClient;
import com.greenbox.coyni.network.ApiService;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    private MutableLiveData<EmailResendResponse> emailresendMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<EmailResponse> emailotpLiveData = new MutableLiveData<>();
    private MutableLiveData<EmailValidateResponse> emailValidateResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SMSResponse> smsresendMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SMSValidate> smsotpLiveData = new MutableLiveData<>();
    private MutableLiveData<LoginResponse> loginLiveData = new MutableLiveData<>();

    private MutableLiveData<CustRegisterResponse> custRegisResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<EmailResponse> emailresendLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<EmailResendResponse> getEmailresendMutableLiveData() {
        return emailresendMutableLiveData;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<EmailResponse> getEmailotpLiveData() {
        return emailotpLiveData;
    }

    public MutableLiveData<SMSResponse> getSmsresendMutableLiveData() {
        return smsresendMutableLiveData;
    }

    public MutableLiveData<SMSValidate> getSmsotpLiveData() {
        return smsotpLiveData;
    }

    public MutableLiveData<LoginResponse> getLoginLiveData() {
        return loginLiveData;
    }

    public MutableLiveData<CustRegisterResponse> getCustRegisResponseMutableLiveData() {
        return custRegisResponseMutableLiveData;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<EmailValidateResponse> getEmailValidateResponseMutableLiveData() {
        return emailValidateResponseMutableLiveData;
    }

    public void smsotpresend(SMSResend resend) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<SMSResponse> mCall = apiService.smsotpresend(resend);
            mCall.enqueue(new Callback<SMSResponse>() {
                @Override
                public void onResponse(Call<SMSResponse> call, Response<SMSResponse> response) {
                    if (response.isSuccessful()) {
                        SMSResponse obj = response.body();
                        smsresendMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<SMSResponse>() {
                        }.getType();
                        SMSResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            smsresendMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SMSResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void emailotpresend(String email) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<EmailResendResponse> mCall = apiService.emailotpresend(email);
            mCall.enqueue(new Callback<EmailResendResponse>() {
                @Override
                public void onResponse(Call<EmailResendResponse> call, Response<EmailResendResponse> response) {
                    if (response.isSuccessful()) {
                        EmailResendResponse obj = response.body();
                        emailresendMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<EmailResendResponse>() {
                        }.getType();
                        EmailResendResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            emailresendMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<EmailResendResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void emailotp(SmsRequest smsRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<EmailResponse> mCall = apiService.emailotp(smsRequest);
            mCall.enqueue(new Callback<EmailResponse>() {
                @Override
                public void onResponse(Call<EmailResponse> call, Response<EmailResponse> response) {
                    if (response.isSuccessful()) {
                        EmailResponse obj = response.body();
                        emailotpLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<EmailResponse>() {
                        }.getType();
                        EmailResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            emailotpLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<EmailResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void smsotp(SmsRequest smsRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<SMSValidate> mCall = apiService.smsotp(smsRequest);
            mCall.enqueue(new Callback<SMSValidate>() {
                @Override
                public void onResponse(Call<SMSValidate> call, Response<SMSValidate> response) {
                    if (response.isSuccessful()) {
                        SMSValidate obj = response.body();
                        smsotpLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<SMSValidate>() {
                        }.getType();
                        SMSValidate errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            smsotpLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SMSValidate> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void login(LoginRequest loginRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<LoginResponse> mCall = apiService.login(loginRequest);
            mCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    try {
                        String strResponse = "";
                        if (response.isSuccessful()) {
                            LoginResponse obj = response.body();
                            loginLiveData.setValue(obj);
                        } else if (response.code() == 500) {
                            strResponse = response.errorBody().string();
                            Gson gson = new Gson();
                            Type type = new TypeToken<LoginResponse>() {
                            }.getType();
                            LoginResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
                                loginLiveData.setValue(errorResponse);
                            } else {
                                LoginResponse errorResponse1 = gson.fromJson(strResponse, type);
                                loginLiveData.setValue(errorResponse1);
                            }
                        } else {
                            strResponse = response.errorBody().string();
                            Gson gson = new Gson();
                            Type type = new TypeToken<APIError>() {
                            }.getType();
                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
                            if (errorResponse != null) {
                                apiErrorMutableLiveData.setValue(errorResponse);
                            } else {
                                APIError errorResponse1 = gson.fromJson(strResponse, type);
                                apiErrorMutableLiveData.setValue(errorResponse1);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    loginLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void emailotpValidate(SmsRequest smsRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<EmailValidateResponse> mCall = apiService.emailotpValidate(smsRequest);
            mCall.enqueue(new Callback<EmailValidateResponse>() {
                @Override
                public void onResponse(Call<EmailValidateResponse> call, Response<EmailValidateResponse> response) {
                    if (response.isSuccessful()) {
                        EmailValidateResponse obj = response.body();
                        emailValidateResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<EmailValidateResponse>() {
                        }.getType();
                        EmailValidateResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            emailValidateResponseMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<EmailValidateResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void customerRegistration(CustRegisRequest custRegisRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<CustRegisterResponse> mCall = apiService.custRegister(custRegisRequest);
            mCall.enqueue(new Callback<CustRegisterResponse>() {
                @Override
                public void onResponse(Call<CustRegisterResponse> call, Response<CustRegisterResponse> response) {
                    if (response.isSuccessful()) {
                        CustRegisterResponse obj = response.body();
                        custRegisResponseMutableLiveData.setValue(obj);
                    }else{
//                        Gson gson = new Gson();
//                        Type type = new TypeToken<Login>() {
//                        }.getType();
//                        Login errorResponse = gson.fromJson(response.errorBody().charStream(), type);
//                        if (errorResponse != null) {
//                            errorMessage.setValue(errorResponse.getError().getErrorDescription());
//                            custRegisResponseMutableLiveData.setValue(null);
//                        }

                        Log.e("Cust reg response", response.isSuccessful()+"");
                        Log.e("Cust reg", "ERROR");
                    }
                }

                @Override
                public void onFailure(Call<CustRegisterResponse> call, Throwable t) {
//                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
