package com.coyni.android.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.coyni.android.model.buytoken.BuyTokenFailure;
import com.coyni.android.model.APIError;
import com.coyni.android.model.bank.BankDeleteResponseData;
import com.coyni.android.model.bank.Banks;
import com.coyni.android.model.bank.SignOn;
import com.coyni.android.model.bank.SyncAccount;
import com.coyni.android.model.buytoken.BuyTokenRequest;
import com.coyni.android.model.buytoken.BuyTokenResponse;
import com.coyni.android.model.cards.CardDeleteResponse;
import com.coyni.android.model.cards.CardEditRequest;
import com.coyni.android.model.cards.CardEditResponse;
import com.coyni.android.model.cards.CardRequest;
import com.coyni.android.model.cards.CardResponse;
import com.coyni.android.model.cards.Cards;
import com.coyni.android.model.giftcard.GiftCard;
import com.coyni.android.model.preauth.PreAuthRequest;
import com.coyni.android.model.preauth.PreAuthResponse;
import com.coyni.android.model.transactions.TransactionLimitRequest;
import com.coyni.android.model.transactions.TransactionLimitResponse;
import com.coyni.android.model.user.PublicKeyResponse;
import com.coyni.android.model.withdraw.WithdrawRequest;
import com.coyni.android.model.withdraw.WithdrawResponse;
import com.coyni.android.network.ApiService;
import com.coyni.android.network.AuthApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyViewModel extends AndroidViewModel {
    private MutableLiveData<Cards> cardsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CardResponse> cardResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CardEditResponse> editCardResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CardDeleteResponse> delCardResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BankDeleteResponseData> delBankResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BuyTokenResponse> buyTokResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<WithdrawResponse> withTokResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Banks> banksMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TransactionLimitResponse> limitMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<GiftCard> giftCardMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SignOn> signOnMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SyncAccount> syncAccountMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PreAuthResponse> preAuthMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PublicKeyResponse> publicKeyLiveData = new MutableLiveData<>();
    private MutableLiveData<BuyTokenFailure> buyTokenFailureMutableLiveData = new MutableLiveData<>();

    public BuyViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Cards> getCardsMutableLiveData() {
        return cardsMutableLiveData;
    }

    public MutableLiveData<CardResponse> getCardResponseMutableLiveData() {
        return cardResponseMutableLiveData;
    }

    public MutableLiveData<CardEditResponse> getEditCardResponseMutableLiveData() {
        return editCardResponseMutableLiveData;
    }

    public MutableLiveData<CardDeleteResponse> getDelCardResponseMutableLiveData() {
        return delCardResponseMutableLiveData;
    }

    public MutableLiveData<BuyTokenResponse> getBuyTokResponseMutableLiveData() {
        return buyTokResponseMutableLiveData;
    }

    public MutableLiveData<Banks> getBanksMutableLiveData() {
        return banksMutableLiveData;
    }

    public MutableLiveData<WithdrawResponse> getWithTokResponseMutableLiveData() {
        return withTokResponseMutableLiveData;
    }

    public MutableLiveData<BankDeleteResponseData> getDelBankResponseMutableLiveData() {
        return delBankResponseMutableLiveData;
    }

    public MutableLiveData<TransactionLimitResponse> getLimitMutableLiveData() {
        return limitMutableLiveData;
    }

    public MutableLiveData<GiftCard> getGiftCardMutableLiveData() {
        return giftCardMutableLiveData;
    }

    public MutableLiveData<SignOn> getSignOnMutableLiveData() {
        return signOnMutableLiveData;
    }

    public MutableLiveData<SyncAccount> getSyncAccountMutableLiveData() {
        return syncAccountMutableLiveData;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<PreAuthResponse> getPreAuthMutableLiveData() {
        return preAuthMutableLiveData;
    }

    public MutableLiveData<PublicKeyResponse> getPublicKeyLiveData() {
        return publicKeyLiveData;
    }

    public MutableLiveData<BuyTokenFailure> getBuyTokenFailureMutableLiveData() {
        return buyTokenFailureMutableLiveData;
    }

    public void meCards() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Cards> mCall = apiService.meCards();
            mCall.enqueue(new Callback<Cards>() {
                @Override
                public void onResponse(Call<Cards> call, Response<Cards> response) {
                    try {
                        if (response.isSuccessful()) {
                            Cards obj = response.body();
                            cardsMutableLiveData.setValue(obj);
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
                public void onFailure(Call<Cards> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meBanks() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Banks> mCall = apiService.meBanks();
            mCall.enqueue(new Callback<Banks>() {
                @Override
                public void onResponse(Call<Banks> call, Response<Banks> response) {
                    try {
                        if (response.isSuccessful()) {
                            Banks obj = response.body();
                            banksMutableLiveData.setValue(obj);
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
                public void onFailure(Call<Banks> call, Throwable t) {
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
                        Type type = new TypeToken<CardResponse>() {
                        }.getType();
                        CardResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        cardResponseMutableLiveData.setValue(errorResponse);
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

    public void editCards(CardEditRequest request, String strCardId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CardEditResponse> mCall = apiService.editCards(request, strCardId);
            mCall.enqueue(new Callback<CardEditResponse>() {
                @Override
                public void onResponse(Call<CardEditResponse> call, Response<CardEditResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            CardEditResponse obj = response.body();
                            editCardResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<CardEditResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteCards(String cardId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CardDeleteResponse> mCall = apiService.deleteCards(cardId);
            mCall.enqueue(new Callback<CardDeleteResponse>() {
                @Override
                public void onResponse(Call<CardDeleteResponse> call, Response<CardDeleteResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            CardDeleteResponse obj = response.body();
                            delCardResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<CardDeleteResponse> call, Throwable t) {
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
            Call<BuyTokenResponse> mCall = apiService.buyTokens(request);
            mCall.enqueue(new Callback<BuyTokenResponse>() {
                @Override
                public void onResponse(Call<BuyTokenResponse> call, Response<BuyTokenResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            BuyTokenResponse obj = response.body();
                            buyTokResponseMutableLiveData.setValue(obj);
                        } else if (response.code() == 400) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BuyTokenFailure>() {
                            }.getType();
                            BuyTokenFailure errorResponse = gson.fromJson(response.errorBody().string(), type);
                            buyTokenFailureMutableLiveData.setValue(errorResponse);
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
            Call<WithdrawResponse> mCall = apiService.withdrawTokens(request);
            mCall.enqueue(new Callback<WithdrawResponse>() {
                @Override
                public void onResponse(Call<WithdrawResponse> call, Response<WithdrawResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            WithdrawResponse obj = response.body();
                            withTokResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<WithdrawResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    withTokResponseMutableLiveData.setValue(null);
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

    public void transactionLimits(TransactionLimitRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TransactionLimitResponse> mCall = apiService.transactionlimits(request);
            mCall.enqueue(new Callback<TransactionLimitResponse>() {
                @Override
                public void onResponse(Call<TransactionLimitResponse> call, Response<TransactionLimitResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            TransactionLimitResponse obj = response.body();
                            limitMutableLiveData.setValue(obj);
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

    public void meGiftCards() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<GiftCard> mCall = apiService.meGiftCards();
            mCall.enqueue(new Callback<GiftCard>() {
                @Override
                public void onResponse(Call<GiftCard> call, Response<GiftCard> response) {
                    try {
                        if (response.isSuccessful()) {
                            GiftCard obj = response.body();
                            giftCardMutableLiveData.setValue(obj);
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
                public void onFailure(Call<GiftCard> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meSignOn() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<SignOn> mCall = apiService.meSignOn();
            mCall.enqueue(new Callback<SignOn>() {
                @Override
                public void onResponse(Call<SignOn> call, Response<SignOn> response) {
                    try {
                        if (response.isSuccessful()) {
                            SignOn obj = response.body();
                            signOnMutableLiveData.setValue(obj);
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
                public void onFailure(Call<SignOn> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meSyncAccount() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<SyncAccount> mCall = apiService.meSyncAccount();
            mCall.enqueue(new Callback<SyncAccount>() {
                @Override
                public void onResponse(Call<SyncAccount> call, Response<SyncAccount> response) {
                    try {
                        if (response.isSuccessful()) {
                            SyncAccount obj = response.body();
                            syncAccountMutableLiveData.setValue(obj);
                        } else if (response.code() == 500) {
                            Gson gson = new Gson();
                            Type type = new TypeToken<SyncAccount>() {
                            }.getType();
                            SyncAccount errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            syncAccountMutableLiveData.setValue(errorResponse);
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
                public void onFailure(Call<SyncAccount> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void preAuthCards(PreAuthRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<PreAuthResponse> mCall = apiService.preAuthCards(request);
            mCall.enqueue(new Callback<PreAuthResponse>() {
                @Override
                public void onResponse(Call<PreAuthResponse> call, Response<PreAuthResponse> response) {
                    if (response.isSuccessful()) {
                        PreAuthResponse obj = response.body();
                        preAuthMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<PreAuthResponse>() {
                        }.getType();
                        PreAuthResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            preAuthMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<PreAuthResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getPublicKey(String email) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<PublicKeyResponse> mCall = apiService.getPublicKey(email);
            mCall.enqueue(new Callback<PublicKeyResponse>() {
                @Override
                public void onResponse(Call<PublicKeyResponse> call, Response<PublicKeyResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            PublicKeyResponse obj = response.body();
                            publicKeyLiveData.setValue(obj);
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
                public void onFailure(Call<PublicKeyResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
