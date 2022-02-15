package com.greenbox.coyni.network;


import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class EncryptionInterceptor implements Interceptor {

    String password = "A#$#@123#431";
    private static final int pswdIterations = 1;
    private static final int keySize = 128;
    private static final String cypherInstance = "AES/CBC/PKCS5Padding";
    private static final String secretKeyInstance = "PBKDF2WithHmacSHA1";
    private static final String plainText = "A#$#@123#431";
    private static final String AESSalt = "exampleSalt";
    private static final String initializationVector = "8119745113154120";

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        RequestBody oldBody = request.body();
        Buffer buffer = new Buffer();
        oldBody.writeTo(buffer);
        String strOldBody = buffer.readUtf8();
        String strNewBody = null;
        String randomReqId = getRandomRequestID();
        String base64Str = java.util.Base64.getEncoder().encodeToString(strOldBody.getBytes(StandardCharsets.UTF_8));
        String finalStr = appendDateTime(base64Str) + "." + randomReqId;
        try {
            strNewBody = encrypt(finalStr);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType, strNewBody);
        request = request
                .newBuilder()
                .header("Content-Type", body.contentType().toString())
                .header("Content-Length", String.valueOf(body.contentLength()))
                .header("X-REQUESTID", randomReqId)
                .method(request.method(), body).build();

        return chain.proceed(request);
    }

    public String encrypt(String data) throws Exception {
        SecretKeySpec keySpec = new SecretKeySpec(password.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.ENCRYPT_MODE, getKeyFromPassword(password, "656465467"), new IvParameterSpec(initializationVector.getBytes()));
        byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.encodeToString(encrypted, Base64.NO_WRAP);
    }

//    public String encrypt1(String data) throws Exception {
//        Cipher cipher = Cipher.getInstance("AES");
//        cipher.init(Cipher.ENCRYPT_MODE, getKeyFromPassword(password, "12345678"), generateIv());// generateIv());
//        byte[] cipherText = cipher.doFinal(data.getBytes());
//        return Base64.getEncoder().encodeToString(cipherText);
//    }

    public static SecretKey getKeyFromPassword(String password, String salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), pswdIterations, keySize);
        SecretKey secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
        return secret;
    }

//    private SecretKey getSecretKey() throws NoSuchAlgorithmException {
//        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
//        keyGenerator.init(128);
//        SecretKey key = keyGenerator.generateKey();
//        return key;
//    }

//    public IvParameterSpec generateIv() {
//        byte[] iv = new byte[16];
//        new SecureRandom().nextBytes(iv);
//        return new IvParameterSpec(iv);
//    }

    private static byte[] getRaw(String plainText, String salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyInstance);
            KeySpec spec = new PBEKeySpec(plainText.toCharArray(), salt.getBytes(), pswdIterations, keySize);
            return factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static String requestBodyToString(RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return buffer.readUtf8();
    }

    private String appendDateTime(String requestData) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMYYYY");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");
        return dateFormat.format(new Date()) + "." + requestData + "." + timeFormat.format(new Date());
    }

    private String getRandomRequestID() {
        int randomPIN = (int)(Math.random()*9000)+1000;
        return String.valueOf(randomPIN);
    }

}
