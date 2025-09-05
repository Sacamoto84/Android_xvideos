package com.client.xvideos.l.ui.urlImage

import com.client.xvideos.common.AppPath
import com.client.xvideos.common.encrypting.Crypto
import com.client.xvideos.common.encrypting.Password
import com.facebook.imagepipeline.backends.okhttp3.OkHttpNetworkFetcher
import com.facebook.imagepipeline.image.EncodedImage
import com.facebook.imagepipeline.producers.Consumer
import com.facebook.imagepipeline.producers.NetworkFetcher
import com.facebook.imagepipeline.producers.ProducerContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.GCMParameterSpec


class FullCustomNetworkFetcher(
    private val okHttpClient: OkHttpClient
) : NetworkFetcher<OkHttpNetworkFetcher.OkHttpNetworkFetchState> {

    override fun createFetchState(
        consumer: Consumer<EncodedImage>,
        context: ProducerContext
    ): OkHttpNetworkFetcher.OkHttpNetworkFetchState {
        return OkHttpNetworkFetcher.OkHttpNetworkFetchState(consumer, context)
    }

    override fun fetch(
        fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState,
        callback: NetworkFetcher.Callback
    ) {

        Timber.i("!!! iii Fresco FullCustomNetworkFetcher fetch")

        val imageRequest = fetchState.context.imageRequest
        val url = imageRequest.sourceUri.toString()


        //Чтение из папки крипто
        if (url.contains("likesCrypto")) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // https://likesCrypto/имя файла
                    val fileName = url.substringAfterLast("/")

                    val file = File(AppPath.likesCrypto_l, fileName)
                    if (!file.exists()) {
                        callback.onFailure(IOException("!!! eee Fresco File not found: $url"))
                    }

                    val key = Password.key ?: throw IllegalStateException("Key is null")

                    FileInputStream(file).use { fis ->
                        val iv = ByteArray(Crypto.IV_SIZE)
                        val read = fis.read(iv)
                        if (read != Crypto.IV_SIZE) throw IOException("Cannot read IV")

                        val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
                        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_SIZE, iv))

                        CipherInputStream(fis, cipher).use { cis ->
                            //val buffer = poolFactory.newByteBuffer(cis)
                            //val bufferRef = CloseableReference.of(buffer)
                            //consumer.onNewResult(bufferRef, Consumer.IS_LAST)
                            callback.onResponse(
                                cis, 200
                            )

                        }
                    }
//
//                    file.inputStream().use { inputStream ->
//                        callback.onResponse(
//                            inputStream, 200
//                        ) // 200 чтобы имитировать успешный HTTP-ответ
//                    }

                    fetchState.submitTime = System.nanoTime()
                } catch (e: Exception) {
                    callback.onFailure(e)
                }
            }
            return
        }


        val request = Request.Builder()
            .url(url)
            //.addHeader("Authorization", "Bearer ${getAuthToken()}")
            //.addHeader("Custom-Header", "custom_value")
            .build()

        val call = okHttpClient.newCall(request)

        call.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                callback.onFailure(e)
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                try {
                    if (response.isSuccessful) {
                        response.body?.byteStream()?.let { inputStream ->
                            callback.onResponse(inputStream, response.code)
                        } ?: callback.onFailure(IOException("Empty response"))
                    } else {
                        callback.onFailure(
                            IOException("HTTP ${response.code}: ${response.message}")
                        )
                    }
                } catch (e: Exception) {
                    callback.onFailure(e)
                } finally {
                    response.close()
                }
            }
        })

        fetchState.submitTime = System.nanoTime()
    }

    override fun shouldPropagate(fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState): Boolean {
        return false
    }

    override fun onFetchCompletion(
        fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState,
        byteSize: Int
    ) {
        fetchState.responseTime = System.nanoTime()
    }

    override fun getExtraMap(
        fetchState: OkHttpNetworkFetcher.OkHttpNetworkFetchState,
        byteSize: Int
    ): Map<String, String>? {
        return mapOf(
            "responseTime" to "${fetchState.responseTime - fetchState.submitTime}",
            "imageSize" to byteSize.toString()
        )
    }

    private fun getAuthToken(): String {
        return "your_token_here"
    }
}
