package com.coyni.android.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.android.model.APIError;
import com.coyni.android.model.login.ChangePasswordRequest;
import com.coyni.android.model.login.ChangePasswordResponse;
import com.coyni.android.model.login.Login;
import com.coyni.android.model.login.LoginRequest;
import com.coyni.android.model.register.CustRegisRequest;
import com.coyni.android.model.register.CustRegisterResponse;
import com.coyni.android.model.register.EmailResendResponse;
import com.coyni.android.model.register.EmailResponse;
import com.coyni.android.model.register.ForgotPassword;
import com.coyni.android.model.register.SMSResend;
import com.coyni.android.model.register.SMSResponse;
import com.coyni.android.model.register.SetPassword;
import com.coyni.android.model.register.SetPasswordResponse;
import com.coyni.android.model.register.SmsRequest;
import com.coyni.android.network.ApiClient;
import com.coyni.android.network.ApiService;
import com.coyni.android.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    private MutableLiveData<CustRegisterResponse> custRegisResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Login> loginLiveData = new MutableLiveData<>();
    private MutableLiveData<Login> smsotpLiveData = new MutableLiveData<>();
    private MutableLiveData<EmailResponse> emailotpLiveData = new MutableLiveData<>();
    private MutableLiveData<SetPasswordResponse> setpwdLiveData = new MutableLiveData<>();
    private MutableLiveData<Login> smsresendLiveData = new MutableLiveData<>();
    private MutableLiveData<EmailResponse> emailresendLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ChangePasswordResponse> changePasswordLiveData = new MutableLiveData<>();
    private MutableLiveData<SMSResponse> smsresendMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<EmailResendResponse> emailresendMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ForgotPassword> forgotPasswordMutableLiveData = new MutableLiveData<>();
    private String errorMessage1 = "";

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<CustRegisterResponse> getCustRegisResponseMutableLiveData() {
        return custRegisResponseMutableLiveData;
    }

    public MutableLiveData<Login> getLoginLiveData() {
        return loginLiveData;
    }

    public MutableLiveData<Login> getSmsotpLiveData() {
        return smsotpLiveData;
    }

    public MutableLiveData<EmailResponse> getEmailotpLiveData() {
        return emailotpLiveData;
    }

    public MutableLiveData<SetPasswordResponse> getSetpwdLiveData() {
        return setpwdLiveData;
    }

    public MutableLiveData<Login> getSmsresendLiveData() {
        return smsresendLiveData;
    }

    public MutableLiveData<EmailResponse> getEmailresendLiveData() {
        return emailresendLiveData;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<ChangePasswordResponse> getChangePasswordLiveData() {
        return changePasswordLiveData;
    }

    public MutableLiveData<SMSResponse> getSmsresendMutableLiveData() {
        return smsresendMutableLiveData;
    }

    public MutableLiveData<EmailResendResponse> getEmailresendMutableLiveData() {
        return emailresendMutableLiveData;
    }

    public MutableLiveData<ForgotPassword> getForgotPasswordLiveData() {
        return forgotPasswordMutableLiveData;
    }

    public String getErrorMessage1() {
        return errorMessage1;
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
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Login>() {
                        }.getType();
                        Login errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            errorMessage.setValue(errorResponse.getError().getErrorDescription());
                            custRegisResponseMutableLiveData.setValue(null);
                        }
                    }
                }

                @Override
                public void onFailure(Call<CustRegisterResponse> call, Throwable t) {
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
            Call<Login> mCall = apiService.login(loginRequest);
            mCall.enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    try {
                        String strResponse = "";
                        if (response.isSuccessful()) {
                            Login obj = response.body();
                            loginLiveData.setValue(obj);
                        } else if (response.code() == 500) {
                            strResponse = response.errorBody().string();
                            Gson gson = new Gson();
                            Type type = new TypeToken<Login>() {
                            }.getType();
                            Login errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
                                loginLiveData.setValue(errorResponse);
                            } else {
                                Login errorResponse1 = gson.fromJson(strResponse, type);
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
                public void onFailure(Call<Login> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    loginLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void smsotp(SmsRequest smsRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<Login> mCall = apiService.smsotp(smsRequest);
            mCall.enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    if (response.isSuccessful()) {
                        Login obj = response.body();
                        smsotpLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Login>() {
                        }.getType();
                        Login errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            smsotpLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<Login> call, Throwable t) {
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

    public void setPassword(SetPassword setPassword) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<SetPasswordResponse> mCall = apiService.setpassword(setPassword);
            mCall.enqueue(new Callback<SetPasswordResponse>() {
                @Override
                public void onResponse(Call<SetPasswordResponse> call, Response<SetPasswordResponse> response) {
                    if (response.isSuccessful()) {
                        SetPasswordResponse obj = response.body();
                        setpwdLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Login>() {
                        }.getType();
                        Login errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            errorMessage.setValue(errorResponse.getError().getErrorDescription());
                            setpwdLiveData.setValue(null);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SetPasswordResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
//                            errorMessage.setValue(errorResponse.getError().getErrorDescription());
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
                            errorMessage.setValue(errorResponse.getError().getErrorDescription());
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

    public void changePassword(ChangePasswordRequest changePasswordRequest, int userId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ChangePasswordResponse> mCall = apiService.changePassword(changePasswordRequest, userId);
            mCall.enqueue(new Callback<ChangePasswordResponse>() {
                @Override
                public void onResponse(Call<ChangePasswordResponse> call, Response<ChangePasswordResponse> response) {
                    if (response.isSuccessful()) {
                        ChangePasswordResponse obj = response.body();
                        changePasswordLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ChangePasswordResponse>() {
                        }.getType();
                        ChangePasswordResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            errorMessage1 = errorResponse.getError().getErrorDescription();
                            changePasswordLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ChangePasswordResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void forgotPassword(String email) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<ForgotPassword> mCall = apiService.forgotPassword(email);
            mCall.enqueue(new Callback<ForgotPassword>() {
                @Override
                public void onResponse(Call<ForgotPassword> call, Response<ForgotPassword> response) {
                    try {
                        if (response.isSuccessful()) {
                            ForgotPassword obj = response.body();
                            forgotPasswordMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<APIError>() {
                            }.getType();
                            APIError errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            apiErrorMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<ForgotPassword> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    loginLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
