package com.client.xvideos.l.ui.screens.explorer.tab.config.atom

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.client.xvideos.common.encrypting.Password
import com.client.xvideos.l.theme.ThemeL

@Composable
fun ScreenLConfig_Encrypt() {

    val context = LocalContext.current

    ConfigTextCenterL("Шифрование:")

    var isKeyAvailable by remember { mutableStateOf(Password.isKeyAvailable()) }

    val textFieldValue = remember { mutableStateOf(TextFieldValue("")) }

    ConfigTextAndButtonWithDialogL(
        text = "Пароль шифрования",
        value = if (isKeyAvailable) "Задан" else "Задать",
        textDialogTitle = "Задать пароль шифрования",
        textDialogBody = "",
        textDialogButton = "Принять",
        onClick = {
            Password.savePassword(context, textFieldValue.value.text)
            textFieldValue.value = TextFieldValue("")
            isKeyAvailable = Password.isKeyAvailable()
        },
        composable = {
            TextField(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                value = textFieldValue.value,
                label = { Text("Новый пароль") },
                placeholder = { },
                onValueChange = { newValue ->
                    textFieldValue.value = newValue
                }
            )
        }
        , composableIcon = {
            if (isKeyAvailable) {
                Icon(Icons.Default.Key, contentDescription = "Key Icon", tint = ThemeL.textColor)
            }
        }
    )

}