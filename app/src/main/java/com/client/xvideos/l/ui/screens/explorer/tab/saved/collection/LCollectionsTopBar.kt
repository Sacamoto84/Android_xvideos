package com.client.xvideos.l.ui.screens.explorer.tab.saved.collection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.client.xvideos.l.featured.saved.LCollectionSortOrder
import com.client.xvideos.l.theme.ThemeL
import com.composeunstyled.Text

@Composable
fun LCollectionsTopBar(
    selectedCollection: String?,
    sortOrder: LCollectionSortOrder,
    onSortOrderClick: (LCollectionSortOrder) -> Unit,
    onSmartCollectionsClick: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ThemeL.greyBackground)
            .padding(start = 8.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                ">Коллекция>${selectedCollection.orEmpty()}",
                color = ThemeL.primaryColor,
                fontSize = 18.sp,
                fontFamily = ThemeL.fontFamilyPopinsRegular
            )
            if (selectedCollection == null) {
                Text(
                    sortOrder.title,
                    color = ThemeL.grey2,
                    fontSize = 12.sp,
                    fontFamily = ThemeL.fontFamilyDMsanss
                )
            }
        }

        if (selectedCollection == null) {
            TextButton(onClick = onSmartCollectionsClick) {
                Icon(Icons.Default.Add, contentDescription = null, tint = ThemeL.primaryColor)
                Spacer(Modifier.width(4.dp))
                Text("Smart", color = ThemeL.primaryColor, style = ThemeL.Type.button)
            }

            Box {
                IconButton(onClick = { menuExpanded = true }) {
                    Icon(Icons.Default.FilterList, contentDescription = null, tint = ThemeL.textColor)
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    containerColor = ThemeL.grey5
                ) {
                    LCollectionSortOrder.entries.forEach { order ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    order.title,
                                    style = ThemeL.Type.menuItem.copy(
                                        color = if (order == sortOrder) ThemeL.primaryColor else ThemeL.textColor
                                    )
                                )
                            },
                            onClick = {
                                onSortOrderClick(order)
                                menuExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

/** Список коллекций: заголовок + сортировка + кнопки «Smart» и меню. */
@Preview
@Composable
private fun LCollectionsTopBarPreview() {
    LCollectionsTopBar(
        selectedCollection = null,
        sortOrder = LCollectionSortOrder.RECENT,
        onSortOrderClick = {},
        onSmartCollectionsClick = {}
    )
}

/** Открыта конкретная коллекция: только заголовок, без управляющих кнопок. */
@Preview
@Composable
private fun LCollectionsTopBarSelectedPreview() {
    LCollectionsTopBar(
        selectedCollection = "Favorites",
        sortOrder = LCollectionSortOrder.NAME,
        onSortOrderClick = {},
        onSmartCollectionsClick = {}
    )
}
