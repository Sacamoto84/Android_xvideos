package com.client.xvideos.common.encrypting

import com.github.javafaker.Faker
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.timeout
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.SecretKeySpec

object Crypto {

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
    fun encryptStream(inputStream: InputStream, outputFile: File, secretKey: SecretKeySpec) {
        val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
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
        val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        outputFile.parentFile?.mkdirs()

        FileInputStream(inputFile).use { fis ->
            CipherOutputStream(FileOutputStream(outputFile), cipher).use { cos ->
                fis.copyTo(cos)
            }
        }
    }


    fun decryptFile(inputFile: File, outputFile: File, secretKey: SecretKeySpec) {
        val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
        cipher.init(Cipher.DECRYPT_MODE, secretKey)

        outputFile.parentFile?.mkdirs()

        CipherInputStream(FileInputStream(inputFile), cipher).use { cis ->
            FileOutputStream(outputFile).use { fos ->
                cis.copyTo(fos)
            }
        }
    }


    /**
     * Загружает файл по указанному [url] и шифрует его с использованием переданного AES [key].
     *
     * Функция выполняет HTTP GET-запрос через общий [HttpClient] из [handler.client],
     * устанавливает таймаут запроса в 30 секунд и шифрует загружаемый контент **на лету**,
     * сохраняя его напрямую в указанный [file]. Шифрование выполняется через [Crypto.encryptStream].
     *
     * @param url URL файла для загрузки.
     * @param file Локальный [File], в который будет записан зашифрованный контент.
     * @param key [SecretKeySpec], используемый для AES-шифрования.
     *
     * @return [Result], содержащий [Unit] в случае успеха или [Throwable] в [Result.failure],
     * если произошла ошибка во время загрузки или шифрования.
     *
     * @throws Exception Возможные исключения:
     * - [java.net.UnknownHostException] если URL недоступен.
     * - [java.io.IOException] при проблемах с чтением/записью потоков.
     * - Исключения из [javax.crypto.*], если шифрование не удалось.
     *
     * Пример использования:
     * ```
     * val result = downloadAndEncryptFile("https://example.com/image.jpg", File("/path/image.enc"), key)
     * result.onSuccess { println("Загрузка и шифрование прошли успешно!") }
     *       .onFailure { println("Ошибка: ${it.message}") }
     * ```
     */
    suspend fun downloadAndEncryptFile(url: String, file: File, key: SecretKeySpec): Result<Unit> {

        val client = HttpClient(OkHttp) {

            // Ретрай на уровне клиента (аналог retry_strategy)
            install(HttpRequestRetry) {
                maxRetries = 5
                retryIf { request, response -> response.status.value in listOf(413, 429, 500, 502, 503, 504) }
                delayMillis { retry -> retry * 1000L }  // backoff factor = 1 секунда * номер попытки
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 30000
                connectTimeoutMillis = 30000
                socketTimeoutMillis  = 30000
            }

            defaultRequest {
                headers.append("User-Agent", Faker().internet().userAgentAny())
            }

        }

        try {
            val response: HttpResponse = client.get(url){
                timeout {
                    requestTimeoutMillis = 30_000
                }
            }
            if (!response.status.isSuccess()) { return Result.failure(Exception("HTTP error: ${response.status}")) }
            // Получаем InputStream из ответа
            response.bodyAsChannel().toInputStream().use { input -> Crypto.encryptStream( inputStream = input, outputFile = file, secretKey = key ) }
            return Result.success(Unit)
        }
        catch (e : Exception){
            e.printStackTrace()
            return Result.failure(e)
        }

    }



}