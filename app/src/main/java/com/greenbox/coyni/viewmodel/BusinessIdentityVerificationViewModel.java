package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.BeneficialOwners.BOIdResp;
import com.greenbox.coyni.model.BeneficialOwners.BOPatchResp;
import com.greenbox.coyni.model.BeneficialOwners.BORequest;
import com.greenbox.coyni.model.BeneficialOwners.BOResp;
import com.greenbox.coyni.model.BeneficialOwners.BOValidateResp;
import com.greenbox.coyni.model.BeneficialOwners.DeleteBOResp;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoRequest;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoResp;
import com.greenbox.coyni.model.CompanyInfo.CompanyInfoUpdateResp;
import com.greenbox.coyni.model.CompanyInfo.ContactInfoRequest;
import com.greenbox.coyni.model.DBAInfo.BusinessTypeResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoRequest;
import com.greenbox.coyni.model.DBAInfo.DBAInfoResp;
import com.greenbox.coyni.model.DBAInfo.DBAInfoUpdateResp;
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
    private MutableLiveData<DBAInfoUpdateResp> updateBasicDBAInfoResponse = new MutableLiveData<>();
    private MutableLiveData<CompanyInfoUpdateResp> postCompanyInfoResponse = new MutableLiveData<>();
    private MutableLiveData<DBAInfoUpdateResp> postDBAInfoResponse = new MutableLiveData<>();
    private MutableLiveData<DBAInfoResp> getDBAInfoResponse = new MutableLiveData<>();
    private MutableLiveData<BusinessTypeResp> businessTypesResponse = new MutableLiveData<>();

    private MutableLiveData<BOResp> beneficialOwnersResponse = new MutableLiveData<>();
    private MutableLiveData<BOIdResp> beneficialOwnersIDResponse = new MutableLiveData<>();
    private MutableLiveData<DeleteBOResp> deleteBOresponse = new MutableLiveData<>();
    private MutableLiveData<BOPatchResp> patchBOresponse = new MutableLiveData<>();
    private MutableLiveData<IdentityImageResponse> uploadBODocResponse = new MutableLiveData<>();
    private MutableLiveData<RemoveIdentityResponse> removeBODocResponse = new MutableLiveData<>();
    private MutableLiveData<BOValidateResp> validateBOResponse = new MutableLiveData<>();
    private MutableLiveData<CompanyInfoUpdateResp> getContactInfoUpdate=new MutableLiveData<>();


    public BusinessIdentityVerificationViewModel(@NonNull Application application) {
        super(application);
    }


    public MutableLiveData<BOValidateResp> getValidateBOResponse() {
        return validateBOResponse;
    }

    public MutableLiveData<IdentityImageResponse> getUploadBODocResponse() {
        return uploadBODocResponse;
    }

    public MutableLiveData<RemoveIdentityResponse> getRemoveBODocResponse() {
        return removeBODocResponse;
    }

    public MutableLiveData<BOPatchResp> getPatchBOresponse() {
        return patchBOresponse;
    }

    public MutableLiveData<DeleteBOResp> getDeleteBOesponse() {
        return deleteBOresponse;
    }

    public MutableLiveData<BOIdResp> getBeneficialOwnersIDResponse() {
        return beneficialOwnersIDResponse;
    }

    public MutableLiveData<BOResp> getBeneficialOwnersResponse() {
        return beneficialOwnersResponse;
    }

    public MutableLiveData<DBAInfoUpdateResp> getPostDBAInfoResponse() {
        return postDBAInfoResponse;
    }

    public MutableLiveData<DBAInfoUpdateResp> getUpdateBasicDBAInfoResponse() {
        return updateBasicDBAInfoResponse;
    }

    public MutableLiveData<BusinessTypeResp> getBusinessTypesResponse() {
        return businessTypesResponse;
    }

    public MutableLiveData<DBAInfoResp> getGetDBAInfoResponse() {
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

    public MutableLiveData<CompanyInfoUpdateResp> getContactInfoUpdateResponse() {
        return getContactInfoUpdate;
    }

    //ID Verification Tracker
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

    public void updateCompanyInfo(ContactInfoRequest contactInfoRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CompanyInfoUpdateResp> mCall = apiService.updateContactInforamtion(contactInfoRequest);
            mCall.enqueue(new Callback<CompanyInfoUpdateResp>() {
                @Override
                public void onResponse(Call<CompanyInfoUpdateResp> call, Response<CompanyInfoUpdateResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            CompanyInfoUpdateResp obj = response.body();
                            getContactInfoUpdate.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CompanyInfoUpdateResp>() {
                            }.getType();
                            CompanyInfoUpdateResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            getContactInfoUpdate.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getContactInfoUpdate.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<CompanyInfoUpdateResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    getContactInfoUpdate.setValue(null);
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
            Call<DBAInfoResp> mCall = apiService.getDBAInforamtion();
            mCall.enqueue(new Callback<DBAInfoResp>() {
                @Override
                public void onResponse(Call<DBAInfoResp> call, Response<DBAInfoResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            DBAInfoResp obj = response.body();
                            getDBAInfoResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<DBAInfoResp>() {
                            }.getType();
                            DBAInfoResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            getDBAInfoResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getDBAInfoResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<DBAInfoResp> call, Throwable t) {
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

    public void patchDBAInfo(DBAInfoRequest dbaInfoRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<DBAInfoUpdateResp> mCall = apiService.updateDBAInforamtion(dbaInfoRequest);
            mCall.enqueue(new Callback<DBAInfoUpdateResp>() {
                @Override
                public void onResponse(Call<DBAInfoUpdateResp> call, Response<DBAInfoUpdateResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            DBAInfoUpdateResp obj = response.body();
                            updateBasicDBAInfoResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<DBAInfoUpdateResp>() {
                            }.getType();
                            DBAInfoUpdateResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            updateBasicDBAInfoResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        updateBasicDBAInfoResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<DBAInfoUpdateResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    updateBasicDBAInfoResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void postDBAInfo(DBAInfoRequest dbaInfoRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<DBAInfoUpdateResp> mCall = apiService.postDBAInforamtion(dbaInfoRequest);
            mCall.enqueue(new Callback<DBAInfoUpdateResp>() {
                @Override
                public void onResponse(Call<DBAInfoUpdateResp> call, Response<DBAInfoUpdateResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            DBAInfoUpdateResp obj = response.body();
                            postDBAInfoResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<DBAInfoUpdateResp>() {
                            }.getType();
                            DBAInfoUpdateResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            postDBAInfoResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        postDBAInfoResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<DBAInfoUpdateResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    postDBAInfoResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    //Beneficial Owners
    public void getBeneficialOwners() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BOResp> mCall = apiService.getBeneficailOwners();
            mCall.enqueue(new Callback<BOResp>() {
                @Override
                public void onResponse(Call<BOResp> call, Response<BOResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            BOResp obj = response.body();
                            beneficialOwnersResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BOResp>() {
                            }.getType();
                            BOResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            beneficialOwnersResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        beneficialOwnersResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<BOResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    beneficialOwnersResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void postBeneficialOwnersID() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BOIdResp> mCall = apiService.postBeneficailOwnersID();
            mCall.enqueue(new Callback<BOIdResp>() {
                @Override
                public void onResponse(Call<BOIdResp> call, Response<BOIdResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            BOIdResp obj = response.body();
                            beneficialOwnersIDResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BOIdResp>() {
                            }.getType();
                            BOIdResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            beneficialOwnersIDResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        beneficialOwnersIDResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<BOIdResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    beneficialOwnersIDResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteBeneficialOwner(int id) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<DeleteBOResp> mCall = apiService.deleteBeneficialOwner(id);
            mCall.enqueue(new Callback<DeleteBOResp>() {
                @Override
                public void onResponse(Call<DeleteBOResp> call, Response<DeleteBOResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            DeleteBOResp obj = response.body();
                            deleteBOresponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<DeleteBOResp>() {
                            }.getType();
                            DeleteBOResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            deleteBOresponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        deleteBOresponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<DeleteBOResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    deleteBOresponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void patchBeneficialOwner(int id, BORequest boRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BOPatchResp> mCall = apiService.patchBeneficialOwner(id, boRequest);
            mCall.enqueue(new Callback<BOPatchResp>() {
                @Override
                public void onResponse(Call<BOPatchResp> call, Response<BOPatchResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            BOPatchResp obj = response.body();
                            patchBOresponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BOPatchResp>() {
                            }.getType();
                            BOPatchResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            patchBOresponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        patchBOresponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<BOPatchResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    patchBOresponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void uploadBODoc(int id, MultipartBody.Part idFile, RequestBody idType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<IdentityImageResponse> mCall = apiService.uploadBODoc(id, idFile, idType);
            mCall.enqueue(new Callback<IdentityImageResponse>() {
                @Override
                public void onResponse(Call<IdentityImageResponse> call, Response<IdentityImageResponse> response) {
                    if (response.isSuccessful()) {
                        IdentityImageResponse obj = response.body();
                        uploadBODocResponse.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<IdentityImageResponse>() {
                        }.getType();
                        try {
                            IdentityImageResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
                                uploadBODocResponse.setValue(errorResponse);
                            }
                        } catch (JsonIOException e) {
                            e.printStackTrace();
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<IdentityImageResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    uploadBODocResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeBODoc(String identityType, String boID) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<RemoveIdentityResponse> mCall = apiService.removeBODoc(identityType, boID);
            mCall.enqueue(new Callback<RemoveIdentityResponse>() {
                @Override
                public void onResponse(Call<RemoveIdentityResponse> call, Response<RemoveIdentityResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            RemoveIdentityResponse obj = response.body();
                            removeBODocResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<RemoveIdentityResponse>() {
                            }.getType();
                            RemoveIdentityResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            removeBODocResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        removeBODocResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<RemoveIdentityResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    removeBODocResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void validateBeneficialOwners() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BOValidateResp> mCall = apiService.validateBeneficailOwners();
            mCall.enqueue(new Callback<BOValidateResp>() {
                @Override
                public void onResponse(Call<BOValidateResp> call, Response<BOValidateResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            BOValidateResp obj = response.body();
                            validateBOResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BOValidateResp>() {
                            }.getType();
                            BOValidateResp errorResponse = gson.fromJson(response.errorBody().string(), type);
                            validateBOResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        validateBOResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<BOValidateResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    validateBOResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
