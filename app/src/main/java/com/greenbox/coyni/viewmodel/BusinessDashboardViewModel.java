package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.businesswallet.BusinessWalletResponse;
import com.greenbox.coyni.model.paymentmethods.PaymentMethodsResponse;
import com.greenbox.coyni.model.signedagreements.SignedAgreementResponse;
import com.greenbox.coyni.model.signet.SignetRequest;
import com.greenbox.coyni.model.signet.SignetResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessDashboardViewModel extends AndroidViewModel {
    private MutableLiveData<PaymentMethodsResponse> paymentMethodsResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SignetResponse> signetResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<BusinessWalletResponse> businessWalletResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SignedAgreementResponse> signedAgreementResponseMutableLiveData = new MutableLiveData<>();


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

    public void meMerchantWallet() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<BusinessWalletResponse> mCall = apiService.meMerchantWallet();
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

    public void signedAgreement(MultipartBody.Part file, RequestBody agreementType) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<SignedAgreementResponse> mCall = apiService.signedAgreement(file,agreementType);
            mCall.enqueue(new Callback<SignedAgreementResponse>() {
                @Override
                public void onResponse(Call<SignedAgreementResponse> call, Response<SignedAgreementResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            SignedAgreementResponse obj = response.body();
                            signedAgreementResponseMutableLiveData.setValue(obj);
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

}
