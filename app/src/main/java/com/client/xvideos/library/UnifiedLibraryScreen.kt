package com.client.xvideos.library

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PhotoAlbum
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.hilt.ScreenModelKey
import cafe.adriel.voyager.hilt.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.client.xvideos.common.AppPath
import com.client.xvideos.common.coil.UrlImage
import com.client.xvideos.l.featured.saved.SavedL
import com.client.xvideos.l.model.PicsDetails
import com.client.xvideos.l.model.lPreviewImageUrl
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.ui.element.expandMenu.ExpandMenuType
import com.client.xvideos.l.ui.screens.explorer.L_ScreenExplorer
import com.client.xvideos.l.ui.screens.explorer.tab.saved.collection.ScreenCollectionName as LCollectionScreen
import com.client.xvideos.l.ui.screens.screenAlbum.ScreenLAlbum
import com.client.xvideos.l.ui.screens.screenFullScreen.L_FullScreenImage
import com.client.xvideos.l.ui.screens.screenFullScreen.fullScreenImageFilteredPicArray
import com.client.xvideos.r.common.downloader.DownloadRed
import com.client.xvideos.r.common.saved.SavedRed
import com.client.xvideos.r.model.GifsInfo
import com.client.xvideos.r.ui.fullscreen.ScreenRedFullScreen
import com.client.xvideos.r.ui.profile.ScreenRedProfile
import com.client.xvideos.r.ui.root.R_Screen_Root
import com.client.xvideos.screens.dashboards.ScreenXDashBoards
import com.client.xvideos.x.feature.saved.SavedX
import com.client.xvideos.x.model.ItemsX
import com.client.xvideos.x.screens.videoplayer.ScreenX_VideoPlayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import java.io.File
import javax.inject.Inject
import com.client.xvideos.r.ui.explorer.tab.saved.tab.collection.ScreenCollectionName as RCollectionScreen

object UnifiedLibraryScreen : Screen {

    private fun readResolve(): Any = UnifiedLibraryScreen

    override val key: ScreenKey = uniqueScreenKey

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    override fun Content() {
        val vm = getScreenModel<UnifiedLibraryScreenModel>()
        val navigator = LocalNavigator.currentOrThrow
        val downloadRed by vm.downloadRed.downloadList.collectAsStateWithLifecycle()
        var sourceFilter by remember { mutableStateOf(LibrarySource.ALL) }
        val state = rememberLazyListState()

        LaunchedEffect(Unit) {
            vm.refresh()
        }

        val snapshot = vm.snapshot(downloadRed, sourceFilter)

        Scaffold(
            topBar = {
                UnifiedLibraryTopBar(
                    sourceFilter = sourceFilter,
                    onSourceFilterChange = { sourceFilter = it },
                    onRefresh = { vm.refresh() }
                )
            },
            containerColor = LibraryColors.background
        ) { padding ->
            LazyColumn(
                state = state,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding()),
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    LibrarySummaryGrid(snapshot.stats)
                }

                if (snapshot.recent.isNotEmpty()) {
                    item {
                        LibrarySectionHeader(
                            title = "Недавно сохранённое",
                            subtitle = "${snapshot.recent.size} последних элементов"
                        )
                    }
                    items(snapshot.recent, key = { it.key }) { item ->
                        LibraryContentRow(item) {
                            when (item.target) {
                                is LibraryTarget.XVideo -> navigator.push(ScreenX_VideoPlayer(item.target.href))
                                is LibraryTarget.RGif -> navigator.push(ScreenRedFullScreen(item.target.item))
                                is LibraryTarget.LAlbum -> navigator.push(ScreenLAlbum(item.target.albumId))
                                is LibraryTarget.LPicture -> {
                                    fullScreenImageFilteredPicArray = vm.savedL.likes.listUrl.toList()
                                    navigator.push(
                                        L_FullScreenImage(
                                            item = item.target.item,
                                            albumName = "library",
                                            expandMenu = ExpandMenuType.LIKES,
                                            isCollection = false,
                                            autoPlay = item.target.item.is_animated,
                                            isAnimated = item.target.item.is_animated
                                        )
                                    )
                                }
                                LibraryTarget.None -> Unit
                            }
                        }
                    }
                }

                if (snapshot.downloads.isNotEmpty()) {
                    item {
                        LibrarySectionHeader(
                            title = "Скачанное",
                            subtitle = "Локально доступные элементы"
                        )
                    }
                    items(snapshot.downloads, key = { it.key }) { item ->
                        LibraryContentRow(item) {
                            val target = item.target
                            if (target is LibraryTarget.RGif) {
                                navigator.push(ScreenRedFullScreen(target.item))
                            }
                        }
                    }
                }

                if (snapshot.collections.isNotEmpty()) {
                    item {
                        LibrarySectionHeader(
                            title = "Коллекции",
                            subtitle = "L и R в одном месте"
                        )
                    }
                    items(snapshot.collections, key = { it.key }) { item ->
                        LibraryCollectionRow(item) {
                            when (item.target) {
                                is LibraryCollectionTarget.LCollection -> navigator.push(
                                    LCollectionScreen(
                                        collectionName = item.target.name,
                                        popOnBack = true
                                    )
                                )
                                is LibraryCollectionTarget.RCollection -> navigator.push(
                                    RCollectionScreen(
                                        collectionName = item.target.name,
                                        popOnBack = true
                                    )
                                )
                            }
                        }
                    }
                }

                if (snapshot.quickLinks.isNotEmpty()) {
                    item {
                        LibrarySectionHeader(
                            title = "Быстрые переходы",
                            subtitle = "Открыть исходный раздел"
                        )
                    }
                    items(snapshot.quickLinks, key = { it.title }) { link ->
                        LibraryQuickLinkRow(link) {
                            when (link.source) {
                                LibrarySource.X -> navigator.push(ScreenXDashBoards())
                                LibrarySource.L -> navigator.push(L_ScreenExplorer())
                                LibrarySource.R -> navigator.push(R_Screen_Root())
                                LibrarySource.ALL -> Unit
                            }
                        }
                    }
                }
            }
        }
    }
}

class UnifiedLibraryScreenModel @Inject constructor(
    val savedX: SavedX,
    val savedL: SavedL,
    val savedRed: SavedRed,
    val downloadRed: DownloadRed
) : ScreenModel {

    fun refresh() {
        savedX.favorites.refresh()
        savedL.likes.refresh()
        savedL.albums.refresh()
        savedL.collection.refreshCollectionList()
        savedRed.likes.refresh()
        savedRed.collections.refreshCollectionList()
        savedRed.creators.refresh()
        savedRed.subscriptions.refresh()
        downloadRed.refreshDownloadList()
    }

    fun snapshot(downloads: List<GifsInfo>, sourceFilter: LibrarySource): LibrarySnapshot {
        val includeX = sourceFilter == LibrarySource.ALL || sourceFilter == LibrarySource.X
        val includeL = sourceFilter == LibrarySource.ALL || sourceFilter == LibrarySource.L
        val includeR = sourceFilter == LibrarySource.ALL || sourceFilter == LibrarySource.R

        val xFavorites = if (includeX) savedX.favorites.list.toList() else emptyList()
        val lLikes = if (includeL) savedL.likes.listUrl.toList() else emptyList()
        val lAlbums = if (includeL) savedL.albums.list.toList() else emptyList()
        val lCollections = if (includeL) savedL.collection.collectionList.toList() else emptyList()
        val rLikes = if (includeR) savedRed.likes.list.toList() else emptyList()
        val rCollections = if (includeR) savedRed.collections.collectionList.toList() else emptyList()
        val rDownloads = if (includeR) downloads else emptyList()
        val rSubscriptions = if (includeR) savedRed.subscriptions.listCreators.toList() else emptyList()

        val recent = buildList {
            addAll(xFavorites.asReversed().take(8).map { it.toLibraryItem() })
            addAll(lLikes.take(8).mapIndexed { index, item -> item.toLibraryItem(index) })
            addAll(lAlbums.asReversed().take(8).map { album ->
                LibraryItem(
                    key = "l-album-${album.id}",
                    source = LibrarySource.L,
                    title = album.title,
                    subtitle = "Альбом · ${album.number_of_pictures} items",
                    previewUrl = album.cover.url,
                    target = LibraryTarget.LAlbum(album.id.toLongOrNull() ?: 0L),
                    sortHint = album.created
                )
            })
            addAll(rLikes.sortedByDescending { it.createDate }.take(8).map { it.toLibraryItem("r-like") })
            addAll(rDownloads.sortedByDescending { it.createDate }.take(8).map { it.toDownloadLibraryItem() })
        }
            .filterNot { it.target is LibraryTarget.LAlbum && it.target.albumId <= 0L }
            .sortedByDescending { it.sortHint }
            .take(20)

        val downloadItems = rDownloads
            .sortedByDescending { it.createDate }
            .map { it.toDownloadLibraryItem() }

        val collectionItems = buildList {
            addAll(lCollections.map {
                LibraryCollectionItem(
                    key = "l-collection-${it.collection}",
                    source = LibrarySource.L,
                    name = it.collection,
                    subtitle = "${it.itemsCount} элементов",
                    previewUrl = it.previewUrl,
                    target = LibraryCollectionTarget.LCollection(it.collection)
                )
            })
            addAll(rCollections.map {
                LibraryCollectionItem(
                    key = "r-collection-${it.collection}",
                    source = LibrarySource.R,
                    name = it.collection,
                    subtitle = "${it.items.size} элементов",
                    previewUrl = it.items.lastOrNull()?.urls?.thumbnail,
                    target = LibraryCollectionTarget.RCollection(it.collection)
                )
            })
        }.sortedWith(compareBy<LibraryCollectionItem> { it.source.ordinal }.thenBy { it.name.lowercase() })

        val stats = listOf(
            LibraryStat("Избранное", xFavorites.size + lLikes.size + rLikes.size, Icons.Default.Favorite),
            LibraryStat("Скачанное", downloadItems.size + countLDownloadedAlbums(), Icons.Default.Download),
            LibraryStat("Коллекции", collectionItems.size, Icons.Default.CollectionsBookmark),
            LibraryStat("Альбомы", lAlbums.size, Icons.Default.PhotoAlbum),
            LibraryStat("Подписки", rSubscriptions.size, Icons.Default.Subscriptions)
        )

        val quickLinks = buildList {
            if (includeX) add(LibraryQuickLink(LibrarySource.X, "X", "${xFavorites.size} избранных"))
            if (includeL) add(LibraryQuickLink(LibrarySource.L, "L", "${lLikes.size} likes · ${lCollections.size} collections"))
            if (includeR) add(LibraryQuickLink(LibrarySource.R, "R", "${rLikes.size} likes · ${rDownloads.size} downloads"))
        }

        return LibrarySnapshot(
            stats = stats,
            recent = recent,
            downloads = downloadItems,
            collections = collectionItems,
            quickLinks = quickLinks
        )
    }

    private fun countLDownloadedAlbums(): Int {
        return File(AppPath.l_downloaded_albums)
            .listFiles()
            ?.count { it.isDirectory }
            ?: 0
    }
}

@Composable
private fun UnifiedLibraryTopBar(
    sourceFilter: LibrarySource,
    onSourceFilterChange: (LibrarySource) -> Unit,
    onRefresh: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(LibraryColors.background)
            .padding(start = 12.dp, end = 6.dp, top = 8.dp, bottom = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Библиотека",
                    color = Color.White,
                    style = ThemeL.Type.screenTitle
                )
                Text(
                    text = "X / L / R сохранения",
                    color = LibraryColors.secondaryText,
                    style = ThemeL.Type.rowSubtitle
                )
            }
            IconButton(onClick = onRefresh) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
            }
        }

        Row(
            modifier = Modifier.padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LibrarySource.entries.forEach { source ->
                LibrarySourceChip(
                    source = source,
                    selected = sourceFilter == source,
                    onClick = { onSourceFilterChange(source) }
                )
            }
        }
    }
}

@Composable
private fun LibrarySourceChip(
    source: LibrarySource,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = {
            Text(
                text = source.title,
                color = if (selected) Color.Black else Color.White,
                style = ThemeL.Type.caption
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = if (selected) Color.Black else LibraryColors.secondaryText,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (selected) LibraryColors.accent else LibraryColors.card,
            labelColor = if (selected) Color.Black else Color.White
        ),
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = if (selected) LibraryColors.accent else LibraryColors.border
        )
    )
}

@Composable
private fun LibrarySummaryGrid(stats: List<LibraryStat>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        stats.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { stat ->
                    LibraryStatCard(
                        stat = stat,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun LibraryStatCard(
    stat: LibraryStat,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, LibraryColors.border, RoundedCornerShape(8.dp))
            .background(LibraryColors.card)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(LibraryColors.accent.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(stat.icon, contentDescription = null, tint = LibraryColors.accent)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(stat.count.toString(), color = Color.White, style = ThemeL.Type.rowTitle)
            Text(stat.title, color = LibraryColors.secondaryText, style = ThemeL.Type.caption)
        }
    }
}

@Composable
private fun LibrarySectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.padding(top = 4.dp)) {
        Text(title, color = Color.White, style = ThemeL.Type.rowTitle)
        Text(subtitle, color = LibraryColors.secondaryText, style = ThemeL.Type.caption)
    }
}

@Composable
private fun LibraryContentRow(item: LibraryItem, onClick: () -> Unit) {
    LibraryBaseRow(
        previewUrl = item.previewUrl,
        source = item.source,
        title = item.title,
        subtitle = item.subtitle,
        icon = Icons.Default.Bookmarks,
        onClick = onClick
    )
}

@Composable
private fun LibraryCollectionRow(item: LibraryCollectionItem, onClick: () -> Unit) {
    LibraryBaseRow(
        previewUrl = item.previewUrl,
        source = item.source,
        title = item.name,
        subtitle = item.subtitle,
        icon = Icons.Default.CollectionsBookmark,
        onClick = onClick
    )
}

@Composable
private fun LibraryQuickLinkRow(link: LibraryQuickLink, onClick: () -> Unit) {
    LibraryBaseRow(
        previewUrl = null,
        source = link.source,
        title = link.title,
        subtitle = link.subtitle,
        icon = Icons.Default.Bookmarks,
        onClick = onClick
    )
}

@Composable
private fun LibraryBaseRow(
    previewUrl: String?,
    source: LibrarySource,
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, LibraryColors.border, RoundedCornerShape(8.dp))
            .background(LibraryColors.card)
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LibraryPreview(previewUrl = previewUrl, fallbackIcon = icon)
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SourcePill(source)
                Spacer(Modifier.width(6.dp))
                Text(
                    title,
                    color = Color.White,
                    style = ThemeL.Type.rowTitle,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                subtitle,
                color = LibraryColors.secondaryText,
                style = ThemeL.Type.rowSubtitle,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun LibraryPreview(previewUrl: String?, fallbackIcon: ImageVector) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(LibraryColors.preview),
        contentAlignment = Alignment.Center
    ) {
        if (!previewUrl.isNullOrBlank()) {
            UrlImage(
                url = previewUrl,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(fallbackIcon, contentDescription = null, tint = LibraryColors.secondaryText)
        }
    }
}

@Composable
private fun SourcePill(source: LibrarySource) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(source.color.copy(alpha = 0.22f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = source.title,
            color = source.color,
            style = ThemeL.Type.caption.copy(fontWeight = FontWeight.Medium)
        )
    }
}

private fun ItemsX.toLibraryItem(): LibraryItem {
    return LibraryItem(
        key = "x-$id",
        source = LibrarySource.X,
        title = title,
        subtitle = listOf(channel, duration, views).filter { it.isNotBlank() }.joinToString(" · "),
        previewUrl = previewImage,
        target = LibraryTarget.XVideo(href),
        sortHint = id
    )
}

private fun PicsDetails.toLibraryItem(index: Int): LibraryItem {
    val albumLabel = album?.takeIf { it.isNotBlank() && it != "null" }?.let { "album $it" } ?: "L media"
    return LibraryItem(
        key = "l-like-${url_to_original ?: url_to_video ?: index}",
        source = LibrarySource.L,
        title = if (is_animated) "Animated media" else "Picture",
        subtitle = "$albumLabel · ${width}x$height",
        previewUrl = lPreviewImageUrl("large_thumbnail"),
        target = LibraryTarget.LPicture(this),
        sortHint = Long.MAX_VALUE - index
    )
}

private fun GifsInfo.toLibraryItem(prefix: String): LibraryItem {
    return LibraryItem(
        key = "$prefix-$id",
        source = LibrarySource.R,
        title = userName,
        subtitle = listOfNotNull(contentType, duration?.let { "${it.toInt()} sec" }).joinToString(" · "),
        previewUrl = urls.thumbnail,
        target = LibraryTarget.RGif(this),
        sortHint = createDate
    )
}

private fun GifsInfo.toDownloadLibraryItem(): LibraryItem {
    val preview = AppPath.r_cache_download + "/" + userName + "/" + id + ".jpg"
    return LibraryItem(
        key = "r-download-$id",
        source = LibrarySource.R,
        title = userName,
        subtitle = "Скачано · $id",
        previewUrl = preview,
        target = LibraryTarget.RGif(this),
        sortHint = createDate
    )
}

private object LibraryColors {
    val background = Color(0xFF0D0F10)
    val card = Color(0xFF171A1C)
    val preview = Color(0xFF25292C)
    val border = Color(0xFF303539)
    val accent = Color(0xFF35C779)
    val secondaryText = Color(0xFFB0B7BC)
}

data class LibrarySnapshot(
    val stats: List<LibraryStat>,
    val recent: List<LibraryItem>,
    val downloads: List<LibraryItem>,
    val collections: List<LibraryCollectionItem>,
    val quickLinks: List<LibraryQuickLink>
)

data class LibraryStat(
    val title: String,
    val count: Int,
    val icon: ImageVector
)

data class LibraryItem(
    val key: String,
    val source: LibrarySource,
    val title: String,
    val subtitle: String,
    val previewUrl: String?,
    val target: LibraryTarget,
    val sortHint: Long
)

data class LibraryCollectionItem(
    val key: String,
    val source: LibrarySource,
    val name: String,
    val subtitle: String,
    val previewUrl: String?,
    val target: LibraryCollectionTarget
)

data class LibraryQuickLink(
    val source: LibrarySource,
    val title: String,
    val subtitle: String
)

enum class LibrarySource(
    val title: String,
    val color: Color
) {
    ALL("Все", Color(0xFF35C779)),
    X("X", Color(0xFFE8E8E8)),
    L("L", Color(0xFFCE9BFF)),
    R("R", Color(0xFFFFD166))
}

sealed interface LibraryTarget {
    data object None : LibraryTarget
    data class XVideo(val href: String) : LibraryTarget
    data class RGif(val item: GifsInfo) : LibraryTarget
    data class LAlbum(val albumId: Long) : LibraryTarget
    data class LPicture(val item: PicsDetails) : LibraryTarget
}

sealed interface LibraryCollectionTarget {
    data class LCollection(val name: String) : LibraryCollectionTarget
    data class RCollection(val name: String) : LibraryCollectionTarget
}

@Module
@InstallIn(SingletonComponent::class)
abstract class UnifiedLibraryScreenModule {
    @Binds
    @IntoMap
    @ScreenModelKey(UnifiedLibraryScreenModel::class)
    abstract fun bindUnifiedLibraryScreenModel(screenModel: UnifiedLibraryScreenModel): ScreenModel
}
