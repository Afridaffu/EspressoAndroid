package com.coyni.mapp.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.coyni.mapp.model.appupdate.AppUpdateResp;
import com.coyni.mapp.network.ApiClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coyni.mapp.model.APIError;
import com.coyni.mapp.model.Agreements;
import com.coyni.mapp.model.AgreementsPdf;
import com.coyni.mapp.model.ChangePassword;
import com.coyni.mapp.model.ChangePasswordRequest;
import com.coyni.mapp.model.activtity_log.ActivityLogResp;
import com.coyni.mapp.model.buytoken.CancelBuyTokenResponse;
import com.coyni.mapp.model.featurecontrols.FeatureControlGlobalResp;
import com.coyni.mapp.model.featurecontrols.FeatureControlRespByUser;
import com.coyni.mapp.model.identity_verification.LatestTransactionsRequest;
import com.coyni.mapp.model.identity_verification.LatestTxnResponse;
import com.coyni.mapp.model.paymentmethods.PaymentMethodsResponse;
import com.coyni.mapp.model.preferences.Preferences;
import com.coyni.mapp.model.preferences.ProfilesResponse;
import com.coyni.mapp.model.preferences.UserPreference;
import com.coyni.mapp.model.profile.DownloadDocumentResponse;
import com.coyni.mapp.model.profile.DownloadImageResponse;
import com.coyni.mapp.model.profile.DownloadUrlRequest;
import com.coyni.mapp.model.profile.ImageResponse;
import com.coyni.mapp.model.profile.Profile;
import com.coyni.mapp.model.transaction.RefundDataResponce;
import com.coyni.mapp.model.transaction.RefundReferenceRequest;
import com.coyni.mapp.model.transaction.TransactionDetails;
import com.coyni.mapp.model.transaction.TransactionList;
import com.coyni.mapp.model.transaction.TransactionListRequest;
import com.coyni.mapp.model.wallet.UserDetails;
import com.coyni.mapp.network.ApiService;
import com.coyni.mapp.network.AuthApiClient;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.MyApplication;
import com.coyni.mapp.utils.Utils;

import java.io.IOException;
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
//    private MutableLiveData<WalletResponse> walletResponseMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<ChangePassword> changePasswordMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Agreements> agreementsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AgreementsPdf> agreementsPdfMutableLiveData = new MutableLiveData<>();

    private MutableLiveData<UserPreference> userPreferenceMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Preferences> preferenceMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ProfilesResponse> profileRespMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UserDetails> userDetailsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<LatestTxnResponse> getUserLatestTxns = new MutableLiveData<>();
    private MutableLiveData<TransactionDetails> transactionDetailsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<RefundDataResponce> refundDetailsMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<RefundDataResponce> refundProcessMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<DownloadImageResponse> downloadUrlResponse = new MutableLiveData<>();
    private MutableLiveData<DownloadDocumentResponse> downloadDocumentResponse = new MutableLiveData<>();
    private MutableLiveData<ActivityLogResp> activityLogRespMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<FeatureControlRespByUser> featureControlRespByUserMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<FeatureControlGlobalResp> featureControlGlobalRespMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<LatestTxnResponse> getGetUserLatestTxns() {
        return getUserLatestTxns;
    }

    private MutableLiveData<TransactionList> transactionListMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CancelBuyTokenResponse> cancelBuyTokenResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<AppUpdateResp> appUpdateRespMutableLiveData = new MutableLiveData<>();


    public MutableLiveData<ActivityLogResp> getActivityLogRespMutableLiveData() {
        return activityLogRespMutableLiveData;
    }

    public MutableLiveData<TransactionList> getTransactionListMutableLiveData() {
        return transactionListMutableLiveData;
    }

    public MutableLiveData<DownloadImageResponse> getDownloadUrlResponse() {
        return downloadUrlResponse;
    }

    public MutableLiveData<DownloadDocumentResponse> getDownloadDocumentResponse() {
        return downloadDocumentResponse;
    }

    public MutableLiveData<UserDetails> getUserDetailsMutableLiveData() {
        return userDetailsMutableLiveData;
    }

    public MutableLiveData<UserPreference> getUserPreferenceMutableLiveData() {
        return userPreferenceMutableLiveData;
    }

    public MutableLiveData<AppUpdateResp> getAppUpdateRespMutableLiveData() {
        return appUpdateRespMutableLiveData;
    }

    public void setAppUpdateRespMutableLiveData(MutableLiveData<AppUpdateResp> appUpdateRespMutableLiveData) {
        this.appUpdateRespMutableLiveData = appUpdateRespMutableLiveData;
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

    public MutableLiveData<String> getErrorMutableLiveData() {
        return errorMutableLiveData;
    }

    public MutableLiveData<TransactionDetails> getTransactionDetailsMutableLiveData() {
        return transactionDetailsMutableLiveData;
    }

    public MutableLiveData<CancelBuyTokenResponse> getCancelBuyTokenResponseMutableLiveData() {
        return cancelBuyTokenResponseMutableLiveData;
    }

    public MutableLiveData<RefundDataResponce> getRefundDetailsMutableLiveData() {
        return refundDetailsMutableLiveData;
    }

    public MutableLiveData<RefundDataResponce> getRefundProcessMutableLiveData() {
        return refundProcessMutableLiveData;
    }

    public MutableLiveData<FeatureControlRespByUser> getFeatureControlRespByUserMutableLiveData() {
        return featureControlRespByUserMutableLiveData;
    }

    public MutableLiveData<FeatureControlGlobalResp> getFeatureControlGlobalRespMutableLiveData() {
        return featureControlGlobalRespMutableLiveData;
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

    public void getDocumentUrl(int docId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<DownloadDocumentResponse> mcall = apiService.getAgreementUrl(docId + "");
            mcall.enqueue(new Callback<DownloadDocumentResponse>() {
                @Override
                public void onResponse(Call<DownloadDocumentResponse> call, Response<DownloadDocumentResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            DownloadDocumentResponse obj = response.body();
                            downloadDocumentResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<DownloadDocumentResponse>() {
                            }.getType();
                            DownloadDocumentResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            downloadDocumentResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        downloadDocumentResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<DownloadDocumentResponse> call, Throwable t) {
                    LogUtils.v("getDocumentUrl failure", "" + t.getLocalizedMessage());
                    downloadDocumentResponse.setValue(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getAgreementUrlByDocumentNumber(String refId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<DownloadDocumentResponse> mcall = apiService.getAgreementUrlByDocumentNumber(refId);
            mcall.enqueue(new Callback<DownloadDocumentResponse>() {
                @Override
                public void onResponse(Call<DownloadDocumentResponse> call, Response<DownloadDocumentResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            DownloadDocumentResponse obj = response.body();
                            downloadDocumentResponse.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<DownloadDocumentResponse>() {
                            }.getType();
                            DownloadDocumentResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            downloadDocumentResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        downloadDocumentResponse.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<DownloadDocumentResponse> call, Throwable t) {
                    LogUtils.v("getDocumentUrl failure", "" + t.getLocalizedMessage());
                    downloadDocumentResponse.setValue(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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

    public void updateProfile(MultipartBody.Part body, MyApplication objMyApplication) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ImageResponse> mCall = null;
            if (objMyApplication.getAccountType() == Utils.SHARED_ACCOUNT)
                mCall = apiService.updateProfileShared(body);
            else
                mCall = apiService.updateProfile(body);

            if (mCall != null) {
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
            }
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

//    public void meWallet() {
//        try {
//            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
//            Call<WalletResponse> mCall = apiService.meWallet();
//            mCall.enqueue(new Callback<WalletResponse>() {
//                @Override
//                public void onResponse(Call<WalletResponse> call, Response<WalletResponse> response) {
//                    try {
//                        if (response.isSuccessful()) {
//                            WalletResponse obj = response.body();
//                            walletResponseMutableLiveData.setValue(obj);
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
//
//                @Override
//                public void onFailure(Call<WalletResponse> call, Throwable t) {
//                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
//                    apiErrorMutableLiveData.setValue(null);
//                }
//            });
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public void meChangePassword(ChangePasswordRequest changePasswordRequest, String token) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
//            Call<ChangePassword> mcall = apiService.mChangePassword(changePasswordRequest);
            Call<ChangePassword> mcall = apiService.mChangePassword(changePasswordRequest, token);
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

    public void preferences(Boolean isShared, String businessUserId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Preferences> mCall = null;
            if (isShared)
                mCall = apiService.mePreferencesShared(businessUserId);
            else
                mCall = apiService.mePreferences();

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


    public void mePreferences(MyApplication myApplicationObj) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Preferences> mCall = null;
            if (myApplicationObj.getAccountType() == Utils.SHARED_ACCOUNT)
                mCall = apiService.mePreferencesShared(myApplicationObj.getBusinessUserID());
            else
                mCall = apiService.mePreferences();

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
        try {
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
//                            Gson gson = new Gson();
//                            Type type = new TypeToken<APIError>() {
//                            }.getType();
//                            APIError errorResponse = gson.fromJson(response.errorBody().string(), type);
//                            if (errorResponse != null) {
//                                apiErrorMutableLiveData.setValue(errorResponse);
//                            } else {
//                                errorMutableLiveData.setValue("Wallet data not found.");
//                            }
                            Gson gson = new Gson();
                            Type type = new TypeToken<UserDetails>() {
                            }.getType();
                            UserDetails errorResponse = gson.fromJson(response.errorBody().string(), type);
                            if (errorResponse != null) {
                                userDetailsMutableLiveData.setValue(errorResponse);
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public void getLatestTxns(LatestTransactionsRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<LatestTxnResponse> mCall = apiService.getLatestTransactions(request);

            mCall.enqueue(new Callback<LatestTxnResponse>() {
                @Override
                public void onResponse(Call<LatestTxnResponse> call, Response<LatestTxnResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            LatestTxnResponse obj = response.body();
                            getUserLatestTxns.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<LatestTxnResponse>() {
                            }.getType();
                            LatestTxnResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                            if (errorResponse != null) {
                                getUserLatestTxns.setValue(errorResponse);
                            }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        getUserLatestTxns.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<LatestTxnResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meTransactionList(TransactionListRequest request) {
        ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
        Call<TransactionList> mcall = apiService.meTransactionList(request);
        mcall.enqueue(new Callback<TransactionList>() {
            @Override
            public void onResponse(Call<TransactionList> call, Response<TransactionList> response) {
                try {
                    if (response.isSuccessful()) {
                        TransactionList obj = response.body();
                        transactionListMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<TransactionList>() {
                        }.getType();
                        TransactionList errorResponse = gson.fromJson(response.errorBody().string(), type);
                        transactionListMutableLiveData.setValue(errorResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<TransactionList> call, Throwable t) {
                Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                apiErrorMutableLiveData.setValue(null);
            }
        });

    }

    public void getTransactionDetails(String gbxTxnIdType, int txnType, Integer txnSubType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<TransactionDetails> call = apiService.getTransactionDetails(gbxTxnIdType, txnType, txnSubType);
            call.enqueue(new Callback<TransactionDetails>() {
                @Override
                public void onResponse(Call<TransactionDetails> call, Response<TransactionDetails> response) {
                    try {
                        if (response.isSuccessful()) {
                            TransactionDetails obj = response.body();
                            transactionDetailsMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<TransactionDetails>() {
                            }.getType();
                            TransactionDetails errorResponse = gson.fromJson(response.errorBody().string(), type);
                            transactionDetailsMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        transactionListMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<TransactionDetails> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cancelBuyToken(String gbxTxnId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CancelBuyTokenResponse> mCall = apiService.cancelBuyToken(gbxTxnId);
            mCall.enqueue(new Callback<CancelBuyTokenResponse>() {
                @Override
                public void onResponse(Call<CancelBuyTokenResponse> call, Response<CancelBuyTokenResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            CancelBuyTokenResponse obj = response.body();
                            cancelBuyTokenResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CancelBuyTokenResponse>() {
                            }.getType();
                            CancelBuyTokenResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            cancelBuyTokenResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        cancelBuyTokenResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<CancelBuyTokenResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    cancelBuyTokenResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cancelWithdrawToken(String gbxTxnId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CancelBuyTokenResponse> mCall = apiService.cancelWithdrawToken(gbxTxnId);
            mCall.enqueue(new Callback<CancelBuyTokenResponse>() {
                @Override
                public void onResponse(Call<CancelBuyTokenResponse> call, Response<CancelBuyTokenResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            CancelBuyTokenResponse obj = response.body();
                            cancelBuyTokenResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CancelBuyTokenResponse>() {
                            }.getType();
                            CancelBuyTokenResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            cancelBuyTokenResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        cancelBuyTokenResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<CancelBuyTokenResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    cancelBuyTokenResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void refundDetails(RefundReferenceRequest refundrefrequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<RefundDataResponce> call = apiService.getRefundDetails(refundrefrequest);
            call.enqueue(new Callback<RefundDataResponce>() {
                @Override
                public void onResponse(Call<RefundDataResponce> call, Response<RefundDataResponce> r) {
                    if (r.isSuccessful()) {
                        RefundDataResponce data = r.body();
                        refundDetailsMutableLiveData.setValue(data);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<RefundDataResponce>() {
                        }.getType();
                        RefundDataResponce errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(r.errorBody().string(), type);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        refundDetailsMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<RefundDataResponce> call, Throwable t) {
                    refundDetailsMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getDownloadUrl(List<DownloadUrlRequest> downloadUrlRequestList) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<DownloadImageResponse> mCall = apiService.getDownloadUrl(downloadUrlRequestList);
            mCall.enqueue(new Callback<DownloadImageResponse>() {
                @Override
                public void onResponse(Call<DownloadImageResponse> call, Response<DownloadImageResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            DownloadImageResponse obj = response.body();
                            downloadUrlResponse.setValue(obj);
                            //setData(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<DownloadImageResponse>() {
                            }.getType();
                            DownloadImageResponse errorResponse = null;
                            try {
                                errorResponse = gson.fromJson(response.errorBody().string(), type);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            downloadUrlResponse.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<DownloadImageResponse> call, Throwable t) {
                    downloadUrlResponse.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void refundprocessDetails(RefundReferenceRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<RefundDataResponce> call = apiService.getRefundProcess(request);
            call.enqueue(new Callback<RefundDataResponce>() {
                @Override
                public void onResponse(Call<RefundDataResponce> call, Response<RefundDataResponce> r) {
                    if (r.isSuccessful()) {
                        RefundDataResponce data = r.body();
                        refundProcessMutableLiveData.setValue(data);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<RefundDataResponce>() {
                        }.getType();
                        RefundDataResponce errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(r.errorBody().string(), type);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        refundProcessMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<RefundDataResponce> call, Throwable t) {
                    refundProcessMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getActivityLog(String txnId, String userType) {
        ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
        Call<ActivityLogResp> call = apiService.activityLog(txnId, userType);
        call.enqueue(new Callback<ActivityLogResp>() {
            @Override
            public void onResponse(Call<ActivityLogResp> call, Response<ActivityLogResp> response) {
                if (response.isSuccessful()) {
                    ActivityLogResp resp = response.body();
                    activityLogRespMutableLiveData.setValue(resp);
                } else {
                    Gson gson = new Gson();
                    Type type = new TypeToken<ActivityLogResp>() {
                    }.getType();
                    ActivityLogResp errorResponse = null;
                    try {
                        errorResponse = gson.fromJson(response.errorBody().string(), type);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    activityLogRespMutableLiveData.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<ActivityLogResp> call, Throwable t) {
                activityLogRespMutableLiveData.setValue(null);
            }
        });
    }

    public void getFeatureControlByUser(int customerId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<FeatureControlRespByUser> mCall = apiService.featureControlByUser(customerId);
            mCall.enqueue(new Callback<FeatureControlRespByUser>() {
                @Override
                public void onResponse(Call<FeatureControlRespByUser> call, Response<FeatureControlRespByUser> response) {
                    try {
                        if (response.isSuccessful()) {
                            FeatureControlRespByUser obj = response.body();
                            featureControlRespByUserMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<FeatureControlRespByUser>() {
                            }.getType();
                            FeatureControlRespByUser errorResponse = null;
                            try {
                                errorResponse = gson.fromJson(response.errorBody().string(), type);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            featureControlRespByUserMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        featureControlRespByUserMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<FeatureControlRespByUser> call, Throwable t) {
                    featureControlRespByUserMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getFeatureControlGlobal(String portalType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<FeatureControlGlobalResp> mCall = apiService.featureControlGlobal(portalType);
            mCall.enqueue(new Callback<FeatureControlGlobalResp>() {
                @Override
                public void onResponse(Call<FeatureControlGlobalResp> call, Response<FeatureControlGlobalResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            FeatureControlGlobalResp obj = response.body();
                            featureControlGlobalRespMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<FeatureControlRespByUser>() {
                            }.getType();
                            FeatureControlGlobalResp errorResponse = null;
                            try {
                                errorResponse = gson.fromJson(response.errorBody().string(), type);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            featureControlGlobalRespMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        featureControlGlobalRespMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<FeatureControlGlobalResp> call, Throwable t) {
                    featureControlGlobalRespMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void getAppUpdate(String osType) {
        try {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<AppUpdateResp> mCall = apiService.getAppUpdate(osType);
            mCall.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<AppUpdateResp> call, Response<AppUpdateResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            AppUpdateResp obj = response.body();
                            appUpdateRespMutableLiveData.setValue(obj);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<AppUpdateResp> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    appUpdateRespMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
