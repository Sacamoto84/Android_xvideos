package com.client.xvideos.common.encrypting

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec;
import javax.security.auth.x500.X500Principal;



import android.annotation.SuppressLint;
import android.content.Context;
import android.security.KeyPairGeneratorSpec;
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.util.Base64
import java.util.Locale
import javax.crypto.CipherOutputStream
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec


/**
 *
 * ```kotlin
 *  val key = SecretKeySpec("1234567890123456".toByteArray(), "AES")
 *  val urlConnection = URL("https://example.com/image.jpg").openConnection()
 *  encryptFile(urlConnection.getInputStream(), File(AppPath.downloaded_albums_l, "album1/img.jpg"), key)
 * ```
 *
 * ```kotlin
 * val key = SecretKeySpec("1234567890123456".toByteArray(), "AES")
 *
 * val url = URL("https://example.com/image.jpg")
 * val connection = url.openConnection()
 * connection.getInputStream().use { input ->
 *     val file = File(AppPath.downloaded_albums_l, "album1/image.enc")
 *     encryptFile(input, file, key)
 * }
 * ```
 */
fun encryptFile(inputStream: InputStream, outputFile: File, secretKey: SecretKeySpec) {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)

    outputFile.parentFile?.mkdirs()

    CipherOutputStream(FileOutputStream(outputFile), cipher).use { cipherOut ->
        inputStream.copyTo(cipherOut)
    }
}

/**
 * ```kotlin
 * val key = SecretKeySpec("1234567890123456".toByteArray(), "AES")
 *
 * val originalFile = File("/storage/emulated/0/Download/test.jpg")
 * val encryptedFile = File("/storage/emulated/0/Download/test.enc")
 * val decryptedFile = File("/storage/emulated/0/Download/test_decrypted.jpg")
 *
 * // Шифруем
 * encryptFile(originalFile, encryptedFile, key)
 *
 * // Расшифровываем обратно
 * decryptFile(encryptedFile, decryptedFile, key)
 * ```
 */


fun encryptFile(inputFile: File, outputFile: File, secretKey: SecretKeySpec) {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)

    outputFile.parentFile?.mkdirs()

    FileInputStream(inputFile).use { fis ->
        CipherOutputStream(FileOutputStream(outputFile), cipher).use { cos ->
            fis.copyTo(cos)
        }
    }
}


fun main(){

    try {


        val crypto = Crypto()

        //crypto.listAlgorithms("AES")

        val key = crypto.generateAesKey()
        //println("Случайный ключ: ${Base64.encodeToString(key.encoded, Base64.NO_WRAP)}")

        val encodedKey = Base64.getEncoder().encodeToString(key.encoded)
        println("SecretKey: $encodedKey")




    }catch (e: Exception){
        e.printStackTrace()
        //Log.d(TAG, e.localizedMessage)
    }
}



class Crypto {

    private val TAG: String = Crypto::class.java.getSimpleName()

    private val DELIMITER: String = "]"

    private val CIPHER_ALGORITHM: String = "AES/CBC/PKCS5Padding"
    private val KEY_LENGTH: Int = 256

    val random: SecureRandom = SecureRandom()


    @SuppressLint("DefaultLocale")
    fun listAlgorithms(algFilter: String?) {
        val providers = Security.getProviders()
        for (p in providers) {
            val providerStr = String.format("%s/%s/%f\n", p.name, p.info, p.version)
            Log.d(TAG, providerStr)

            val services = p.services
            val algs = mutableListOf<String>()
            for (s in services) {
                val match = algFilter?.let {
                    s.algorithm.lowercase().contains(it.lowercase())
                } ?: true

                if (match) {
                    val algStr = String.format("\t%s/%s/%s", s.type, s.algorithm, s.className)
                    algs.add(algStr)
                }
            }

            algs.sort()
            for (alg in algs) {
                Log.d(TAG, "\t$alg")
            }
            Log.d(TAG, "")
        }
    }

    fun generateAesKey(): SecretKey {
        return try {
            val kg = KeyGenerator.getInstance("AES")
            kg.init(KEY_LENGTH)
            kg.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
    }

    // Генерация AES ключа
    fun generateAesKey(keyLength: Int = 256): SecretKey {
        val keyBytes = ByteArray(keyLength / 8)
        SecureRandom().nextBytes(keyBytes)
        return SecretKeySpec(keyBytes, "AES")
    }


    /**
     * Создаёт фабрику для алгоритма PBKDF2 с HMAC-SHA256.
     * Обычный пароль слишком слабый для AES, PBKDF2 делает его безопасным ключом нужной длины.
     *
     * - password.toCharArray() — сам пароль.
     * - salt — случайная последовательность байт (обеспечивает уникальность ключа даже при одинаковом пароле). val salt1 = ByteArray(16) { 0x01 }
     * - 65536 — количество итераций PBKDF2. Чем больше, тем сложнее подобрать ключ подбором.
     * - 256 — длина ключа в битах (AES-256).
     */
    // Генерация AES ключа из пароля
    fun keyFromPassword(password: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(password.toCharArray(), salt, 65536, 256)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }


}