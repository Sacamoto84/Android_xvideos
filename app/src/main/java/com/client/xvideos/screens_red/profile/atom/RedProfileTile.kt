package com.client.xvideos.screens_red.profile.atom

import android.net.Uri
import android.os.Looper
import androidx.annotation.OptIn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.PlayerView
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import com.client.xvideos.R
import com.client.xvideos.feature.redgifs.types.MediaInfo
import com.client.xvideos.feature.vibrate.vibrateWithPatternAndAmplitude
import com.client.xvideos.screens.common.urlVideImage.UrlImage
import com.client.xvideos.screens_red.ThemeRed
import com.composables.core.Icon
import timber.log.Timber

@Composable
fun RedProfileTile(item: MediaInfo, index: Int) {

    val isVideo by remember{ mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            //.aspectRatio(1f)
            .clip(RoundedCornerShape(8.dp))
    ) {

        UrlImage(
            url = item.urls.thumbnail,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
        )


        //Индекс картинки
        Text(
            index.toString(),
            color = Color.Gray,
            modifier = Modifier
                .padding(start = 8.dp)
                .offset(1.dp, 1.dp),
            fontFamily = ThemeRed.fontFamilyPopinsMedium
        )


        //Нижний ряд с лайками и длительностью
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    Icon(
                        painter = painterResource(R.drawable.rg_button),
                        contentDescription = null,
                        tint = Color.Black, modifier = Modifier.offset(1.dp, 1.dp)
                    )
                    Icon(
                        painter = painterResource(R.drawable.rg_button),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Box {
                    Text(
                        item.views?.toPrettyCount() ?: "-",
                        color = Color.Black,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .offset(1.dp, 1.dp),
                        fontFamily = ThemeRed.fontFamilyPopinsMedium
                    )

                    Text(
                        item.views?.toPrettyCount() ?: "-",
                        color = Color.White,
                        modifier = Modifier
                            .padding(start = 8.dp),
                        fontFamily = ThemeRed.fontFamilyPopinsMedium
                    )
                }
            }
            Box {

                Text(
                    item.duration?.toMinSec() ?: "-",
                    color = Color.Black,
                    modifier = Modifier
                        .padding(8.dp)
                        .offset(1.dp, 1.dp),
                    fontFamily = ThemeRed.fontFamilyPopinsMedium
                )

                Text(
                    item.duration?.toMinSec() ?: "-",
                    color = Color.White,
                    modifier = Modifier
                        .padding(8.dp),
                    fontFamily = ThemeRed.fontFamilyPopinsMedium
                )
            }
        }



    }

}












