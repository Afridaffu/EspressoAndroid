package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.buytoken.BuyTokenRequest;
import com.greenbox.coyni.model.buytoken.BuyTokenResponse;
import com.greenbox.coyni.model.coynipin.ValidateResponse;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitRequest;
import com.greenbox.coyni.model.transactionlimit.TransactionLimitResponse;
import com.greenbox.coyni.model.transferfee.TransferFeeRequest;
import com.greenbox.coyni.model.transferfee.TransferFeeResponse;
import com.greenbox.coyni.model.withdraw.WithdrawRequest;
import com.greenbox.coyni.model.withdraw.WithdrawResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;
import com.greenbox.coyni.utils.Utils;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyTokenViewModel extends AndroidViewModel {
    private MutableLiveData<TransactionLimitResponse> transactionLimitResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TransferFeeResponse> transferFeeResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BuyTokenResponse> buyTokResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BuyTokenResponse> buyTokenFailureMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<WithdrawResponse> withdrawResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> withdrawFailureResponseMutableLiveData = new MutableLiveData<>();

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

    public MutableLiveData<BuyTokenResponse> getBuyTokResponseMutableLiveData() {
        return buyTokResponseMutableLiveData;
    }

    public MutableLiveData<BuyTokenResponse> getBuyTokenFailureMutableLiveData() {
        return buyTokenFailureMutableLiveData;
    }

    public MutableLiveData<WithdrawResponse> getWithdrawResponseMutableLiveData() {
        return withdrawResponseMutableLiveData;
    }

    public MutableLiveData<APIError> getWithdrawFailureResponseMutableLiveData() {
        return withdrawFailureResponseMutableLiveData;
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
                            Type type = new TypeToken<TransactionLimitResponse>() {
                            }.getType();
                            TransactionLimitResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            transactionLimitResponseMutableLiveData.setValue(errorResponse);
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

    public void buyTokens(BuyTokenRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BuyTokenResponse> mCall = apiService.buyTokens(request, Utils.getStrToken());
            mCall.enqueue(new Callback<BuyTokenResponse>() {
                @Override
                public void onResponse(Call<BuyTokenResponse> call, Response<BuyTokenResponse> response) {
                    try {
                        String strResponse = "";
                        if (response.isSuccessful()) {
                            BuyTokenResponse obj = response.body();
                            buyTokResponseMutableLiveData.setValue(obj);
                        } else if (response.code() == 400) {
                            strResponse = response.errorBody().string().replaceAll("\"", "'");
                            Gson gson = new Gson();
                            Type type = new TypeToken<BuyTokenResponse>() {
                            }.getType();
                            BuyTokenResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            if (errorResponse != null) {
                                buyTokenFailureMutableLiveData.setValue(errorResponse);
                            } else {
                                BuyTokenResponse errorResponse1 = gson.fromJson(strResponse, type);
                                buyTokenFailureMutableLiveData.setValue(errorResponse1);
                            }
                        } else {
                            strResponse = response.errorBody().string().replaceAll("\"", "'");
                            Gson gson = new Gson();
                            Type type = new TypeToken<BuyTokenResponse>() {
                            }.getType();
                            BuyTokenResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            if (errorResponse != null) {
                                buyTokResponseMutableLiveData.setValue(errorResponse);
                            } else {
                                BuyTokenResponse errorResponse1 = gson.fromJson(strResponse, type);
                                buyTokResponseMutableLiveData.setValue(errorResponse1);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<BuyTokenResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void withdrawTokens(WithdrawRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<WithdrawResponse> mCall = apiService.withdrawTokens(request,Utils.getStrToken());
            mCall.enqueue(new Callback<WithdrawResponse>() {
                @Override
                public void onResponse(Call<WithdrawResponse> call, Response<WithdrawResponse> response) {
                    try {
                        String strResponse = "";
                        if (response.isSuccessful()) {
                            WithdrawResponse obj = response.body();
                            withdrawResponseMutableLiveData.setValue(obj);
                        } else if (response.code() == 400) {
                            strResponse = response.errorBody().string().replaceAll("\"", "'");
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<APIError>() {
//                            }.getType();
//                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
//                            if (errorResponse != null) {
//                                withdrawFailureResponseMutableLiveData.setValue(errorResponse);
//                            } else {
//                                APIError errorResponse1 = gson.fromJson(strResponse, type);
//                                withdrawFailureResponseMutableLiveData.setValue(errorResponse1);
//                            }
                            Gson gson = new Gson();
                            Type type = new TypeToken<WithdrawResponse>() {
                            }.getType();
                            WithdrawResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            if (errorResponse != null) {
                                withdrawResponseMutableLiveData.setValue(errorResponse);
                            } else {
                                WithdrawResponse errorResponse1 = gson.fromJson(strResponse, type);
                                withdrawResponseMutableLiveData.setValue(errorResponse1);
                            }
                        } else {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<APIError>() {
//                            }.getType();
//                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
//                            apiErrorMutableLiveData.setValue(errorResponse);
                            Gson gson = new Gson();
                            Type type = new TypeToken<WithdrawResponse>() {
                            }.getType();
                            WithdrawResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            withdrawResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        withdrawResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<WithdrawResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    withdrawResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
