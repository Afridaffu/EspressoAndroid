package com.coyni.mapp.network;


import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESEncryptionHelper {

    static int KEY_SIZE = 32;
    static int IV_SIZE = 16;
    static String HASH_CIPHER = "AES/CBC/PKCS5Padding";
    static String AES = "AES";
    static String KDF_DIGEST = "MD5";

    static String APPEND = "Salted__";

    public static String encrypt(String password, String plainText) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        byte[] saltBytes = generateSalt(8);
        byte[] key = new byte[KEY_SIZE];
        byte[] iv = new byte[IV_SIZE];

        EvpKDF(password.getBytes(StandardCharsets.UTF_8), KEY_SIZE, IV_SIZE, saltBytes, key, iv);

        SecretKey keyS = new SecretKeySpec(key, AES);

        Cipher cipher = Cipher.getInstance(HASH_CIPHER);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, keyS, ivSpec);
        byte[] cipherText = cipher.doFinal(plainText.getBytes());

        byte[] sBytes = APPEND.getBytes();
        byte[] b = new byte[sBytes.length + saltBytes.length + cipherText.length];
        System.arraycopy(sBytes, 0, b, 0, sBytes.length);
        System.arraycopy(saltBytes, 0, b, sBytes.length, saltBytes.length);
        System.arraycopy(cipherText, 0, b, sBytes.length + saltBytes.length, cipherText.length);

        byte[] bEncode = Base64.getEncoder().encode(b);

        return new String(bEncode);
    }

    public static String decrypt(String password, String cipherText) throws NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidAlgorithmParameterException, InvalidKeyException,
            BadPaddingException, IllegalBlockSizeException {
        byte[] ctBytes = Base64.getDecoder().decode(cipherText.getBytes());
        byte[] saltBytes = Arrays.copyOfRange(ctBytes, 8, 16);
        byte[] ciphertextBytes = Arrays.copyOfRange(ctBytes, 16, ctBytes.length);
        byte[] key = new byte[KEY_SIZE / 8];
        byte[] iv = new byte[IV_SIZE / 8];

        EvpKDF(password.getBytes(), KEY_SIZE, IV_SIZE, saltBytes, key, iv);

        Cipher cipher = Cipher.getInstance(HASH_CIPHER);
        SecretKey keyS = new SecretKeySpec(key, AES);

        cipher.init(Cipher.DECRYPT_MODE, keyS, new IvParameterSpec(iv));
        byte[] plainText = cipher.doFinal(ciphertextBytes);
        return new String(plainText);
    }

    private static byte[] EvpKDF(byte[] password, int keySize, int ivSize, byte[] salt,
                                 byte[] resultKey, byte[] resultIv) throws NoSuchAlgorithmException {
        return EvpKDF(password, keySize, ivSize, salt, 1, KDF_DIGEST, resultKey, resultIv);
    }

    private static byte[] EvpKDF(byte[] password, int keySize, int ivSize, byte[] salt,
                                 int iterations, String hashAlgorithm, byte[] resultKey,
                                 byte[] resultIv) throws NoSuchAlgorithmException {
        int targetKeySize = keySize + ivSize;
        byte[] derivedBytes = new byte[targetKeySize];
        int numberOfDerivedWords = 0;
        byte[] block = null;
        MessageDigest hasher = MessageDigest.getInstance(hashAlgorithm);
        while (numberOfDerivedWords < targetKeySize) {
            if (block != null) {
                hasher.update(block);
            }
            hasher.update(password);
            block = hasher.digest(salt);
            hasher.reset();

            // Iterations
            for (int i = 1; i < iterations; i++) {
                block = hasher.digest(block);
                hasher.reset();
            }

            System.arraycopy(block, 0, derivedBytes, numberOfDerivedWords,
                    Math.min(block.length, (targetKeySize - numberOfDerivedWords)));

            numberOfDerivedWords += block.length;
        }

        System.arraycopy(derivedBytes, 0, resultKey, 0, keySize);
        System.arraycopy(derivedBytes, keySize, resultIv, 0, ivSize);

        return derivedBytes; // key + iv
    }

    private static byte[] generateSalt(int length) {
        Random r = new SecureRandom();
        byte[] salt = new byte[length];
        r.nextBytes(salt);
        return salt;
    }
}
