package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.actionRqrd.ActionRqrdResponse;
import com.greenbox.coyni.model.actionRqrd.SubmitActionRqrdResponse;
import com.greenbox.coyni.model.underwriting.ActionRequiredResponse;
import com.greenbox.coyni.model.underwriting.ActionRequiredSubmitResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;
import com.greenbox.coyni.utils.LogUtils;

import java.lang.reflect.Type;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnderwritingUserActionRequiredViewModel extends AndroidViewModel {

    public final String TAG = getClass().getName();

    public UnderwritingUserActionRequiredViewModel(@NonNull Application application) {
        super(application);
    }

    private MutableLiveData<ActionRequiredResponse> ActionRequiredResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ActionRqrdResponse> ActionRqrdCustRespMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<ActionRequiredSubmitResponse> ActionRequiredSubmitResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<SubmitActionRqrdResponse> ActRqrdSubmitResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<SubmitActionRqrdResponse> getActRqrdSubmitResponseMutableLiveData() {
        return ActRqrdSubmitResponseMutableLiveData;
    }

    public MutableLiveData<ActionRequiredResponse> getUserAccountLimitsMutableLiveData() {
        return ActionRequiredResponseMutableLiveData;
    }

    public MutableLiveData<ActionRequiredSubmitResponse> getActionRequiredSubmitResponseMutableLiveData() {
        return ActionRequiredSubmitResponseMutableLiveData;
    }

    public MutableLiveData<ActionRqrdResponse> getActionRqrdCustRespMutableLiveData() {
        return ActionRqrdCustRespMutableLiveData;
    }

    public void getAdditionalActionRequiredData() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ActionRequiredResponse> mCall = apiService.postAdditionActionRequired();

            mCall.enqueue(new Callback<ActionRequiredResponse>() {

                @Override
                public void onResponse(Call<ActionRequiredResponse> call, Response<ActionRequiredResponse> response) {
                    LogUtils.d(TAG, "" + response);
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
                    LogUtils.d(TAG, "UnderwritingUserActionRequired" + t.getMessage());
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void submitActionRequired(MultipartBody.Part[] requestBody, RequestBody underWriting) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
//            LogUtils.d(TAG, "submitActionRequired" + documentsImageList);

            Call<ActionRequiredSubmitResponse> mCall = apiService.submitActionRequired(requestBody, underWriting);
            mCall.enqueue(new Callback<ActionRequiredSubmitResponse>() {
                @Override
                public void onResponse(Call<ActionRequiredSubmitResponse> call, Response<ActionRequiredSubmitResponse> response) {
                    LogUtils.d(TAG, "submitActionRequired" + response);
                    if (response.isSuccessful()) {
                        ActionRequiredSubmitResponse obj = response.body();
                        ActionRequiredSubmitResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ActionRequiredSubmitResponse>() {
                        }.getType();
                        ActionRequiredSubmitResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            ActionRequiredSubmitResponseMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ActionRequiredSubmitResponse> call, Throwable t) {
                    LogUtils.d(TAG, "submitActionRequired" + t.getMessage());
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getActionRequiredCustData() {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<ActionRqrdResponse> mCall = apiService.getActionRqrdCust();

            mCall.enqueue(new Callback<ActionRqrdResponse>() {

                @Override
                public void onResponse(Call<ActionRqrdResponse> call, Response<ActionRqrdResponse> response) {
                    LogUtils.d(TAG, "" + response);
                    if (response.isSuccessful()) {
                        ActionRqrdResponse obj = response.body();
                        ActionRqrdCustRespMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<ActionRqrdResponse>() {
                        }.getType();
                        ActionRqrdResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            ActionRqrdCustRespMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<ActionRqrdResponse> call, Throwable t) {
                    LogUtils.d(TAG, "UnderwritingUserActionRequired" + t.getMessage());
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    ActionRqrdCustRespMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void submitActionRequiredCustomer(MultipartBody.Part[] requestBody, RequestBody underWriting) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
//            LogUtils.d(TAG, "submitActionRequired" + documentsImageList);
            Call<SubmitActionRqrdResponse> mCall = apiService.submitActRqrd(requestBody, underWriting);

            mCall.enqueue(new Callback<SubmitActionRqrdResponse>() {
                @Override
                public void onResponse(Call<SubmitActionRqrdResponse> call, Response<SubmitActionRqrdResponse> response) {
                    LogUtils.d(TAG, "submitActionRequired" + response);
                    if (response.isSuccessful()) {
                        SubmitActionRqrdResponse obj = response.body();
                        ActRqrdSubmitResponseMutableLiveData.setValue(obj);
                    } else {
                        Gson gson = new Gson();
                        Type type = new TypeToken<SubmitActionRqrdResponse>() {
                        }.getType();
                        SubmitActionRqrdResponse errorResponse = gson.fromJson(response.errorBody().charStream(), type);
                        if (errorResponse != null) {
                            ActRqrdSubmitResponseMutableLiveData.setValue(errorResponse);
                        }
                    }
                }

                @Override
                public void onFailure(Call<SubmitActionRqrdResponse> call, Throwable t) {
                    LogUtils.d(TAG, "submitActionRequired" + t.getMessage());
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
