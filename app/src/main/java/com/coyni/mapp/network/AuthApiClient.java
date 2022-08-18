package com.coyni.mapp.network;

import com.google.gson.Gson;
import com.coyni.mapp.BuildConfig;
import com.coyni.mapp.utils.LogUtils;
import com.coyni.mapp.utils.Utils;

import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthApiClient {
    private final String TAG = getClass().getSimpleName();
    public static final String TYPE_NO_NETWORK = "NO_NETWORK";
    public static final String TYPE_CONNECTION_INTERRUPT = "CONNECTION_INTERRUPT";
    public static final String TYPE_SESSION_TIMEOUT = "SESSION_TIMEOUT";
    private static final String TYPE_SOMETHING_WENT_WRONG = "WENT_WRONG";
    //    private final static String URL_PRODUCTION = "http://3.94.123.86:9001/";  //QA
//    private final static String URL_PRODUCTION = "https://api-stg.coyni.com/";  //SAT
    //    private final static String URL_PRODUCTION = "https://api.coyni.com/";  //UAT
//    private static final String Referer = "http://mobile/"; //QA
//    private static final String Referer = "https://members.coyni.com"; //SAT && //UAT
    private final int TIME_OUT = 120;
    private static final String KEY_AUTHORIZATION = "Authorization";


    private HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(
            BuildConfig.LOGGING_ENABLED ? HttpLoggingInterceptor.Level.BODY
                    : HttpLoggingInterceptor.Level.NONE);
    private AuthApiClient.TokenInterceptor tokenInterceptor = new AuthApiClient.TokenInterceptor();
    private CustomEncryptionHandler encryptionInterceptor = new CustomEncryptionHandler();

    private final OkHttpClient client = new OkHttpClient.Builder().
            connectTimeout(TIME_OUT, TimeUnit.SECONDS).
            readTimeout(TIME_OUT, TimeUnit.SECONDS).
            addInterceptor(tokenInterceptor).
            addInterceptor(encryptionInterceptor).
            addInterceptor(interceptor).
            build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BuildConfig.URL_PRODUCTION)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static AuthApiClient apiClient;

    public static Retrofit getInstance() {
        if (apiClient == null)
            apiClient = new AuthApiClient();
        return apiClient.retrofit;
    }

    private static class TokenInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws ApiClient.NoConnectivityException, ApiClient.SessionTimeOutException, ApiClient.ConnectionInterruptedException {
            Request initialRequest = chain.request();
            Request.Builder requestBuild = initialRequest.newBuilder()
                    .addHeader(KEY_AUTHORIZATION, "Bearer " + Utils.getStrAuth());
            initialRequest = requestBuild.build();
            Response response = null;
            try {
                response = chain.proceed(initialRequest);
            } catch (SocketTimeoutException ste) {
                throw new ApiClient.SessionTimeOutException();
            } catch (SSLHandshakeException she) {
                throw new ApiClient.ConnectionInterruptedException(TYPE_CONNECTION_INTERRUPT);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new ApiClient.ConnectionInterruptedException(TYPE_SOMETHING_WENT_WRONG);
            }
            LogUtils.e("resp auth", new Gson().toJson(response.code()));
            return response;
        }
    }
}
