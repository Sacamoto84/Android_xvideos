package com.client.xvideos.common.diagnostics

import android.content.Context
import android.os.Build
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.coil.CoilImageLoaderFactory
import com.client.xvideos.common.settings.Settings
import com.client.xvideos.common.util.formatBytes
import com.client.xvideos.common.util.getFolderSize
import com.client.xvideos.common.videoplayer.util.CacheManager
import com.client.xvideos.screens.videoplayer.video.cache.VideoPlayerCacheManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class DiagnosticEvent(
    val timeMs: Long = System.currentTimeMillis(),
    val type: String,
    val title: String,
    val message: String,
    val details: String? = null,
    val albumId: Int? = null,
    val page: Int? = null
)

data class DiagnosticsStatus(
    val lLoginConfigured: Boolean,
    val lPasswordConfigured: Boolean,
    val lLoginMasked: String,
    val imageCacheBytes: Long,
    val videoCacheBytes: Long,
    val redDownloadBytes: Long,
    val totalEvents: Int
)

object AppDiagnostics {
    private const val MAX_EVENTS = 160
    private val lock = Any()

    private val _events = MutableStateFlow<List<DiagnosticEvent>>(emptyList())
    val events = _events.asStateFlow()

    fun recordLNetworkError(
        operation: String,
        message: String,
        details: String? = null,
        requestHash: String? = null
    ) {
        record(
            DiagnosticEvent(
                type = "L network",
                title = operation,
                message = message.compactForDiagnostics(),
                details = details.withRequestHash(requestHash)?.compactForDiagnostics()
            )
        )
    }

    fun recordLHtmlChallenge(
        operation: String,
        message: String,
        requestHash: String? = null,
        retryDelayMs: Long? = null
    ) {
        val retry = retryDelayMs?.let { "retryDelay=${it}ms" }
        record(
            DiagnosticEvent(
                type = "L HTML",
                title = operation,
                message = message.compactForDiagnostics(),
                details = listOfNotNull("Cloudflare/HTML challenge", retry)
                    .joinToString(" | ")
                    .withRequestHash(requestHash)
            )
        )
    }

    fun recordLAlbumPage(
        albumId: Int,
        page: Int,
        message: String,
        details: String? = null
    ) {
        record(
            DiagnosticEvent(
                type = "L album page",
                title = "Album $albumId page $page",
                message = message.compactForDiagnostics(),
                details = details?.compactForDiagnostics(),
                albumId = albumId,
                page = page
            )
        )
    }

    fun recordPlayerError(
        source: String,
        url: String?,
        message: String
    ) {
        record(
            DiagnosticEvent(
                type = "Player",
                title = source,
                message = message.compactForDiagnostics(),
                details = url?.compactUrlForDiagnostics()
            )
        )
    }

    fun clear() {
        synchronized(lock) {
            _events.value = emptyList()
        }
    }

    suspend fun collectStatus(context: Context): DiagnosticsStatus = withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        val login = Settings.l_login.field.value.trim()
        val password = Settings.l_pass.field.value
        val imageCache = CoilImageLoaderFactory.imageDiskCacheSizeBytes(appContext)
        val videoCache = CacheManager.cacheSizeBytes(appContext) + VideoPlayerCacheManager.cacheSizeBytes(appContext)
        val redDownload = getFolderSize(File(AppPath.r_cache_download))

        DiagnosticsStatus(
            lLoginConfigured = login.isNotBlank(),
            lPasswordConfigured = password.isNotBlank(),
            lLoginMasked = login.maskLogin(),
            imageCacheBytes = imageCache,
            videoCacheBytes = videoCache,
            redDownloadBytes = redDownload,
            totalEvents = events.value.size
        )
    }

    suspend fun buildReport(context: Context): String {
        val status = collectStatus(context)
        val snapshot = events.value

        return buildString {
            appendLine("Diagnostics report")
            appendLine("Created: ${formatTime(System.currentTimeMillis())}")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}, Android ${Build.VERSION.RELEASE} (SDK ${Build.VERSION.SDK_INT})")
            appendLine()
            appendLine("Status")
            appendLine("- L login: ${if (status.lLoginConfigured) "configured (${status.lLoginMasked})" else "empty"}")
            appendLine("- L password: ${if (status.lPasswordConfigured) "configured" else "empty"}")
            appendLine("- Image cache: ${formatBytes(status.imageCacheBytes)}")
            appendLine("- Video cache: ${formatBytes(status.videoCacheBytes)}")
            appendLine("- R Download folder: ${formatBytes(status.redDownloadBytes)}")
            appendLine("- Events: ${status.totalEvents}")
            appendLine()
            appendLine("Events")
            if (snapshot.isEmpty()) {
                appendLine("- none")
            } else {
                snapshot.takeLast(80).forEach { event ->
                    appendLine("- ${formatTime(event.timeMs)} [${event.type}] ${event.title}: ${event.message}")
                    if (event.albumId != null || event.page != null) {
                        appendLine("  albumId=${event.albumId ?: "-"} page=${event.page ?: "-"}")
                    }
                    event.details?.takeIf { it.isNotBlank() }?.let {
                        appendLine("  $it")
                    }
                }
            }
        }
    }

    private fun record(event: DiagnosticEvent) {
        synchronized(lock) {
            _events.value = (_events.value + event).takeLast(MAX_EVENTS)
        }
    }

    fun formatTime(timeMs: Long): String {
        return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(timeMs))
    }

    private fun String?.withRequestHash(requestHash: String?): String? {
        return listOfNotNull(this?.takeIf { it.isNotBlank() }, requestHash?.let { "requestHash=$it" })
            .joinToString(" | ")
            .takeIf { it.isNotBlank() }
    }

    private fun String.compactForDiagnostics(): String {
        return replace(Regex("\\s+"), " ").take(500)
    }

    private fun String.compactUrlForDiagnostics(): String {
        return replace(Regex("\\s+"), "").take(220)
    }

    private fun String.maskLogin(): String {
        if (isBlank()) return ""
        if (length <= 2) return "**"
        return take(2) + "***" + takeLast(1)
    }
}
