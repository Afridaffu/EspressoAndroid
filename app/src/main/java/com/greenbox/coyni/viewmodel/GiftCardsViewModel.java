package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.giftcard.BrandsResponse;
import com.greenbox.coyni.model.transferfee.TransferFeeRequest;
import com.greenbox.coyni.model.transferfee.TransferFeeResponse;
import com.greenbox.coyni.model.users.AccountLimits;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GiftCardsViewModel extends AndroidViewModel {
    public GiftCardsViewModel(@NonNull Application application) {
        super(application);
    }
    private MutableLiveData<BrandsResponse> giftCardsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BrandsResponse> giftCardDetailsMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<BrandsResponse> getGiftCardsMutableLiveData() {
        return giftCardsMutableLiveData;
    }

    public MutableLiveData<BrandsResponse> getGiftCardDetailsMutableLiveData() {
        return giftCardDetailsMutableLiveData;
    }

    public void getGiftCards() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BrandsResponse> mCall = apiService.getGiftCards();
            mCall.enqueue(new Callback<BrandsResponse>() {
                @Override
                public void onResponse(Call<BrandsResponse> call, Response<BrandsResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            BrandsResponse obj = response.body();
                            giftCardsMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BrandsResponse>() {}.getType();
                            BrandsResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            giftCardsMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        giftCardsMutableLiveData.setValue(null);
                    }
                }
                @Override
                public void onFailure(Call<BrandsResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    giftCardsMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getGiftCardDetails(String brandKey) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BrandsResponse> mCall = apiService.getGiftCardItems(brandKey);
            mCall.enqueue(new Callback<BrandsResponse>() {
                @Override
                public void onResponse(Call<BrandsResponse> call, Response<BrandsResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            BrandsResponse obj = response.body();
                            giftCardDetailsMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BrandsResponse>() {}.getType();
                            BrandsResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            giftCardDetailsMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        giftCardDetailsMutableLiveData.setValue(null);
                    }
                }
                @Override
                public void onFailure(Call<BrandsResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    giftCardDetailsMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
