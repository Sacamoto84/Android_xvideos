package com.client.xvideos.red.common.expand_menu_video.impl

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import com.client.xvideos.red.common.block.BlockRed
import com.client.xvideos.red.common.downloader.DownloadRed
import com.client.xvideos.red.common.expand_menu_video.ExpandMenuVideoModel
import com.client.xvideos.red.common.saved.SavedRed

object ExpandMenuVideoImpl {


    val expandMenuVideoList =
        listOf(
            ExpandMenuVideoModel("Скачать", Icons.Filled.FileDownload, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                DownloadRed.downloadItem(it)
            }),
            ExpandMenuVideoModel("Поделиться", Icons.Default.Share),
            ExpandMenuVideoModel("Блокировать", Icons.Default.Block, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                BlockRed.blockVisibleDialog = true
            }),

            ExpandMenuVideoModel("Like", Icons.Default.Favorite, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                SavedRed.addLikes(it)
            }),

            ExpandMenuVideoModel("!Like", Icons.Default.Block, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                SavedRed.removeLikes(it)
            }),
        )

    val expandMenuVideoListLikes =
        listOf(
            ExpandMenuVideoModel("Скачать", Icons.Filled.FileDownload, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                DownloadRed.downloadItem(it)
            }),
            ExpandMenuVideoModel("Поделиться", Icons.Default.Share),
            ExpandMenuVideoModel("Блокировать", Icons.Default.Block, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                BlockRed.blockVisibleDialog = true
            }),

            ExpandMenuVideoModel("!Like", Icons.Default.Block, onClick = {
                if (it == null) return@ExpandMenuVideoModel
                SavedRed.removeLikes(it)
                SavedRed.refreshLikesList()
            }),
        )



}