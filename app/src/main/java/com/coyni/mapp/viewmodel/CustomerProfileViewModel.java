package com.coyni.mapp.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.coyni.mapp.model.websocket.WebSocketUrlResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.mapp.model.APIError;
import com.coyni.mapp.model.bank.BankResponse;
import com.coyni.mapp.model.bank.SignOn;
import com.coyni.mapp.model.bank.SyncAccount;
import com.coyni.mapp.model.preferences.UserPreference;
import com.coyni.mapp.model.profile.updateemail.UpdateEmailRequest;
import com.coyni.mapp.model.profile.updateemail.UpdateEmailResponse;
import com.coyni.mapp.model.profile.updatephone.UpdatePhoneRequest;
import com.coyni.mapp.model.profile.updatephone.UpdatePhoneResponse;
import com.coyni.mapp.model.users.User;
import com.coyni.mapp.model.users.UserData;
import com.coyni.mapp.model.users.UserPreferenceModel;
import com.coyni.mapp.network.ApiService;
import com.coyni.mapp.network.AuthApiClient;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerProfileViewModel extends AndroidViewModel {
    private MutableLiveData<UpdateEmailResponse> updateEmailSendOTPResponse = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UpdatePhoneResponse> updatePhoneSendOTPResponse = new MutableLiveData<>();
    private MutableLiveData<UserPreference> userPreferenceMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BankResponse> bankResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<WebSocketUrlResponse> webSocketUrlResponseMutableLiveData = new MutableLiveData<>();

    public CustomerProfileViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<User> getUserMutableLiveData() {
        return userMutableLiveData;
    }

//    private MutableLiveData<SignOn> signOnMutableLiveData = new MutableLiveData<>();
//    private MutableLiveData<SyncAccount> syncAccountMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<UpdateEmailResponse> getUpdateEmailSendOTPResponse() {
        return updateEmailSendOTPResponse;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<UpdatePhoneResponse> getUpdatePhoneSendOTPResponse() {
        return updatePhoneSendOTPResponse;
    }

    public MutableLiveData<UserPreference> getUserPreferenceMutableLiveData() {
        return userPreferenceMutableLiveData;
    }

//    public MutableLiveData<SignOn> getSignOnMutableLiveData() {
//        return signOnMutableLiveData;
//    }
//
//    public MutableLiveData<SyncAccount> getSyncAccountMutableLiveData() {
//        return syncAccountMutableLiveData;
//    }

    public MutableLiveData<BankResponse> getBankResponseMutableLiveData() {
        return bankResponseMutableLiveData;
    }

    public MutableLiveData<WebSocketUrlResponse> getWebSocketUrlResponseMutableLiveData() {
        return webSocketUrlResponseMutableLiveData;
    }

    public void updateEmailSendOTP(UpdateEmailRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UpdateEmailResponse> mCall = apiService.updateEmailSendOTP(request);
            mCall.enqueue(new Callback<UpdateEmailResponse>() {
                @Override
                public void onResponse(Call<UpdateEmailResponse> call, Response<UpdateEmailResponse> response) {
                    if (response.isSuccessful()) {
                        UpdateEmailResponse obj = response.body();
                        updateEmailSendOTPResponse.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<UpdateEmailResponse>() {
                        }.getType();
                        UpdateEmailResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            updateEmailSendOTPResponse.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UpdateEmailResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updatePhoneSendOTP(UpdatePhoneRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UpdatePhoneResponse> mCall = apiService.updatePhoneSendOTP(request);
            mCall.enqueue(new Callback<UpdatePhoneResponse>() {
                @Override
                public void onResponse(Call<UpdatePhoneResponse> call, Response<UpdatePhoneResponse> response) {
                    if (response.isSuccessful()) {
                        UpdatePhoneResponse obj = response.body();
                        updatePhoneSendOTPResponse.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<UpdatePhoneResponse>() {
                        }.getType();
                        UpdatePhoneResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            updatePhoneSendOTPResponse.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<UpdatePhoneResponse> call, Throwable t) {
//                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updatePreferences(UserPreferenceModel request, int accountType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UserPreference> mCall = null;
            if (accountType == Utils.SHARED_ACCOUNT)
                mCall = apiService.meUpdatePreferences_Shared(request);
            else
                mCall = apiService.meUpdatePreferences(request);
            if (mCall != null) {
                mCall.enqueue(new Callback<UserPreference>() {
                    @Override
                    public void onResponse(Call<UserPreference> call, Response<UserPreference> response) {
                        try {
                            if (response.isSuccessful()) {
                                UserPreference obj = response.body();
                                userPreferenceMutableLiveData.setValue(obj);
                            } else {
                                Gson gson = new Gson();
                                Type type = new TypeToken<UserPreference>() {
                                }.getType();
                                UserPreference errorResponse = gson.fromJson(response.errorBody().string(), type);
                                userPreferenceMutableLiveData.setValue(errorResponse);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            apiErrorMutableLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserPreference> call, Throwable t) {
                        Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                        apiErrorMutableLiveData.setValue(null);
                    }
                });
            } else {
//                Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateTimeZoneShared(UserPreferenceModel request,int userID) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UserPreference> mCall = apiService.updateTimeZoneShared(String.valueOf(userID), request);

            if (mCall != null) {
                mCall.enqueue(new Callback<UserPreference>() {
                    @Override
                    public void onResponse(Call<UserPreference> call, Response<UserPreference> response) {
                        try {
                            if (response.isSuccessful()) {
                                UserPreference obj = response.body();
                                userPreferenceMutableLiveData.setValue(obj);
                            } else {
                                Gson gson = new Gson();
                                Type type = new TypeToken<UserPreference>() {
                                }.getType();
                                UserPreference errorResponse = gson.fromJson(response.errorBody().string(), type);
                                userPreferenceMutableLiveData.setValue(errorResponse);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            apiErrorMutableLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserPreference> call, Throwable t) {
                        Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                        apiErrorMutableLiveData.setValue(null);
                    }
                });
            } else {
//                Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void updateDefaultAccount(UserPreferenceModel request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UserPreference> mCall = null;

            mCall = apiService.meUpdatePreferences(request);
            if (mCall != null) {
                mCall.enqueue(new Callback<UserPreference>() {
                    @Override
                    public void onResponse(Call<UserPreference> call, Response<UserPreference> response) {
                        try {
                            if (response.isSuccessful()) {
                                UserPreference obj = response.body();
                                userPreferenceMutableLiveData.setValue(obj);
                            } else {
                                Gson gson = new Gson();
                                Type type = new TypeToken<UserPreference>() {
                                }.getType();
                                UserPreference errorResponse = gson.fromJson(response.errorBody().string(), type);
                                userPreferenceMutableLiveData.setValue(errorResponse);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            apiErrorMutableLiveData.setValue(null);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserPreference> call, Throwable t) {
                        Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                        apiErrorMutableLiveData.setValue(null);
                    }
                });
            } else {
//                Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meUpdateAddress(UserData request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<User> mCall = apiService.meUpdateAddress(request);
            mCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    try {
                        if (response.isSuccessful()) {
                            User obj = response.body();
                            userMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<User>() {
                            }.getType();
                            User errorResponse = gson.fromJson(response.errorBody().string(), type);
                            userMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

//    public void meSignOn() {
//        try {
//            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
//            Call<SignOn> mCall = apiService.meSignOn();
//            mCall.enqueue(new Callback<SignOn>() {
//                @Override
//                public void onResponse(Call<SignOn> call, Response<SignOn> response) {
//                    try {
//                        if (response.isSuccessful()) {
//                            SignOn obj = response.body();
//                            signOnMutableLiveData.setValue(obj);
//                        } else {
//                            String strResponse = response.errorBody().string();
////                            Gson gson = new Gson();
////                            Type type = new TypeToken<APIError>() {
////                            }.getType();
////                            APIError errorResponse = gson.fromJson(strResponse, type);
////                            apiErrorMutableLiveData.setValue(errorResponse);
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<SignOn>() {
//                            }.getType();
//                            SignOn errorResponse1 = gson.fromJson(strResponse, type);
//                            signOnMutableLiveData.setValue(errorResponse1);
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        apiErrorMutableLiveData.setValue(null);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<SignOn> call, Throwable t) {
//                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
//                    apiErrorMutableLiveData.setValue(null);
//                }
//            });
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

//    public void meSyncAccount() {
//        try {
//            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
//            Call<SyncAccount> mCall = apiService.meSyncAccount();
//            mCall.enqueue(new Callback<SyncAccount>() {
//                @Override
//                public void onResponse(Call<SyncAccount> call, Response<SyncAccount> response) {
//                    try {
//                        if (response.isSuccessful()) {
//                            SyncAccount obj = response.body();
//                            syncAccountMutableLiveData.setValue(obj);
//                        } else if (response.code() == 500) {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<SyncAccount>() {
//                            }.getType();
//                            SyncAccount errorResponse = gson.fromJson(response.errorBody().charStream(), type);
//                            syncAccountMutableLiveData.setValue(errorResponse);
//                        } else {
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<APIError>() {
//                            }.getType();
//                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
//                            apiErrorMutableLiveData.setValue(errorResponse);
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        apiErrorMutableLiveData.setValue(null);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<SyncAccount> call, Throwable t) {
//                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
//                    apiErrorMutableLiveData.setValue(null);
//                }
//            });
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public void meBanks() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BankResponse> mCall = apiService.meBanks();
            mCall.enqueue(new Callback<BankResponse>() {
                @Override
                public void onResponse(Call<BankResponse> call, Response<BankResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            BankResponse obj = response.body();
                            bankResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BankResponse>() {
                            }.getType();
                            BankResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            bankResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        bankResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<BankResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    bankResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void webSocketUrl() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<WebSocketUrlResponse> mCall = apiService.webSocketUrl();
            mCall.enqueue(new Callback<WebSocketUrlResponse>() {
                @Override
                public void onResponse(Call<WebSocketUrlResponse> call, Response<WebSocketUrlResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            WebSocketUrlResponse obj = response.body();
                            webSocketUrlResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<WebSocketUrlResponse>() {
                            }.getType();
                            WebSocketUrlResponse errorResponse = null;
                            try {
                                errorResponse = gson.fromJson(response.errorBody().string(), type);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            webSocketUrlResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        webSocketUrlResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<WebSocketUrlResponse> call, Throwable t) {
                    webSocketUrlResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void webSocketUrl() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<WebSocketUrlResponse> mCall = apiService.webSocketUrl();
            mCall.enqueue(new Callback<WebSocketUrlResponse>() {
                @Override
                public void onResponse(Call<WebSocketUrlResponse> call, Response<WebSocketUrlResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            WebSocketUrlResponse obj = response.body();
                            webSocketUrlResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<WebSocketUrlResponse>() {
                            }.getType();
                            WebSocketUrlResponse errorResponse = null;
                            try {
                                errorResponse = gson.fromJson(response.errorBody().string(), type);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            webSocketUrlResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        webSocketUrlResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<WebSocketUrlResponse> call, Throwable t) {
                    webSocketUrlResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
