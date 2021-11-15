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
import com.greenbox.coyni.model.coynipin.PINRegisterResponse;
import com.greenbox.coyni.model.coynipin.RegisterRequest;
import com.greenbox.coyni.model.coynipin.ValidateRequest;
import com.greenbox.coyni.model.coynipin.ValidateResponse;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailRequest;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerProfileViewModel extends AndroidViewModel {
    private MutableLiveData<UpdateEmailResponse> updateEmailSendOTPResponse = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();


    public CustomerProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<UpdateEmailResponse> getUpdateEmailSendOTPResponse() {
        return updateEmailSendOTPResponse;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
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

}
