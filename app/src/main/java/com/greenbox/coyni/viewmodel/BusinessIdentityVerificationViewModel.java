package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoRequest;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoUpdateResp;
import com.greenbox.coyni.model.DBAInfo.BusinessTypeResp;
import com.greenbox.coyni.model.business_id_verification.BusinessTrackerResponse;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.identity_verification.RemoveIdentityResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessIdentityVerificationViewModel extends AndroidViewModel {

    private MutableLiveData<BusinessTrackerResponse> getBusinessTrackerResponse = new MutableLiveData<>();
    private MutableLiveData<CompanyInfoResp> getCompanyInfoResponse = new MutableLiveData<>();
    private MutableLiveData<CompanyInfoUpdateResp> updateBasicCompanyInfoResponse = new MutableLiveData<>();
    private MutableLiveData<CompanyInfoUpdateResp> postCompanyInfoResponse = new MutableLiveData<>();

    private MutableLiveData<CompanyInfoResp> getDBAInfoResponse = new MutableLiveData<>();
    private MutableLiveData<BusinessTypeResp> businessTypesResponse = new MutableLiveData<>();

    public BusinessIdentityVerificationViewModel(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<BusinessTypeResp> getBusinessTypesResponse() {
        return businessTypesResponse;
    }

    public MutableLiveData<CompanyInfoResp> getGetDBAInfoResponse() {
        return getDBAInfoResponse;
    }

    public MutableLiveData<CompanyInfoUpdateResp> getPostCompanyInfoResponse() {
        return postCompanyInfoResponse;
    }

    public MutableLiveData<CompanyInfoResp> getGetCompanyInfoResponse() {
        return getCompanyInfoResponse;
    }

    public MutableLiveData<CompanyInfoUpdateResp> getUpdateBasicCompanyInfoResponse() {
        return updateBasicCompanyInfoResponse;
    }

    public MutableLiveData<BusinessTrackerResponse> getGetBusinessTrackerResponse() {
        return getBusinessTrackerResponse;
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

    //Company info
    public void getCompanyInfo() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CompanyInfoResp> mCall = apiService.getCompanyInforamtion();
            mCall.enqueue(new Callback<CompanyInfoResp>() {
                @Override
                public void onResponse(Call<CompanyInfoResp> call, Response<CompanyInfoResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            CompanyInfoResp obj = response.body();
                            getCompanyInfoResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CompanyInfoResp>() {
                            }.getType();
                            CompanyInfoResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            getCompanyInfoResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getCompanyInfoResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<CompanyInfoResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    getCompanyInfoResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void patchCompanyInfo(CompanyInfoRequest companyInfoRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CompanyInfoUpdateResp> mCall = apiService.updateCompanyInforamtion(companyInfoRequest);
            mCall.enqueue(new Callback<CompanyInfoUpdateResp>() {
                @Override
                public void onResponse(Call<CompanyInfoUpdateResp> call, Response<CompanyInfoUpdateResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            CompanyInfoUpdateResp obj = response.body();
                            updateBasicCompanyInfoResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CompanyInfoUpdateResp>() {
                            }.getType();
                            CompanyInfoUpdateResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            updateBasicCompanyInfoResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        updateBasicCompanyInfoResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<CompanyInfoUpdateResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    updateBasicCompanyInfoResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void postCompanyInfo(CompanyInfoRequest companyInfoRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CompanyInfoUpdateResp> mCall = apiService.postCompanyInforamtion(companyInfoRequest);
            mCall.enqueue(new Callback<CompanyInfoUpdateResp>() {
                @Override
                public void onResponse(Call<CompanyInfoUpdateResp> call, Response<CompanyInfoUpdateResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            CompanyInfoUpdateResp obj = response.body();
                            postCompanyInfoResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CompanyInfoUpdateResp>() {
                            }.getType();
                            CompanyInfoUpdateResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            postCompanyInfoResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        postCompanyInfoResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<CompanyInfoUpdateResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    postCompanyInfoResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    //DBA info
    public void getDBAInfo() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CompanyInfoResp> mCall = apiService.getDBAInforamtion();
            mCall.enqueue(new Callback<CompanyInfoResp>() {
                @Override
                public void onResponse(Call<CompanyInfoResp> call, Response<CompanyInfoResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            CompanyInfoResp obj = response.body();
                            getDBAInfoResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CompanyInfoResp>() {
                            }.getType();
                            CompanyInfoResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            getDBAInfoResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getDBAInfoResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<CompanyInfoResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    getDBAInfoResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getBusinessType() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BusinessTypeResp> mCall = apiService.getBusinessType();
            mCall.enqueue(new Callback<BusinessTypeResp>() {
                @Override
                public void onResponse(Call<BusinessTypeResp> call, Response<BusinessTypeResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            BusinessTypeResp obj = response.body();
                            businessTypesResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BusinessTypeResp>() {
                            }.getType();
                            BusinessTypeResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            businessTypesResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        businessTypesResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<BusinessTypeResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    businessTypesResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
