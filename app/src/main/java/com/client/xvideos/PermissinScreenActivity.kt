package com.client.xvideos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.alexstyl.warden.PermissionState
import com.alexstyl.warden.Warden
import com.client.xvideos.ui.theme.XvideosTheme
import kotlinx.coroutines.launch

class PermissionScreenActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            XvideosTheme {
                var hasPermission by remember { mutableStateOf(PermissionStorage.hasPermissions(this)) }

                LaunchedEffect(hasPermission) {
                    if (hasPermission) {
                        navigateToMain()
                    }
                }

                PermissionScreenContent(
                    hasPermission = hasPermission,
                    onRequestPermission = {
                        PermissionStorage.requestPermissions(this@PermissionScreenActivity)
                    }
                )
            }
        }
    }

    @Composable
    private fun PermissionScreenContent(
        hasPermission: Boolean,
        onRequestPermission: () -> Unit
    ) {
        if (!hasPermission) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .semantics { testTagsAsResourceId = true },
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Отсутствуют Файловые разрешения",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    color = Color(0xFFFFE800)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    modifier = Modifier.padding(horizontal = 8.dp).testTag("bPermission"),
                    onClick = onRequestPermission
                ) {
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

    override fun onResume() {
        super.onResume()
        // Проверка при возврате из настроек
        if (PermissionStorage.hasPermissions(this)) {
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    @Preview(showBackground = true)
    @Composable
    private fun PermissionScreenPreview() {
        XvideosTheme {
            PermissionScreenContent(
                hasPermission = false,
                onRequestPermission = {}
            )
        }
    }

    object PermissionStorage {

        fun hasPermissions(context: Context): Boolean {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Environment.isExternalStorageManager()
            } else {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }
        }

        fun requestPermissions(activity: ComponentActivity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                        "package:${activity.packageName}".toUri()
                    ).apply {
                        addCategory("android.intent.category.DEFAULT")
                    }
                    activity.startActivity(intent)
                } catch (_: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    activity.startActivity(intent)
                }
            } else {
                activity.lifecycleScope.launch {
                    val result = Warden.with(activity).requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    val message = when (result) {
                        is PermissionState.Denied -> "WRITE_EXTERNAL_STORAGE Denied"
                        PermissionState.Granted -> "WRITE_EXTERNAL_STORAGE Granted"
                    }
                    Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}