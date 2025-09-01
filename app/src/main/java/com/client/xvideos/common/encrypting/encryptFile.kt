package com.client.xvideos.common.encrypting

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
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