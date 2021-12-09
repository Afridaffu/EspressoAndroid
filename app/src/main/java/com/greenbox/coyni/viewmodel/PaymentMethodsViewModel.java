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
import com.greenbox.coyni.model.cards.CardRequest;
import com.greenbox.coyni.model.cards.CardResponse;
import com.greenbox.coyni.model.cards.CardTypeRequest;
import com.greenbox.coyni.model.cards.CardTypeResponse;
import com.greenbox.coyni.model.preauth.PreAuthRequest;
import com.greenbox.coyni.model.preauth.PreAuthResponse;
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
    private MutableLiveData<APIError> preAuthErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BankDeleteResponseData> delBankResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CardResponse> cardResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PreAuthResponse> preAuthResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CardTypeResponse> cardTypeResponseMutableLiveData = new MutableLiveData<>();

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

    public MutableLiveData<CardResponse> getCardResponseMutableLiveData() {
        return cardResponseMutableLiveData;
    }

    public MutableLiveData<PreAuthResponse> getPreAuthResponseMutableLiveData() {
        return preAuthResponseMutableLiveData;
    }

    public MutableLiveData<CardTypeResponse> getCardTypeResponseMutableLiveData() {
        return cardTypeResponseMutableLiveData;
    }

    public MutableLiveData<APIError> getPreAuthErrorMutableLiveData() {
        return preAuthErrorMutableLiveData;
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

    public void saveCards(CardRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CardResponse> mCall = apiService.saveCards(request);
            mCall.enqueue(new Callback<CardResponse>() {
                @Override
                public void onResponse(Call<CardResponse> call, Response<CardResponse> response) {
                    if (response.isSuccessful()) {
                        CardResponse obj = response.body();
                        cardResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<CardResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void preAuthVerify(PreAuthRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<PreAuthResponse> mCall = apiService.preAuthVerify(request);
            mCall.enqueue(new Callback<PreAuthResponse>() {
                @Override
                public void onResponse(Call<PreAuthResponse> call, Response<PreAuthResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            PreAuthResponse obj = response.body();
                            preAuthResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<APIError>() {
                            }.getType();
                            APIError errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse == null) {
                                errorResponse = gson.fromJson(response.errorBody().string(), type);
                            }
                            preAuthErrorMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<PreAuthResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    preAuthErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cardType(CardTypeRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CardTypeResponse> mCall = apiService.cardType(request);
            mCall.enqueue(new Callback<CardTypeResponse>() {
                @Override
                public void onResponse(Call<CardTypeResponse> call, Response<CardTypeResponse> response) {
                    if (response.isSuccessful()) {
                        CardTypeResponse obj = response.body();
                        cardTypeResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<CardTypeResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
