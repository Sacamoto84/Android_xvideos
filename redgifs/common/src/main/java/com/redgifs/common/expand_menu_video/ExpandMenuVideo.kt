package com.redgifs.common.expand_menu_video


import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.redgifs.network.api.RedApi
import com.redgifs.model.GifsInfo
import com.redgifs.common.ThemeRed
import com.redgifs.common.block.BlockRed
import com.redgifs.common.downloader.DownloadRed
import com.redgifs.common.saved.SavedRed
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private val tintColor = Color(0xFF48454E)
private val style =
    TextStyle(color = tintColor, fontFamily = ThemeRed.fontFamilyPopinsRegular, fontSize = 20.sp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandMenuVideo(
    item: GifsInfo? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onRunLike: () -> Unit = {},
    onRefresh: () -> Unit = {},
    isCollection : Boolean = false,
    block: BlockRed,
    redApi: RedApi,
    savedRed: SavedRed,
    downloadRed: DownloadRed,
    haptic : ()->Unit = {}
) {

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(expanded) {
        haptic.invoke()
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (it) onClick.invoke(); expanded = it },
        modifier = Modifier.then(modifier)
    )
    {
        IconButton(
            modifier = Modifier.size(48.dp).menuAnchor(ExposedDropdownMenuAnchorType.SecondaryEditable),
            onClick = {}) {
            Icon( Icons.Default.MoreVert, contentDescription = "", tint = Color.White, modifier = Modifier.size(24.dp))
        }

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(IntrinsicSize.Min),
            containerColor = Color(0xFFF1EDF4)//ThemeRed.colorCommonBackground
        ) {
            DropdownMenuItem_Download(item, onClick = {downloadRed.downloadItem(it)}){ expanded = false }
            DropdownMenuItem_Share(item, onClick = {downloadRed.downloadItem(it)}){ expanded = false }
            DropdownMenuItem_Block(item = item, block = block){ expanded = false }
            DropdownMenuItem_Like(item, onRunLike, savedRed){expanded = false}
            DropdownMenuItem_Follow(item, redApi, savedRed){ expanded = false }
            DropdownMenuItem_AddCollection(item, savedRed) { expanded = false }
            if(isCollection) DropdownMenuItem_RemoveFromCollection(item, onRefresh, savedRed) { expanded = false }
        }
    }
}







@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuItem_Download(item: GifsInfo? = null, onClick: (GifsInfo) -> Unit = {}, onDismiss: () -> Unit){
    DropdownMenuItem(
        leadingIcon = {Icon(Icons.Filled.FileDownload, contentDescription = "", tint = tintColor)},
        text = {Text("Скачать", style = style)},
        onClick = {
            if (item == null) return@DropdownMenuItem
            onClick.invoke(item)
            //DownloadRed.downloadItem(item)
            onDismiss.invoke()
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuItem_Share(item: GifsInfo? = null, onClick: (GifsInfo) -> Unit, onDismiss: () -> Unit){
    DropdownMenuItem(
        leadingIcon = {Icon(Icons.Default.Share, contentDescription = "", tint = tintColor)},
        text = { Text("Поделиться", style = style) },
        onClick = {
            if (item == null) return@DropdownMenuItem
            //DownloadRed.downloadItem(item)
            onClick.invoke(item)
            onDismiss.invoke()
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuItem_Block(item: GifsInfo? = null, block: BlockRed, onDismiss: () -> Unit){
    DropdownMenuItem(
        leadingIcon = {Icon(Icons.Default.Block, contentDescription = "", tint = tintColor)},
        text = { Text("Блокировать", style = style) },
        onClick = {
            if (item == null) return@DropdownMenuItem; block.blockVisibleDialog = true
            onDismiss.invoke()
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun DropdownMenuItem_Like(item: GifsInfo? = null, onRunLike: () -> Unit, savedRed: SavedRed, onDismiss: () -> Unit){
    val isLiked = savedRed.likes.list.any { it.id == item?.id }
    val textLiked = if (isLiked) "Unlike" else "Like"
    val textLikedIcon = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder
    DropdownMenuItem(
        leadingIcon = {Icon(textLikedIcon, contentDescription = "", tint = tintColor)},
        text = { Text(textLiked, style = style) },
        onClick = {
            if (item == null) return@DropdownMenuItem
            GlobalScope.launch {
                delay(200)
                if (!isLiked) savedRed.likes.add(item) else savedRed.likes.remove(item)
                onRunLike.invoke()
                onDismiss.invoke()
            }
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun DropdownMenuItem_Follow(item: GifsInfo? = null, redApi: RedApi, savedRed: SavedRed, onDismiss: () -> Unit){
    val isFollowed = savedRed.creators.list.any { it.username == item?.userName }
    val textFollowed = if (isFollowed) "Unfollow" else "Follow"
    val textFollowedIcon = if (isFollowed) Icons.Default.Person else Icons.Default.PermIdentity
    DropdownMenuItem(
        leadingIcon = {Icon(textFollowedIcon, contentDescription = "", tint = tintColor)},
        text = { Text(textFollowed, style = style) },
        onClick = {
            if (item == null) return@DropdownMenuItem
            GlobalScope.launch {
                delay(200)
                if (!isFollowed) {
                    try {
                        val a = redApi.readCreator(item.userName)
                        savedRed.creators.add(a)
                    } catch (e: Exception) { e.printStackTrace() }
                }
                else {
                    savedRed.creators.remove(item.userName)
                }
            }
            onDismiss.invoke()
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuItem_AddCollection(item: GifsInfo? = null, savedRed: SavedRed, onDismiss: () -> Unit){
    DropdownMenuItem(
        leadingIcon = {
            Icon(
                Icons.Default.AddCircleOutline,
                contentDescription = "",
                tint = tintColor
            )
        },
        text = { Text("Add to Collection", style = style) },
        onClick = {
            if (item == null) return@DropdownMenuItem
            savedRed.collections.collectionItemGifInfo = item
            savedRed.collections.collectionVisibleDialog = true
            onDismiss.invoke()
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuItem_RemoveFromCollection(item: GifsInfo? = null, onRefresh: () -> Unit, savedRed: SavedRed, onDismiss: () -> Unit){
    DropdownMenuItem(
        leadingIcon = {
            Icon(
                Icons.Default.RemoveCircleOutline,
                contentDescription = "",
                tint = tintColor
            )
        },
        text = { Text("Remove from Collection", style = style) },
        onClick = {
            if (item == null) return@DropdownMenuItem
            if (savedRed.collections.selectedCollection == null) {
                onDismiss.invoke()
                return@DropdownMenuItem
            }
            savedRed.collections.deleteItemFromCollection(item, savedRed.collections.selectedCollection!!)
            onRefresh.invoke()

            onDismiss.invoke()
        }, contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
    )
}







