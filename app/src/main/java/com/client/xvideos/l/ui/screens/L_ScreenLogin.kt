package com.client.xvideos.l.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import com.client.xvideos.l.theme.ThemeL

class L_ScreenExplorer : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        PContent()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PContent() {

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Получаем доступ к обработчику ссылок
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .background(Color(0xFF212121))
            .padding(horizontal = 16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "https://www.luscious.net",
            fontStyle = FontStyle.Italic,
            textDecoration = TextDecoration.Underline,
            color = ThemeL.b0,
            modifier = Modifier.clickable { uriHandler.openUri("https://www.luscious.net") },
            fontSize = 24.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Авторизация",
            style = MaterialTheme.typography.headlineMedium,
            color = ThemeL.textColor
        )

        Spacer(modifier = Modifier.height(32.dp))


        Text(
            "Логин",
            color = ThemeL.textColor,
            fontSize = 22.sp,
            fontFamily = ThemeL.fontFamilyKarla
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            modifier = Modifier

                .fillMaxWidth(),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF484848),
                unfocusedContainerColor = Color(0xFF3A3A3A),
                focusedTextColor = Color(0xFFB8B7B7),
                unfocusedTextColor = Color(0xFFB8B7B7),
                focusedIndicatorColor = Color(0xFF888888),
            ),
            textStyle = TextStyle(
                fontSize = 24.sp
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Пароль",
            color = ThemeL.textColor,
            fontSize = 22.sp,
            fontFamily = ThemeL.fontFamilyKarla
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF484848),
                unfocusedContainerColor = Color(0xFF3A3A3A),
                focusedTextColor = Color(0xFFB8B7B7),
                unfocusedTextColor = Color(0xFFB8B7B7),
                focusedIndicatorColor = Color(0xFF888888),
            ),
            textStyle = TextStyle(
                fontSize = 24.sp
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        HorizontalDivider(color = Color.DarkGray)

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* TODO: Обработка входа */ },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ThemeL.primaryColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "Принять",
                fontSize = 22.sp,
                fontFamily = ThemeL.fontFamilyKarla
            )
        }
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { /* TODO: Обработка входа */ },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ThemeL.b0),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "Назад",
                fontSize = 22.sp,
                fontFamily = ThemeL.fontFamilyKarla
            )
        }
    }
}

@Preview(showBackground = false)
@Composable
fun PContentPreview() {
    PContent()
}
