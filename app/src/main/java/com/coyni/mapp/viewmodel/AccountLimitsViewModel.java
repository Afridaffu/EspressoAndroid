package com.coyni.mapp.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.mapp.model.APIError;
import com.coyni.mapp.model.users.AccountLimits;
import com.coyni.mapp.network.ApiService;
import com.coyni.mapp.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountLimitsViewModel extends AndroidViewModel {
    public AccountLimitsViewModel(@NonNull Application application) {
        super(application);
    }
    private MutableLiveData<AccountLimits> userAccountLimitsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();


    public MutableLiveData<AccountLimits> getUserAccountLimitsMutableLiveData() {
        return userAccountLimitsMutableLiveData;
    }

    public void meAccountLimits(int request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<AccountLimits> mCall = apiService.meAccountLimits(request);
            mCall.enqueue(new Callback<AccountLimits>() {
                @Override
                public void onResponse(Call<AccountLimits> call, Response<AccountLimits> response) {
                    try {
                        if (response.isSuccessful()) {
                            AccountLimits obj = response.body();
                            userAccountLimitsMutableLiveData.setValue(obj);
                            Log.e("AccLi",""+new Gson().toJson(obj));
                        } else {
                            Gson gson = new Gson();

                            Type type = new TypeToken<AccountLimits>() {
                            }.getType();
                            AccountLimits errorResponse = gson.fromJson(response.errorBody().string(), type);
                            userAccountLimitsMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        userAccountLimitsMutableLiveData.setValue(null);
                    }
                }
                @Override
                public void onFailure(Call<AccountLimits> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    userAccountLimitsMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
