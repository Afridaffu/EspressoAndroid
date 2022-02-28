package com.greenbox.coyni.network;

import androidx.annotation.NonNull;

import com.google.android.gms.common.util.ArrayUtils;
import com.greenbox.coyni.BuildConfig;
import com.greenbox.coyni.utils.LogUtils;
import com.greenbox.coyni.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

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
import okhttp3.ResponseBody;
import okio.Buffer;

public class CustomEncryptionHandler implements Interceptor {

    private final String TAG = getClass().getSimpleName();
    private final String encryptionPassword = "A#$#@123#431";
    private final String[] methodsAllowed = {"POST", "PUT", "PATCH"};
    private final String USER_AGENT = "Coyni";
    private final String APPLICATION_JSON = "application/json";
    private final String TEXT_PLAIN = "text/plain";

    private final String KEY_CLIENT = "client";
    private final String CLIENT = "android";
    private final String VERSION = "1.4";
    private final String KEY_PROTOCOL_VERSION = "X-ProtocolVersion";
    private final String KEY_REFERER = "Referer";
    private final String KEY_ACCEPT = "Accept";
    private final String KEY_USER_AGENT = "User-Agent";
    private final String KEY_APP_VERSION = "App-version";
    private final String KEY_ACCEPT_LANGUAGE = "Accept-Language";
    private final String KEY_REQUEST_ID = "X-REQUESTID";
    private final String KEY_SKIP_DECRYPTION = "SkipDecryption";
    private final String KEY_CONTENT_TYPE = "Content-Type";

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody requestBody = request.body();
        String method = request.method();
        String randomReqId = getRandomRequestID();

        Request.Builder requestBuild = request.newBuilder();
        requestBuild.header(KEY_PROTOCOL_VERSION, VERSION);
        requestBuild.header(KEY_CLIENT, CLIENT);
        requestBuild.header(KEY_REFERER, Utils.getStrReferer());
        requestBuild.header(KEY_ACCEPT, APPLICATION_JSON);
        requestBuild.header(KEY_USER_AGENT, USER_AGENT);
        requestBuild.header(KEY_APP_VERSION, Utils.getAppVersion());
        requestBuild.header(KEY_ACCEPT_LANGUAGE, Utils.getStrLang());
        requestBuild.header(KEY_REQUEST_ID, randomReqId);

        if (BuildConfig.SKIP_ENCRYPTION) {
            requestBuild.header(KEY_SKIP_DECRYPTION, "true");
            requestBuild.header(KEY_CONTENT_TYPE, APPLICATION_JSON);
        } else {
            requestBuild.header(KEY_CONTENT_TYPE, TEXT_PLAIN);
        }

        /*Encryption should be skipped when SKIP_ENCRYPTION is set to true and for GET requests.
         As per the requirement while developing this feature, encryption should not be done for
         Multipart requests. Changes needs to done for Multipart requests based on Backend changes*/
        Response response = null;
        if (BuildConfig.SKIP_ENCRYPTION || !ArrayUtils.contains(methodsAllowed, method)
                || requestBody instanceof MultipartBody) {
            response = chain.proceed(requestBuild.build());
        } else {
            requestBody = getEncryptedRequestBody(randomReqId, requestBody);
            requestBuild.method(request.method(), requestBody);
            request = requestBuild.build();
            response = chain.proceed(request);
        }
        MediaType mediaType = MediaType.parse(APPLICATION_JSON);
        if (response.code() == 400 && response.body() != null) {
            String errorResponse = response.peekBody(2048).string();
            if (!Utils.isValidJson(errorResponse)) {
                response = response.newBuilder()
                        .body(ResponseBody.create(getCustomError(), mediaType))
                        .build();
            }
        }
        return response;
    }

    private String getCustomError() {
        JSONObject jObj = new JSONObject();
        try {
            jObj.put("status", "error");
            jObj.put("timestamp", null);
            jObj.put("data", null);

            JSONObject jErrorObject = new JSONObject();
            jErrorObject.put("errorCode", 400);
            jErrorObject.put("errorDescription", "Something went wrong. Please try again");
            jErrorObject.put("fieldErrors", null);
            jObj.put("error", jErrorObject);
        } catch (JSONException je) {
            je.printStackTrace();
        }
        return jObj.toString();
    }

    private RequestBody getEncryptedRequestBody(String randomRequestId, RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        String strOldBody = buffer.readUtf8();
        String strNewBody = null;
        String base64Str = java.util.Base64.getEncoder().encodeToString(strOldBody.getBytes());
        String finalStr = appendDateTime(base64Str) + "." + randomRequestId;
        try {
            strNewBody = AESEncryptionHelper.encrypt(encryptionPassword, finalStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaType mediaType = MediaType.parse(TEXT_PLAIN);
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
