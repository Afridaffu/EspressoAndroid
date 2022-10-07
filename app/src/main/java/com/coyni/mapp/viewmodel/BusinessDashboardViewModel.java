package com.coyni.mapp.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.coyni.mapp.model.BatchNow.BatchNowPaymentRequest;
import com.coyni.mapp.model.BatchNow.BatchNowRequest;
import com.coyni.mapp.model.BatchNow.BatchNowResponse;
import com.coyni.mapp.model.BatchPayoutIdDetails.BatchPayoutDetailsRequest;
import com.coyni.mapp.model.BatchPayoutIdDetails.BatchPayoutIdDetailsResponse;
import com.coyni.mapp.model.BusinessBatchPayout.BatchPayoutListResponse;
import com.coyni.mapp.model.BusinessBatchPayout.RollingListRequest;
import com.coyni.mapp.model.DashboardReserveList.ReserveListResponse;
import com.coyni.mapp.model.EmptyRequest;
import com.coyni.mapp.model.SearchKeyRequest;
import com.coyni.mapp.model.UpdateSignAgree.UpdateSignAgreementsResponse;
import com.coyni.mapp.model.business_activity.BusinessActivityRequest;
import com.coyni.mapp.model.business_activity.BusinessActivityResp;
import com.coyni.mapp.model.business_id_verification.CancelApplicationResponse;
import com.coyni.mapp.model.businesswallet.BusinessWalletResponse;
import com.coyni.mapp.model.businesswallet.WalletRequest;
import com.coyni.mapp.model.cogent.CogentRequest;
import com.coyni.mapp.model.cogent.CogentResponse;
import com.coyni.mapp.model.fee.Fees;
import com.coyni.mapp.model.merchant_activity.MerchantActivityRequest;
import com.coyni.mapp.model.merchant_activity.MerchantActivityResp;
import com.coyni.mapp.model.paymentmethods.PaymentMethodsResponse;
import com.coyni.mapp.model.reserveIdDetails.DetailsRequest;
import com.coyni.mapp.model.reserveIdDetails.DetailsResponse;
import com.coyni.mapp.model.reservemanual.ManualListResponse;
import com.coyni.mapp.model.reservemanual.RollingSearchRequest;
import com.coyni.mapp.model.reserverule.RollingRuleResponse;
import com.coyni.mapp.model.signedagreements.SignedAgreementResponse;
import com.coyni.mapp.network.ApiService;
import com.coyni.mapp.network.AuthApiClient;
import com.coyni.mapp.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessDashboardViewModel extends AndroidViewModel {

    private MutableLiveData<PaymentMethodsResponse> paymentMethodsResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CogentResponse> CogentResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BusinessWalletResponse> businessWalletResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SignedAgreementResponse> signedAgreementResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UpdateSignAgreementsResponse> updateSignAgreementsResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CancelApplicationResponse> cancelApplicationResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BatchPayoutListResponse> batchPayoutListMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BatchPayoutIdDetailsResponse> batchPayoutIdDetailsResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BatchPayoutListResponse> rollingListResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BatchPayoutListResponse> batchNowResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ManualListResponse> manualListResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<RollingRuleResponse> rollingRuleResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<DetailsResponse> detailsResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<Fees> feesMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ReserveListResponse> reserveListResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BatchNowResponse> batchNowSlideResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BusinessActivityResp> businessActivityRespMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<MerchantActivityResp> merchantActivityRespMutableLiveData = new MutableLiveData<>();

    public BusinessDashboardViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<MerchantActivityResp> getMerchantActivityRespMutableLiveData() {
        return merchantActivityRespMutableLiveData;
    }

    public MutableLiveData<BusinessActivityResp> getBusinessActivityRespMutableLiveData() {
        return businessActivityRespMutableLiveData;
    }

    public MutableLiveData<PaymentMethodsResponse> getPaymentMethodsResponseMutableLiveData() {
        return paymentMethodsResponseMutableLiveData;
    }

    public MutableLiveData<ReserveListResponse> getReserveListResponseMutableLiveData() {
        return reserveListResponseMutableLiveData;
    }

    public MutableLiveData<CogentResponse> getCogentResponseMutableLiveData() {
        return CogentResponseMutableLiveData;
    }

    public MutableLiveData<BusinessWalletResponse> getBusinessWalletResponseMutableLiveData() {
        return businessWalletResponseMutableLiveData;
    }

    public MutableLiveData<SignedAgreementResponse> getSignedAgreementResponseMutableLiveData() {
        return signedAgreementResponseMutableLiveData;
    }

    public MutableLiveData<UpdateSignAgreementsResponse> getUpdateSignAgreementsResponseMutableLiveData() {
        return updateSignAgreementsResponseMutableLiveData;
    }

    public MutableLiveData<CancelApplicationResponse> getCancelApplicationResponseMutableLiveData() {
        return cancelApplicationResponseMutableLiveData;
    }

    public MutableLiveData<BatchPayoutListResponse> getBatchPayoutListMutableLiveData() {
        return batchPayoutListMutableLiveData;
    }

    public MutableLiveData<BatchPayoutIdDetailsResponse> getBatchPayoutIdDetailsResponseMutableLiveData() {
        return batchPayoutIdDetailsResponseMutableLiveData;
    }

    public MutableLiveData<BatchPayoutListResponse> getBatchNowResponseMutableLiveData() {
        return batchNowResponseMutableLiveData;
    }

    public MutableLiveData<BatchNowResponse> getBatchNowSlideResponseMutableLiveData() {
        return batchNowSlideResponseMutableLiveData;
    }

    public MutableLiveData<BatchPayoutListResponse> getRollingListResponseMutableLiveData() {
        return rollingListResponseMutableLiveData;
    }

    public MutableLiveData<DetailsResponse> getDetailsResponseMutableLiveData() {
        return detailsResponseMutableLiveData;
    }

    public MutableLiveData<ManualListResponse> getManualListResponseMutableLiveData() {
        return manualListResponseMutableLiveData;
    }

    public MutableLiveData<RollingRuleResponse> getRollingRuleResponseMutableLiveData() {
        return rollingRuleResponseMutableLiveData;
    }

    public MutableLiveData<Fees> getFeesMutableLiveData() {
        return feesMutableLiveData;
    }

    public void meBusinessPaymentMethods() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<PaymentMethodsResponse> mCall = apiService.meBusinessPaymentMethods();
            mCall.enqueue(new Callback<PaymentMethodsResponse>() {
                @Override
                public void onResponse(Call<PaymentMethodsResponse> call, Response<PaymentMethodsResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            PaymentMethodsResponse obj = response.body();
                            paymentMethodsResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<PaymentMethodsResponse>() {
                            }.getType();
                            PaymentMethodsResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            paymentMethodsResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        paymentMethodsResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<PaymentMethodsResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    paymentMethodsResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveCogentBank(CogentRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<CogentResponse> mCall = apiService.saveBanks(request);
            mCall.enqueue(new Callback<CogentResponse>() {
                @Override
                public void onResponse(Call<CogentResponse> call, Response<CogentResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            CogentResponse obj = response.body();
                            CogentResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<CogentResponse>() {
                            }.getType();
                            CogentResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            CogentResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        CogentResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<CogentResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    CogentResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meMerchantWallet(WalletRequest walletRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BusinessWalletResponse> mCall = apiService.meMerchantWallet(walletRequest);
            mCall.enqueue(new Callback<BusinessWalletResponse>() {
                @Override
                public void onResponse(Call<BusinessWalletResponse> call, Response<BusinessWalletResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            BusinessWalletResponse obj = response.body();
                            businessWalletResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BusinessWalletResponse>() {
                            }.getType();
                            BusinessWalletResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            businessWalletResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        businessWalletResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<BusinessWalletResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    businessWalletResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void signedAgreement(MultipartBody.Part file, int agreementType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<SignedAgreementResponse> mCall = apiService.signedAgreement(file, agreementType);
            mCall.enqueue(new Callback<SignedAgreementResponse>() {
                @Override
                public void onResponse(Call<SignedAgreementResponse> call, Response<SignedAgreementResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            SignedAgreementResponse obj = response.body();
                            signedAgreementResponseMutableLiveData.setValue(obj);
                            Log.d("Signed agreement", obj.toString());

                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<SignedAgreementResponse>() {
                            }.getType();
                            SignedAgreementResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            signedAgreementResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        signedAgreementResponseMutableLiveData.setValue(null);
                    }

                }

                @Override
                public void onFailure(Call<SignedAgreementResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    signedAgreementResponseMutableLiveData.setValue(null);

                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateSignedAgree() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<UpdateSignAgreementsResponse> mCall = apiService.updateSignAgreemets();
            mCall.enqueue(new Callback<UpdateSignAgreementsResponse>() {
                @Override
                public void onResponse(Call<UpdateSignAgreementsResponse> call, Response<UpdateSignAgreementsResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            UpdateSignAgreementsResponse obj = response.body();
                            updateSignAgreementsResponseMutableLiveData.setValue(obj);
                            Log.d("Updated Signed agreement", obj.toString());

                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<UpdateSignAgreementsResponse>() {
                            }.getType();
                            UpdateSignAgreementsResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            updateSignAgreementsResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        updateSignAgreementsResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<UpdateSignAgreementsResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    updateSignAgreementsResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void cancelMerchantApplication() {
        ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
        Call<CancelApplicationResponse> mCall = apiService.cancelMerchant();
        mCall.enqueue(new Callback<CancelApplicationResponse>() {
            @Override
            public void onResponse(Call<CancelApplicationResponse> call, Response<CancelApplicationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CancelApplicationResponse cancelledData = response.body();
                    cancelApplicationResponseMutableLiveData.setValue(cancelledData);
                } else {
                    Gson gson = new Gson();
                    Type type = new TypeToken<CancelApplicationResponse>() {
                    }.getType();
                    CancelApplicationResponse errorResponse = null;
                    try {
                        errorResponse = gson.fromJson(response.errorBody().string(), type);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cancelApplicationResponseMutableLiveData.setValue(errorResponse);
                }
            }

            @Override
            public void onFailure(Call<CancelApplicationResponse> call, Throwable t) {
                cancelApplicationResponseMutableLiveData.setValue(null);
            }
        });
    }

    public void meFees(int feeStructureId) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<Fees> mCall = apiService.meFees(feeStructureId);
            mCall.enqueue(new Callback<Fees>() {
                @Override
                public void onResponse(Call<Fees> call, Response<Fees> response) {
                    if (response.isSuccessful()) {
                        Fees feeData = response.body();
                        feesMutableLiveData.setValue(feeData);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<Fees>() {
                        }.getType();
                        Fees errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(response.errorBody().string(), type);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        feesMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<Fees> call, Throwable t) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRollingListData(RollingListRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BatchPayoutListResponse> call = apiService.getRollingListData(request);
            call.enqueue(new Callback<BatchPayoutListResponse>() {
                @Override
                public void onResponse(Call<BatchPayoutListResponse> call, Response<BatchPayoutListResponse> response) {
                    if (response.isSuccessful()) {
                        BatchPayoutListResponse list = response.body();
                        rollingListResponseMutableLiveData.setValue(list);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<BatchPayoutListResponse>() {
                        }.getType();
                        BatchPayoutListResponse errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(response.errorBody().string(), type);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        rollingListResponseMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<BatchPayoutListResponse> call, Throwable t) {
                    rollingListResponseMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getBatchNowData(BatchNowRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BatchPayoutListResponse> call = apiService.getPayoutListData(request);
            call.enqueue(new Callback<BatchPayoutListResponse>() {
                @Override
                public void onResponse(Call<BatchPayoutListResponse> call, Response<BatchPayoutListResponse> response) {
                    if (response.isSuccessful()) {
                        BatchPayoutListResponse list = response.body();
                        batchNowResponseMutableLiveData.setValue(list);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<BatchPayoutListResponse>() {
                        }.getType();
                        BatchPayoutListResponse errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(response.errorBody().string(), type);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        batchNowResponseMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<BatchPayoutListResponse> call, Throwable t) {
                    batchNowResponseMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRollingListData(RollingSearchRequest searchKeyReq) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BatchPayoutListResponse> call = apiService.getRollingListData(searchKeyReq);
            call.enqueue(new Callback<BatchPayoutListResponse>() {
                @Override
                public void onResponse(Call<BatchPayoutListResponse> call, Response<BatchPayoutListResponse> response) {
                    if (response.isSuccessful()) {
                        BatchPayoutListResponse list = response.body();
                        rollingListResponseMutableLiveData.setValue(list);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<BatchPayoutListResponse>() {
                        }.getType();
                        BatchPayoutListResponse errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(response.errorBody().string(), type);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        rollingListResponseMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<BatchPayoutListResponse> call, Throwable t) {
                    rollingListResponseMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getRollingRuleDetails() {
        ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
        Call<RollingRuleResponse> mCall = apiService.getRollingRuleDetails();
        mCall.enqueue(new Callback<RollingRuleResponse>() {
            @Override
            public void onResponse(Call<RollingRuleResponse> call, Response<RollingRuleResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    RollingRuleResponse ruleResponse = response.body();
                    rollingRuleResponseMutableLiveData.setValue(ruleResponse);
                } else {
                    Gson gson = new Gson();
                    Type type = new TypeToken<RollingRuleResponse>() {
                    }.getType();
                    RollingRuleResponse ruleResponse = null;
                    try {
                        ruleResponse = gson.fromJson(response.errorBody().string(), type);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    rollingRuleResponseMutableLiveData.setValue(ruleResponse);
                }
            }

            @Override
            public void onFailure(Call<RollingRuleResponse> call, Throwable t) {
                rollingRuleResponseMutableLiveData.setValue(null);
            }
        });
    }

    public void getReserveIdDetails(DetailsRequest detailsRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<DetailsResponse> call = apiService.getReserveIdDetails(detailsRequest);
            call.enqueue(new Callback<DetailsResponse>() {
                @Override
                public void onResponse(Call<DetailsResponse> call, Response<DetailsResponse> response) {
                    if (response.isSuccessful()) {
                        DetailsResponse list = response.body();
                        detailsResponseMutableLiveData.setValue(list);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<DetailsResponse>() {
                        }.getType();
                        DetailsResponse errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(response.errorBody().string(), type);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        detailsResponseMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<DetailsResponse> call, Throwable t) {
                    detailsResponseMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getManualListData() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ManualListResponse> call = apiService.getManualListData(new EmptyRequest());
            call.enqueue(new Callback<ManualListResponse>() {
                @Override
                public void onResponse(Call<ManualListResponse> call, Response<ManualListResponse> response) {
                    if (response.isSuccessful()) {
                        ManualListResponse list = response.body();
                        manualListResponseMutableLiveData.setValue(list);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ManualListResponse>() {
                        }.getType();
                        ManualListResponse errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(response.errorBody().string(), type);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        manualListResponseMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<ManualListResponse> call, Throwable t) {
                    manualListResponseMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getReserveList() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ReserveListResponse> call = apiService.getReserveListItems();
            call.enqueue(new Callback<ReserveListResponse>() {
                @Override
                public void onResponse(Call<ReserveListResponse> call, Response<ReserveListResponse> response) {
                    if (response.isSuccessful()) {
                        ReserveListResponse list = response.body();
                        reserveListResponseMutableLiveData.setValue(list);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ReserveListResponse>() {
                        }.getType();
                        ReserveListResponse errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(response.errorBody().string(), type);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        reserveListResponseMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<ReserveListResponse> call, Throwable t) {
                    reserveListResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getManualListData(SearchKeyRequest searchKey) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ManualListResponse> call = apiService.getManualListData(searchKey);
            call.enqueue(new Callback<ManualListResponse>() {
                @Override
                public void onResponse(Call<ManualListResponse> call, Response<ManualListResponse> response) {
                    if (response.isSuccessful()) {
                        ManualListResponse list = response.body();
                        manualListResponseMutableLiveData.setValue(list);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ManualListResponse>() {
                        }.getType();
                        ManualListResponse errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(response.errorBody().string(), type);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        manualListResponseMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<ManualListResponse> call, Throwable t) {
                    manualListResponseMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void batchNowSlideData(BatchNowPaymentRequest request) {
        ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
        Call<BatchNowResponse> call = apiService.getSlideBatchNow(request);
        LogUtils.e("Modell", "success" + request.toString());
        call.enqueue(new Callback<BatchNowResponse>() {
            @Override
            public void onResponse(Call<BatchNowResponse> call, Response<BatchNowResponse> response) {
//                LogUtils.e("Modell", "success" + response.isSuccessful());
                if (response.isSuccessful()) {
                    LogUtils.e("Modell", "success" + response.body());
                    BatchNowResponse obj = response.body();
                    batchNowSlideResponseMutableLiveData.setValue(obj);
                    //Utils.showCustomToast(getApplication(), "Successfully_Closed_Batch ", R.drawable.ic_custom_tick, "Batch");
                    Log.e("Success", new Gson().toJson(obj));
                } else {
                    Gson gson = new Gson();
                    Type type = new TypeToken<BatchNowResponse>() {
                    }.getType();
                    BatchNowResponse errorResponse = null;
                    try {
                        errorResponse = gson.fromJson(response.errorBody().string(), type);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    batchNowSlideResponseMutableLiveData.setValue(errorResponse);
                    LogUtils.e("Modell", "error" + errorResponse);
                }
            }

            @Override
            public void onFailure(Call<BatchNowResponse> call, Throwable t) {
                LogUtils.d("Modell", "failuree" + t);
                batchNowSlideResponseMutableLiveData.setValue(null);
            }
        });

    }

//    public void getPayoutListData() {
//        try {
//            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
//            Call<BatchPayoutListResponse> call = apiService.getPayoutListData(new EmptyRequest());
//            call.enqueue(new Callback<BatchPayoutListResponse>() {
//                @Override
//                public void onResponse(Call<BatchPayoutListResponse> call, Response<BatchPayoutListResponse> response) {
//                    if (response.isSuccessful()) {
//                        BatchPayoutListResponse list = response.body();
//                        batchPayoutListMutableLiveData.setValue(list);
//                    } else {
//                        Gson gson = new Gson();
//                        Type type = new TypeToken<BatchPayoutListResponse>() {
//                        }.getType();
//                        BatchPayoutListResponse errorResponse = null;
//                        try {
//                            errorResponse = gson.fromJson(response.errorBody().string(), type);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        batchPayoutListMutableLiveData.setValue(errorResponse);
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<BatchPayoutListResponse> call, Throwable t) {
//                    batchPayoutListMutableLiveData.setValue(null);
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void getPayoutlistData(String searchKey) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BatchPayoutListResponse> call = apiService.getPayoutlistData(searchKey);
            call.enqueue(new Callback<BatchPayoutListResponse>() {
                @Override
                public void onResponse(Call<BatchPayoutListResponse> call, Response<BatchPayoutListResponse> response) {
                    if (response.isSuccessful()) {
                        BatchPayoutListResponse list = response.body();
                        batchPayoutListMutableLiveData.setValue(list);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<BatchPayoutListResponse>() {
                        }.getType();
                        BatchPayoutListResponse errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(response.errorBody().string(), type);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        batchPayoutListMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<BatchPayoutListResponse> call, Throwable t) {
                    batchPayoutListMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPayoutlistdata(String fromDate, String toDate) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BatchPayoutListResponse> call = apiService.getPayoutlistdata(fromDate, toDate);
            call.enqueue(new Callback<BatchPayoutListResponse>() {
                @Override
                public void onResponse(Call<BatchPayoutListResponse> call, Response<BatchPayoutListResponse> response) {
                    if (response.isSuccessful()) {
                        BatchPayoutListResponse list = response.body();
                        batchPayoutListMutableLiveData.setValue(list);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<BatchPayoutListResponse>() {
                        }.getType();
                        BatchPayoutListResponse errorResponse = null;
                        try {
                            errorResponse = gson.fromJson(response.errorBody().string(), type);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        batchPayoutListMutableLiveData.setValue(errorResponse);
                    }
                }

                @Override
                public void onFailure(Call<BatchPayoutListResponse> call, Throwable t) {
                    batchPayoutListMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void batchPayoutIdDetails(BatchPayoutDetailsRequest batchPayoutDetailsRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create((ApiService.class));
            Call<BatchPayoutIdDetailsResponse> call = apiService.batchPayoutIdDetails(batchPayoutDetailsRequest);
            call.enqueue(new Callback<BatchPayoutIdDetailsResponse>() {
                @Override
                public void onResponse(Call<BatchPayoutIdDetailsResponse> call, Response<BatchPayoutIdDetailsResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            BatchPayoutIdDetailsResponse list = response.body();
                            batchPayoutIdDetailsResponseMutableLiveData.setValue(list);
//                            Log.e("Success", new Gson().toJson(list));

                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BatchPayoutListResponse>() {
                            }.getType();
                            BatchPayoutIdDetailsResponse errorResponse = null;
                            try {
                                errorResponse = gson.fromJson(response.errorBody().string(), type);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            batchPayoutIdDetailsResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BatchPayoutIdDetailsResponse> call, Throwable t) {
                    batchPayoutIdDetailsResponseMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void businessActivity(BusinessActivityRequest businessActivityRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BusinessActivityResp> call = apiService.businessActivity(businessActivityRequest);
            call.enqueue(new Callback<BusinessActivityResp>() {
                @Override
                public void onResponse(Call<BusinessActivityResp> call, Response<BusinessActivityResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            BusinessActivityResp list = response.body();
                            businessActivityRespMutableLiveData.setValue(list);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<BusinessActivityResp>() {
                            }.getType();
                            BusinessActivityResp errorResponse = null;
                            try {
                                errorResponse = gson.fromJson(response.errorBody().string(), type);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            businessActivityRespMutableLiveData.setValue(errorResponse);
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<BusinessActivityResp> call, Throwable t) {
                    businessActivityRespMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void merchantActivity(MerchantActivityRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<MerchantActivityResp> call = apiService.merchantActivity(request);
            call.enqueue(new Callback<MerchantActivityResp>() {
                @Override
                public void onResponse(Call<MerchantActivityResp> call, Response<MerchantActivityResp> response) {
                    try {
                        if (response.isSuccessful()) {
                            MerchantActivityResp list = response.body();
                            merchantActivityRespMutableLiveData.setValue(list);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<MerchantActivityResp>() {
                            }.getType();
                            MerchantActivityResp errorResponse = null;
                            try {
                                errorResponse = gson.fromJson(response.errorBody().string(), type);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            merchantActivityRespMutableLiveData.setValue(errorResponse);
                        }
                    } catch (JsonSyntaxException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<MerchantActivityResp> call, Throwable t) {
                    merchantActivityRespMutableLiveData.setValue(null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}



