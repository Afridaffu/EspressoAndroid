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
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.profile.ImageResponse;
import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.ChangePassword;
import com.greenbox.coyni.model.ChangePasswordRequest;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.model.wallet.UserDetailsData;
import com.greenbox.coyni.model.wallet.WalletResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Profile> profileMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<PaymentMethodsResponse> paymentMethodsResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ImageResponse> imageResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<WalletResponse> walletResponseMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<ChangePassword> changePasswordMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Agreements> agreementsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AgreementsPdf> agreementsPdfMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<UserPreference> userPreferenceMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Preferences> preferenceMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ProfilesResponse> profileRespMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UserDetails> userDetailsMutableLiveData=new MutableLiveData<>();
    private MutableLiveData<String> errorMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<UserDetails> getUserDetailsMutableLiveData() {
        return userDetailsMutableLiveData;
    }

    public MutableLiveData<UserPreference> getUserPreferenceMutableLiveData() {
        return userPreferenceMutableLiveData;
    }

    public MutableLiveData<String> getErrorMutableLiveData() {
        return errorMutableLiveData;
    }

    public MutableLiveData<ProfilesResponse> getProfileRespMutableLiveData() {
        return profileRespMutableLiveData;
    }

    public MutableLiveData<AgreementsPdf> getAgreementsPdfMutableLiveData() {
        return agreementsPdfMutableLiveData;
    }

    public MutableLiveData<ChangePassword> getChangePasswordMutableLiveData() {
        return changePasswordMutableLiveData;
    }

    public MutableLiveData<PaymentMethodsResponse> getPaymentMethodsResponseMutableLiveData() {
        return paymentMethodsResponseMutableLiveData;
    }

    public MutableLiveData<ImageResponse> getImageResponseMutableLiveData() {
        return imageResponseMutableLiveData;
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

    public MutableLiveData<WalletResponse> getWalletResponseMutableLiveData() {
        return walletResponseMutableLiveData;
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

    public void meAgreementsById() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Agreements> mcall = apiService.meAgreementsByType();

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
        } catch (Exception ex) {
            ex.printStackTrace();

        }


    }

    public void agreementsByType(String agreementsType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<AgreementsPdf> mcall = apiService.agreementsByType(agreementsType);
            mcall.enqueue(new Callback<AgreementsPdf>() {
                @Override
                public void onResponse(Call<AgreementsPdf> call, Response<AgreementsPdf> response) {
                    try {
                        if (response.isSuccessful()) {
                            AgreementsPdf obj = response.body();
                            Log.e("agreeType", "" + new Gson().toJson(obj));
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
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public void mePaymentMethods() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<PaymentMethodsResponse> mCall = apiService.mePaymentMethods();
            mCall.enqueue(new Callback<PaymentMethodsResponse>() {
                @Override
                public void onResponse(Call<PaymentMethodsResponse> call, Response<PaymentMethodsResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            PaymentMethodsResponse obj = response.body();
                            paymentMethodsResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<PaymentMethodsResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

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
                            Log.e("success", "success");
                        } else {
                            Log.e("failed", "failed");
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

    public void meWallet() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<WalletResponse> mCall = apiService.meWallet();
            mCall.enqueue(new Callback<WalletResponse>() {
                @Override
                public void onResponse(Call<WalletResponse> call, Response<WalletResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            WalletResponse obj = response.body();
                            walletResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<WalletResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
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
                    } catch (Exception ex) {
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


    public void getProfiles() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ProfilesResponse> mCall = apiService.getProfiles();
            mCall.enqueue(new Callback<ProfilesResponse>() {
                @Override
                public void onResponse(Call<ProfilesResponse> call, Response<ProfilesResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            ProfilesResponse obj = response.body();
                            profileRespMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<ProfilesResponse>() {
                            }.getType();
                            ProfilesResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
                                profileRespMutableLiveData.setValue(errorResponse);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        apiErrorMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<ProfilesResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    public void getUserDetail(String walletId) {
        try{

        ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
        Call<UserDetails> mcall = apiService.getUserDetails(walletId);
        mcall.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {
                try {
                    if (response.isSuccessful()) {
                        UserDetails obj = response.body();
                        userDetailsMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<APIError>() {
                        }.getType();
                        APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
                        if (errorResponse != null) {
                            apiErrorMutableLiveData.setValue(errorResponse);
                        } else {
                            errorMutableLiveData.setValue("Wallet data not found.");
                        }

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    if (!response.message().toLowerCase().equals("")) {
                        errorMutableLiveData.setValue("Wallet data not found.");
                    } else {
                        apiErrorMutableLiveData.setValue(null);
                }

                }
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {
                Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                apiErrorMutableLiveData.setValue(null);
            }
        });
    }
        catch (Exception ex){
            ex.printStackTrace();
        }

    }


}
