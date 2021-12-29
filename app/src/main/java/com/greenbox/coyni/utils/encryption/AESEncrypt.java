package com.greenbox.coyni.utils.encryption;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
//import org.apache.tomcat.util.buf.HexUtils;

public class AESEncrypt {
    private static final int SALT_LENGTH_BYTE = 16;

    public static EncryptRequest encryptPayload(String autoGenID, String payload, String rsaPublicKey)
            throws Exception {

        // 16 bytes salt
        byte[] salt = CryptoUtils.getRandomNonce(SALT_LENGTH_BYTE);

        // GCM recommended 12 bytes iv?
        byte[] iv = rsaPublicKey.substring(0, 16).getBytes();

        // secret key from password
        SecretKey aesKey = CryptoUtils.getAESKeyFromPassword(autoGenID.toCharArray(), salt);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        // ASE-GCM needs GCMParameterSpec
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(iv));

        byte[] cipherText = cipher.doFinal(payload.getBytes());

        // prefix IV and Salt to cipher text
        byte[] cipherTextWithIvSalt = ByteBuffer.allocate(iv.length + salt.length + cipherText.length).put(iv).put(salt)
                .put(cipherText).array();

        EncryptRequest er = new EncryptRequest();
        er.setEncryptData(Base64.getEncoder().encodeToString(cipherText));

        //er.setEncryptKey(CryptoUtils.encryptWithRsaPublicKey(HexUtils.toHexString(aesKey.getEncoded()).getBytes(), rsaPublicKey));
        er.setEncryptKey(CryptoUtils.encryptWithRsaPublicKey(convertByteToHexadecimal(aesKey.getEncoded()).getBytes(), rsaPublicKey));
        return er;
    }

    private static String convertByteToHexadecimal(byte[] byteArray) {
        String hex = "";
        // Iterating through each byte in the array
        for (byte i : byteArray) {
            hex += String.format("%02X", i);
        }
        return hex;
    }

//    public static String decryptPayload(final String key, final String enPayload, final String privateKey, final String publicKey) throws Exception {
//        final String encAesKeyBase64 = key;
//        final byte[] encAesKeyBytes = Base64.getDecoder().decode(encAesKeyBase64.getBytes());
//        final byte[] decryptedAesKeyHex = CryptoUtils.decryptWithPrivateRsaKey(encAesKeyBytes, privateKey);
//        final byte[] decryptedAesKey = HexUtils.fromHexString(new String(decryptedAesKeyHex));
//        final byte[] iv = publicKey.substring(0, 16).getBytes();
//        final byte[] payloadInBytes = Base64.getDecoder().decode(enPayload);
//        final byte[] decryptedPayloadInBytes = CryptoUtils.decryptWithAes(payloadInBytes, decryptedAesKey, iv);
//        final String payload = new String(decryptedPayloadInBytes);
//        return payload;
//    }
}
