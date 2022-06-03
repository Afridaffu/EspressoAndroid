package com.greenbox.coyni.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.check_out_transactions.OrderInfoRequest;
import com.greenbox.coyni.model.check_out_transactions.OrderInfoResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutViewModel extends AndroidViewModel {

   private MutableLiveData<OrderInfoResponse> orderInfoResponseMutableLiveData = new MutableLiveData<>();

    public CheckOutViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<OrderInfoResponse> getOrderInfoResponseMutableLiveData() {
        return orderInfoResponseMutableLiveData;
    }

    public void setOrderInfoResponseMutableLiveData(MutableLiveData<OrderInfoResponse> orderInfoResponseMutableLiveData) {
        this.orderInfoResponseMutableLiveData = orderInfoResponseMutableLiveData;
    }

    public void getOrderInfo(OrderInfoRequest request) {
        ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
        Call<OrderInfoResponse> mCall = apiService.getOrderInfoDetails(request);
        mCall.enqueue(new Callback<OrderInfoResponse>() {
            @Override
            public void onResponse(Call<OrderInfoResponse> call, Response<OrderInfoResponse> response) {
                if (response.isSuccessful()) {
                    OrderInfoResponse orderInfoResponse = response.body();
                    orderInfoResponseMutableLiveData.setValue(orderInfoResponse);
                } else {
                    Gson gson = new Gson();
                    Type type = new TypeToken<OrderInfoResponse>() {
                    }.getType();
                    OrderInfoResponse errorResponse = gson.fromJson(response.errorBody().charStream(),type);
                    if (errorResponse!= null){
                        orderInfoResponseMutableLiveData.setValue(errorResponse);
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderInfoResponse> call, Throwable t) {
                orderInfoResponseMutableLiveData.setValue(null);
            }
        });
    }
}
