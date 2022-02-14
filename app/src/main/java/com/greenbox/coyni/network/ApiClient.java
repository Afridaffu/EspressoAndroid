package com.greenbox.coyni.network;

import android.util.Log;

import com.google.gson.Gson;
import com.greenbox.coyni.utils.Utils;

import java.io.IOException;
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


public class ApiClient {
    public static final String TYPE_NO_NETWORK = "NO_NETWORK";
    public static final String TYPE_CONNECTION_INTERRUPT = "CONNECTION_INTERRUPT";
    public static final String TYPE_SESSION_TIMEOUT = "SESSION_TIMEOUT";
    private static final String TYPE_SOMETHING_WENT_WRONG = "WENT_WRONG";
    private final int TIME_OUT = 120;


    private HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);
    private TokenInterceptor tokenInterceptor = new TokenInterceptor();

    private OkHttpClient client = new OkHttpClient.Builder().
            connectTimeout(TIME_OUT, TimeUnit.SECONDS).
            readTimeout(TIME_OUT, TimeUnit.SECONDS).
            addInterceptor(tokenInterceptor).
            addInterceptor(interceptor).
            build();

    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Utils.getStrURL_PRODUCTION())
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    private static ApiClient apiClient;

    public static Retrofit getInstance() {
        if (apiClient == null)
            apiClient = new ApiClient();
        return apiClient.retrofit;
    }

    public static class NoConnectivityException extends IOException {

        @Override
        public String getMessage() {
            return TYPE_NO_NETWORK;
        }

    }

    private static class TokenInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws NoConnectivityException, SessionTimeOutException, ConnectionInterruptedException {
            Request initialRequest = chain.request();
            String CLIENT = "android";
            String KEY_CLIENT = "client";
            String VERSION = "1.4";
            String KEY_PROTOCOL_VERSION = "X-ProtocolVersion";
            Request.Builder requestBuild = initialRequest.newBuilder()
                    .header(KEY_PROTOCOL_VERSION, VERSION)
                    .addHeader(KEY_CLIENT, CLIENT)
                    .addHeader("Referer", Utils.getStrReferer())
                    .addHeader("Accept", "application/json")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("User-Agent", "Coyni")
                    .addHeader("App-version", Utils.getAppVersion())
                    .addHeader("SkipDecryption", Utils.getStrDesc())
                    .addHeader("Accept-Language", Utils.getStrLang())
                    .addHeader("X-REQUESTID", Utils.getStrCode());

            initialRequest = requestBuild.build();

            Response response = null;
            try {
                response = chain.proceed(initialRequest);
            } catch (SocketTimeoutException ste) {
                throw new SessionTimeOutException();
            } catch (SSLHandshakeException she) {
                throw new ConnectionInterruptedException(TYPE_CONNECTION_INTERRUPT);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new ConnectionInterruptedException(TYPE_SOMETHING_WENT_WRONG);
            }
            Log.e("resp auth",new Gson().toJson(response.code()));
            return response;
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
