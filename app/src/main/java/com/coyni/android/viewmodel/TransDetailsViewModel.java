package com.coyni.android.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.android.model.APIError;
import com.coyni.android.model.transactions.buycreditcard.BuyCreditCard;
import com.coyni.android.model.transactions.payrequest.PayRequest;
import com.coyni.android.model.transactions.withdraw.WithdrawGiftCard;
import com.coyni.android.model.transactions.withdraw.WithdrawSignet;
import com.coyni.android.model.transactions.withdraw.WithdrawTransDetails;
import com.coyni.android.network.ApiService;
import com.coyni.android.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransDetailsViewModel extends AndroidViewModel {
    private MutableLiveData<BuyCreditCard> buyCreditCardMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PayRequest> payRequestMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<WithdrawTransDetails> withdrawTransDetailsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<WithdrawGiftCard> withdrawGiftCardMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<WithdrawSignet> withdrawSignetMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();

    public TransDetailsViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<BuyCreditCard> getBuyCreditCardMutableLiveData() {
        return buyCreditCardMutableLiveData;
    }

    public MutableLiveData<PayRequest> getPayRequestMutableLiveData() {
        return payRequestMutableLiveData;
    }

    public MutableLiveData<WithdrawTransDetails> getWithdrawTransDetailsMutableLiveData() {
        return withdrawTransDetailsMutableLiveData;
    }

    public MutableLiveData<WithdrawGiftCard> getWithdrawGiftCardMutableLiveData() {
        return withdrawGiftCardMutableLiveData;
    }

    public MutableLiveData<WithdrawSignet> getWithdrawSignetMutableLiveData() {
        return withdrawSignetMutableLiveData;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public void getBuyCardTransDetails(String gbxTxnId, String txnType, String txnSubType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BuyCreditCard> mCall = apiService.getBuyCardTransDetails(gbxTxnId, txnType, txnSubType);
            mCall.enqueue(new Callback<BuyCreditCard>() {
                @Override
                public void onResponse(Call<BuyCreditCard> call, Response<BuyCreditCard> response) {
                    try {
                        if (response.isSuccessful()) {
                            BuyCreditCard obj = response.body();
                            buyCreditCardMutableLiveData.setValue(obj);
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
                public void onFailure(Call<BuyCreditCard> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getPayReqTransDetails(String gbxTxnId, String txnType, String txnSubType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<PayRequest> mCall = apiService.getPayReqTransDetails(gbxTxnId, txnType, txnSubType);
            mCall.enqueue(new Callback<PayRequest>() {
                @Override
                public void onResponse(Call<PayRequest> call, Response<PayRequest> response) {
                    try {
                        if (response.isSuccessful()) {
                            PayRequest obj = response.body();
                            payRequestMutableLiveData.setValue(obj);
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
                public void onFailure(Call<PayRequest> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getWithdrawTransDetails(String gbxTxnId, String txnType, String txnSubType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<WithdrawTransDetails> mCall = apiService.getWithdrawTransDetails(gbxTxnId, txnType, txnSubType);
            mCall.enqueue(new Callback<WithdrawTransDetails>() {
                @Override
                public void onResponse(Call<WithdrawTransDetails> call, Response<WithdrawTransDetails> response) {
                    try {
                        if (response.isSuccessful()) {
                            WithdrawTransDetails obj = response.body();
                            withdrawTransDetailsMutableLiveData.setValue(obj);
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
                public void onFailure(Call<WithdrawTransDetails> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getGiftTransDetails(String gbxTxnId, String txnType, String txnSubType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<WithdrawGiftCard> mCall = apiService.getGiftTransDetails(gbxTxnId, txnType, txnSubType);
            mCall.enqueue(new Callback<WithdrawGiftCard>() {
                @Override
                public void onResponse(Call<WithdrawGiftCard> call, Response<WithdrawGiftCard> response) {
                    try {
                        if (response.isSuccessful()) {
                            WithdrawGiftCard obj = response.body();
                            withdrawGiftCardMutableLiveData.setValue(obj);
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
                public void onFailure(Call<WithdrawGiftCard> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getSignetTransDetails(String gbxTxnId, String txnType, String txnSubType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<WithdrawSignet> mCall = apiService.getSignetTransDetails(gbxTxnId, txnType, txnSubType);
            mCall.enqueue(new Callback<WithdrawSignet>() {
                @Override
                public void onResponse(Call<WithdrawSignet> call, Response<WithdrawSignet> response) {
                    try {
                        if (response.isSuccessful()) {
                            WithdrawSignet obj = response.body();
                            withdrawSignetMutableLiveData.setValue(obj);
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
                public void onFailure(Call<WithdrawSignet> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
