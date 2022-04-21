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
import com.greenbox.coyni.model.biometric.BiometricRequest;
import com.greenbox.coyni.model.biometric.BiometricResponse;
import com.greenbox.coyni.model.forgotpassword.EmailValidateResponse;
import com.greenbox.coyni.model.forgotpassword.ManagePasswordRequest;
import com.greenbox.coyni.model.forgotpassword.ManagePasswordResponse;
import com.greenbox.coyni.model.forgotpassword.SetPassword;
import com.greenbox.coyni.model.forgotpassword.SetPasswordResponse;
import com.greenbox.coyni.model.login.BiometricLoginRequest;
import com.greenbox.coyni.model.login.LoginRequest;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.model.login.PasswordRequest;
import com.greenbox.coyni.model.logout.LogoutResponse;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailValidateRequest;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneResponse;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneValidateRequest;
import com.greenbox.coyni.model.register.CustRegisRequest;
import com.greenbox.coyni.model.register.CustRegisterResponse;
import com.greenbox.coyni.model.register.EmailExistsResponse;
import com.greenbox.coyni.model.register.EmailResendResponse;
import com.greenbox.coyni.model.register.EmailResponse;
import com.greenbox.coyni.model.register.InitCustomerRequest;
import com.greenbox.coyni.model.register.InitializeCustomerResponse;
import com.greenbox.coyni.model.register.SMSResend;
import com.greenbox.coyni.model.register.SMSResponse;
import com.greenbox.coyni.model.register.SMSValidate;
import com.greenbox.coyni.model.register.SmsRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveEmailRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveEmailResponse;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveUsersResponse;
import com.greenbox.coyni.model.update_resend_otp.UpdateResendOTPResponse;
import com.greenbox.coyni.model.update_resend_otp.UpdateResendRequest;
import com.greenbox.coyni.network.ApiClient;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Singleton;

import java.lang.reflect.Type;

import okhttp3.RequestBody;
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
    private MutableLiveData<SetPasswordResponse> setpwdLiveData = new MutableLiveData<>();
    private MutableLiveData<SetPasswordResponse> registerPINLiveData = new MutableLiveData<>();
    private MutableLiveData<InitializeCustomerResponse> initCustomerLiveData = new MutableLiveData<>();
    private MutableLiveData<RetrieveEmailResponse> retrieveEmailResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<RetrieveUsersResponse> retrieveUsersResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<LoginResponse> biometricResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UpdateEmailResponse> updateEmailValidateResponse = new MutableLiveData<>();
    private MutableLiveData<UpdatePhoneResponse> updatePhoneValidateResponse = new MutableLiveData<>();
    private MutableLiveData<ManagePasswordResponse> managePasswordResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<EmailExistsResponse> emailExistsResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AddBusinessUserResponse> postChangeAccountResponse = new MutableLiveData<>();
    private MutableLiveData<UpdateResendOTPResponse> updateResendOTPMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<LoginResponse> authenticatePasswordResponse = new MutableLiveData<>();
    private MutableLiveData<LogoutResponse> logoutLiveData = new MutableLiveData<>();

    public MutableLiveData<LoginResponse> getAuthenticatePasswordResponse() {
        return authenticatePasswordResponse;
    }

    public MutableLiveData<UpdateResendOTPResponse> getUpdateResendOTPMutableLiveData() {
        return updateResendOTPMutableLiveData;
    }

    public void setUpdateResendOTPMutableLiveData(MutableLiveData<UpdateResendOTPResponse> updateResendOTPMutableLiveData) {
        this.updateResendOTPMutableLiveData = updateResendOTPMutableLiveData;
    }

    public LoginViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<EmailResendResponse> getEmailresendMutableLiveData() {
        return emailresendMutableLiveData;
    }
    public MutableLiveData<AddBusinessUserResponse> postChangeAccountResponse() {
        return postChangeAccountResponse;
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

    public MutableLiveData<SetPasswordResponse> getSetpwdLiveData() {
        return setpwdLiveData;
    }

    public MutableLiveData<InitializeCustomerResponse> getInitCustomerLiveData() {
        return initCustomerLiveData;
    }

    public void setInitCustomerLiveData(MutableLiveData<InitializeCustomerResponse> initCustomerLiveData) {
        this.initCustomerLiveData = initCustomerLiveData;
    }

    public MutableLiveData<RetrieveEmailResponse> getRetrieveEmailResponseMutableLiveData() {
        return retrieveEmailResponseMutableLiveData;
    }

    public MutableLiveData<RetrieveUsersResponse> getRetrieveUsersResponseMutableLiveData() {
        return retrieveUsersResponseMutableLiveData;
    }

    public MutableLiveData<LoginResponse> getBiometricResponseMutableLiveData() {
        return biometricResponseMutableLiveData;
    }

    public MutableLiveData<ManagePasswordResponse> getManagePasswordResponseMutableLiveData() {
        return managePasswordResponseMutableLiveData;
    }

    public MutableLiveData<UpdateEmailResponse> getUpdateEmailValidateResponse() {
        return updateEmailValidateResponse;
    }

    public MutableLiveData<UpdatePhoneResponse> getUpdatePhoneValidateResponse() {
        return updatePhoneValidateResponse;
    }

    public MutableLiveData<EmailExistsResponse> getEmailExistsResponseMutableLiveData() {
        return emailExistsResponseMutableLiveData;
    }

    public MutableLiveData<LogoutResponse> getLogoutLiveData() {
        return logoutLiveData;
    }

    public void smsotpresend(SMSResend resend) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<SMSResponse> mCall = apiService.smsotpresend(resend);
            mCall.enqueue(new Callback<SMSResponse>() {
                @Override
                public void onResponse(Call<SMSResponse> call, Response<SMSResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            SMSResponse obj = response.body();
                            smsresendMutableLiveData.setValue(obj);
                            Log.e("SMS Resend Resp", new Gson().toJson(obj));
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<SMSResponse>() {
                            }.getType();
                            SMSResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
                                smsresendMutableLiveData.setValue(errorResponse);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
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
                        Log.e("Email Resend Resp", new Gson().toJson(obj));
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
                        Log.e("Email Validate Resp", new Gson().toJson(obj));
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
                        Log.e("SMS Validate Resp", new Gson().toJson(obj));
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

    public void smsotpLoginValidate(SmsRequest smsRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<SMSValidate> mCall = apiService.smsotpLogin(smsRequest);
            mCall.enqueue(new Callback<SMSValidate>() {
                @Override
                public void onResponse(Call<SMSValidate> call, Response<SMSValidate> response) {
                    if (response.isSuccessful()) {
                        SMSValidate obj = response.body();
                        smsotpLiveData.setValue(obj);
                        Log.e("SMS Validate Resp", new Gson().toJson(obj));
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

    public void logout() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<LogoutResponse> mCall = apiService.logout();
            mCall.enqueue(new Callback<LogoutResponse>() {
                @Override
                public void onResponse(Call<LogoutResponse> call, Response<LogoutResponse> response) {
                    if (response.isSuccessful()) {
                        LogoutResponse obj = response.body();
                        logoutLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<LogoutResponse>() {
                        }.getType();
                        LogoutResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            logoutLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<LogoutResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    logoutLiveData.setValue(null);
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

    public void customerRegistration(CustRegisRequest custRegisRequest, String type) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<CustRegisterResponse> mCall;
            if (type.equals("POST"))
                mCall = apiService.custRegister(custRegisRequest);
            else
                mCall = apiService.custRegisterPatch(custRegisRequest, Integer.parseInt(custRegisRequest.getUserId()));

            mCall.enqueue(new Callback<CustRegisterResponse>() {
                @Override
                public void onResponse(Call<CustRegisterResponse> call, Response<CustRegisterResponse> response) {

                    if (response.isSuccessful()) {
                        Log.e("CustReg Success", "CustReg Success");
                        try {
                            CustRegisterResponse obj = response.body();
                            custRegisResponseMutableLiveData.setValue(obj);
                            Singleton.setCustRegisterResponse(obj);
                            Log.e("CustReg Success", new Gson().toJson(obj));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e("CustReg Error", "CustReg Error");
                        try {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CustRegisterResponse>() {
                            }.getType();
                            CustRegisterResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
//                                errorMessage.setValue(errorResponse.getError().getErrorDescription());
                                custRegisResponseMutableLiveData.setValue(errorResponse);
                            }
                            Log.e("CustReg Error", new Gson().toJson(errorResponse));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


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
                        Type type = new TypeToken<SetPasswordResponse>() {
                        }.getType();
                        SetPasswordResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        setpwdLiveData.setValue(errorResponse);
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

    public void initializeCustomer(InitCustomerRequest initCustomerRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<InitializeCustomerResponse> mCall = apiService.initializeCustomer(initCustomerRequest);
            mCall.enqueue(new Callback<InitializeCustomerResponse>() {
                @Override
                public void onResponse(Call<InitializeCustomerResponse> call, Response<InitializeCustomerResponse> response) {
                    if (response.isSuccessful()) {
                        InitializeCustomerResponse obj = response.body();
                        initCustomerLiveData.setValue(obj);
                        Log.e("Init Customer", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<InitializeCustomerResponse>() {
                        }.getType();
                        InitializeCustomerResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            initCustomerLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<InitializeCustomerResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void retrieveEmail(RetrieveEmailRequest request) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<RetrieveEmailResponse> mCall = apiService.retrieveEmail(request);
            mCall.enqueue(new Callback<RetrieveEmailResponse>() {
                @Override
                public void onResponse(Call<RetrieveEmailResponse> call, Response<RetrieveEmailResponse> response) {
                    if (response.isSuccessful()) {
                        RetrieveEmailResponse obj = response.body();
                        retrieveEmailResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<APIError>() {
                        }.getType();
                        APIError errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            apiErrorMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<RetrieveEmailResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void retrieveUsers(RetrieveUsersRequest request, String strOTP) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<RetrieveUsersResponse> mCall = apiService.retrieveUsers(request, strOTP);
            mCall.enqueue(new Callback<RetrieveUsersResponse>() {
                @Override
                public void onResponse(Call<RetrieveUsersResponse> call, Response<RetrieveUsersResponse> response) {
                    if (response.isSuccessful()) {
                        RetrieveUsersResponse obj = response.body();
                        retrieveUsersResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<RetrieveUsersResponse>() {
                        }.getType();
                        RetrieveUsersResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            retrieveUsersResponseMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<RetrieveUsersResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void biometricLogin(BiometricLoginRequest request) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<LoginResponse> mCall = apiService.biometricLogin(request);
            mCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse obj = response.body();
                        biometricResponseMutableLiveData.setValue(obj);
                        Log.e("Bio Success", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<LoginResponse>() {
                        }.getType();
                        LoginResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            biometricResponseMutableLiveData.setValue(errorResponse);
                            Log.e("Biometric Error", new Gson().toJson(errorResponse));
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateEmailotpValidate(UpdateEmailValidateRequest updateEmailValidateRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UpdateEmailResponse> mCall = apiService.updateEmailValidateOTP(updateEmailValidateRequest);
            mCall.enqueue(new Callback<UpdateEmailResponse>() {
                @Override
                public void onResponse(Call<UpdateEmailResponse> call, Response<UpdateEmailResponse> response) {
                    if (response.isSuccessful()) {
                        UpdateEmailResponse obj = response.body();
                        updateEmailValidateResponse.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<UpdateEmailResponse>() {
                        }.getType();
                        UpdateEmailResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            updateEmailValidateResponse.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UpdateEmailResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updatePhoneotpValidate(UpdatePhoneValidateRequest updatePhoneValidateRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UpdatePhoneResponse> mCall = apiService.updatePhoneValidateOTP(updatePhoneValidateRequest);
            mCall.enqueue(new Callback<UpdatePhoneResponse>() {
                @Override
                public void onResponse(Call<UpdatePhoneResponse> call, Response<UpdatePhoneResponse> response) {
                    if (response.isSuccessful()) {
                        UpdatePhoneResponse obj = response.body();
                        updatePhoneValidateResponse.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<UpdatePhoneResponse>() {
                        }.getType();
                        UpdatePhoneResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            updatePhoneValidateResponse.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UpdatePhoneResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setExpiryPassword(ManagePasswordRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ManagePasswordResponse> mCall = apiService.setExpiryPassword(request);
            mCall.enqueue(new Callback<ManagePasswordResponse>() {
                @Override
                public void onResponse(Call<ManagePasswordResponse> call, Response<ManagePasswordResponse> response) {
                    if (response.isSuccessful()) {
                        ManagePasswordResponse obj = response.body();
                        managePasswordResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ManagePasswordResponse>() {
                        }.getType();
                        ManagePasswordResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        managePasswordResponseMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<ManagePasswordResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void validateEmail(String strEmail) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<EmailExistsResponse> mCall = apiService.validateEmail(strEmail);
            mCall.enqueue(new Callback<EmailExistsResponse>() {
                @Override
                public void onResponse(Call<EmailExistsResponse> call, Response<EmailExistsResponse> response) {
                    if (response.isSuccessful()) {
                        EmailExistsResponse obj = response.body();
                        emailExistsResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<EmailExistsResponse>() {
                        }.getType();
                        EmailExistsResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        emailExistsResponseMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<EmailExistsResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateOtpResend(UpdateResendRequest resendRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UpdateResendOTPResponse> mCall = apiService.updateOtpResend(resendRequest);
            mCall.enqueue(new Callback<UpdateResendOTPResponse>() {
                @Override
                public void onResponse(Call<UpdateResendOTPResponse> call, Response<UpdateResendOTPResponse> response) {
                    if (response.isSuccessful()) {
                        UpdateResendOTPResponse obj = response.body();
                        updateResendOTPMutableLiveData.setValue(obj);
                        Log.e("Email Resend Resp", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<UpdateResendOTPResponse>() {
                        }.getType();
                        UpdateResendOTPResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            updateResendOTPMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UpdateResendOTPResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void authenticatePassword(PasswordRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<LoginResponse> mCall = apiService.authenticatePassword(request);
            mCall.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse obj = response.body();
                        authenticatePasswordResponse.setValue(obj);
                        Log.e("Email Resend Resp", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<LoginResponse>() {
                        }.getType();
                        LoginResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            authenticatePasswordResponse.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void postChangeAccount(int loginUsedId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<AddBusinessUserResponse> mCall = apiService.getChangeAccount(loginUsedId);
            mCall.enqueue(new Callback<AddBusinessUserResponse>() {
                @Override
                public void onResponse(Call<AddBusinessUserResponse> call, Response<AddBusinessUserResponse> response) {
                    try {
                        LogUtils.d("LOGINVIEW","MOdel"+response);
                        if (response.isSuccessful()) {
                            AddBusinessUserResponse obj = response.body();
                            postChangeAccountResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<AddBusinessUserResponse>() {
                            }.getType();
                            AddBusinessUserResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
                                postChangeAccountResponse.setValue(errorResponse);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<AddBusinessUserResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
