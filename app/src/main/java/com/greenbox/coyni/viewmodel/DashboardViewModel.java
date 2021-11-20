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
import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.ChangePassword;
import com.greenbox.coyni.model.ChangePasswordRequest;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Profile> profileMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ChangePassword> changePasswordMutableLiveData=new MutableLiveData<>();
    private MutableLiveData<Agreements> agreementsMutableLiveData=new MutableLiveData<>();
    private MutableLiveData<AgreementsPdf> agreementsPdfMutableLiveData=new MutableLiveData<>();
    public MutableLiveData<AgreementsPdf> getAgreementsPdfMutableLiveData() {
        return agreementsPdfMutableLiveData;
    }

    public void setAgreementsPdfMutableLiveData(MutableLiveData<AgreementsPdf> agreementsPdfMutableLiveData) {
        this.agreementsPdfMutableLiveData = agreementsPdfMutableLiveData;
    }

    public MutableLiveData<ChangePassword> getChangePasswordMutableLiveData() {
        return changePasswordMutableLiveData;
    }

    public void setChangePasswordMutableLiveData(MutableLiveData<ChangePassword> changePasswordMutableLiveData) {
        this.changePasswordMutableLiveData = changePasswordMutableLiveData;
    }

    public void setApiErrorMutableLiveData(MutableLiveData<APIError> apiErrorMutableLiveData) {
        this.apiErrorMutableLiveData = apiErrorMutableLiveData;
    }

    public void setProfileMutableLiveData(MutableLiveData<Profile> profileMutableLiveData) {
        this.profileMutableLiveData = profileMutableLiveData;
    }

    public MutableLiveData<Agreements> getAgreementsMutableLiveData() {
        return agreementsMutableLiveData;
    }

    public void setAgreementsMutableLiveData(MutableLiveData<Agreements> agreementsMutableLiveData) {
        this.agreementsMutableLiveData = agreementsMutableLiveData;
    }


    public DashboardViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<Profile> getProfileMutableLiveData() {
        return profileMutableLiveData;
    }

    public void meProfile() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Profile> mCall = apiService.meProfile();
            mCall.enqueue(new Callback<Profile>() {
                @Override
                public void onResponse(Call<Profile> call, Response<Profile> response) {
                    try {
                        if (response.isSuccessful()) {
                            Profile obj = response.body();
                            profileMutableLiveData.setValue(obj);
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
                public void onFailure(Call<Profile> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    public void meAgreementsById(){
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Agreements> mcall =apiService.meAgreementsByType();

            mcall.enqueue(new Callback<Agreements>() {
                @Override
                public void onResponse(Call<Agreements> call, Response<Agreements> response) {
                    try {
                        if (response.isSuccessful()) {
                            Agreements obj = response.body();
                            agreementsMutableLiveData.setValue(obj);
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
                public void onFailure(Call<Agreements> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        }
       catch (Exception ex){
            ex.printStackTrace();

       }


    }

    public void agreementsByType(String agreementsType){
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<AgreementsPdf> mcall =apiService.agreementsByType(agreementsType);
            mcall.enqueue(new Callback<AgreementsPdf>() {
                @Override
                public void onResponse(Call<AgreementsPdf> call, Response<AgreementsPdf> response) {
                        try {
                            if (response.isSuccessful()) {
                                AgreementsPdf obj = response.body();
                                Log.e("agreeType",""+new Gson().toJson(obj));
                                agreementsPdfMutableLiveData.setValue(obj);
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
                public void onFailure(Call<AgreementsPdf> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });

        }
        catch (Exception ex){
            ex.printStackTrace();

        }


    }
    public void meChangePassword(ChangePasswordRequest changePasswordRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ChangePassword> mcall = apiService.mChangePassword(changePasswordRequest);
            mcall.enqueue(new Callback<ChangePassword>() {
                @Override
                public void onResponse(Call<ChangePassword> call, Response<ChangePassword> response) {
                   try {

                       if (response.isSuccessful()) {
                           ChangePassword obj = response.body();
                           changePasswordMutableLiveData.setValue(obj);

                       } else {
                           Gson gson = new Gson();
                           Type type = new TypeToken<APIError>() {
                           }.getType();
                           APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
                           apiErrorMutableLiveData.setValue(errorResponse);
                       }
                   }
                   catch (Exception ex){
                       ex.printStackTrace();
                   }
                }

                @Override
                public void onFailure(Call<ChangePassword> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
