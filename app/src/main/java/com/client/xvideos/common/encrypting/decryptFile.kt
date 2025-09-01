package com.client.xvideos.common.encrypting

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.spec.SecretKeySpec

fun decryptFile(inputFile: File, outputFile: File, secretKey: SecretKeySpec) {
    val cipher = Cipher.getInstance("AES")
    cipher.init(Cipher.DECRYPT_MODE, secretKey)

    outputFile.parentFile?.mkdirs()

    CipherInputStream(FileInputStream(inputFile), cipher).use { cis ->
        FileOutputStream(outputFile).use { fos ->
            cis.copyTo(fos)
        }
    }
}
