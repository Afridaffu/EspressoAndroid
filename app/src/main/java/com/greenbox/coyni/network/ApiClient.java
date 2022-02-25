package com.greenbox.coyni.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.greenbox.coyni.BuildConfig;
import com.greenbox.coyni.utils.Utils;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLHandshakeException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {
    public static final String TYPE_NO_NETWORK = "NO_NETWORK";
    public static final String TYPE_CONNECTION_INTERRUPT = "CONNECTION_INTERRUPT";
    public static final String TYPE_SESSION_TIMEOUT = "SESSION_TIMEOUT";
    private static final String TYPE_SOMETHING_WENT_WRONG = "WENT_WRONG";
    private final int TIME_OUT = 120;

    private HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(
            BuildConfig.LOGGING_ENABLED ? HttpLoggingInterceptor.Level.BODY
                    : HttpLoggingInterceptor.Level.NONE);

    private CustomEncryptionHandler encryptionInterceptor = new CustomEncryptionHandler();

    private OkHttpClient client = new OkHttpClient.Builder().
            connectTimeout(TIME_OUT, TimeUnit.SECONDS).
            readTimeout(TIME_OUT, TimeUnit.SECONDS).
            addInterceptor(encryptionInterceptor).
                    addInterceptor(interceptor).
                    build();

    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Utils.getStrURL_PRODUCTION())
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();
    private static ApiClient apiClient;

    public static Retrofit getInstance() {
        if (apiClient == null) {
            apiClient = new ApiClient();
        }
        return apiClient.retrofit;
    }

    public static class NoConnectivityException extends IOException {

        @Override
        public String getMessage() {
            return TYPE_NO_NETWORK;
        }

    }

    public static class ConnectionInterruptedException extends SSLHandshakeException {

        /**
         * Constructs an exception reporting an error found by
         * an SSL subsystem during handshaking.
         *
         * @param reason describes the problem.
         */
        ConnectionInterruptedException(String reason) {
            super(reason);
        }

        @Override
        public String getMessage() {
            return TYPE_CONNECTION_INTERRUPT;
        }

    }

    public static class SessionTimeOutException extends SocketTimeoutException {
        @Override
        public String getMessage() {
            return TYPE_SESSION_TIMEOUT;
        }
    }

}
