package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
//import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Profile> profileMutableLiveData = new MutableLiveData<>();

    public void setApiErrorMutableLiveData(MutableLiveData<APIError> apiErrorMutableLiveData) {
        this.apiErrorMutableLiveData = apiErrorMutableLiveData;
    }

    public void setProfileMutableLiveData(MutableLiveData<Profile> profileMutableLiveData) {
        this.profileMutableLiveData = profileMutableLiveData;
    }

//    public MutableLiveData<Agreements> getAgreementsMutableLiveData() {
//        return agreementsMutableLiveData;
//    }
//
//    public void setAgreementsMutableLiveData(MutableLiveData<Agreements> agreementsMutableLiveData) {
//        this.agreementsMutableLiveData = agreementsMutableLiveData;
//    }
//
//    private MutableLiveData<Agreements> agreementsMutableLiveData=new MutableLiveData<>();

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
//    public void meAgreementsById(){
//        try {
//            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
//            Call<Agreements> mcall = apiService.meAgreementsByType();
//            mcall.enqueue(new Callback<Agreements>() {
//                @Override
//                public void onResponse(Call<Agreements> call, Response<Agreements> response) {
//                    try {
//                        if (response.isSuccessful()) {
//                            Agreements obj = response.body();
//                            agreementsMutableLiveData.setValue(obj);
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
//                @Override
//                public void onFailure(Call<Agreements> call, Throwable t) {
//                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
//                    apiErrorMutableLiveData.setValue(null);
//                }
//            });
//        }
//       catch (Exception ex){
//            ex.printStackTrace();
//
//       }
//
//
//    }


}
