package com.client.xvideos.x.screens.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.ui.theme.XvideosTheme
import com.client.xvideos.urlStart
import com.client.xvideos.x.model.ItemsX
import com.client.xvideos.common.urlVideoImage.UrlVideoImageAndLongClickX
import com.client.xvideos.x.screens.videoplayer.ScreenX_VideoPlayer

/**
 * Содержимое вкладки «Избранное» (список) с привязкой к VM/навигатору.
 *
 * Раньше это был отдельный push-экран `ScreenFavorites` со своим `Scaffold`/topBar.
 * Теперь рендерится как контент под-вкладки внутри [X_ScreenFavoritesTab],
 * нижнее меню которого остаётся видимым — по аналогии с разделами R/L.
 * VM приходит параметром, т.к. `getScreenModel()` доступен только в `Screen`.
 *
 * Вся отрисовка вынесена в stateless [FavoritesList], чтобы её можно было показать в `@Preview`
 * (без Hilt-VM и навигатора, которые в превью недоступны).
 */
@Composable
fun FavoritesListContent(vm: ScreenFavoritesSM) {

    val navigator = LocalNavigator.currentOrThrow

    FavoritesList(
        favorites = vm.favorites,
        // Long/double-click открывает полное видео (по href), как в дашбордах.
        onOpenFull = { item -> navigator.push(ScreenX_VideoPlayer(urlStart + item.href)) },
        onDelete = { item -> vm.removeFavorite(item) }
    )
}

/**
 * Stateless-список избранного. Получает данные и колбэки — пригоден для `@Preview`.
 *
 * @param favorites список видео в избранном.
 * @param onOpenFull открыть полное видео (long/double-click по превью).
 * @param onDelete подтверждённое удаление элемента из избранного.
 */
@Composable
internal fun FavoritesList(
    favorites: List<ItemsX>,
    onOpenFull: (ItemsX) -> Unit,
    onDelete: (ItemsX) -> Unit
) {
    // Элемент, ожидающий подтверждения удаления (null — диалог скрыт).
    var itemToDelete by remember { mutableStateOf<ItemsX?>(null) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(favorites) { item ->

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
            ) {

                Box(
                    modifier = Modifier
                        .aspectRatio(352f / 198f)
                        .background(Color.DarkGray)
                ) {
                    UrlVideoImageAndLongClickX(
                        item,
                        modifier = Modifier.fillMaxSize(),
                        onLongClick = { onOpenFull(item) },
                        onDoubleClick = { onOpenFull(item) }
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Black),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Удаление — через диалог подтверждения.
                    IconButton(onClick = { itemToDelete = item }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowCircleDown,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Box(modifier = Modifier.weight(1f))

                    // Продолжительность видео в строке кнопок.
                    Text(
                        text = item.duration,
                        color = Color.LightGray,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )

                }

            }
        }
    }

    // Диалог подтверждения удаления из избранного.
    itemToDelete?.let { target ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            // Светлый фон + явные цвета текста — как в рабочих диалогах проекта
            // (DialogNicheDelete и т.п.). Без этого диалог берёт тёмный surface темы
            // и сливается со scrim ("тёмное стекло на весь экран").
            containerColor = Color(0xFFEBE6EE),
            title = {
                Text(
                    "Удалить из избранного?",
                    color = Color(0xFF1C1C1C),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = { Text(target.title, color = Color(0xFF1C1C1C), fontSize = 16.sp) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(target)
                    itemToDelete = null
                }) { Text("Удалить", color = Color(0xFF6552A5), fontSize = 16.sp) }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Отмена", color = Color(0xFF6552A5), fontSize = 16.sp)
                }
            }
        )
    }
}

/** Тестовые данные для превью. */
internal fun previewFavorites(): List<ItemsX> = listOf(
    ItemsX(
        id = 1,
        title = "Sample video 1",
        duration = "11 мин.",
        views = "1,2M",
        channel = "Channel A",
        href = "/video1",
        nameProfile = "Channel A",
        linkProfile = "/a"
    ),
    ItemsX(
        id = 2,
        title = "Sample video 2 с более длинным названием",
        duration = "7 мин.",
        views = "523K",
        channel = "Channel B",
        href = "/video2",
        nameProfile = "Channel B",
        linkProfile = "/b"
    ),
)

@Preview
@Composable
private fun FavoritesListContentPreview() {
    XvideosTheme(darkTheme = true) {
        FavoritesList(
            favorites = previewFavorites(),
            onOpenFull = {},
            onDelete = {}
        )
    }
}
