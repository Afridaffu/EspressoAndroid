package com.coyni.android.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.android.model.APIError;
import com.coyni.android.model.notification.Notifications;
import com.coyni.android.network.ApiService;
import com.coyni.android.network.AuthApiClient;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsViewModel extends AndroidViewModel {
    private MutableLiveData<Notifications> notificationsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> notiMarkReadMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();

    public NotificationsViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<Notifications> getNotificationsMutableLiveData() {
        return notificationsMutableLiveData;
    }

    public MutableLiveData<APIError> getNotiMarkReadMutableLiveData() {
        return notiMarkReadMutableLiveData;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public void meNotifications() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Notifications> mCall = apiService.meNotifications();
            mCall.enqueue(new Callback<Notifications>() {
                @Override
                public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                    try {
                        if (response.isSuccessful()) {
                            Notifications obj = response.body();
                            notificationsMutableLiveData.setValue(obj);
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
                public void onFailure(Call<Notifications> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void notificationsMarkRead(List<Integer> list) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<APIError> mCall = apiService.notificationsMarkRead(list);
            mCall.enqueue(new Callback<APIError>() {
                @Override
                public void onResponse(Call<APIError> call, Response<APIError> response) {
                    try {
                        if (response.isSuccessful()) {
                            APIError obj = response.body();
                            notiMarkReadMutableLiveData.setValue(obj);
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
                public void onFailure(Call<APIError> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void notificationsMarkClear(List<Integer> list) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<APIError> mCall = apiService.notificationsMarkClear(list);
            mCall.enqueue(new Callback<APIError>() {
                @Override
                public void onResponse(Call<APIError> call, Response<APIError> response) {
                    try {
                        if (response.isSuccessful()) {
                            APIError obj = response.body();
                            notiMarkReadMutableLiveData.setValue(obj);
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
                public void onFailure(Call<APIError> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void notificationsClearAll() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<APIError> mCall = apiService.notificationsClearAll();
            mCall.enqueue(new Callback<APIError>() {
                @Override
                public void onResponse(Call<APIError> call, Response<APIError> response) {
                    try {
                        if (response.isSuccessful()) {
                            APIError obj = response.body();
                            notiMarkReadMutableLiveData.setValue(obj);
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
                public void onFailure(Call<APIError> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
