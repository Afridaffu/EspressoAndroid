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
import com.greenbox.coyni.model.identity_verification.GetIdentityResponse;
import com.greenbox.coyni.model.identity_verification.IdentityAddressRequest;
import com.greenbox.coyni.model.identity_verification.IdentityAddressResponse;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.identity_verification.RemoveIdentityResponse;
import com.greenbox.coyni.model.profile.AddBusinessUserResponse;
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
    private MutableLiveData<IdentityAddressResponse> uploadIdentityAddressResponse = new MutableLiveData<>();
    private MutableLiveData<IdentityAddressResponse> uploadIdentityAddressPatchResponse = new MutableLiveData<>();
    private MutableLiveData<TrackerResponse> getStatusTracker = new MutableLiveData<>();
    private MutableLiveData<AddBusinessUserResponse> getBusinessAddCustomer = new MutableLiveData<>();
    private MutableLiveData<AddBusinessUserResponse> getBusinessAddDBAResponse = new MutableLiveData<>();
    private MutableLiveData<GetIdentityResponse> getIdentity = new MutableLiveData<>();

    public IdentityVerificationViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<IdentityAddressResponse> getUploadIdentityAddressPatchResponse() {
        return uploadIdentityAddressPatchResponse;
    }

    public MutableLiveData<GetIdentityResponse> getGetIdentity() {
        return getIdentity;
    }

    public MutableLiveData<TrackerResponse> getGetStatusTracker() {
        return getStatusTracker;
    }

    public MutableLiveData<AddBusinessUserResponse> getBusinessAddCustomer() {
        return getBusinessAddCustomer;
    }
    public MutableLiveData<AddBusinessUserResponse> getBusinessAddDBAResponse() {
        return getBusinessAddDBAResponse;
    }

    public MutableLiveData<IdentityAddressResponse> getUploadIdentityAddressResponse() {
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
            Call<IdentityAddressResponse> mCall = apiService.uploadIdentityAddress(identityAddressRequest);
            mCall.enqueue(new Callback<IdentityAddressResponse>() {
                @Override
                public void onResponse(Call<IdentityAddressResponse> call, Response<IdentityAddressResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            IdentityAddressResponse obj = response.body();
                            uploadIdentityAddressResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<IdentityAddressResponse>() {
                            }.getType();
                            IdentityAddressResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            uploadIdentityAddressResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        uploadIdentityAddressResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<IdentityAddressResponse> call, Throwable t) {
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

    public void getPostAddCustomer() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<AddBusinessUserResponse> mCall = apiService.registerAddCustomer();
            mCall.enqueue(new Callback<AddBusinessUserResponse>() {
                @Override
                public void onResponse(Call<AddBusinessUserResponse> call, Response<AddBusinessUserResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            AddBusinessUserResponse obj = response.body();
                            getBusinessAddCustomer.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<AddBusinessUserResponse>() {
                            }.getType();
                            AddBusinessUserResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            getBusinessAddCustomer.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getBusinessAddCustomer.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<AddBusinessUserResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void getAddBusinessUser() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<AddBusinessUserResponse> mCall = apiService.addBusinessUserInIndividual();
            mCall.enqueue(new Callback<AddBusinessUserResponse>() {
                @Override
                public void onResponse(Call<AddBusinessUserResponse> call, Response<AddBusinessUserResponse> response) {
                    try {
                        Log.d("businessreg","rrrrr"+response);
                        if (response.isSuccessful()) {
                            AddBusinessUserResponse obj = response.body();
                            getBusinessAddCustomer.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<AddBusinessUserResponse>() {
                            }.getType();
                            AddBusinessUserResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            getBusinessAddCustomer.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getBusinessAddCustomer.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<AddBusinessUserResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getPostAddDBABusiness(int companyID) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<AddBusinessUserResponse> mCall = apiService.addDBAInBusinessAccount(companyID);
            mCall.enqueue(new Callback<AddBusinessUserResponse>() {
                @Override
                public void onResponse(Call<AddBusinessUserResponse> call, Response<AddBusinessUserResponse> response) {
                    try {
                        Log.d("businessreg","rrrrr"+response);
                        if (response.isSuccessful()) {
                            AddBusinessUserResponse obj = response.body();
                            getBusinessAddDBAResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<AddBusinessUserResponse>() {
                            }.getType();
                            AddBusinessUserResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            getBusinessAddDBAResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getBusinessAddDBAResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<AddBusinessUserResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getIdentityData() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<GetIdentityResponse> mCall = apiService.getIdentity();
            mCall.enqueue(new Callback<GetIdentityResponse>() {
                @Override
                public void onResponse(Call<GetIdentityResponse> call, Response<GetIdentityResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            GetIdentityResponse obj = response.body();
                            getIdentity.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<GetIdentityResponse>() {
                            }.getType();
                            GetIdentityResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            getIdentity.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getIdentity.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<GetIdentityResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void uploadIdentityAddressPatch(IdentityAddressRequest identityAddressRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<IdentityAddressResponse> mCall = apiService.uploadIdentityAddressPatch(identityAddressRequest);
            mCall.enqueue(new Callback<IdentityAddressResponse>() {
                @Override
                public void onResponse(Call<IdentityAddressResponse> call, Response<IdentityAddressResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            IdentityAddressResponse obj = response.body();
                            uploadIdentityAddressPatchResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<IdentityAddressResponse>() {
                            }.getType();
                            IdentityAddressResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            uploadIdentityAddressPatchResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        uploadIdentityAddressPatchResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<IdentityAddressResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
