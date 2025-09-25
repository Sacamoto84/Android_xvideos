package com.client.xvideos.common.urlVideImage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.fresco.FrescoImage

/**
 * url: строка с адресом изображения
 */
@Composable
fun UrlImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.FillWidth,
    loadIndicator: Boolean = true
) {
    //Timber.d("!!! UrlImage: $url")
    Box(modifier = modifier) {
        FrescoImage(
            imageUrl = url,
            imageOptions = ImageOptions(
                contentScale = contentScale,
                alignment = Alignment.Center
            ),
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            loading = {
                if (loadIndicator) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            modifier = Modifier.size(
                                32.dp
                            ), color = Color.Gray
                        )
                    }
                }
            },
            failure = {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) { Text("Ошибка загрузки", color = Color.Gray) }
            },
        )
    }
}
