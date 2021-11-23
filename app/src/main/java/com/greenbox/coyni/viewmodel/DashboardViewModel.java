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
//import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.login.LoginResponse;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.model.profile.ImageResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Profile> profileMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ImageResponse> imageResponseMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<UserPreference> userPreferenceMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Preferences> preferenceMutableLiveData = new MutableLiveData<>();


    public void setApiErrorMutableLiveData(MutableLiveData<APIError> apiErrorMutableLiveData) {
        this.apiErrorMutableLiveData = apiErrorMutableLiveData;
    }

    public void setProfileMutableLiveData(MutableLiveData<Profile> profileMutableLiveData) {
        this.profileMutableLiveData = profileMutableLiveData;
    }

    public MutableLiveData<ImageResponse> getImageResponseMutableLiveData() {
        return imageResponseMutableLiveData;
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

    public MutableLiveData<Preferences> getPreferenceMutableLiveData() {
        return preferenceMutableLiveData;
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


    public void updateProfile(MultipartBody.Part body) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ImageResponse> mCall = apiService.updateProfile(body);
            mCall.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            ImageResponse obj = response.body();
                            imageResponseMutableLiveData.setValue(obj);
                            Log.e("success","success");
                        } else {
                            Log.e("failed","failed");
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
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeImage(String filename) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ImageResponse> mCall = apiService.removeImage(filename);
            mCall.enqueue(new Callback<ImageResponse>() {
                @Override
                public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            ImageResponse obj = response.body();
                            imageResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<ImageResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void mePreferences() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Preferences> mCall = apiService.mePreferences();
            mCall.enqueue(new Callback<Preferences>() {
                @Override
                public void onResponse(Call<Preferences> call, Response<Preferences> response) {
                    try {
                        if (response.isSuccessful()) {
                            Preferences obj = response.body();
                            preferenceMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<Preferences>() {
                            }.getType();
                            Preferences errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
                                preferenceMutableLiveData.setValue(errorResponse);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<Preferences> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
