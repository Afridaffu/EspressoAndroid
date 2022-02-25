package com.greenbox.coyni.network;


import androidx.annotation.NonNull;

import com.google.android.gms.common.util.ArrayUtils;
import com.greenbox.coyni.BuildConfig;
import com.greenbox.coyni.utils.LogUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class EncryptionInterceptor implements Interceptor {

    private final String TAG = getClass().getSimpleName();
    private final String encryptionPassword = "A#$#@123#431";
    private final String[] methodsAllowed = {"POST", "PUT", "PATCH"};
    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        String method = request.method();
        String randomReqId = getRandomRequestID();

          /*Encryption should be skipped when SKIP_ENCRYPTION is set to true and for GET requests.
          As per the requirement while developing this feature, encryption should not be done for
          Multipart requests. Changes needs to done for Multipart requests based on Backend changes*/
        if(BuildConfig.SKIP_ENCRYPTION || !ArrayUtils.contains(methodsAllowed, method)
                || requestBody instanceof MultipartBody) {
            Request newRequest = request.newBuilder()
                    .addHeader("X-REQUESTID", randomReqId)
                    .build();
            return chain.proceed(newRequest);
        }

        requestBody = getEncryptedRequestBody(randomReqId, requestBody);

        Request.Builder builder = request.newBuilder();
        //builder.headers(request.headers());
        builder.header("X-REQUESTID", randomReqId);

        builder.method(request.method(), requestBody);
        request = builder.build();
        return chain.proceed(request);
    }

    private RequestBody getEncryptedRequestBody(String randomRequestId, RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        String strOldBody = buffer.readUtf8();
        String strNewBody = null;
        LogUtils.v(TAG, "BNR Old Body " + strOldBody);
        String base64Str = java.util.Base64.getEncoder().encodeToString(strOldBody.getBytes());
        String finalStr = appendDateTime(base64Str) + "." + randomRequestId;
        try {
            strNewBody = AESEncryptionHelper.encrypt(encryptionPassword, finalStr);
            LogUtils.v(TAG, "BNR New Body " + strNewBody);
        } catch (Exception e) {
            LogUtils.e(TAG, "BNR Encryption Exception " + e.getMessage());
            e.printStackTrace();
        }

        MediaType mediaType = MediaType.parse("text/plain");
        return RequestBody.create(strNewBody, mediaType);
    }

    private String appendDateTime(String requestData) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMYYYY");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        return dateFormat.format(new Date()) + "." + requestData + "." + timeFormat.format(new Date());
    }

    private String getRandomRequestID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

}
