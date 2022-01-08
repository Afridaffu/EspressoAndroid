package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.giftcard.BrandsResponse;
import com.greenbox.coyni.model.notification.Notifications;
import com.greenbox.coyni.model.notification.StatusRequest;
import com.greenbox.coyni.model.notification.UnReadDelResponse;
import com.greenbox.coyni.model.userrequest.UserRequestResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;
import com.greenbox.coyni.view.NotificationsActivity;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsViewModel extends AndroidViewModel {
    public NotificationsViewModel(@NonNull Application application) {
        super(application);
    }

    private final MutableLiveData<Notifications> notificationsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Notifications> receivedNotificationsMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Notifications> sentNotificationsMutableLiveData = new MutableLiveData<>();

    private final MutableLiveData<UnReadDelResponse> markReadResponse = new MutableLiveData<>();
    private final MutableLiveData<UnReadDelResponse> markUnReadResponse = new MutableLiveData<>();
    private final MutableLiveData<UnReadDelResponse> deleteNotifResponse = new MutableLiveData<>();

    private final MutableLiveData<UserRequestResponse> notificationStatusUpdateResponse = new MutableLiveData<>();


    public MutableLiveData<UserRequestResponse> getNotificationStatusUpdateResponse() {
        return notificationStatusUpdateResponse;
    }

    public MutableLiveData<UnReadDelResponse> getDeleteNotifResponse() {
        return deleteNotifResponse;
    }

    public MutableLiveData<UnReadDelResponse> getMarkUnReadResponse() {
        return markUnReadResponse;
    }

    public MutableLiveData<UnReadDelResponse> getMarkReadResponse() {
        return markReadResponse;
    }

    public MutableLiveData<Notifications> getNotificationsMutableLiveData() {
        return notificationsMutableLiveData;
    }

    public MutableLiveData<Notifications> getReceivedNotificationsMutableLiveData() {
        return receivedNotificationsMutableLiveData;
    }

    public MutableLiveData<Notifications> getSentNotificationsMutableLiveData() {
        return sentNotificationsMutableLiveData;
    }

    public void getNotifications() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Notifications> mCall = apiService.getNotifications();
            mCall.enqueue(new Callback<Notifications>() {
                @Override
                public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                    try {
                        if (response.isSuccessful()) {
                            Notifications obj = response.body();
                            notificationsMutableLiveData.setValue(obj);
//                            new Thread(() -> NotificationsActivity.notificationsActivity.notificationsViewModel.getReceivedNotifications()).start();
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<Notifications>() {
                            }.getType();
                            Notifications errorResponse = gson.fromJson(response.errorBody().string(), type);
                            notificationsMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        notificationsMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<Notifications> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    notificationsMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getReceivedNotifications() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Notifications> mCall = apiService.getReceivedNotifications();
            mCall.enqueue(new Callback<Notifications>() {
                @Override
                public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                    try {
                        if (response.isSuccessful()) {
                            Notifications obj = response.body();
                            receivedNotificationsMutableLiveData.setValue(obj);
//                            new Thread(() -> NotificationsActivity.notificationsActivity.notificationsViewModel.getSentNotifications()).start();

                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<Notifications>() {
                            }.getType();
                            Notifications errorResponse = gson.fromJson(response.errorBody().string(), type);
                            receivedNotificationsMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        receivedNotificationsMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<Notifications> call, Throwable t) {
                    try {
                        Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                        receivedNotificationsMutableLiveData.setValue(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getSentNotifications() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Notifications> mCall = apiService.getSentNotifications();
            mCall.enqueue(new Callback<Notifications>() {
                @Override
                public void onResponse(Call<Notifications> call, Response<Notifications> response) {
                    try {
                        if (response.isSuccessful()) {
                            Notifications obj = response.body();
                            sentNotificationsMutableLiveData.setValue(obj);

                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<Notifications>() {
                            }.getType();
                            Notifications errorResponse = gson.fromJson(response.errorBody().string(), type);
                            sentNotificationsMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        sentNotificationsMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<Notifications> call, Throwable t) {
                    try {
                        sentNotificationsMutableLiveData.setValue(null);
                        Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setReadNotification(List<Integer> list) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UnReadDelResponse> mCall = apiService.notificationsMarkRead(list);
            mCall.enqueue(new Callback<UnReadDelResponse>() {
                @Override
                public void onResponse(Call<UnReadDelResponse> call, Response<UnReadDelResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            UnReadDelResponse obj = response.body();
                            markReadResponse.setValue(obj);

                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<UnReadDelResponse>() {
                            }.getType();
                            UnReadDelResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            markReadResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        markReadResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<UnReadDelResponse> call, Throwable t) {
                    try {
                        markReadResponse.setValue(null);
                        Toast.makeText(NotificationsActivity.notificationsActivity, "something went wrong", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setUnReadNotification(List<Integer> list) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UnReadDelResponse> mCall = apiService.notificationsMarkUnRead(list);
            mCall.enqueue(new Callback<UnReadDelResponse>() {
                @Override
                public void onResponse(Call<UnReadDelResponse> call, Response<UnReadDelResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            UnReadDelResponse obj = response.body();
                            markUnReadResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<UnReadDelResponse>() {
                            }.getType();
                            UnReadDelResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            markUnReadResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        markUnReadResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<UnReadDelResponse> call, Throwable t) {
                    try {
                        markUnReadResponse.setValue(null);
                        Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setDeleteNotification(List<Integer> list) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UnReadDelResponse> mCall = apiService.notificationDelete(list);
            mCall.enqueue(new Callback<UnReadDelResponse>() {
                @Override
                public void onResponse(Call<UnReadDelResponse> call, Response<UnReadDelResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            UnReadDelResponse obj = response.body();
                            deleteNotifResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<UnReadDelResponse>() {
                            }.getType();
                            UnReadDelResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            deleteNotifResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        deleteNotifResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<UnReadDelResponse> call, Throwable t) {
                    try {
                        deleteNotifResponse.setValue(null);
                        Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void notificationStatusUpdate(StatusRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UserRequestResponse> mCall = apiService.updateUserRequests(request);
            mCall.enqueue(new Callback<UserRequestResponse>() {
                @Override
                public void onResponse(Call<UserRequestResponse> call, Response<UserRequestResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            UserRequestResponse obj = response.body();
                            notificationStatusUpdateResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<UserRequestResponse>() {
                            }.getType();
                            UserRequestResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            notificationStatusUpdateResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        notificationStatusUpdateResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<UserRequestResponse> call, Throwable t) {
                    try {
                        notificationStatusUpdateResponse.setValue(null);
                        Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
