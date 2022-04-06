package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.bank.BankResponse;
import com.greenbox.coyni.model.bank.SignOn;
import com.greenbox.coyni.model.bank.SyncAccount;
import com.greenbox.coyni.model.identity_verification.IdentityImageResponse;
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailRequest;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneRequest;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneResponse;
import com.greenbox.coyni.model.underwriting.ActionRequiredDataResponse;
import com.greenbox.coyni.model.underwriting.ActionRequiredResponse;
import com.greenbox.coyni.model.users.AccountLimits;
import com.greenbox.coyni.model.users.User;
import com.greenbox.coyni.model.users.UserData;
import com.greenbox.coyni.model.users.UserPreferenceModel;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;
import com.greenbox.coyni.utils.LogUtils;

import java.lang.reflect.Type;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnderwritingUserActionRequired extends AndroidViewModel {

    public UnderwritingUserActionRequired(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<ActionRequiredResponse> ActionRequiredResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ActionRequiredResponse> ActionRequiredSubmitResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<ActionRequiredResponse> getUserAccountLimitsMutableLiveData() {
        return ActionRequiredResponseMutableLiveData;
    }
 public MutableLiveData<ActionRequiredResponse> getSubmitActionRequired() {
        return ActionRequiredSubmitResponseMutableLiveData;
    }


    public void postactionRequired() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ActionRequiredResponse> mCall = apiService.postAdditionActionRequired();

            mCall.enqueue(new Callback<ActionRequiredResponse>() {

                @Override
                public void onResponse(Call<ActionRequiredResponse> call, Response<ActionRequiredResponse> response) {
                    LogUtils.d("UnderwritingUserActionRequired",""+response);
                    if (response.isSuccessful()) {
                        ActionRequiredResponse obj = response.body();
                        ActionRequiredResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ActionRequiredResponse>() {
                        }.getType();
                        ActionRequiredResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            ActionRequiredResponseMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ActionRequiredResponse> call, Throwable t) {
                    LogUtils.d("UnderwritingUserActionRequired","UnderwritingUserActionRequired"+t.getMessage());
                    LogUtils.d("UnderwritingUserActionRequired","UnderwritingUserActionRequired44"+t.toString());
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void submitActionRequired(RequestBody information, MultipartBody.Part[] documentsImageList) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            LogUtils.d("submitActionRequired","submitActionRequired"+documentsImageList);
            LogUtils.d("submitActionRequired","submitActionRequired"+information.toString());
            Call<ActionRequiredResponse> mCall = apiService.submitActionrequired(information, documentsImageList);
            mCall.enqueue(new Callback<ActionRequiredResponse>() {
                @Override
                public void onResponse(Call<ActionRequiredResponse> call, Response<ActionRequiredResponse> response) {
                    LogUtils.d("UderWritingUserActionRequired","submitActionRequired"+response);
                    if (response.isSuccessful()) {
                        ActionRequiredResponse obj = response.body();
                        ActionRequiredSubmitResponseMutableLiveData.setValue(obj);


                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ActionRequiredResponse>() {
                        }.getType();
                        ActionRequiredResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            ActionRequiredSubmitResponseMutableLiveData.setValue(errorResponse);

                        }
                    }
                }

                @Override
                public void onFailure(Call<ActionRequiredResponse> call, Throwable t) {
                    LogUtils.d("UderWritingUserActionRequired","submitActionRequired"+t.getMessage());
                    LogUtils.d("UderWritingUserActionRequired","submitActionRequiredeeee"+t.getLocalizedMessage());
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
