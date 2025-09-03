package com.client.xvideos.common.encrypting

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.signature.ObjectKey
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.GCMParameterSpec

@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        //val key = SecretKeySpec("1234567890123456".toByteArray(), "AES")
        registry.append( EncryptedFileModel::class.java, InputStream::class.java, EncryptedFileModelLoaderFactory() )
    }
}

class EncryptedFileModel(val file: File)

class EncryptedFileFetcher(
    private val file: File
) : DataFetcher<InputStream> {

    private var inputStream: InputStream? = null

    override fun loadData(
        priority: Priority,
        callback: DataFetcher.DataCallback<in InputStream>
    ) {
        try {
            val key = Password.key ?: throw Exception("EncryptedFileFetcher Key is null")

            // Сначала читаем IV из файла (AES/GCM/NoPadding)
            val iv = ByteArray(Crypto.IV_SIZE)
            val fis = FileInputStream(file)
            fis.read(iv)

            val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_SIZE, iv))

            // CipherInputStream для расшифровки
            inputStream = CipherInputStream(fis, cipher)
            callback.onDataReady(inputStream)

        } catch (e: Exception) {
            e.printStackTrace()
            callback.onLoadFailed(e)
        }
    }

    override fun cleanup() {
        inputStream?.close()
        inputStream = null
    }

    override fun cancel() {
        cleanup()
    }

    override fun getDataClass() = InputStream::class.java
    override fun getDataSource() = DataSource.LOCAL
}

private class EncryptedFileModelLoader(
    //private val secretKeySpec: SecretKeySpec
) : ModelLoader<EncryptedFileModel, InputStream> {
    override fun buildLoadData( model: EncryptedFileModel, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream> {
        return ModelLoader.LoadData(ObjectKey(model.file), EncryptedFileFetcher(model.file))
    }
    override fun handles(model: EncryptedFileModel) = true
}

private class EncryptedFileModelLoaderFactory(
    //private val secretKeySpec: SecretKeySpec
) : ModelLoaderFactory<EncryptedFileModel, InputStream> {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<EncryptedFileModel, InputStream> {
        return EncryptedFileModelLoader()//secretKeySpec)
    }
    override fun teardown() {}
}
