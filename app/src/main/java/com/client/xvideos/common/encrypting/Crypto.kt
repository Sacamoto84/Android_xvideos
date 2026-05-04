package com.client.xvideos.common.encrypting

import com.client.xvideos.common.net.UserAgentProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.isSuccess
import io.ktor.utils.io.jvm.javaio.toInputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

object Crypto {

    const val IV_SIZE = 12 // рекомендовано для GCM
    const val TAG_SIZE = 128 // 16 байт аутентификационного тега

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
     *
     * Шифрует файл с помощью AES/GCM/NoPadding.
     *
     * @param inputFile исходный файл (например, jpg/gif)
     * @param outputFile куда сохранять зашифрованный файл
     * @param secretKey AES-ключ (256 бит)
     */
    fun encryptFile(inputFile: File, outputFile: File, secretKey: SecretKeySpec) : Result<Unit> {

        try {
            val iv = ByteArray(IV_SIZE)
            SecureRandom().nextBytes(iv)
            val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(TAG_SIZE, iv))
            FileInputStream(inputFile).use { fis ->
                FileOutputStream(outputFile).use { fos ->
                    // Сначала записываем IV в начало файла
                    fos.write(iv)
                    CipherOutputStream(fos, cipher).use { cos ->
                        fis.copyTo(cos, bufferSize = 8192)
                    }
                }
            }

            return Result.success(Unit)
        }catch (e: Exception){
            return Result.failure(e)
        }
    }

    /**
     * Расшифровывает файл с помощью AES/GCM/NoPadding.
     *
     * @param inputFile зашифрованный файл
     * @param outputFile куда сохранять расшифрованные данные
     * @param secretKey AES-ключ (256 бит)
     */
    fun decryptFile(inputFile: File, outputFile: File, secretKey: SecretKeySpec) {
        FileInputStream(inputFile).use { fis ->
            val iv = ByteArray(IV_SIZE)
            fis.read(iv) // читаем IV из файла

            val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(TAG_SIZE, iv))

            CipherInputStream(fis, cipher).use { cis ->
                FileOutputStream(outputFile).use { fos ->
                    cis.copyTo(fos, bufferSize = 8192)
                }
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
    suspend fun downloadAndEncryptFile(
        url: String,
        file: File,
        key: SecretKeySpec,
        requestHeaders: Map<String, String> = emptyMap()
    ): Result<Unit> {

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
                requestHeaders.forEach { (key, value) -> headers.append(key, value) }
                if (!requestHeaders.containsKey("User-Agent")) {
                    headers.append("User-Agent", UserAgentProvider.randomDesktopBrowser())
                }
            }

        }

        return try {
            val response: HttpResponse = client.get(url)
            if (!response.status.isSuccess()) {
                return Result.failure(Exception("HTTP error: ${response.status}"))
            }

            // Генерация IV
            val iv = ByteArray(IV_SIZE)
            SecureRandom().nextBytes(iv)

            val i = Password.CIPHER_ALGORITHM

            val cipher = Cipher.getInstance(i)
            cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(TAG_SIZE, iv))

            // Читаем из сети и пишем зашифрованное сразу в файл
            response.bodyAsChannel().toInputStream().use { input ->
                FileOutputStream(file).use { fos ->
                    // Сначала пишем IV
                    fos.write(iv)

                    CipherOutputStream(fos, cipher).use { cos ->
                        input.copyTo(cos, bufferSize = 8192)


                    }
                }
            }
            client.close()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
        finally {
            client.close()
        }
    }

}
