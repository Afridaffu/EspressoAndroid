package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailRequest;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneRequest;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneResponse;
import com.greenbox.coyni.model.users.User;
import com.greenbox.coyni.model.users.UserData;
import com.greenbox.coyni.model.users.UserPreferenceModel;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerProfileViewModel extends AndroidViewModel {
    private MutableLiveData<UpdateEmailResponse> updateEmailSendOTPResponse = new MutableLiveData<>();

    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<UpdatePhoneResponse> updatePhoneSendOTPResponse = new MutableLiveData<>();

    private MutableLiveData<UserPreference> userPreferenceMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();

    public CustomerProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<User> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public void setUserMutableLiveData(MutableLiveData<User> userMutableLiveData) {
        this.userMutableLiveData = userMutableLiveData;
    }

    public MutableLiveData<UpdateEmailResponse> getUpdateEmailSendOTPResponse() {
        return updateEmailSendOTPResponse;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<UpdatePhoneResponse> getUpdatePhoneSendOTPResponse() {
        return updatePhoneSendOTPResponse;
    }

    public MutableLiveData<UserPreference> getUserPreferenceMutableLiveData() {
        return userPreferenceMutableLiveData;
    }

    public void setUserPreferenceMutableLiveData(MutableLiveData<UserPreference> userPreferenceMutableLiveData) {
        this.userPreferenceMutableLiveData = userPreferenceMutableLiveData;
    }

    public void updateEmailSendOTP(UpdateEmailRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UpdateEmailResponse> mCall = apiService.updateEmailSendOTP(request);
            mCall.enqueue(new Callback<UpdateEmailResponse>() {
                @Override
                public void onResponse(Call<UpdateEmailResponse> call, Response<UpdateEmailResponse> response) {
                    if (response.isSuccessful()) {
                        UpdateEmailResponse obj = response.body();
                        updateEmailSendOTPResponse.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<UpdateEmailResponse>() {
                        }.getType();
                        UpdateEmailResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            updateEmailSendOTPResponse.setValue(errorResponse);
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

    public void updatePhoneSendOTP(UpdatePhoneRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UpdatePhoneResponse> mCall = apiService.updatePhoneSendOTP(request);
            mCall.enqueue(new Callback<UpdatePhoneResponse>() {
                @Override
                public void onResponse(Call<UpdatePhoneResponse> call, Response<UpdatePhoneResponse> response) {
                    if (response.isSuccessful()) {
                        UpdatePhoneResponse obj = response.body();
                        updatePhoneSendOTPResponse.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<UpdatePhoneResponse>() {
                        }.getType();
                        UpdatePhoneResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            updatePhoneSendOTPResponse.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UpdatePhoneResponse> call, Throwable t) {
//                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updatePreferences(UserPreferenceModel request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UserPreference> mCall = apiService.meUpdatePreferences(request);
            mCall.enqueue(new Callback<UserPreference>() {
                @Override
                public void onResponse(Call<UserPreference> call, Response<UserPreference> response) {
                    try {
                        if (response.isSuccessful()) {
                            UserPreference obj = response.body();
                            userPreferenceMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<UserPreference>() {
                            }.getType();
                            UserPreference errorResponse = gson.fromJson(response.errorBody().string(), type);
                            userPreferenceMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<UserPreference> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meUpdateAddress(UserData request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<User> mCall = apiService.meUpdateAddress(request);
            mCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    try {
                        if (response.isSuccessful()) {
                            User obj = response.body();
                            userMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<User>() {
                            }.getType();
                            User errorResponse = gson.fromJson(response.errorBody().string(), type);
                            userMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
