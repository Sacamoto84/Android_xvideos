package com.client.xvideos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import com.alexstyl.warden.PermissionState
import com.alexstyl.warden.Warden
import com.client.xvideos.ui.theme.XvideosTheme
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PermissionScreenActivity : ComponentActivity() {

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("Запуск PermissionScreenActivity")

        setContent {

            XvideosTheme {

                var granded by remember {
                    mutableStateOf(false)
                }

                if (!PermissionStorage.hasPermissions(this)) {

                    LaunchedEffect(key1 = true, block = {
                        while (!granded) {
                            delay(100)
                            granded = PermissionStorage.hasPermissions(applicationContext)
                        }

                        val intent = Intent(this@PermissionScreenActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()

                    })

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black),
                        Arrangement.Center
                    )
                    {
                        Text(
                            text = "Отсуствуют Файловые разрешения",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontSize = 24.sp,
                            color = Color(0xFFFFE800)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { PermissionStorage.requestPermissions(applicationContext) }) {
                            Text(
                                text = "Запрос",
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                fontSize = 24.sp
                            )
                        }
                    }

                }

            }
        }


    }


    fun isLoggedIn(): Boolean {
        // реализация метода
        return true
    }

    object PermissionStorage {

        fun hasPermissions(context: Context?): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager() //Проверка есть ли разрешение? >=A11
            } else {
                (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED)
            }
        }

        @OptIn(DelicateCoroutinesApi::class, DelicateCoroutinesApi::class,
            DelicateCoroutinesApi::class, DelicateCoroutinesApi::class
        )
        fun requestPermissions(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val intent =
                            Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                                addFlags(FLAG_ACTIVITY_NEW_TASK)

                            }
                        intent.addCategory("android.intent.category.DEFAULT")
                        intent.data = Uri.parse(String.format("package:%s", context.packageName))
                        //activity.startActivityForResult(intent, requestCode);
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        val intent = Intent()
                        intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                        //activity.startActivityForResult(intent, requestCode);
                        context.startActivity(intent)
                    }
                }


            } else {

                GlobalScope.launch(Dispatchers.Main) {
                    val result = Warden.with(context)
                        .requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    when (result) {
                        is PermissionState.Denied -> Toast.makeText(
                            context,
                            "WRITE_EXTERNAL_STORAGE Denied",
                            Toast.LENGTH_LONG
                        ).show()

                        PermissionState.Granted -> Toast.makeText(
                            context,
                            "WRITE_EXTERNAL_STORAGE Granted",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            }

        }

    }



}