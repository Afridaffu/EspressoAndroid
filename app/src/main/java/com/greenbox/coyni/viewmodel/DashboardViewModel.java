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
import com.greenbox.coyni.model.AbstractResponse;
import com.greenbox.coyni.model.Agreements;
import com.greenbox.coyni.model.AgreementsPdf;
import com.greenbox.coyni.model.ChangePassword;
import com.greenbox.coyni.model.ChangePasswordRequest;
import com.greenbox.coyni.model.buytoken.CancelBuyTokenResponse;
import com.greenbox.coyni.model.identity_verification.LatestTxnResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.preferences.Preferences;
import com.greenbox.coyni.model.preferences.ProfilesResponse;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.model.profile.DownloadDocumentResponse;
import com.greenbox.coyni.model.profile.DownloadImageResponse;
import com.greenbox.coyni.model.profile.DownloadUrlRequest;
import com.greenbox.coyni.model.profile.ImageResponse;
import com.greenbox.coyni.model.profile.Profile;
import com.greenbox.coyni.model.transaction.RefundDataResponce;
import com.greenbox.coyni.model.transaction.RefundReferenceRequest;
import com.greenbox.coyni.model.transaction.TransactionDetails;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.model.transaction.TransactionListRequest;
import com.greenbox.coyni.model.wallet.UserDetails;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;

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

    public MutableLiveData<LatestTxnResponse> getGetUserLatestTxns() {
        return getUserLatestTxns;
    }

    private MutableLiveData<TransactionList> transactionListMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CancelBuyTokenResponse> cancelBuyTokenResponseMutableLiveData = new MutableLiveData<>();


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

//    public MutableLiveData<WalletResponse> getWalletResponseMutableLiveData() {
//        return walletResponseMutableLiveData;
//    }

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

    public void meChangePassword(ChangePasswordRequest changePasswordRequest,String token) {
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

    public void getLatestTxns() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<LatestTxnResponse> mCall = apiService.getLatestTransactions();
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
                        }
                        else {
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

    public void refundprocessDetails(RefundReferenceRequest refundrefrequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<RefundDataResponce> call = apiService.getRefundProcess(refundrefrequest);
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


}
