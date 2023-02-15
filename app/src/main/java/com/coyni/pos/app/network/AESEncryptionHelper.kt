package com.coyni.pos.app.network

import kotlin.Throws
import java.nio.charset.StandardCharsets
import java.security.*
import java.util.*
import javax.crypto.*
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object AESEncryptionHelper {
    var KEY_SIZE = 32
    var IV_SIZE = 16
    var HASH_CIPHER = "AES/CBC/PKCS5Padding"
    var AES = "AES"
    var KDF_DIGEST = "MD5"
    var APPEND = "Salted__"

    @JvmStatic
    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun encrypt(password: String, plainText: String): String {
        val saltBytes = generateSalt(8)
        val key = ByteArray(KEY_SIZE)
        val iv = ByteArray(IV_SIZE)
        EvpKDF(password.toByteArray(StandardCharsets.UTF_8), KEY_SIZE, IV_SIZE, saltBytes, key, iv)
        val keyS: SecretKey = SecretKeySpec(key, AES)
        val cipher = Cipher.getInstance(HASH_CIPHER)
        val ivSpec = IvParameterSpec(iv)
        cipher.init(Cipher.ENCRYPT_MODE, keyS, ivSpec)
        val cipherText = cipher.doFinal(plainText.toByteArray())
        val sBytes = APPEND.toByteArray()
        val b = ByteArray(sBytes.size + saltBytes.size + cipherText.size)
        System.arraycopy(sBytes, 0, b, 0, sBytes.size)
        System.arraycopy(saltBytes, 0, b, sBytes.size, saltBytes.size)
        System.arraycopy(cipherText, 0, b, sBytes.size + saltBytes.size, cipherText.size)
        val bEncode = Base64.getEncoder().encode(b)
        return String(bEncode)
    }

    @Throws(
        NoSuchAlgorithmException::class,
        NoSuchPaddingException::class,
        InvalidAlgorithmParameterException::class,
        InvalidKeyException::class,
        BadPaddingException::class,
        IllegalBlockSizeException::class
    )
    fun decrypt(password: String, cipherText: String): String {
        val ctBytes = Base64.getDecoder().decode(cipherText.toByteArray())
        val saltBytes = Arrays.copyOfRange(ctBytes, 8, 16)
        val ciphertextBytes = Arrays.copyOfRange(ctBytes, 16, ctBytes.size)
        val key = ByteArray(KEY_SIZE / 8)
        val iv = ByteArray(IV_SIZE / 8)
        EvpKDF(password.toByteArray(), KEY_SIZE, IV_SIZE, saltBytes, key, iv)
        val cipher = Cipher.getInstance(HASH_CIPHER)
        val keyS: SecretKey = SecretKeySpec(key, AES)
        cipher.init(Cipher.DECRYPT_MODE, keyS, IvParameterSpec(iv))
        val plainText = cipher.doFinal(ciphertextBytes)
        return String(plainText)
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun EvpKDF(
        password: ByteArray, keySize: Int, ivSize: Int, salt: ByteArray,
        resultKey: ByteArray, resultIv: ByteArray
    ): ByteArray {
        return EvpKDF(password, keySize, ivSize, salt, 1, KDF_DIGEST, resultKey, resultIv)
    }

    @Throws(NoSuchAlgorithmException::class)
    private fun EvpKDF(
        password: ByteArray, keySize: Int, ivSize: Int, salt: ByteArray,
        iterations: Int, hashAlgorithm: String, resultKey: ByteArray,
        resultIv: ByteArray
    ): ByteArray {
        val targetKeySize = keySize + ivSize
        val derivedBytes = ByteArray(targetKeySize)
        var numberOfDerivedWords = 0
        var block: ByteArray? = null
        val hasher = MessageDigest.getInstance(hashAlgorithm)
        while (numberOfDerivedWords < targetKeySize) {
            if (block != null) {
                hasher.update(block)
            }
            hasher.update(password)
            block = hasher.digest(salt)
            hasher.reset()

            // Iterations
            for (i in 1 until iterations) {
                block = hasher.digest(block)
                hasher.reset()
            }
            System.arraycopy(
                block, 0, derivedBytes, numberOfDerivedWords,
                Math.min(block!!.size, targetKeySize - numberOfDerivedWords)
            )
            numberOfDerivedWords += block.size
        }
        System.arraycopy(derivedBytes, 0, resultKey, 0, keySize)
        System.arraycopy(derivedBytes, keySize, resultIv, 0, ivSize)
        return derivedBytes // key + iv
    }

    private fun generateSalt(length: Int): ByteArray {
        val r: Random = SecureRandom()
        val salt = ByteArray(length)
        r.nextBytes(salt)
        return salt
    }
}