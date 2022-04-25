package com.greenbox.coyni.viewmodel;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.greenbox.coyni.BuildConfig;
import com.greenbox.coyni.model.APIError;
import com.greenbox.coyni.model.underwriting.ActionRequiredResponse;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.UUID;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
    private MutableLiveData<ActionRequiredResponse> ActionRequiredSubmitResponseMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<APIError> apiErrorMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<ActionRequiredResponse> getUserAccountLimitsMutableLiveData() {
        return ActionRequiredResponseMutableLiveData;
    }

    public MutableLiveData<ActionRequiredResponse> getActionRequiredSubmitResponseMutableLiveData() {
        return ActionRequiredSubmitResponseMutableLiveData;
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

    public void submitActionRequired(MultipartBody documentsImageList) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            LogUtils.d(TAG, "submitActionRequired" + documentsImageList);

            Call<ActionRequiredResponse> mCall = apiService.submitActionRequired(documentsImageList);
            mCall.enqueue(new Callback<ActionRequiredResponse>() {
                @Override
                public void onResponse(Call<ActionRequiredResponse> call, Response<ActionRequiredResponse> response) {
                    LogUtils.d(TAG, "submitActionRequired" + response);
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
                    LogUtils.d(TAG, "submitActionRequired" + t.getMessage());
                    Toast.makeText(getApplication(), "something went wrong", Toast.LENGTH_LONG).show();
                    apiErrorMutableLiveData.setValue(null);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void submitAdditionalActionRequired(MultipartBody requestBody) {
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url(BuildConfig.URL_PRODUCTION + "api/v2/underwriting/user/business/action-required")
                .method("POST", requestBody)
                .addHeader("Accept-Language", "en-us")
                .addHeader("SkipDecryption", "true")
                .addHeader("X-REQUESTID", UUID.randomUUID().toString())
                .addHeader("Requested-portal", "")
                .addHeader("Referer", Utils.getStrReferer())
                .addHeader("Authorization", "Bearer " + Utils.getStrAuth())
                .build();

        LogUtils.d(TAG, "request" + request);
        LogUtils.d(TAG, "upload" + requestBody.toString());

        okhttp3.Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                LogUtils.d(TAG, "callback" + e.getMessage());
                ActionRequiredSubmitResponseMutableLiveData.postValue(null);
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
                LogUtils.d(TAG, "callback" + response.body());
                try {
                    Gson gson = new Gson();
                    ActionRequiredResponse res = gson.fromJson(response.body().string(), ActionRequiredResponse.class);
                    ActionRequiredSubmitResponseMutableLiveData.postValue(res);
                } catch (Exception e) {
                    ActionRequiredSubmitResponseMutableLiveData.postValue(null);
                }

            }
        });
    }

}
