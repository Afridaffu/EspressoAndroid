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
import com.greenbox.coyni.model.preferences.UserPreference;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailRequest;
import com.greenbox.coyni.model.profile.updateemail.UpdateEmailResponse;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneRequest;
import com.greenbox.coyni.model.profile.updatephone.UpdatePhoneResponse;
import com.greenbox.coyni.model.underwriting.ActionRequiredDataResponse;
import com.greenbox.coyni.model.users.AccountLimits;
import com.greenbox.coyni.model.users.User;
import com.greenbox.coyni.model.users.UserData;
import com.greenbox.coyni.model.users.UserPreferenceModel;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnderwritingUserActionRequired extends AndroidViewModel {

    public UnderwritingUserActionRequired(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<ActionRequiredDataResponse> actionRequiredDataResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<ActionRequiredDataResponse> getUserAccountLimitsMutableLiveData() {
        return actionRequiredDataResponseMutableLiveData;
    }

    public void postactionRequired() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ActionRequiredDataResponse> mCall = apiService.postAdditionActionRequired();
            mCall.enqueue(new Callback<ActionRequiredDataResponse>() {
                @Override
                public void onResponse(Call<ActionRequiredDataResponse> call, Response<ActionRequiredDataResponse> response) {
                    if (response.isSuccessful()) {
                        ActionRequiredDataResponse obj = response.body();
                        actionRequiredDataResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ActionRequiredDataResponse>() {
                        }.getType();
                        ActionRequiredDataResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            actionRequiredDataResponseMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ActionRequiredDataResponse> call, Throwable t) {
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
