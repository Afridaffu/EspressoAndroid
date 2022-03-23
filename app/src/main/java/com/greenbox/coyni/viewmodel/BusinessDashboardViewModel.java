package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.UpdateSignAgree.UpdateSignAgreementsResponse;
import com.greenbox.coyni.model.business_id_verification.ApplicationCancelledData;
import com.greenbox.coyni.model.business_id_verification.CancelApplicationResponse;
//import com.greenbox.coyni.model.business_transactions.BusinessTransactionDetailsResp;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.fee.Fees;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.signedagreements.SignedAgreementResponse;
import com.greenbox.coyni.model.signet.SignetRequest;
import com.greenbox.coyni.model.signet.SignetResponse;
import com.greenbox.coyni.model.transaction.TransactionDetails;
import com.greenbox.coyni.model.transaction.TransactionList;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessDashboardViewModel extends AndroidViewModel {

    private MutableLiveData<PaymentMethodsResponse> paymentMethodsResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SignetResponse> signetResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BusinessWalletResponse> businessWalletResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SignedAgreementResponse> signedAgreementResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<UpdateSignAgreementsResponse> updateSignAgreementsResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<CancelApplicationResponse> cancelApplicationResponseMutableLiveData = new MutableLiveData<>();


    private MutableLiveData<Fees> feesMutableLiveData = new MutableLiveData<>();

    public BusinessDashboardViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<PaymentMethodsResponse> getPaymentMethodsResponseMutableLiveData() {
        return paymentMethodsResponseMutableLiveData;
    }

    public MutableLiveData<SignetResponse> getSignetResponseMutableLiveData() {
        return signetResponseMutableLiveData;
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

    public void saveSignetBank(SignetRequest request) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<SignetResponse> mCall = apiService.saveBanks(request);
            mCall.enqueue(new Callback<SignetResponse>() {
                @Override
                public void onResponse(Call<SignetResponse> call, Response<SignetResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            SignetResponse obj = response.body();
                            signetResponseMutableLiveData.setValue(obj);
                        } else {
                            Gson gson = new Gson();
                            Type type = new TypeToken<SignetResponse>() {
                            }.getType();
                            SignetResponse errorResponse = gson.fromJson(response.errorBody().string(), type);
                            signetResponseMutableLiveData.setValue(errorResponse);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        signetResponseMutableLiveData.setValue(null);
                    }
                }

                @Override
                public void onFailure(Call<SignetResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    signetResponseMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void meMerchantWallet(String walletType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BusinessWalletResponse> mCall = apiService.meMerchantWallet(walletType);
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
}



