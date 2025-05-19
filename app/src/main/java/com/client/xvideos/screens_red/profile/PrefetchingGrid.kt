package com.client.xvideos.screens_red.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.request.ImageResult
import com.client.xvideos.feature.redgifs.types.MediaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PrefetchingGrid(
    list: List<MediaInfo>,
    state: LazyGridState,
    imageLoader: ImageLoader,
    content: @Composable LazyGridItemScope.(MediaInfo) -> Unit
) {
    // — сколько элементов «вперёд» префетчить
    val ahead = 6

    val context = LocalContext.current

    LaunchedEffect(
        state,
        list
    ) { // Добавляем list как ключ для перезапуска, если он значительно изменится
        snapshotFlow { state.layoutInfo }
            .collect { info ->
                // Если список для предварительной выборки пуст, ничего делать не нужно
                if (list.isEmpty()) {
                    return@collect
                }

//                val lastVisibleItemIndex = info.visibleItemsInfo.lastOrNull()?.index ?: return@collect
//
//                // Определяем начальный индекс для предварительной выборки: элемент после последнего видимого
//                val prefetchStartIndex = lastVisibleItemIndex + 1
//
//                // Определяем конечный индекс для предварительной выборки
//                // Убеждаемся, что он не выходит за пределы фактического списка
//                val prefetchEndIndex = (lastVisibleItemIndex + ahead).coerceAtMost(list.lastIndex)
//
//                // Критическая проверка: убеждаемся, что индексы действительны и fromIndex не больше toIndex
//                if (prefetchStartIndex <= prefetchEndIndex && prefetchStartIndex < list.size) {
//                    // Получаем подсписок элементов для предварительной выборки
//                    // Примечание: toIndex в subList является эксклюзивным, поэтому используем prefetchEndIndex + 1
//                    val itemsToPrefetch = list.subList(prefetchStartIndex, prefetchEndIndex + 1)

                list.forEach { item ->
                    launch(Dispatchers.IO) {
                        val req = ImageRequest.Builder(context)
                            .data(item.urls.thumbnail.toString())
                            .diskCacheKey(item.urls.thumbnail.toString())
                            .memoryCacheKey(item.urls.thumbnail.toString())
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build()
                        imageLoader.enqueue(req)
                    }
                }
            }

    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = state,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        items(list, key = { it.id } ) { item ->
            content(item)
        }
    }
}
