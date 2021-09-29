package com.coyni.android.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.coyni.android.model.APIError;
import com.coyni.android.model.receiverequests.ReceiveRequests;
import com.coyni.android.model.recentuser.RecentUsers;
import com.coyni.android.model.reguser.RegUsersResponse;
import com.coyni.android.model.reguser.RegisteredUsersRequest;
import com.coyni.android.model.sentrequests.SentRequests;
import com.coyni.android.model.templates.TemplateRequest;
import com.coyni.android.model.templates.TemplateResponse;
import com.coyni.android.model.userdetails.UserDetails;
import com.coyni.android.model.userrequest.UserReqPatchResponse;
import com.coyni.android.model.userrequest.UserRequest;
import com.coyni.android.model.userrequest.UserRequestResponse;
import com.coyni.android.network.ApiService;
import com.coyni.android.network.AuthApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.android.model.userrequest.UserRequestPatch;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.QueryMap;

public class PayViewModel extends AndroidViewModel {
    private MutableLiveData<RegUsersResponse> regUsersResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<RecentUsers> recentUsersMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UserDetails> userDetailsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TemplateResponse> templateResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UserRequestResponse> userRequestResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SentRequests> sentRequestsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ReceiveRequests> receiveRequestsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> postNoDataMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> receNoDataMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UserReqPatchResponse> userReqPatchResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMutableLiveData = new MutableLiveData<>();

    public PayViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<RegUsersResponse> getRegUsersResponseMutableLiveData() {
        return regUsersResponseMutableLiveData;
    }

    public MutableLiveData<RecentUsers> getRecentUsersMutableLiveData() {
        return recentUsersMutableLiveData;
    }

    public MutableLiveData<UserDetails> getUserDetailsMutableLiveData() {
        return userDetailsMutableLiveData;
    }

    public MutableLiveData<TemplateResponse> getTemplateResponseMutableLiveData() {
        return templateResponseMutableLiveData;
    }

    public MutableLiveData<UserRequestResponse> getUserRequestResponseMutableLiveData() {
        return userRequestResponseMutableLiveData;
    }

    public MutableLiveData<SentRequests> getSentRequestsMutableLiveData() {
        return sentRequestsMutableLiveData;
    }

    public MutableLiveData<ReceiveRequests> getReceiveRequestsMutableLiveData() {
        return receiveRequestsMutableLiveData;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<UserReqPatchResponse> getUserReqPatchResponseMutableLiveData() {
        return userReqPatchResponseMutableLiveData;
    }

    public MutableLiveData<APIError> getPostNoDataMutableLiveData() {
        return postNoDataMutableLiveData;
    }

    public MutableLiveData<APIError> getReceNoDataMutableLiveData() {
        return receNoDataMutableLiveData;
    }

    public MutableLiveData<String> getErrorMutableLiveData() {
        return errorMutableLiveData;
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
                public void onFailure(Call<RegUsersResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
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
                public void onFailure(Call<RecentUsers> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getUserDetails(String walletId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UserDetails> mCall = apiService.getUserDetails(walletId);
            mCall.enqueue(new Callback<UserDetails>() {
                @Override
                public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                    try {
                        if (response.isSuccessful()) {
                            UserDetails obj = response.body();
                            userDetailsMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<APIError>() {
                            }.getType();
                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
                            apiErrorMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        if (response.message().toLowerCase().contains("bad request")) {
                            errorMutableLiveData.setValue("Wallet data not found.");
                        } else {
                            apiErrorMutableLiveData.setValue(null);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UserDetails> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
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
                public void onFailure(Call<TemplateResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
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
                public void onFailure(Call<UserRequestResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getSentRequests() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<SentRequests> mCall = apiService.getSentRequests();
            mCall.enqueue(new Callback<SentRequests>() {
                @Override
                public void onResponse(Call<SentRequests> call, Response<SentRequests> response) {
                    try {
                        if (response.isSuccessful()) {
                            SentRequests obj = response.body();
                            sentRequestsMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<APIError>() {
                            }.getType();
                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
                            postNoDataMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<SentRequests> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getReceiveRequests() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ReceiveRequests> mCall = apiService.getReceiveRequests();
            mCall.enqueue(new Callback<ReceiveRequests>() {
                @Override
                public void onResponse(Call<ReceiveRequests> call, Response<ReceiveRequests> response) {
                    try {
                        if (response.isSuccessful()) {
                            ReceiveRequests obj = response.body();
                            receiveRequestsMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<APIError>() {
                            }.getType();
                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
                            receNoDataMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<ReceiveRequests> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateUserRequests(UserRequestPatch request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UserReqPatchResponse> mCall = apiService.updateUserRequests(request);
            mCall.enqueue(new Callback<UserReqPatchResponse>() {
                @Override
                public void onResponse(Call<UserReqPatchResponse> call, Response<UserReqPatchResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            UserReqPatchResponse obj = response.body();
                            userReqPatchResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<UserReqPatchResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
