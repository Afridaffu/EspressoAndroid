package com.coyni.pos.app.network

import android.content.Context
import android.content.Intent
import android.util.Log
import com.coyni.pos.app.BuildConfig
import com.coyni.pos.app.model.AbstractResponse
import com.coyni.pos.app.network.AESEncryptionHelper.encrypt
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.utils.Utils.Companion.isValidJson
import com.coyni.pos.app.view.LoginActivity
import com.google.android.gms.common.util.ArrayUtils
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CustomEncryptionHandler : Interceptor {
    private val TAG = javaClass.simpleName
    private val encryptionPassword = "A#$#@123#431"
    private val methodsAllowed = arrayOf("POST", "PUT", "PATCH")
    private val errorCodes = arrayOf(500)
    private val USER_AGENT = "Coyni"
    private val APPLICATION_JSON = "application/json"
    private val TEXT_PLAIN = "text/plain"
    private val KEY_CLIENT = "client"
    private val CLIENT = "android"
    private val VERSION = "1.4"
    private val PLATFORM_TYPE = "Android"
    private val KEY_PROTOCOL_VERSION = "X-ProtocolVersion"
    private val KEY_REFERER = "Referer"
    private val KEY_ACCEPT = "Accept"
    private val KEY_USER_AGENT = "User-Agent"
    private val KEY_APP_VERSION = "App-version"
    private val KEY_ACCEPT_LANGUAGE = "Accept-Language"
    private val KEY_REQUEST_ID = "X-REQUESTID"
    private val KEY_SKIP_DECRYPTION = "SkipDecryption"
    private val KEY_CONTENT_TYPE = "Content-Type"
    private val KEY_PLATFORM_TYPE = "platform-type"
    private val KEY_REQUESTED_PORTAL = "Requested-portal"
    private val PORTAL = "mpos"
    private val LANGUAGE = "en-US"

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        var requestBody = request.body
        val method = request.method
        val randomReqId = randomRequestID
        val requestBuild: Request.Builder = request.newBuilder()
        requestBuild.header(KEY_PROTOCOL_VERSION, VERSION)
        requestBuild.header(KEY_CLIENT, CLIENT)
        requestBuild.header(KEY_REFERER, BuildConfig.Referer)
        requestBuild.header(KEY_ACCEPT, APPLICATION_JSON)
        requestBuild.header(KEY_USER_AGENT, USER_AGENT)
        requestBuild.header(
            KEY_APP_VERSION,
            "Android : " + BuildConfig.VERSION_NAME + "(" + BuildConfig.VERSION_CODE + ")"
        )
        requestBuild.header(KEY_ACCEPT_LANGUAGE, LANGUAGE)
        requestBuild.header(KEY_REQUEST_ID, randomReqId)
        requestBuild.header(KEY_PLATFORM_TYPE, PLATFORM_TYPE)
        requestBuild.header(KEY_REQUESTED_PORTAL, PORTAL)
        if (BuildConfig.SKIP_ENCRYPTION) {
            requestBuild.header(KEY_SKIP_DECRYPTION, "true")
            requestBuild.header(KEY_CONTENT_TYPE, APPLICATION_JSON)
        } else {
            requestBuild.header(KEY_CONTENT_TYPE, TEXT_PLAIN)
        }
        var response: Response? = null
        try {
            if (BuildConfig.SKIP_ENCRYPTION
                || !ArrayUtils.contains<String>(methodsAllowed, method)
                || requestBody is MultipartBody
            ) {
                response = chain.proceed(requestBuild.build())
            } else {
                requestBody = getEncryptedRequestBody(randomReqId, requestBody)
                requestBuild.method(request.method, requestBody)
                request = requestBuild.build()
                response = chain.proceed(request)
            }
            val mediaType: MediaType = APPLICATION_JSON.toMediaTypeOrNull()!!
            if (response.code != 200 && response.body != null) {
//            if (ArrayUtils.contains(errorCodes, response.code())) {
//                Utils.deploymentErrorDialog();
//            } else {
                val errorResponse = response.peekBody(2048).string()
                if (!isValidJson(errorResponse)) {
                    response = response.newBuilder()
                        .body(ResponseBody.create(mediaType, customError))
                        .build()
                } else {
                    val gson = Gson()
                    val resp = gson.fromJson(errorResponse, AbstractResponse::class.java)
                    if (resp != null && resp.error != null && (resp.error!!.errorDescription.equals(
                            Utils.ACCESS_TOKEN_EXPIRED, ignoreCase = true
                        ) || resp.error!!.errorDescription.equals(
                            Utils.TIME_EXCEEDED, ignoreCase = true
                        ))
                    ) {
                        response = response.newBuilder()
                            .body(ResponseBody.create(mediaType, ""))
                            .build()
                        //launchOnboarding(MyApplication.getContext());
                    } else if (resp != null && resp.error != null && resp.error!!.errorDescription.equals(
                            Utils.INVALID_TOKEN, ignoreCase = true
                        )
                    ) {
                        response = response.newBuilder()
                            .body(ResponseBody.create(mediaType, ""))
                            .build()
                        launchLogin(MyApplication.context);
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return response!!
    }

    private fun launchLogin(context: Context?) {
        Utils.onBoard = true
        val i = Intent(context, LoginActivity::class.java)
        i.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        context!!.startActivity(i)
    }

    private val customError: String
        private get() {
            val jObj = JSONObject()
            try {
                jObj.put("status", "error")
                jObj.put("timestamp", null)
                jObj.put("data", null)
                val jErrorObject = JSONObject()
                jErrorObject.put("errorCode", 400)
                jErrorObject.put("errorDescription", Utils.TOKEN_EXPIRED)
                jErrorObject.put("fieldErrors", null)
                jObj.put("error", jErrorObject)
            } catch (je: JSONException) {
                je.printStackTrace()
            }
            return jObj.toString()
        }

    @Throws(IOException::class)
    private fun getEncryptedRequestBody(
        randomRequestId: String,
        requestBody: RequestBody?
    ): RequestBody {
        val buffer = Buffer()
        requestBody!!.writeTo(buffer)
        var strOldBody = buffer.readUtf8()
        if (strOldBody == "") {
            strOldBody = "{}"
        }
        var strNewBody: String? = null
        Log.e("API REQUEST", strOldBody);
        val base64Str = Base64.getEncoder().encodeToString(strOldBody.toByteArray())
        val finalStr = appendDateTime(base64Str) + "." + randomRequestId
        try {
            strNewBody = encrypt(encryptionPassword, finalStr)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val mediaType: MediaType = TEXT_PLAIN.toMediaTypeOrNull()!!
        return RequestBody.create(mediaType, strNewBody!!)
    }

    private fun appendDateTime(requestData: String): String {
        val dateFormat = SimpleDateFormat("ddMMYYYY")
        val timeFormat = SimpleDateFormat("HHmmss")
        return dateFormat.format(Date()) + "." + requestData + "." + timeFormat.format(Date())
    }

    //    private void launchOnboarding(Context context) {
    private val randomRequestID: String
        private get() {
            val uuid = UUID.randomUUID()
            return uuid.toString()
        }
}