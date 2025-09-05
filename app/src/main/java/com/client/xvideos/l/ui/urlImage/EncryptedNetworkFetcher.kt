package com.client.xvideos.l.ui.urlImage

import com.client.xvideos.common.encrypting.Crypto
import com.client.xvideos.common.encrypting.Password
import com.facebook.common.memory.PooledByteBufferFactory
import com.facebook.imagepipeline.image.EncodedImage
import com.facebook.imagepipeline.producers.Consumer
import com.facebook.imagepipeline.producers.FetchState
import com.facebook.imagepipeline.producers.NetworkFetcher
import com.facebook.imagepipeline.producers.ProducerContext
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.GCMParameterSpec

//object CryptoUri {
//    fun fromFile(file: File): Uri = Uri.Builder().scheme("crypto").path(file.absolutePath).build()
//}

//class CryptoFetchProducer(
//    private val poolFactory: PooledByteBufferFactory
//) : Producer<CloseableReference<PooledByteBuffer>> {
//
//    override fun produceResults(
//        consumer: Consumer<CloseableReference<PooledByteBuffer>>,
//        context: ProducerContext
//    ) {
//        try {
//            val sourceUri = context.imageRequest.sourceUri
//            val path = sourceUri.path ?: return consumer.onFailure(
//                IllegalArgumentException("No path in crypto:// URI")
//            )
//
//            val file = File(path)
//
//            val key = Password.key ?: throw IllegalStateException("Key is null")
//
//            FileInputStream(file).use { fis ->
//                val iv = ByteArray(Crypto.IV_SIZE)
//                val read = fis.read(iv)
//                if (read != Crypto.IV_SIZE) throw IOException("Cannot read IV")
//
//                val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
//                cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_SIZE, iv))
//
//                CipherInputStream(fis, cipher).use { cis ->
//                    val buffer = poolFactory.newByteBuffer(cis)
//                    val bufferRef = CloseableReference.of(buffer)
//                    consumer.onNewResult(bufferRef, Consumer.IS_LAST)
//                }
//            }
//        } catch (e: Exception) {
//            consumer.onFailure(e)
//        }
//    }
//}

class CryptoFetchState(
    consumer: Consumer<EncodedImage>,
    producerContext: ProducerContext
) : FetchState(consumer, producerContext)

class CryptoSchemeFetcher(
    private val poolFactory: PooledByteBufferFactory
) : NetworkFetcher<CryptoFetchState> {

    override fun createFetchState(
        consumer: Consumer<EncodedImage>,
        producerContext: ProducerContext
    ): CryptoFetchState = CryptoFetchState(consumer, producerContext)

    override fun fetch(
        fetchState: CryptoFetchState,
        callback: NetworkFetcher.Callback
    ) {
        try {

            Timber.i("!!! iii  Fresco CryptoSchemeFetcher fetchState:${fetchState}")

            val uri = fetchState.uri
            val file = File(uri.path ?: throw IllegalArgumentException("No path in crypto:// uri"))

            Timber.i("!!! iii  Fresco CryptoSchemeFetcher uri:${uri} file:${file}")

            FileInputStream(file).use { fis ->
                val iv = ByteArray(Crypto.IV_SIZE)
                if (fis.read(iv) != Crypto.IV_SIZE) throw Exception("Cannot read IV")

                val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
                cipher.init(Cipher.DECRYPT_MODE, Password.key, GCMParameterSpec(Crypto.TAG_SIZE, iv))

                CipherInputStream(fis, cipher).use { cis ->
                    //val bufRef = CloseableReference.of(poolFactory.newByteBuffer(cis))
                    callback.onResponse(cis, -1)
                }
            }
        } catch (e: Exception) {
            callback.onFailure(e)
        }
    }

    override fun shouldPropagate(fetchState: CryptoFetchState?): Boolean = true
    override fun onFetchCompletion(fetchState: CryptoFetchState, byteSize: Int) {}
    override fun getExtraMap(fetchState: CryptoFetchState, byteSize: Int): Map<String, String>? = null
}
