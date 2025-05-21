package chaintech.videoplayer.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.client.xvideos.feature.videoplayer.chaintech.videoplayer.model.Platform

expect fun formatMinSec(value: Int): String

internal expect val youtubeProgressColor: Color

@Composable
internal expect fun LandscapeOrientation(
    enableFullEdgeToEdge: Boolean,
    isLandscape: Boolean,
    content: @Composable () -> Unit
)

internal fun extractYouTubeVideoId(url: String): String? {
    val regex = Regex(
        "https?:\\/\\/(?:www\\.|m\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?v=|embed\\/|v\\/|e\\/|live\\/|shorts\\/|user\\/))([^&#?\\n]+)"
    )
    return regex.find(url)?.groups?.get(1)?.value
}

fun isDesktop(): Boolean {
    return isPlatform() == Platform.Desktop
}

expect fun isPlatform(): Platform