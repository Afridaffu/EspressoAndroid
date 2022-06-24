package com.greenbox.coyni.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.check_out_transactions.OrderInfoRequest;
import com.greenbox.coyni.model.check_out_transactions.OrderInfoResponse;
import com.greenbox.coyni.model.check_out_transactions.OrderPayRequest;
import com.greenbox.coyni.model.check_out_transactions.OrderPayResponse;
import com.greenbox.coyni.model.check_out_transactions.ScanQRRequest;
import com.greenbox.coyni.model.check_out_transactions.ScanQrCodeResp;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckOutViewModel extends AndroidViewModel {

   private MutableLiveData<OrderInfoResponse> orderInfoResponseMutableLiveData = new MutableLiveData<>();
   private MutableLiveData<OrderPayResponse> orderPayResponseMutableLiveData = new MutableLiveData<>();
   private MutableLiveData<ScanQrCodeResp> scanQrCodeRespMutableLiveData = new MutableLiveData<>();

    public CheckOutViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<OrderInfoResponse> getOrderInfoResponseMutableLiveData() {
        return orderInfoResponseMutableLiveData;
    }

    public void setOrderInfoResponseMutableLiveData(MutableLiveData<OrderInfoResponse> orderInfoResponseMutableLiveData) {
        this.orderInfoResponseMutableLiveData = orderInfoResponseMutableLiveData;
    }

    public MutableLiveData<OrderPayResponse> getOrderPayResponseMutableLiveData() {
        return orderPayResponseMutableLiveData;
    }

    public void setOrderPayResponseMutableLiveData(MutableLiveData<OrderPayResponse> orderPayResponseMutableLiveData) {
        this.orderPayResponseMutableLiveData = orderPayResponseMutableLiveData;
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


    public void orderPay(OrderPayRequest request) {
        ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
        Call<OrderPayResponse> mCall = apiService.orderPay(request);
        mCall.enqueue(new Callback<OrderPayResponse>() {
            @Override
            public void onResponse(Call<OrderPayResponse> call, Response<OrderPayResponse> response) {
                if (response.isSuccessful()) {
                    OrderPayResponse orderPayResponse = response.body();
                    orderPayResponseMutableLiveData.setValue(orderPayResponse);
                } else {
                    Gson gson = new Gson();
                    Type type = new TypeToken<OrderPayResponse>() {
                    }.getType();
                    OrderPayResponse errorResponse = gson.fromJson(response.errorBody().charStream(),type);
                    if (errorResponse!= null){
                        orderPayResponseMutableLiveData.setValue(errorResponse);
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderPayResponse> call, Throwable t) {
                orderPayResponseMutableLiveData.setValue(null);
            }
        });
    }

    public void scanQRCode(ScanQRRequest string) {
        ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
        Call<ScanQrCodeResp> mCall = apiService.scanQrCode(string);
        mCall.enqueue(new Callback<ScanQrCodeResp>() {
            @Override
            public void onResponse(Call<ScanQrCodeResp> call, Response<ScanQrCodeResp> response) {
                if (response.isSuccessful()) {
                    ScanQrCodeResp scanQrCodeResp = response.body();
                    scanQrCodeRespMutableLiveData.setValue(scanQrCodeResp);
                } else {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ScanQrCodeResp>() {
                    }.getType();
                    ScanQrCodeResp errorResponse = gson.fromJson(response.errorBody().charStream(),type);
                    if (errorResponse!= null){
                        scanQrCodeRespMutableLiveData.setValue(errorResponse);
                    }
                }
            }

            @Override
            public void onFailure(Call<ScanQrCodeResp> call, Throwable t) {
                scanQrCodeRespMutableLiveData.setValue(null);
            }
        });
    }


}
