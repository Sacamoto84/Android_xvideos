//package com.client.xvideos.common.encrypting
//
//import android.util.Log
//import com.bumptech.glide.Priority
//import com.bumptech.glide.load.DataSource
//import com.bumptech.glide.load.Options
//import com.bumptech.glide.load.data.DataFetcher
//import com.bumptech.glide.load.model.ModelLoader
//import com.bumptech.glide.load.model.ModelLoaderFactory
//import com.bumptech.glide.load.model.MultiModelLoaderFactory
//import com.bumptech.glide.signature.ObjectKey
//import java.io.File
//import java.io.FileInputStream
//import java.io.FileNotFoundException
//import java.io.IOException
//import java.io.InputStream
//import javax.crypto.Cipher
//import javax.crypto.CipherInputStream
//import javax.crypto.spec.GCMParameterSpec
//
//class EncryptedFileModel(val file: File)
//
//class EncryptedFileFetcher(
//    private val file: File
//) : DataFetcher<InputStream>
//{
//
//    private var inputStream: InputStream? = null
//    private var fileInputStream: FileInputStream? = null
//
//    override fun loadData(
//        priority: Priority,
//        callback: DataFetcher.DataCallback<in InputStream>
//    ) {
//        try {
//            // Проверяем существование файла
//            if (!file.exists()) {
//                callback.onLoadFailed(FileNotFoundException("File not found: ${file.absolutePath}"))
//                return
//            }
//
//            val key = Password.key ?: run {
//                callback.onLoadFailed(IllegalStateException("EncryptedFileFetcher Key is null"))
//                return
//            }
//
//            // Проверяем минимальный размер файла (IV + хотя бы 1 байт данных)
//            if (file.length() < Crypto.IV_SIZE + 1) {
//                callback.onLoadFailed(IllegalArgumentException("File too small to contain IV and data"))
//                return
//            }
//
//            // Читаем IV
//            val iv = ByteArray(Crypto.IV_SIZE)
//            fileInputStream = FileInputStream(file)
//            val bytesRead = fileInputStream!!.read(iv)
//
//            if (bytesRead != Crypto.IV_SIZE) {
//                callback.onLoadFailed(IOException("Could not read full IV from file"))
//                return
//            }
//
//            val cipher = Cipher.getInstance(Password.CIPHER_ALGORITHM)
//            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(Crypto.TAG_SIZE, iv))
//
//            // CipherInputStream для расшифровки
//            inputStream = CipherInputStream(fileInputStream, cipher)
//            callback.onDataReady(inputStream)
//
//        } catch (e: Exception) {
//            Log.e("EncryptedFileFetcher", "Failed to decrypt file: ${file.absolutePath}", e)
//            cleanup()
//            callback.onLoadFailed(e)
//        }
//    }
//
//    override fun cleanup() {
//        try {
//            inputStream?.close()
//            fileInputStream?.close()
//        } catch (e: Exception) {
//            Log.w("EncryptedFileFetcher", "Error during cleanup", e)
//        } finally {
//            inputStream = null
//            fileInputStream = null
//        }
//    }
//
//    override fun cancel() {
//        cleanup()
//    }
//
//    override fun getDataClass() = InputStream::class.java
//    override fun getDataSource() = DataSource.LOCAL
//}
//
//
//
//private class EncryptedFileModelLoader(
//    //private val secretKeySpec: SecretKeySpec
//) : ModelLoader<EncryptedFileModel, InputStream> {
//    override fun buildLoadData( model: EncryptedFileModel, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream> {
//        return ModelLoader.LoadData(ObjectKey(model.file), EncryptedFileFetcher(model.file))
//    }
//    override fun handles(model: EncryptedFileModel) = true
//}
//
//
//
//class EncryptedFileModelLoaderFactory(
//    //private val secretKeySpec: SecretKeySpec
//) : ModelLoaderFactory<EncryptedFileModel, InputStream> {
//    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<EncryptedFileModel, InputStream> {
//        return EncryptedFileModelLoader()//secretKeySpec)
//    }
//    override fun teardown() {}
//}
