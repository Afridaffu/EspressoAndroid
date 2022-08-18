package com.coyni.mapp.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.mapp.model.bank.BanksResponseModel;
import com.coyni.mapp.network.ApiService;
import com.coyni.mapp.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BankAccountsViewModel extends AndroidViewModel {
    public BankAccountsViewModel(@NonNull Application application) {
        super(application);
    }
    private MutableLiveData<BanksResponseModel> bankAccountsMutableLiveData = new MutableLiveData<>();


    public MutableLiveData<BanksResponseModel> getBankAccountsMutableLiveData() {
        return bankAccountsMutableLiveData;
    }


    public void getBankAccountsData() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BanksResponseModel> mCall = apiService.getBankAccountsData();
            mCall.enqueue(new Callback<BanksResponseModel>() {
                @Override
                public void onResponse(Call<BanksResponseModel> call, Response<BanksResponseModel> response) {
                    try {
                        if (response.isSuccessful()) {
                            BanksResponseModel obj = response.body();

                            bankAccountsMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BanksResponseModel>() {}.getType();
                            BanksResponseModel errorResponse = gson.fromJson(response.errorBody().string(), type);
                            bankAccountsMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        bankAccountsMutableLiveData.setValue(null);
                    }
                }
                @Override
                public void onFailure(Call<BanksResponseModel> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    bankAccountsMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
