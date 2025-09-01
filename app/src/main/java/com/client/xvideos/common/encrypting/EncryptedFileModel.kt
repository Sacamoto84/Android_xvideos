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
import java.io.FileOutputStream
import java.io.InputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.SecretKeySpec













@GlideModule
class MyAppGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val key = SecretKeySpec("1234567890123456".toByteArray(), "AES")
        registry.append(EncryptedFileModel::class.java, InputStream::class.java, EncryptedFileModelLoaderFactory(key))
    }
}
class EncryptedFileModel(val file: File)

class EncryptedFileFetcher(
    private val file: File,
    private val secretKeySpec: SecretKeySpec
) : DataFetcher<InputStream> {

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in InputStream>) {
        try {
            val cipher = Cipher.getInstance("AES")
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
            val inputStream = CipherInputStream(FileInputStream(file), cipher)
            callback.onDataReady(inputStream)
        } catch (e: Exception) {
            callback.onLoadFailed(e)
        }
    }

    override fun cleanup() { }
    override fun cancel() { }
    override fun getDataClass() = InputStream::class.java
    override fun getDataSource() = DataSource.LOCAL
}

class EncryptedFileModelLoader(
    private val secretKeySpec: SecretKeySpec
) : ModelLoader<EncryptedFileModel, InputStream> {

    override fun buildLoadData(
        model: EncryptedFileModel,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<InputStream> {
        return ModelLoader.LoadData(ObjectKey(model.file), EncryptedFileFetcher(model.file, secretKeySpec))
    }

    override fun handles(model: EncryptedFileModel) = true
}

class EncryptedFileModelLoaderFactory(
    private val secretKeySpec: SecretKeySpec
) : ModelLoaderFactory<EncryptedFileModel, InputStream> {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<EncryptedFileModel, InputStream> {
        return EncryptedFileModelLoader(secretKeySpec)
    }
    override fun teardown() {}
}
