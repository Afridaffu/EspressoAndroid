package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.bank.BanksResponseModel;
import com.greenbox.coyni.model.summary.ApplicationSummaryModelResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessApplicationSummaryViewModel extends AndroidViewModel {
    public BusinessApplicationSummaryViewModel(@NonNull Application application) {
        super(application);
    }
    private MutableLiveData<ApplicationSummaryModelResponse> summaryMutableLiveData = new MutableLiveData<>();


    public MutableLiveData<ApplicationSummaryModelResponse> getSummaryMutableLiveData() {
        return summaryMutableLiveData;
    }


    public void getApplicationSummaryData() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ApplicationSummaryModelResponse> mCall = apiService.getApplicationSummaryData();
            mCall.enqueue(new Callback<ApplicationSummaryModelResponse>() {
                @Override
                public void onResponse(Call<ApplicationSummaryModelResponse> call, Response<ApplicationSummaryModelResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            ApplicationSummaryModelResponse obj = response.body();

                            summaryMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<ApplicationSummaryModelResponse>() {}.getType();
                            ApplicationSummaryModelResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            summaryMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        summaryMutableLiveData.setValue(null);
                    }
                }
                @Override
                public void onFailure(Call<ApplicationSummaryModelResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    summaryMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}