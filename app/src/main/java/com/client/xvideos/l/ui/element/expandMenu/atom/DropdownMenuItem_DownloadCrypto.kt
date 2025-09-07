package com.client.xvideos.l.ui.element.expandMenu.atom

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EnhancedEncryption
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.ui.element.expandMenu.themeExpandMenu.style
import com.client.xvideos.l.ui.element.expandMenu.themeExpandMenu.tintColor
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuItem_DownloadCrypto(url: PicsDetails? = null, onClick: (PicsDetails) -> Unit = {}, onDismiss: () -> Unit){
    DropdownMenuItem(
        leadingIcon = {Icon(Icons.Filled.EnhancedEncryption, contentDescription = "", tint = tintColor)},
        text = {Text("Скачать  в сейф", style = style)},
        onClick = {
            if (url == null) return@DropdownMenuItem
            onClick.invoke(url)
            onDismiss.invoke()
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}