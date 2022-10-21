package com.coyni.mapp.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.coyni.mapp.model.SignAgreementsResp;
import com.coyni.mapp.model.coynipin.StepUpOTPResponse;
import com.coyni.mapp.model.register.OTPResendRequest;
import com.coyni.mapp.model.register.OTPValidateRequest;
import com.coyni.mapp.model.register.OTPValidateResponse;
import com.coyni.mapp.model.register.SignAgreementRequest;
import com.coyni.mapp.model.register.SignAgreementResponse;
import com.coyni.mapp.model.signin.BiometricSignIn;
import com.coyni.mapp.model.signin.InitializeResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.mapp.model.APIError;
import com.coyni.mapp.model.EmailRequest;
import com.coyni.mapp.model.deviceintialize.DeviceInitializeResponse;
import com.coyni.mapp.model.forgotpassword.EmailValidateResponse;
import com.coyni.mapp.model.forgotpassword.ManagePasswordRequest;
import com.coyni.mapp.model.forgotpassword.ManagePasswordResponse;
import com.coyni.mapp.model.forgotpassword.SetPassword;
import com.coyni.mapp.model.forgotpassword.SetPasswordResponse;
import com.coyni.mapp.model.login.BiometricLoginRequest;
import com.coyni.mapp.model.login.LoginRequest;
import com.coyni.mapp.model.login.LoginResponse;
import com.coyni.mapp.model.login.PasswordRequest;
import com.coyni.mapp.model.logout.LogoutResponse;
import com.coyni.mapp.model.profile.AddBusinessUserResponse;
import com.coyni.mapp.model.profile.updateemail.UpdateEmailResponse;
import com.coyni.mapp.model.profile.updateemail.UpdateEmailValidateRequest;
import com.coyni.mapp.model.profile.updatephone.UpdatePhoneResponse;
import com.coyni.mapp.model.profile.updatephone.UpdatePhoneValidateRequest;
import com.coyni.mapp.model.register.CustRegisRequest;
import com.coyni.mapp.model.register.CustRegisterResponse;
import com.coyni.mapp.model.register.EmailExistsResponse;
import com.coyni.mapp.model.register.EmailResendResponse;
import com.coyni.mapp.model.register.EmailResponse;
import com.coyni.mapp.model.register.InitCustomerRequest;
import com.coyni.mapp.model.register.InitializeCustomerResponse;
import com.coyni.mapp.model.register.SMSResend;
import com.coyni.mapp.model.register.SMSResponse;
import com.coyni.mapp.model.register.SMSValidate;
import com.coyni.mapp.model.register.SmsRequest;
import com.coyni.mapp.model.retrieveemail.RetrieveEmailRequest;
import com.coyni.mapp.model.retrieveemail.RetrieveEmailResponse;
import com.coyni.mapp.model.retrieveemail.RetrieveUsersRequest;
import com.coyni.mapp.model.retrieveemail.RetrieveUsersResponse;
import com.coyni.mapp.model.update_resend_otp.UpdateResendOTPResponse;
import com.coyni.mapp.model.update_resend_otp.UpdateResendRequest;
import com.coyni.mapp.network.ApiClient;
import com.coyni.mapp.network.ApiService;
import com.coyni.mapp.network.AuthApiClient;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Singleton;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends AndroidViewModel {
    private MutableLiveData<EmailResendResponse> emailresendMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<OTPValidateResponse> regEmailOTPResendLiveData = new MutableLiveData<>();
    private MutableLiveData<OTPValidateResponse> regPhoneOTPResendLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<EmailResponse> emailotpLiveData = new MutableLiveData<>();
    private MutableLiveData<OTPValidateResponse> regEmailOTPValidateLiveData = new MutableLiveData<>();
    private MutableLiveData<EmailValidateResponse> emailValidateResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SMSResponse> smsresendMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SMSValidate> smsotpLiveData = new MutableLiveData<>();
    private MutableLiveData<OTPValidateResponse> regMobileOTPLiveData = new MutableLiveData<>();
    private MutableLiveData<LoginResponse> loginLiveData = new MutableLiveData<>();
    private MutableLiveData<BiometricSignIn> loginNewLiveData = new MutableLiveData<>();

    private MutableLiveData<CustRegisterResponse> custRegisResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<EmailResponse> emailresendLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private MutableLiveData<SetPasswordResponse> setpwdLiveData = new MutableLiveData<>();
    private MutableLiveData<SetPasswordResponse> registerPINLiveData = new MutableLiveData<>();
    private MutableLiveData<InitializeCustomerResponse> initCustomerLiveData = new MutableLiveData<>();
    private MutableLiveData<RetrieveEmailResponse> retrieveEmailResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<RetrieveUsersResponse> retrieveUsersResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BiometricSignIn> biometricResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UpdateEmailResponse> updateEmailValidateResponse = new MutableLiveData<>();
    private MutableLiveData<UpdatePhoneResponse> updatePhoneValidateResponse = new MutableLiveData<>();
    private MutableLiveData<ManagePasswordResponse> managePasswordResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<EmailExistsResponse> emailExistsResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AddBusinessUserResponse> postChangeAccountResponse = new MutableLiveData<>();
    private MutableLiveData<UpdateResendOTPResponse> updateResendOTPMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<LoginResponse> authenticatePasswordResponse = new MutableLiveData<>();
    private MutableLiveData<LogoutResponse> logoutLiveData = new MutableLiveData<>();
    private MutableLiveData<DeviceInitializeResponse> deviceInitializeResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<StepUpOTPResponse> stepUpEmailOTPResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<StepUpOTPResponse> stepUpPhoneOTPResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<OTPValidateResponse> signAgreementTOSotpLiveData = new MutableLiveData<>();
    private MutableLiveData<SignAgreementResponse> signAgreementPPotpLiveData = new MutableLiveData<>();
    private MutableLiveData<InitializeResponse> initializeResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SignAgreementsResp> hasToSignResponseMutableLiveData = new MutableLiveData<>();


    public MutableLiveData<OTPValidateResponse> getRegEmailOTPResendLiveData() {
        return regEmailOTPResendLiveData;
    }

    public MutableLiveData<OTPValidateResponse> getRegPhoneOTPResendLiveData() {
        return regPhoneOTPResendLiveData;
    }

    public MutableLiveData<InitializeResponse> getInitializeResponseMutableLiveData() {
        return initializeResponseMutableLiveData;
    }

    public MutableLiveData<BiometricSignIn> getLoginNewLiveData() {
        return loginNewLiveData;
    }

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

    public MutableLiveData<OTPValidateResponse> getRegMobileOTPLiveData() {
        return regMobileOTPLiveData;
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

    public MutableLiveData<BiometricSignIn> getBiometricResponseMutableLiveData() {
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

    public MutableLiveData<DeviceInitializeResponse> getDeviceInitializeResponseMutableLiveData() {
        return deviceInitializeResponseMutableLiveData;
    }

    public MutableLiveData<StepUpOTPResponse> getStepUpEmailOTPResponseMutableLiveData() {
        return stepUpEmailOTPResponseMutableLiveData;
    }

    public MutableLiveData<StepUpOTPResponse> getStepUpPhoneOTPResponseMutableLiveData() {
        return stepUpPhoneOTPResponseMutableLiveData;
    }

    public MutableLiveData<OTPValidateResponse> getRegEmailOTPValidateLiveData() {
        return regEmailOTPValidateLiveData;
    }

    public MutableLiveData<OTPValidateResponse> getSignAgreementTOSotpLiveData() {
        return signAgreementTOSotpLiveData;
    }

    public MutableLiveData<SignAgreementResponse> getSignAgreementPPotpLiveData() {
        return signAgreementPPotpLiveData;
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

    public void emailotpresend(EmailRequest request) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<EmailResendResponse> mCall = apiService.emailotpresend(request);
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

    public void validateRegisterEmailOTP(OTPValidateRequest OTPValidateRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<OTPValidateResponse> mCall = apiService.validateRegisterEmailOTP(OTPValidateRequest);
            mCall.enqueue(new Callback<OTPValidateResponse>() {
                @Override
                public void onResponse(Call<OTPValidateResponse> call, Response<OTPValidateResponse> response) {
                    if (response.isSuccessful()) {
                        OTPValidateResponse obj = response.body();
                        regEmailOTPValidateLiveData.setValue(obj);
                        Log.e("Email Validate Resp", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<OTPValidateResponse>() {
                        }.getType();
                        OTPValidateResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            regEmailOTPValidateLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<OTPValidateResponse> call, Throwable t) {
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

    public void validateRegisterMobileOTP(OTPValidateRequest OTPValidateRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<OTPValidateResponse> mCall = apiService.validateRegisterMobileOTP(OTPValidateRequest);
            mCall.enqueue(new Callback<OTPValidateResponse>() {
                @Override
                public void onResponse(Call<OTPValidateResponse> call, Response<OTPValidateResponse> response) {
                    if (response.isSuccessful()) {
                        OTPValidateResponse obj = response.body();
                        regMobileOTPLiveData.setValue(obj);
                        Log.e("SMS Validate Resp", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<OTPValidateResponse>() {
                        }.getType();
                        OTPValidateResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            regMobileOTPLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<OTPValidateResponse> call, Throwable t) {
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

    public void loginNew(LoginRequest loginRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<BiometricSignIn> mCall = apiService.loginNew(loginRequest);
            mCall.enqueue(new Callback<BiometricSignIn>() {
                @Override
                public void onResponse(Call<BiometricSignIn> call, Response<BiometricSignIn> response) {
                    try {
                        String strResponse = "";
                        if (response.isSuccessful()) {
                            BiometricSignIn obj = response.body();
                            loginNewLiveData.setValue(obj);
                        } else if (response.code() == 500) {
                            strResponse = response.errorBody().string();
                            Gson gson = new Gson();
                            Type type = new TypeToken<BiometricSignIn>() {
                            }.getType();
                            BiometricSignIn errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
                                loginNewLiveData.setValue(errorResponse);
                            } else {
                                BiometricSignIn errorResponse1 = gson.fromJson(strResponse, type);
                                loginNewLiveData.setValue(errorResponse1);
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
                public void onFailure(Call<BiometricSignIn> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    loginNewLiveData.setValue(null);
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

    //new Signup flow
    public void registrationNew(CustRegisRequest custRegisRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<CustRegisterResponse> mCall = apiService.registerNew(custRegisRequest);
            mCall.enqueue(new Callback<CustRegisterResponse>() {
                @Override
                public void onResponse(Call<CustRegisterResponse> call, Response<CustRegisterResponse> response) {

                    if (response.isSuccessful()) {
                        Log.e("CustReg Success", "CustReg Success");
                        try {
                            CustRegisterResponse obj = response.body();
                            custRegisResponseMutableLiveData.setValue(obj);
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

    public void biometricLogin(BiometricLoginRequest request, MyApplication objMyApplication) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<BiometricSignIn> mCall = apiService.biometricLogin(request);
            mCall.enqueue(new Callback<BiometricSignIn>() {
                @Override
                public void onResponse(Call<BiometricSignIn> call, Response<BiometricSignIn> response) {
                    if (response.isSuccessful()) {
                        BiometricSignIn obj = response.body();
                        if (obj != null && obj.getData() != null) {
                            objMyApplication.setBusinessUserID(String.valueOf(obj.getData().getBusinessUserId()));
                            objMyApplication.setOwnerImage(obj.getData().getOwnerImage());
                        }
                        biometricResponseMutableLiveData.setValue(obj);
                        Log.e("Bio Success", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<BiometricSignIn>() {
                        }.getType();
                        BiometricSignIn errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            biometricResponseMutableLiveData.setValue(errorResponse);
                            Log.e("Biometric Error", new Gson().toJson(errorResponse));
                        }
                    }
                }

                @Override
                public void onFailure(Call<BiometricSignIn> call, Throwable t) {
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

    public void validateEmail(EmailRequest request) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<EmailExistsResponse> mCall = apiService.validateEmail(request);
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
                        LogUtils.d("LOGINVIEW", "MOdel" + response);
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

    public void initializeDevice(String token) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<DeviceInitializeResponse> mCall = apiService.initializedevice(token);
            mCall.enqueue(new Callback<DeviceInitializeResponse>() {
                @Override
                public void onResponse(Call<DeviceInitializeResponse> call, Response<DeviceInitializeResponse> response) {
                    if (response.isSuccessful()) {
                        DeviceInitializeResponse obj = response.body();
                        deviceInitializeResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<DeviceInitializeResponse>() {
                        }.getType();
                        DeviceInitializeResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            deviceInitializeResponseMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<DeviceInitializeResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    deviceInitializeResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stepUpEmailOTP(SmsRequest smsRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<StepUpOTPResponse> mCall = apiService.stepUpEmailOTP(smsRequest);
            mCall.enqueue(new Callback<StepUpOTPResponse>() {
                @Override
                public void onResponse(Call<StepUpOTPResponse> call, Response<StepUpOTPResponse> response) {
                    if (response.isSuccessful()) {
                        StepUpOTPResponse obj = response.body();
                        stepUpEmailOTPResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<StepUpOTPResponse>() {
                        }.getType();
                        StepUpOTPResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            stepUpEmailOTPResponseMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<StepUpOTPResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    stepUpEmailOTPResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stepUpPhoneOTP(SmsRequest smsRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<StepUpOTPResponse> mCall = apiService.stepUpPhoneOTP(smsRequest);
            mCall.enqueue(new Callback<StepUpOTPResponse>() {
                @Override
                public void onResponse(Call<StepUpOTPResponse> call, Response<StepUpOTPResponse> response) {
                    if (response.isSuccessful()) {
                        StepUpOTPResponse obj = response.body();
                        stepUpPhoneOTPResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<StepUpOTPResponse>() {
                        }.getType();
                        StepUpOTPResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            stepUpPhoneOTPResponseMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<StepUpOTPResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    stepUpPhoneOTPResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void signAgreementTOS(SignAgreementRequest signAgreementRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<OTPValidateResponse> mCall = apiService.signAgreementTOS(signAgreementRequest);
            mCall.enqueue(new Callback<OTPValidateResponse>() {
                @Override
                public void onResponse(Call<OTPValidateResponse> call, Response<OTPValidateResponse> response) {
                    if (response.isSuccessful()) {
                        OTPValidateResponse obj = response.body();
                        signAgreementTOSotpLiveData.setValue(obj);
                        Log.e("SMS Validate Resp", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<OTPValidateResponse>() {
                        }.getType();
                        OTPValidateResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            signAgreementTOSotpLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<OTPValidateResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    signAgreementTOSotpLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void signAgreementPP(SignAgreementRequest signAgreementRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<SignAgreementResponse> mCall = apiService.signAgreementPP(signAgreementRequest);
            mCall.enqueue(new Callback<SignAgreementResponse>() {
                @Override
                public void onResponse(Call<SignAgreementResponse> call, Response<SignAgreementResponse> response) {
                    if (response.isSuccessful()) {
                        SignAgreementResponse obj = response.body();
                        signAgreementPPotpLiveData.setValue(obj);
                        Log.e("SMS Validate Resp", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<SignAgreementResponse>() {
                        }.getType();
                        SignAgreementResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            signAgreementPPotpLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SignAgreementResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    signAgreementPPotpLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void initialize() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<InitializeResponse> mCall = apiService.initialize();
            mCall.enqueue(new Callback<InitializeResponse>() {
                @Override
                public void onResponse(Call<InitializeResponse> call, Response<InitializeResponse> response) {
                    try {
                        String strResponse = "";
                        if (response.isSuccessful()) {
                            InitializeResponse obj = response.body();
                            initializeResponseMutableLiveData.setValue(obj);
                        } else if (response.code() == 500) {
                            strResponse = response.errorBody().string();
                            Gson gson = new Gson();
                            Type type = new TypeToken<InitializeResponse>() {
                            }.getType();
                            InitializeResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
                                initializeResponseMutableLiveData.setValue(errorResponse);
                            } else {
                                InitializeResponse errorResponse1 = gson.fromJson(strResponse, type);
                                initializeResponseMutableLiveData.setValue(errorResponse1);
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
                public void onFailure(Call<InitializeResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    initializeResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void hasToSignAgreements() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<SignAgreementsResp> mCall = apiService.hasToSignAgreements();
            mCall.enqueue(new Callback<SignAgreementsResp>() {
                @Override
                public void onResponse(Call<SignAgreementsResp> call, Response<SignAgreementsResp> response) {
                    try {
                        String strResponse = "";
                        if (response.isSuccessful()) {
                            SignAgreementsResp obj = response.body();
                            hasToSignResponseMutableLiveData.setValue(obj);
                        } else if (response.code() == 500) {
                            strResponse = response.errorBody().string();
                            Gson gson = new Gson();
                            Type type = new TypeToken<SignAgreementsResp>() {
                            }.getType();
                            SignAgreementsResp errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
                                hasToSignResponseMutableLiveData.setValue(errorResponse);
                            } else {
                                SignAgreementsResp errorResponse1 = gson.fromJson(strResponse, type);
                                hasToSignResponseMutableLiveData.setValue(errorResponse1);
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
                public void onFailure(Call<SignAgreementsResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    initializeResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void regEmailOTPResend(OTPResendRequest resendRequest) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<OTPValidateResponse> mCall = apiService.regEmailOTPResend(resendRequest);
            mCall.enqueue(new Callback<OTPValidateResponse>() {
                @Override
                public void onResponse(Call<OTPValidateResponse> call, Response<OTPValidateResponse> response) {
                    if (response.isSuccessful()) {
                        OTPValidateResponse obj = response.body();
                        regEmailOTPResendLiveData.setValue(obj);
                        Log.e("Email Resend Resp", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<OTPValidateResponse>() {
                        }.getType();
                        OTPValidateResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            regEmailOTPResendLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<OTPValidateResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    regEmailOTPResendLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void regPhoneOTPResend(OTPResendRequest resend) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<OTPValidateResponse> mCall = apiService.regPhoneOTPResend(resend);
            mCall.enqueue(new Callback<OTPValidateResponse>() {
                @Override
                public void onResponse(Call<OTPValidateResponse> call, Response<OTPValidateResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            OTPValidateResponse obj = response.body();
                            regPhoneOTPResendLiveData.setValue(obj);
                            Log.e("SMS Resend Resp", new Gson().toJson(obj));
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<OTPValidateResponse>() {
                            }.getType();
                            OTPValidateResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
                                regPhoneOTPResendLiveData.setValue(errorResponse);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<OTPValidateResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    regPhoneOTPResendLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
