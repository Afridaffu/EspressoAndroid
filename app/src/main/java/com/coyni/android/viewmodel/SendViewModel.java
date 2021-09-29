package com.coyni.android.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.coyni.android.model.APIError;
import com.coyni.android.model.exchangerate.ExchangeRate;
import com.coyni.android.model.transactions.TransactionDetails;
import com.coyni.android.model.transferfee.TransferFeeRequest;
import com.coyni.android.model.transferfee.TransferFeeResponse;
import com.coyni.android.network.ApiService;
import com.coyni.android.network.AuthApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.android.model.sendtransfer.TransferSendRequest;
import com.coyni.android.model.sendtransfer.TransferSendResponse;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendViewModel extends AndroidViewModel {
    private MutableLiveData<TransferFeeResponse> transferFeeMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ExchangeRate> exchangeRateMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TransferSendResponse> sendTokenMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TransactionDetails> transactionDetailsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();

    public SendViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<TransferFeeResponse> getTransferFeeMutableLiveData() {
        return transferFeeMutableLiveData;
    }

    public MutableLiveData<ExchangeRate> getExchangeRateMutableLiveData() {
        return exchangeRateMutableLiveData;
    }

    public MutableLiveData<TransferSendResponse> getSendTokenMutableLiveData() {
        return sendTokenMutableLiveData;
    }

    public MutableLiveData<TransactionDetails> getTransactionDetailsMutableLiveData() {
        return transactionDetailsMutableLiveData;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
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
                            transferFeeMutableLiveData.setValue(obj);
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

    public void exchangeRate(String fromCurrency, String toCurrency, String amount) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ExchangeRate> mCall = apiService.exchangerate(fromCurrency, toCurrency, amount);
            mCall.enqueue(new Callback<ExchangeRate>() {
                @Override
                public void onResponse(Call<ExchangeRate> call, Response<ExchangeRate> response) {
                    try {
                        if (response.isSuccessful()) {
                            ExchangeRate obj = response.body();
                            exchangeRateMutableLiveData.setValue(obj);
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
                public void onFailure(Call<ExchangeRate> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendTokens(TransferSendRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TransferSendResponse> mCall = apiService.sendTokens(request);
            mCall.enqueue(new Callback<TransferSendResponse>() {
                @Override
                public void onResponse(Call<TransferSendResponse> call, Response<TransferSendResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            TransferSendResponse obj = response.body();
                            sendTokenMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<APIError>() {
                            }.getType();
                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
                            apiErrorMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<TransferSendResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
