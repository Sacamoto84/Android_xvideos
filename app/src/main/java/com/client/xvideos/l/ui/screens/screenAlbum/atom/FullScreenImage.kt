package com.client.xvideos.l.ui.screens.screenAlbum.atom

import android.widget.Button
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.common.fresco.UrlImageLusciousGifsGlide
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


@Composable
fun FullScreenImage(
    item: PicsDetails,
    //startBounds: Rect?,
    albumName: String,
    filteredPic: List<PicsDetails>,
    //onDownload: (PicsDetails) -> Unit = {},
    autoPlay: Boolean = false,
    isAnimated: Boolean = false,
    expandMenu: @Composable (PicsDetails) -> Unit = {},
    onClose: (Int) -> Unit
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }


    var isClosing by remember { mutableStateOf(false) }

    var success by remember { mutableStateOf(false) }
    val alphaAnim = remember { Animatable(0f) }

    var dataItem by remember(Unit) { mutableStateOf(item) }

    var corruptCancel by remember { mutableStateOf(false) }

    LaunchedEffect(success) {
        if (success) {
            alphaAnim.animateTo(1f, tween(200))
        }
    }


    // Анимация закрытия
    LaunchedEffect(isClosing) {
        if (isClosing) {

            onClose(
                if (corruptCancel) filteredPic.indexOf(dataItem)
                    .coerceIn(0, filteredPic.size - 1) else -1
            )
        }
    }

    BackHandler { isClosing = true }

    val zoomState = rememberZoomState()

    val pagerState = rememberPagerState(filteredPic.indexOf(item).coerceIn(0, filteredPic.lastIndex), pageCount = { filteredPic.size })

    Box(
        modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart
    ) {

        // Полупрозрачный фон
        Box( modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = alphaAnim.value)) )


        HorizontalPager(
           state = pagerState,
            modifier = Modifier.align(Alignment.Center).fillMaxWidth().wrapContentHeight().zoomable(zoomState)
        ) {
              page ->

            dataItem = filteredPic[page]

                //UrlImageLusciousGifsFull(
                UrlImageLusciousGifsGlide(
                    url = dataItem.url_to_original!!,
                    modifier = Modifier.aspectRatio(dataItem.width.toFloat() / dataItem.height),//.fillMaxSize(),
                    //contentScale = ContentScale.FillBounds,
                    onSuccess = { success = true },
                    albumName = albumName,
                    autoPlay = autoPlay,
                    isAnimated = dataItem.is_animated
                )

        }

        Box(modifier = Modifier.align(Alignment.TopStart)) {
            Text(
                filteredPic.indexOf(dataItem).coerceIn(0, filteredPic.size - 1).toString(),
                color = Color.Gray, modifier = Modifier.padding(start = 8.dp)
            )
        }

        Box(modifier = Modifier.align(Alignment.TopEnd)) { expandMenu(item) }


        Column(modifier = Modifier.align(Alignment.BottomCenter)) {

            LazyRow(modifier = Modifier.height(96.dp)) {
                items(filteredPic) { it1 ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 1.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .aspectRatio(it1.width.toFloat() / it1.height)
                            .clickable(onClick = { dataItem = it1 })
                            .border(2.dp, if (dataItem == it1) Color.Yellow else Color.Transparent ,RoundedCornerShape(4.dp))
                            .padding(2.dp)

                    ) {
                        UrlImageLusciousGifsGlide(
                            url = it1.url_to_original!!,
                            modifier = Modifier.clip(RoundedCornerShape(4.dp)).fillMaxSize(),
                            contentScale = ContentScale.FillBounds,
                            onSuccess = { },
                            albumName = albumName,
                            autoPlay = false,
                            isAnimated = dataItem.is_animated
                        )
                    }
                }
            }



            Row(
                modifier = Modifier
                    .height(46.dp)
                    .padding(horizontal = 96.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {


                Button(onClick = {
                    val index =
                        filteredPic.indexOf(dataItem).minus(1).coerceIn(0, filteredPic.size - 1)
                    dataItem = filteredPic[index]
                    corruptCancel = true
                    //Timber.e(" eee index $index")
                }) {

                }

                Button(onClick = {
                    val index =
                        filteredPic.indexOf(dataItem).plus(1).coerceIn(0, filteredPic.size - 1)
                    dataItem = filteredPic[index]

                    corruptCancel = true
                    //Timber.e(" eee index $index")
                }) {

                }

            }
        }

    }
}
