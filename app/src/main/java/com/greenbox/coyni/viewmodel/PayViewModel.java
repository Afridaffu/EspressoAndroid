package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.coyniusers.CoyniUsers;
import com.greenbox.coyni.model.paidorder.PaidOrderRequest;
import com.greenbox.coyni.model.paidorder.PaidOrderResp;
import com.greenbox.coyni.model.payrequest.PayRequestResponse;
import com.greenbox.coyni.model.payrequest.TransferPayRequest;
import com.greenbox.coyni.model.recentusers.RecentUsers;
import com.greenbox.coyni.model.reguser.RegUsersResponse;
import com.greenbox.coyni.model.reguser.RegisteredUsersRequest;
import com.greenbox.coyni.model.templates.TemplateRequest;
import com.greenbox.coyni.model.templates.TemplateResponse;
import com.greenbox.coyni.model.userrequest.UserRequest;
import com.greenbox.coyni.model.userrequest.UserRequestResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;
import com.greenbox.coyni.utils.MyApplication;
import com.greenbox.coyni.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayViewModel extends AndroidViewModel {
    private MutableLiveData<RegUsersResponse> regUsersResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<RecentUsers> recentUsersMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CoyniUsers> coyniUsersMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TemplateResponse> templateResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PayRequestResponse> payRequestResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UserRequestResponse> userRequestResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PaidOrderResp> paidOrderRespMutableLiveData = new MutableLiveData<>();
    private MyApplication myApplication;

    public PayViewModel(@NonNull Application application) {
        super(application);
        myApplication = (MyApplication) application;
    }

    public MutableLiveData<PaidOrderResp> getPaidOrderRespMutableLiveData() {
        return paidOrderRespMutableLiveData;
    }

    public MutableLiveData<RegUsersResponse> getRegUsersResponseMutableLiveData() {
        return regUsersResponseMutableLiveData;
    }

    public MutableLiveData<RecentUsers> getRecentUsersMutableLiveData() {
        return recentUsersMutableLiveData;
    }

    public MutableLiveData<CoyniUsers> getCoyniUsersMutableLiveData() {
        return coyniUsersMutableLiveData;
    }

    public MutableLiveData<TemplateResponse> getTemplateResponseMutableLiveData() {
        return templateResponseMutableLiveData;
    }

    public MutableLiveData<PayRequestResponse> getPayRequestResponseMutableLiveData() {
        return payRequestResponseMutableLiveData;
    }

    public MutableLiveData<UserRequestResponse> getUserRequestResponseMutableLiveData() {
        return userRequestResponseMutableLiveData;
    }

    public void registeredUsers(List<RegisteredUsersRequest> request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<RegUsersResponse> mCall = apiService.registeredUsers(request);
            mCall.enqueue(new Callback<RegUsersResponse>() {
                @Override
                public void onResponse(Call<RegUsersResponse> call, Response<RegUsersResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            RegUsersResponse obj = response.body();
                            regUsersResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<RegUsersResponse>() {
                            }.getType();
                            RegUsersResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            regUsersResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        regUsersResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<RegUsersResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    regUsersResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void recentUsers() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<RecentUsers> mCall = apiService.recentUsers();
            mCall.enqueue(new Callback<RecentUsers>() {
                @Override
                public void onResponse(Call<RecentUsers> call, Response<RecentUsers> response) {
                    try {
                        if (response.isSuccessful()) {
                            RecentUsers obj = response.body();
                            recentUsersMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<RecentUsers>() {
                            }.getType();
                            RecentUsers errorResponse = gson.fromJson(response.errorBody().string(), type);
                            recentUsersMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        recentUsersMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<RecentUsers> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    recentUsersMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getCoyniUsers(String searchKey) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CoyniUsers> mCall = apiService.getCoyniUsers(searchKey);
            mCall.enqueue(new Callback<CoyniUsers>() {
                @Override
                public void onResponse(Call<CoyniUsers> call, Response<CoyniUsers> response) {
                    try {
                        if (response.isSuccessful()) {
                            CoyniUsers obj = response.body();
                            coyniUsersMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CoyniUsers>() {
                            }.getType();
                            CoyniUsers errorResponse = gson.fromJson(response.errorBody().string(), type);
                            coyniUsersMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        coyniUsersMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<CoyniUsers> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    coyniUsersMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getTemplate(int templateId, TemplateRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TemplateResponse> mCall = apiService.getTemplate(templateId, request);
            mCall.enqueue(new Callback<TemplateResponse>() {
                @Override
                public void onResponse(Call<TemplateResponse> call, Response<TemplateResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            TemplateResponse obj = response.body();
                            templateResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<TemplateResponse>() {
                            }.getType();
                            TemplateResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            templateResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        templateResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<TemplateResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    templateResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void sendTokens(TransferPayRequest request, String token) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<PayRequestResponse> mCall = apiService.sendTokens(request, token);
            mCall.enqueue(new Callback<PayRequestResponse>() {
                @Override
                public void onResponse(Call<PayRequestResponse> call, Response<PayRequestResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            PayRequestResponse obj = response.body();
                            payRequestResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<PayRequestResponse>() {
                            }.getType();
                            PayRequestResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            payRequestResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<PayRequestResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    payRequestResponseMutableLiveData.setValue(null);
//                    Utils.setStrToken("");
                    myApplication.clearStrToken();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void userRequests(UserRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UserRequestResponse> mCall = apiService.userRequests(request);
            mCall.enqueue(new Callback<UserRequestResponse>() {
                @Override
                public void onResponse(Call<UserRequestResponse> call, Response<UserRequestResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            UserRequestResponse obj = response.body();
                            userRequestResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<UserRequestResponse>() {
                            }.getType();
                            UserRequestResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            userRequestResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        userRequestResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<UserRequestResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    userRequestResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void paidOrder(PaidOrderRequest paidOrderRequest) {
        ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
        Call<PaidOrderResp> mCall = apiService.paidOrder(paidOrderRequest);
        mCall.enqueue(new Callback<PaidOrderResp>() {
            @Override
            public void onResponse(Call<PaidOrderResp> call, Response<PaidOrderResp> response) {
                try {
                    if (response.isSuccessful()) {
                        PaidOrderResp obj = response.body();
                        paidOrderRespMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<PaidOrderResp>() {
                        }.getType();

                        PaidOrderResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                        paidOrderRespMutableLiveData.setValue(errorResponse);
                    }
                } catch (JsonSyntaxException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<PaidOrderResp> call, Throwable t) {

                Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                paidOrderRespMutableLiveData.setValue(null);
//                Utils.setStrToken("");
                myApplication.clearStrToken();
            }
        });
    }

}
