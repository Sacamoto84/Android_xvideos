package com.client.xvideos.feature.videoplayer.chaintech.videoplayer.youtube

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import chaintech.videoplayer.youtube.PlayerEvent
import chaintech.videoplayer.youtube.VideoPlayerHost

//private const val BASE_URL = "https://www.youtube.com"
//private const val BASE_MIME_TYPE = "text/html"
//private const val BASE_ENCODING = "utf-8"

@Composable
internal fun EmbeddedPlayer(
    modifier: Modifier = Modifier,
    host: VideoPlayerHost,
    onEvent: ((PlayerEvent) -> Unit)? = null,
)
{

}
//    val webViewState = rememberWebViewStateWithHTMLData(
//        data = htmlContent,
//        baseUrl = BASE_URL,
//        mimeType = BASE_MIME_TYPE,
//        encoding = BASE_ENCODING,
//    )
//
//    val navigator = rememberWebViewNavigator()
//    val command = host.pendingCommand
//
//    webViewState.webSettings.apply {
//        isJavaScriptEnabled = true
//        androidWebSettings.apply {
//            isAlgorithmicDarkeningAllowed = true
//            safeBrowsingEnabled = false
//            domStorageEnabled = true
//            supportZoom = false
//            hideDefaultVideoPoster = true
//        }
//        iOSWebSettings.apply {
//            backgroundColor = Color.Black
//            scrollEnabled = false
//            bounces = false
//            showHorizontalScrollIndicator = false
//            showVerticalScrollIndicator = false
//        }
//    }
//
//    LaunchedEffect(command) {
//        command?.let {
//            executeCommand(navigator, it)
//            host.complete()
//        }
//    }
//
//    PlayerEventParser.parse(webViewState.pageTitle)?.let { event ->
//        host.update(event)
//        onEvent?.invoke(event)
//    }
//
//    WebView(
//        modifier = modifier.fillMaxSize(),
//        state = webViewState,
//        navigator = navigator,
//    )
//}

//internal expect fun executeCommand(
//    navigator: WebViewNavigator,
//    execCommand: PlayerCommand,
//)
//
//@Composable
//expect fun DesktopYoutubeComposable(
//    modifier: Modifier,
//    videoId: String,
//)