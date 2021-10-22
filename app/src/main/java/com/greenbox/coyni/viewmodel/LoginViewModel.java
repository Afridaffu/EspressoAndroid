package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.register.EmailResendResponse;
import com.greenbox.coyni.model.register.EmailResponse;
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

}
