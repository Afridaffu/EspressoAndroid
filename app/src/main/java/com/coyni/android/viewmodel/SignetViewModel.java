package com.coyni.android.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.coyni.android.model.shuftipro.ShuftiProResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.android.model.APIError;
import com.coyni.android.model.kycchecks.KYC_ChecksResponse;
import com.coyni.android.model.signet.SignetEditResponse;
import com.coyni.android.model.signet.SignetRequest;
import com.coyni.android.model.signet.SignetResponse;
import com.coyni.android.network.ApiService;
import com.coyni.android.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignetViewModel extends AndroidViewModel {
    private MutableLiveData<SignetResponse> signetResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SignetEditResponse> signetEditResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<KYC_ChecksResponse> kycChecksResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ShuftiProResponse> shuftiProResponseMutableLiveData = new MutableLiveData<>();

    public SignetViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<SignetResponse> getSignetResponseMutableLiveData() {
        return signetResponseMutableLiveData;
    }

    public MutableLiveData<SignetEditResponse> getSignetEditResponseMutableLiveData() {
        return signetEditResponseMutableLiveData;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<KYC_ChecksResponse> getKycChecksResponseMutableLiveData() {
        return kycChecksResponseMutableLiveData;
    }

    public MutableLiveData<ShuftiProResponse> getShuftiProResponseMutableLiveData() {
        return shuftiProResponseMutableLiveData;
    }

    public void saveBank(SignetRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<SignetResponse> mCall = apiService.saveBanks(request);
            mCall.enqueue(new Callback<SignetResponse>() {
                @Override
                public void onResponse(Call<SignetResponse> call, Response<SignetResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            SignetResponse obj = response.body();
                            signetResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<APIError>() {
                            }.getType();
                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
                            apiErrorMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<SignetResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void editBank(SignetRequest request, String strBankId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<SignetEditResponse> mCall = apiService.editBanks(request, strBankId);
            mCall.enqueue(new Callback<SignetEditResponse>() {
                @Override
                public void onResponse(Call<SignetEditResponse> call, Response<SignetEditResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            SignetEditResponse obj = response.body();
                            signetEditResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<APIError>() {
                            }.getType();
                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
                            apiErrorMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<SignetEditResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateKyc(String reference_id) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<KYC_ChecksResponse> mCall = apiService.updateKYC(reference_id);
            mCall.enqueue(new Callback<KYC_ChecksResponse>() {
                @Override
                public void onResponse(Call<KYC_ChecksResponse> call, Response<KYC_ChecksResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            KYC_ChecksResponse obj = response.body();
                            kycChecksResponseMutableLiveData.setValue(obj);
                        } else if (response.code() == 417) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<ShuftiProResponse>() {
                            }.getType();
                            ShuftiProResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            shuftiProResponseMutableLiveData.setValue(errorResponse);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<APIError>() {
                            }.getType();
                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
                            apiErrorMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        apiErrorMutableLiveData.setValue(null);
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<KYC_ChecksResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
