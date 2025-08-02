package com.client.xvideos.l.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.calculateTargetValue
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.splineBasedDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.forEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyHorizontalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.ViewConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.internal.BackHandler
import com.client.common.urlVideImage.UrlImage
import com.client.common.util.toPrettyCount
import com.client.common.util.toPrettyCountInt
import com.client.xvideos.l.Album
import com.client.xvideos.l.Luscious
import com.client.xvideos.l.ThemeL
import com.redgifs.common.ThemeRed
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import net.engawapg.lib.zoomable.ExperimentalZoomableApi
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import timber.log.Timber
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class ScreenLRoot() : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @OptIn(ExperimentalZoomableApi::class)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val vm: ScreenLRootSM = getScreenModel()

        val album = vm.album.collectAsStateWithLifecycle().value

        val parsed = vm.album.collectAsStateWithLifecycle().value?.parsed?.collectAsStateWithLifecycle()?.value


        var selectedImage by remember { mutableStateOf<String?>(null) }
        var selectedBounds by remember { mutableStateOf<Rect?>(null) }


        Scaffold(
            containerColor = ThemeL.greyBackground

        ) {

            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(6),
                modifier = Modifier.fillMaxSize()
            ) {



                item( span =  StaggeredGridItemSpan.FullLine){
                    Column {
                        Row {
                            UrlImage(parsed?.cover?.url.toString(), modifier = Modifier.size(72.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Column {
                                if (parsed != null) {
                                    Text( parsed.title, color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyDMsanss )
                                    Text("${parsed.number_of_animated_pictures} gifs / ${parsed.number_of_pictures} pictures", color = ThemeL.textColor )
                                }
                            }
                        }
                    }

                }

                item(span = StaggeredGridItemSpan.FullLine){
                    if (parsed != null) {
                            FlowRow {
                                Text("Genres: ", color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla, fontWeight = FontWeight.ExtraBold)
                                parsed.genres.forEachIndexed { index, item ->
                                    Text(
                                        text = buildString {
                                            append(item.title)
                                            if (index != parsed.audiences.lastIndex) append(",")
                                        },
                                        color = ThemeL.primaryColor,
                                        fontFamily = ThemeL.fontFamilyKarla,
                                    )
                                    if (index != parsed.audiences.lastIndex) {
                                        Text(" ", color = ThemeL.primaryColor)
                                    }
                                }
                            }
                    }
                }

                item(span =  StaggeredGridItemSpan.FullLine)  {
                    if (parsed != null) {
                        FlowRow {
                            Text("Audiences: ", color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla,)
                            parsed.audiences.forEachIndexed { index, item ->
                                Text(
                                    text = buildString {
                                        append(item.title)
                                        if (index != parsed.audiences.lastIndex) append(",")
                                    },
                                    color = ThemeL.primaryColor,
                                    fontFamily = ThemeL.fontFamilyKarla,
                                )
                                if (index != parsed.audiences.lastIndex) {
                                    Text(" ", color = ThemeL.primaryColor)
                                }
                            }
                        }
                    }
                }

                item(span =  StaggeredGridItemSpan.FullLine)  {
                    if (parsed != null) {
                        FlowRow(verticalArrangement = Arrangement.Center) {
                            parsed.tags.reversed().forEach {
                                Text("${it.text.capitalizeEachWord()} (${it.count.toPrettyCountInt()})" , modifier = Modifier.padding(horizontal = 2.dp).padding(vertical = 2.dp).border(1.dp, ThemeL.secondaryColor, RoundedCornerShape(4.dp)).padding(4.dp), color = ThemeL.textColor, fontFamily = ThemeL.fontFamilyKarla)
                            }
                        }
                    }
                }

                items(album?.pics ?: emptyList()) {
                    var imageBounds by remember { mutableStateOf<Rect?>(null) }
                    if (it.url_to_original != null) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            val aspect = it.width.toFloat() / it.height
                            UrlImageLusciousGifs(
                                it.url_to_original,
                                modifier = Modifier.padding(2.dp).aspectRatio(aspect).clipToBounds().border(0.5.dp, Color.Gray)
                                    .onGloballyPositioned { coordinates ->
                                        val rect = coordinates.boundsInRoot()
                                        imageBounds = rect
                                    }
                                    .clickable {
                                        selectedImage = it.url_to_original
                                        selectedBounds = imageBounds
                                    },
                                contentScale = ContentScale.FillBounds,
                            )

                            val targetAlpha = if (selectedImage == it.url_to_original) 1f else 0f

                            val animatedAlpha by animateFloatAsState(
                                targetValue = targetAlpha,
                                animationSpec = if (targetAlpha == 1f) {
                                    tween(durationMillis = 300) // Появление с задержкой
                                } else {
                                    tween(durationMillis = 0)   // Мгновенное исчезновение
                                },
                                label = "imageAlpha"
                            )

                            if (selectedImage == it.url_to_original) { Box( modifier = Modifier.alpha(animatedAlpha).padding(2.dp).aspectRatio(aspect).clipToBounds().border(0.5.dp, Color.Gray).background(Color.Gray) ) }
                        }
                    }
                }

            }


            // Полноэкранное изображение с анимацией
            selectedImage?.let { imageUrl ->
                FullScreenImage(
                    imageUrl = imageUrl,
                    startBounds = selectedBounds,
                    onClose = { selectedImage = null },
                )
            }


        }


    }


}

fun String.capitalizeEachWord(): String =
    lowercase(Locale.getDefault())
        .split(" ")
        .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }

class ScreenLRootSM @Inject constructor(
    val luscious: Luscious
) : ScreenModel {

    val album = MutableStateFlow<Album?>(null)

    init {
        screenModelScope.launch {

            if (!luscious.loggedIn) {
                luscious.login()
            }
            album.value = luscious.getAlbum(556543)//336743)//(499900)//(374481)
            album
        }
    }


}

@Module
@InstallIn(SingletonComponent::class)
abstract class ScreenModuleLRootBlock {
    @Binds
    @IntoMap
    @ScreenModelKey(ScreenLRootSM::class)
    abstract fun bindScreenLRootScreenModel(hiltListScreenModel: ScreenLRootSM): ScreenModel
}
