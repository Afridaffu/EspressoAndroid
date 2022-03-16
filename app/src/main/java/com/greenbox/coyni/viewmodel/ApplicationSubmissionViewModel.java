package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.submit.ApplicationSubmitRequest;
import com.greenbox.coyni.model.submit.ApplicationSubmitResponseModel;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApplicationSubmissionViewModel extends AndroidViewModel {

        private MutableLiveData<ApplicationSubmitResponseModel> postApplicationSubmissionData = new MutableLiveData<>();

    public ApplicationSubmissionViewModel(@NonNull Application application) {
        super(application);
    }

        public MutableLiveData<ApplicationSubmitResponseModel> getPostApplicationSubmissionData() {
            return postApplicationSubmissionData;
        }

        public void postApplicationData() {
            try {
                ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
                Call<ApplicationSubmitResponseModel> mCall = apiService.postApplicationSubmissionData();
                mCall.enqueue(new Callback<ApplicationSubmitResponseModel>() {
                    @Override
                    public void onResponse(Call<ApplicationSubmitResponseModel> call, Response<ApplicationSubmitResponseModel> response) {
                        try {
                            if (response.isSuccessful()) {
                                ApplicationSubmitResponseModel obj = response.body();
                                postApplicationSubmissionData.setValue(obj);
                            } else {
                                Gson gson = new Gson();
                                Type type = new TypeToken<ApplicationSubmitResponseModel>() {
                                }.getType();
                                ApplicationSubmitResponseModel errorResponse = gson.fromJson(response.errorBody().string(), type);
                                postApplicationSubmissionData.setValue(errorResponse);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            postApplicationSubmissionData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApplicationSubmitResponseModel> call, Throwable t) {
                        Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                        postApplicationSubmissionData.setValue(null);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


}
