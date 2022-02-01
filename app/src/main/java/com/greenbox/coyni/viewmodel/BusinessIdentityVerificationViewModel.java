package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoRequest;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.model.identity_verification.GetIdentityResponse;
import com.greenbox.coyni.model.identity_verification.IdentityAddressRequest;
import com.greenbox.coyni.model.identity_verification.IdentityAddressResponse;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.identity_verification.RemoveIdentityResponse;
import com.greenbox.coyni.model.profile.TrackerResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessIdentityVerificationViewModel extends AndroidViewModel {

    private MutableLiveData<IdentityImageResponse> uploadIdentityImageResponse = new MutableLiveData<>();
    private MutableLiveData<RemoveIdentityResponse> removeIdentityImageResponse = new MutableLiveData<>();
    private MutableLiveData<BusinessTrackerResponse> getBusinessTrackerResponse = new MutableLiveData<>();

    private MutableLiveData<BusinessTrackerResponse> getCompanyInfoResponse = new MutableLiveData<>();
    private MutableLiveData<BusinessTrackerResponse> updateBasicCompanyInfoResponse = new MutableLiveData<>();

    public BusinessIdentityVerificationViewModel(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<BusinessTrackerResponse> getGetCompanyInfoResponse() {
        return getCompanyInfoResponse;
    }

    public MutableLiveData<BusinessTrackerResponse> getUpdateBasicCompanyInfoResponse() {
        return updateBasicCompanyInfoResponse;
    }

    public MutableLiveData<BusinessTrackerResponse> getGetBusinessTrackerResponse() {
        return getBusinessTrackerResponse;
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
            Call<IdentityImageResponse> mCall = apiService.uploadIdentityImage(idFile, idType, idNumber);
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
                    uploadIdentityImageResponse.setValue(null);
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
                    removeIdentityImageResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getBusinessTracker() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BusinessTrackerResponse> mCall = apiService.getBusinessTracker();
            mCall.enqueue(new Callback<BusinessTrackerResponse>() {
                @Override
                public void onResponse(Call<BusinessTrackerResponse> call, Response<BusinessTrackerResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            BusinessTrackerResponse obj = response.body();
                            getBusinessTrackerResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BusinessTrackerResponse>() {
                            }.getType();
                            BusinessTrackerResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            getBusinessTrackerResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getBusinessTrackerResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<BusinessTrackerResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    getBusinessTrackerResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getCompanyInfo() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BusinessTrackerResponse> mCall = apiService.getCompanyInforamtion();
            mCall.enqueue(new Callback<BusinessTrackerResponse>() {
                @Override
                public void onResponse(Call<BusinessTrackerResponse> call, Response<BusinessTrackerResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            BusinessTrackerResponse obj = response.body();
                            getCompanyInfoResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BusinessTrackerResponse>() {
                            }.getType();
                            BusinessTrackerResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            getCompanyInfoResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getCompanyInfoResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<BusinessTrackerResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    getCompanyInfoResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void submitBasicCompanyInfo(CompanyInfoRequest companyInfoRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BusinessTrackerResponse> mCall = apiService.updateBasicCompanyInforamtion(companyInfoRequest);
            mCall.enqueue(new Callback<BusinessTrackerResponse>() {
                @Override
                public void onResponse(Call<BusinessTrackerResponse> call, Response<BusinessTrackerResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            BusinessTrackerResponse obj = response.body();
                            getBusinessTrackerResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BusinessTrackerResponse>() {
                            }.getType();
                            BusinessTrackerResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            getBusinessTrackerResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getBusinessTrackerResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<BusinessTrackerResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    getBusinessTrackerResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
