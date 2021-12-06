package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.bank.BankDeleteResponseData;
import com.greenbox.coyni.model.publickey.PublicKeyResponse;
import com.greenbox.coyni.model.register.EmailResendResponse;
import com.greenbox.coyni.model.retrieveemail.RetrieveEmailRequest;
import com.greenbox.coyni.model.retrieveemail.RetrieveEmailResponse;
import com.greenbox.coyni.network.ApiClient;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentMethodsViewModel extends AndroidViewModel {
    private MutableLiveData<PublicKeyResponse> publicKeyResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BankDeleteResponseData> delBankResponseMutableLiveData = new MutableLiveData<>();

    public PaymentMethodsViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<PublicKeyResponse> getPublicKeyResponseMutableLiveData() {
        return publicKeyResponseMutableLiveData;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<BankDeleteResponseData> getDelBankResponseMutableLiveData() {
        return delBankResponseMutableLiveData;
    }

    public void getPublicKey(int userId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<PublicKeyResponse> mCall = apiService.getPublicKey(userId);
            mCall.enqueue(new Callback<PublicKeyResponse>() {
                @Override
                public void onResponse(Call<PublicKeyResponse> call, Response<PublicKeyResponse> response) {
                    if (response.isSuccessful()) {
                        PublicKeyResponse obj = response.body();
                        publicKeyResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<PublicKeyResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteBanks(String bankId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BankDeleteResponseData> mCall = apiService.deleteBank(bankId);
            mCall.enqueue(new Callback<BankDeleteResponseData>() {
                @Override
                public void onResponse(Call<BankDeleteResponseData> call, Response<BankDeleteResponseData> response) {
                    try {
                        if (response.isSuccessful()) {
                            BankDeleteResponseData obj = response.body();
                            delBankResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<BankDeleteResponseData> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
