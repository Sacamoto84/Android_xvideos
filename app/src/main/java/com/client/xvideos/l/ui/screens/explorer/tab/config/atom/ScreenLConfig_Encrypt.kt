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
import androidx.media3.session.CommandButton
import com.client.xvideos.common.encrypting.Password
import com.client.xvideos.l.ThemeL
import com.client.xvideos.l.ui.screens.explorer.tab.config.ConfigTextCenter

@Composable
fun ScreenLConfig_Encrypt() {

    val context = LocalContext.current

    ConfigTextCenter("Шифрование:")

    var isPasswordSet by remember { mutableStateOf(Password.password != null) }

    val textFieldValue = remember { mutableStateOf(TextFieldValue("")) }

    ConfigTextAndButtonWithDialogL(
        text = "Пароль шифрования",
        value = if (isPasswordSet) "Задан" else "Задать",
        textDialogTitle = "Задать пароль шифрования",
        textDialogBody = "",
        textDialogButton = "Принять",
        onClick = {
            val password = textFieldValue.value.text
            Password.savePassword(context, password)
            textFieldValue.value = TextFieldValue("")
            isPasswordSet = true
        },
        composable = {

            TextField(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                value = textFieldValue.value,
                label = { Text("Пароль") },
                placeholder = { },
                onValueChange = { newValue ->
                    textFieldValue.value = newValue
                }
            )

        }
        , composableIcon = {
            if (isPasswordSet) {
                Icon(Icons.Default.Key, contentDescription = "Key Icon", tint = ThemeL.textColor)
            }
        }
    )


}