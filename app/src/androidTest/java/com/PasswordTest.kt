package com

import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.client.xvideos.common.encrypting.Password
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class PasswordTest {

    private val TAG = "ExperimentTest"

    @Test
    fun deviceContextTest() {
        // Получаем Context приложения на устройстве
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        Log.d(TAG, "!!! Package name: ${appContext.packageName}")
        // Проверяем имя пакета
        //assertEquals("com.example.myapp", appContext.packageName)
        //val pass = Password.password
        //Log.d(TAG, "!!! Password: $pass")
        val key = Password.key
        Log.d(TAG, "!!! Key: $key")
        val keyGen = Password.keyFromPassword("123456", ByteArray(16) { 0x01 })
        keyGen
        Log.d(TAG, "!!! keyGen: $keyGen")
        //val a = Password.initKey(appContext, ByteArray(16) { 0x01 }).algorithm
        //Log.d(TAG, "!!! a: $a")
    }

    @Test
    fun cpuTest() {
        // Пример: считаем сумму, чтобы нагрузить CPU
        var sum = 0L
        for (i in 1..1_000_000) sum += i

        // Проверяем, что сумма корректная
        assertEquals(500000500000L, sum)
    }

}