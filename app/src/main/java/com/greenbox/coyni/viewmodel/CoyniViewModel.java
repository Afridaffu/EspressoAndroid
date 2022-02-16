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
import com.greenbox.coyni.model.biometric.BiometricTokenRequest;
import com.greenbox.coyni.model.biometric.BiometricTokenResponse;
import com.greenbox.coyni.model.coynipin.PINRegisterResponse;
import com.greenbox.coyni.model.coynipin.RegisterRequest;
import com.greenbox.coyni.model.coynipin.StepUpResponse;
import com.greenbox.coyni.model.coynipin.ValidateRequest;
import com.greenbox.coyni.model.coynipin.ValidateResponse;
import com.greenbox.coyni.model.register.EmailResendResponse;
import com.greenbox.coyni.model.register.SMSResend;
import com.greenbox.coyni.model.register.SMSResponse;
import com.greenbox.coyni.network.ApiClient;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;
import com.greenbox.coyni.utils.Utils;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CoyniViewModel extends AndroidViewModel {
    private MutableLiveData<ValidateResponse> validateResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<StepUpResponse> stepUpResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PINRegisterResponse> registerPINResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BiometricResponse> biometricResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BiometricTokenResponse> biometricTokenResponseMutableLiveData = new MutableLiveData<>();


    public MutableLiveData<StepUpResponse> getStepUpResponseMutableLiveData() {
        return stepUpResponseMutableLiveData;
    }

    public MutableLiveData<PINRegisterResponse> getRegisterPINResponseMutableLiveData() {
        return registerPINResponseMutableLiveData;
    }

    public MutableLiveData<BiometricResponse> getBiometricResponseMutableLiveData() {
        return biometricResponseMutableLiveData;
    }

    public CoyniViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<ValidateResponse> getValidateResponseMutableLiveData() {
        return validateResponseMutableLiveData;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<BiometricTokenResponse> getBiometricTokenResponseMutableLiveData() {
        return biometricTokenResponseMutableLiveData;
    }

    public void validateCoyniPin(ValidateRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ValidateResponse> mCall = apiService.validateCoyniPin(request);
            mCall.enqueue(new Callback<ValidateResponse>() {
                @Override
                public void onResponse(Call<ValidateResponse> call, Response<ValidateResponse> response) {
                    if (response.isSuccessful()) {
                        ValidateResponse obj = response.body();
                        validateResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ValidateResponse>() {
                        }.getType();
                        ValidateResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            validateResponseMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ValidateResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stepUpPin(ValidateRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<StepUpResponse> mCall = apiService.stepUpPin(request);
            mCall.enqueue(new Callback<StepUpResponse>() {
                @Override
                public void onResponse(Call<StepUpResponse> call, Response<StepUpResponse> response) {
                    if (response.isSuccessful()) {
                        StepUpResponse obj = response.body();
                        stepUpResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<StepUpResponse>() {
                        }.getType();
                        StepUpResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            stepUpResponseMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<StepUpResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void registerCoyniPin(RegisterRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<PINRegisterResponse> mCall = apiService.coyniPINRegister(request);
            mCall.enqueue(new Callback<PINRegisterResponse>() {
                @Override
                public void onResponse(Call<PINRegisterResponse> call, Response<PINRegisterResponse> response) {
                    if (response.isSuccessful()) {
                        PINRegisterResponse obj = response.body();
                        registerPINResponseMutableLiveData.setValue(obj);
                        Log.e("PIN Success", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<PINRegisterResponse>() {
                        }.getType();
                        PINRegisterResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            registerPINResponseMutableLiveData.setValue(errorResponse);
                            Log.e("PIN Error", new Gson().toJson(errorResponse));
                        }
                    }
                }

                @Override
                public void onFailure(Call<PINRegisterResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveBiometric(BiometricRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BiometricResponse> mCall = apiService.saveBiometric(request);
            mCall.enqueue(new Callback<BiometricResponse>() {
                @Override
                public void onResponse(Call<BiometricResponse> call, Response<BiometricResponse> response) {
                    if (response.isSuccessful()) {
                        BiometricResponse obj = response.body();
                        biometricResponseMutableLiveData.setValue(obj);
                        Log.e("Bio Success", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<BiometricResponse>() {
                        }.getType();
                        BiometricResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            biometricResponseMutableLiveData.setValue(errorResponse);
                            Log.e("Biometric Error", new Gson().toJson(errorResponse));
                        }
                    }
                }

                @Override
                public void onFailure(Call<BiometricResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void biometricToken(BiometricTokenRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BiometricTokenResponse> mCall = apiService.biometricToken(request);
            mCall.enqueue(new Callback<BiometricTokenResponse>() {
                @Override
                public void onResponse(Call<BiometricTokenResponse> call, Response<BiometricTokenResponse> response) {
                    if (response.isSuccessful()) {
                        BiometricTokenResponse obj = response.body();
                        biometricTokenResponseMutableLiveData.setValue(obj);
                        Log.e("Bio Success", new Gson().toJson(obj));
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<BiometricTokenResponse>() {
                        }.getType();
                        BiometricTokenResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            biometricTokenResponseMutableLiveData.setValue(errorResponse);
                            Log.e("Biometric Error", new Gson().toJson(errorResponse));
                        }
                    }
                }

                @Override
                public void onFailure(Call<BiometricTokenResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    biometricTokenResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
