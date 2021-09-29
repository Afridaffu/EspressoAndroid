package com.coyni.android.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.coyni.android.model.user.ImageRequest;
import com.coyni.android.model.user.ImageResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.android.model.APIError;
import com.coyni.android.model.cards.CardType;
import com.coyni.android.model.cards.CardTypeRequest;
import com.coyni.android.model.coynipin.CoyniPinRequest;
import com.coyni.android.model.coynipin.CoyniPinResponse;
import com.coyni.android.model.export.ExportColumns;
import com.coyni.android.model.export.ExportRequest;
import com.coyni.android.model.export.ExportResponse;
import com.coyni.android.model.login.Login;
import com.coyni.android.model.preferences.Preferences;
import com.coyni.android.model.preferences.UserPreference;
import com.coyni.android.model.register.AgreementsByType;
import com.coyni.android.model.transactions.TokenTransactions;
import com.coyni.android.model.user.AccountLimits;
import com.coyni.android.model.user.Agreements;
import com.coyni.android.model.user.AgreementsById;
import com.coyni.android.model.user.SavePdfRequest;
import com.coyni.android.model.user.User;
import com.coyni.android.model.user.UserData;
import com.coyni.android.model.user.UserPreferenceModel;
import com.coyni.android.model.usertracker.UserTracker;
import com.coyni.android.model.wallet.WalletResponse;
import com.coyni.android.network.ApiClient;
import com.coyni.android.network.ApiService;
import com.coyni.android.network.AuthApiClient;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends AndroidViewModel {
    private MutableLiveData<WalletResponse> walletResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<TokenTransactions> tokenTransactionsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UserTracker> userTrackerMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ExportColumns> exportColumnsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ExportResponse> exportResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CardType> cardTypeMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CoyniPinResponse> coyniPinResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ImageResponse> imageResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();
    private String errorMessage = "";

    //Nishanth
    private MutableLiveData<Agreements> userAgreeMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AgreementsById> userAgreementsByIdMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ResponseBody> userAgreementsSavePdfMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AccountLimits> userAccountLimitsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UserPreference> userPreferenceMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Preferences> preferenceMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AgreementsByType> userAgreementsByTypeMutableLiveData = new MutableLiveData<>();
    //End

    public DashboardViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<WalletResponse> getWalletResponseMutableLiveData() {
        return walletResponseMutableLiveData;
    }

    public MutableLiveData<User> getUserMutableLiveData() {
        return userMutableLiveData;
    }

    public MutableLiveData<TokenTransactions> getTokenTransactionsMutableLiveData() {
        return tokenTransactionsMutableLiveData;
    }

    public MutableLiveData<UserTracker> getUserTrackerMutableLiveData() {
        return userTrackerMutableLiveData;
    }

    public MutableLiveData<ExportColumns> getExportColumnsMutableLiveData() {
        return exportColumnsMutableLiveData;
    }

    public MutableLiveData<ExportResponse> getExportResponseMutableLiveData() {
        return exportResponseMutableLiveData;
    }

    public MutableLiveData<CardType> getCardTypeMutableLiveData() {
        return cardTypeMutableLiveData;
    }

    public MutableLiveData<APIError> getApiErrorMutableLiveData() {
        return apiErrorMutableLiveData;
    }

    public MutableLiveData<CoyniPinResponse> getCoyniPinResponseMutableLiveData() {
        return coyniPinResponseMutableLiveData;
    }

    public MutableLiveData<ImageResponse> getImageResponseMutableLiveData() {
        return imageResponseMutableLiveData;
    }

    //Nishanth
    public String getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Agreements> getUserAgreementsMutableLiveData() {
        return userAgreeMutableLiveData;
    }

    public MutableLiveData<AgreementsById> getUserAgreementsByIdMutableLiveData() {
        return userAgreementsByIdMutableLiveData;
    }

    public MutableLiveData<ResponseBody> getUserAgreementsSavePdfMutableLiveData() {
        return userAgreementsSavePdfMutableLiveData;
    }

    public MutableLiveData<UserPreference> getUserPreferenceMutableLiveData() {
        return userPreferenceMutableLiveData;
    }

    public MutableLiveData<AccountLimits> getUserAccountLimitsMutableLiveData() {
        return userAccountLimitsMutableLiveData;
    }

    public MutableLiveData<Preferences> getPreferencesResponseMutableLiveData() {
        return preferenceMutableLiveData;
    }

    public MutableLiveData<AgreementsByType> getUserAgreementsByTypeMutableLiveData() {
        return userAgreementsByTypeMutableLiveData;
    }

    //End

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

    public void meProfile() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<User> mCall = apiService.meProfile();
            mCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    try {
                        if (response.isSuccessful()) {
                            User obj = response.body();
                            userMutableLiveData.setValue(obj);
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
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meTransactions(Map<String, String> objMap) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TokenTransactions> mCall = apiService.meTransactions(objMap);
            mCall.enqueue(new Callback<TokenTransactions>() {
                @Override
                public void onResponse(Call<TokenTransactions> call, Response<TokenTransactions> response) {
                    try {
                        if (response.isSuccessful()) {
                            TokenTransactions obj = response.body();
                            tokenTransactionsMutableLiveData.setValue(obj);
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
                public void onFailure(Call<TokenTransactions> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meTracker() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UserTracker> mCall = apiService.meTracker(new Object());
            mCall.enqueue(new Callback<UserTracker>() {
                @Override
                public void onResponse(Call<UserTracker> call, Response<UserTracker> response) {
                    try {
                        if (response.isSuccessful()) {
                            UserTracker obj = response.body();
                            userTrackerMutableLiveData.setValue(obj);
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
                public void onFailure(Call<UserTracker> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getExportColumns(Map<String, String> objMap) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ExportColumns> mCall = apiService.getExportColumns(objMap);
            mCall.enqueue(new Callback<ExportColumns>() {
                @Override
                public void onResponse(Call<ExportColumns> call, Response<ExportColumns> response) {
                    try {
                        if (response.isSuccessful()) {
                            ExportColumns obj = response.body();
                            exportColumnsMutableLiveData.setValue(obj);
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
                public void onFailure(Call<ExportColumns> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void exportData(ExportRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ExportResponse> mCall = apiService.exportData(request);
            mCall.enqueue(new Callback<ExportResponse>() {
                @Override
                public void onResponse(Call<ExportResponse> call, Response<ExportResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            ExportResponse obj = response.body();
                            exportResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<ExportResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cardType(CardTypeRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CardType> mCall = apiService.cardType(request);
            mCall.enqueue(new Callback<CardType>() {
                @Override
                public void onResponse(Call<CardType> call, Response<CardType> response) {
                    try {
                        if (response.isSuccessful()) {
                            CardType obj = response.body();
                            cardTypeMutableLiveData.setValue(obj);
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
                public void onFailure(Call<CardType> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //Nishanth
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
                public void onFailure(Call<Preferences> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meAddAddress(UserData request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<User> mCall = apiService.meAddAddress(request);
            mCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    try {
                        if (response.isSuccessful()) {
                            User obj = response.body();
                            userMutableLiveData.setValue(obj);
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
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meUpdateAddress(UserData request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<User> mCall = apiService.meUpdateAddress(request);
            mCall.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    try {
                        if (response.isSuccessful()) {
                            User obj = response.body();
                            userMutableLiveData.setValue(obj);
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
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meUpdatePreferences(UserPreferenceModel request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UserPreference> mCall = apiService.meUpdatePreferences(request);
            mCall.enqueue(new Callback<UserPreference>() {
                @Override
                public void onResponse(Call<UserPreference> call, Response<UserPreference> response) {
                    try {
                        if (response.isSuccessful()) {
                            UserPreference obj = response.body();
                            userPreferenceMutableLiveData.setValue(obj);
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
                public void onFailure(Call<UserPreference> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meAccountLimits(int request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<AccountLimits> mCall = apiService.meAccountLimits(request);
            mCall.enqueue(new Callback<AccountLimits>() {
                @Override
                public void onResponse(Call<AccountLimits> call, Response<AccountLimits> response) {
                    try {
                        if (response.isSuccessful()) {
                            AccountLimits obj = response.body();
                            userAccountLimitsMutableLiveData.setValue(obj);
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
                public void onFailure(Call<AccountLimits> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meAgreements() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Agreements> mCall = apiService.meAgreements();
            mCall.enqueue(new Callback<Agreements>() {
                @Override
                public void onResponse(Call<Agreements> call, Response<Agreements> response) {
                    try {
                        if (response.isSuccessful()) {
                            Agreements obj = response.body();
                            userAgreeMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<APIError>() {
                            }.getType();
                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
                            if (errorResponse != null) {
                                apiErrorMutableLiveData.setValue(errorResponse);
                            } else {
                                JSONObject jsonObject = new JSONObject(response.errorBody().string());
                                if (jsonObject != null) {
                                    String strMsg = jsonObject.getJSONObject("error").getString("errorDescription");
                                    Toast.makeText(getApplication(), strMsg, Toast.LENGTH_LONG).show();
                                }
                            }
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

    public void meAgreementsById(int latestPrivacyPolicyRefId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<AgreementsById> mCall = apiService.meAgreementsById(latestPrivacyPolicyRefId);
            mCall.enqueue(new Callback<AgreementsById>() {
                @Override
                public void onResponse(Call<AgreementsById> call, Response<AgreementsById> response) {
                    try {
                        if (response.isSuccessful()) {
                            AgreementsById obj = response.body();
                            userAgreementsByIdMutableLiveData.setValue(obj);
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
                public void onFailure(Call<AgreementsById> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Agreements through SIgnUp screen
    public void meAgreementsByType(int agreeType) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<AgreementsByType> mCall = apiService.meAgreementsByType(agreeType);
            mCall.enqueue(new Callback<AgreementsByType>() {
                @Override
                public void onResponse(Call<AgreementsByType> call, Response<AgreementsByType> response) {
                    try {
                        if (response.isSuccessful()) {
                            AgreementsByType obj = response.body();
                            userAgreementsByTypeMutableLiveData.setValue(obj);
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
                public void onFailure(Call<AgreementsByType> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meAgreementsSavePdf(SavePdfRequest savePdfRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ResponseBody> mCall = apiService.meAgreementsSavePdf(savePdfRequest);
            mCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        if (response.isSuccessful()) {
                            ResponseBody obj = response.body();
                            userAgreementsSavePdfMutableLiveData.setValue(obj);
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
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    //End

    public void coyniPin(CoyniPinRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CoyniPinResponse> mCall = apiService.coyniPin(request);
            mCall.enqueue(new Callback<CoyniPinResponse>() {
                @Override
                public void onResponse(Call<CoyniPinResponse> call, Response<CoyniPinResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            CoyniPinResponse obj = response.body();
                            coyniPinResponseMutableLiveData.setValue(obj);
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
                public void onFailure(Call<CoyniPinResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void fiservUser() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Login> mCall = apiService.fiservUsers();
            mCall.enqueue(new Callback<Login>() {
                @Override
                public void onResponse(Call<Login> call, Response<Login> response) {
                    try {
                        if (response.isSuccessful()) {
                            Login obj = response.body();

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
                public void onFailure(Call<Login> call, Throwable t) {
                    //Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void uploadImage(ImageRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ImageResponse> mCall = apiService.uploadImage(request);
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

}
