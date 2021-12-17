package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.coynipin.ValidateResponse;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.model.transferfee.TransferFeeRequest;
import com.greenbox.coyni.model.transferfee.TransferFeeResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyTokenViewModel extends AndroidViewModel {
    private MutableLiveData<TransactionLimitResponse> transactionLimitResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TransferFeeResponse> transferFeeResponseMutableLiveData = new MutableLiveData<>();

    public BuyTokenViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<TransactionLimitResponse> getTransactionLimitResponseMutableLiveData() {
        return transactionLimitResponseMutableLiveData;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<TransferFeeResponse> getTransferFeeResponseMutableLiveData() {
        return transferFeeResponseMutableLiveData;
    }

    public void transactionLimits(TransactionLimitRequest request, int userType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TransactionLimitResponse> mCall = apiService.transactionLimits(request, userType);
            mCall.enqueue(new Callback<TransactionLimitResponse>() {
                @Override
                public void onResponse(Call<TransactionLimitResponse> call, Response<TransactionLimitResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            TransactionLimitResponse obj = response.body();
                            transactionLimitResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<TransactionLimitResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void transferFee(TransferFeeRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TransferFeeResponse> mCall = apiService.transferFee(request);
            mCall.enqueue(new Callback<TransferFeeResponse>() {
                @Override
                public void onResponse(Call<TransferFeeResponse> call, Response<TransferFeeResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            TransferFeeResponse obj = response.body();
                            transferFeeResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<TransferFeeResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
