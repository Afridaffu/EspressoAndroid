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
import com.greenbox.coyni.model.biometric.BiometricRequest;
import com.greenbox.coyni.model.biometric.BiometricResponse;
import com.greenbox.coyni.model.coynipin.PINRegisterResponse;
import com.greenbox.coyni.model.coynipin.RegisterRequest;
import com.greenbox.coyni.model.coynipin.ValidateRequest;
import com.greenbox.coyni.model.coynipin.ValidateResponse;
import com.greenbox.coyni.model.identity_verification.IdentityAddressRequest;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.identity_verification.RemoveIdentityResponse;
import com.greenbox.coyni.model.profile.ImageResponse;
import com.greenbox.coyni.model.profile.TrackerResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IdentityVerificationViewModel extends AndroidViewModel {
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<IdentityImageResponse> uploadIdentityImageResponse = new MutableLiveData<>();
    private MutableLiveData<RemoveIdentityResponse> removeIdentityImageResponse = new MutableLiveData<>();
    private MutableLiveData<ImageResponse> uploadIdentityAddressResponse = new MutableLiveData<>();
    private MutableLiveData<TrackerResponse> getStatusTracker = new MutableLiveData<>();

    public IdentityVerificationViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<TrackerResponse> getGetStatusTracker() {
        return getStatusTracker;
    }

    public MutableLiveData<ImageResponse> getUploadIdentityAddressResponse() {
        return uploadIdentityAddressResponse;
    }

    public MutableLiveData<IdentityImageResponse> getUploadIdentityImageResponse() {
        return uploadIdentityImageResponse;
    }

    public MutableLiveData<RemoveIdentityResponse> getRemoveIdentityImageResponse() {
        return removeIdentityImageResponse;
    }

    public void uploadIdentityImage(MultipartBody.Part idFile, RequestBody idType, RequestBody idNumber) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<IdentityImageResponse> mCall = apiService.uploadIdentityImage(idFile,idType,idNumber);
            mCall.enqueue(new Callback<IdentityImageResponse>() {
                @Override
                public void onResponse(Call<IdentityImageResponse> call, Response<IdentityImageResponse> response) {
                    if (response.isSuccessful()) {
                        IdentityImageResponse obj = response.body();
                        uploadIdentityImageResponse.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<IdentityImageResponse>() {
                        }.getType();
                        IdentityImageResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            uploadIdentityImageResponse.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<IdentityImageResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeIdentityImage(String identityType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<RemoveIdentityResponse> mCall = apiService.removeIdentityImage(identityType);
            mCall.enqueue(new Callback<RemoveIdentityResponse>() {
                @Override
                public void onResponse(Call<RemoveIdentityResponse> call, Response<RemoveIdentityResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            RemoveIdentityResponse obj = response.body();
                            removeIdentityImageResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<RemoveIdentityResponse>() {
                            }.getType();
                            RemoveIdentityResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            removeIdentityImageResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        removeIdentityImageResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<RemoveIdentityResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void uploadIdentityAddress(IdentityAddressRequest identityAddressRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ImageResponse> mCall = apiService.uploadIdentityAddress(identityAddressRequest);
            mCall.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            ImageResponse obj = response.body();
                            uploadIdentityAddressResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<ImageResponse>() {
                            }.getType();
                            ImageResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            uploadIdentityAddressResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        uploadIdentityAddressResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getStatusTracker() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TrackerResponse> mCall = apiService.statusTracker();
            mCall.enqueue(new Callback<TrackerResponse>() {
                @Override
                public void onResponse(Call<TrackerResponse> call, Response<TrackerResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            TrackerResponse obj = response.body();
                            getStatusTracker.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<TrackerResponse>() {
                            }.getType();
                            TrackerResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            getStatusTracker.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getStatusTracker.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<TrackerResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
