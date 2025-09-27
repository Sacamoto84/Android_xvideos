package com.client.xvideos.l.ui.screens.screenAlbum.atom

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.client.xvideos.common.eventBus.snackBarInfo
import com.client.xvideos.common.eventBus.snackBarSuccess
import com.client.xvideos.common.util.toPrettyCount2
import com.client.xvideos.l.theme.ThemeL
import com.client.xvideos.l.featured.downloader.DownloaderAlbum
import com.client.xvideos.l.net.AlbumInfo
import com.client.xvideos.l.ui.screens.screenAlbum.ScreenLAlbumSM
import com.client.xvideos.ui.theme.PurpleGrey80

@Composable
fun AlbumInfoDownloadButton(
    folderSize: Long,
    album: AlbumInfo?,
    fileCountDownloaded: Int,
    fileCountError: Int,
    vm: ScreenLAlbumSM,
    isDownloading: Boolean,
    isDeletingFiles: Boolean,
    deletionState: DownloaderAlbum.DeletionProgress,
    isDeletingChange : (Boolean) -> Unit
) {

    Column {

        Row()
        {
            Text(
                " Size: " + folderSize.toPrettyCount2(),
                color = ThemeL.textColor
            )

            Row {
                val a = album?.albumPicsDetails?.pics?.size

                Text(
                    " All: $a ",
                    color = ThemeL.textColor
                )

                if (a != fileCountDownloaded + fileCountError) {
                    Text(
                        "D: $fileCountDownloaded E:",
                        color = ThemeL.textColor
                    )
                    Text(
                        fileCountError.toString(),
                        color = ThemeL.textColor
                    )
                }
            }

        }

        Row(
            modifier = Modifier
                .height(64.dp)
                .padding(horizontal = 4.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {


            Row(verticalAlignment = Alignment.CenterVertically) {

                Button(onClick = { vm.saveFullAlbum() }) {
                    Text(text = "Load All Pics")
                }
                Spacer(modifier = Modifier.width(4.dp))
                if (isDownloading) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PurpleGrey80)
                            .clickable { vm.downloader.stop() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = null,
                            tint = ThemeL.grey7
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            if (folderSize > 0) {
                Button(
                    onClick = {
                        vm.downloader.deleteAlbum(
                            onStart = {
                                isDeletingChange(true)
                                //isDeletingFiles = true
                                snackBarInfo("Удаление файлов альбома")
                            },
                            onComplete = {
                                isDeletingChange(false)
                                //isDeletingFiles = false
                                snackBarSuccess("Удаление файлов альбома завершено")
                            },
                        )
                    }, enabled = !isDeletingFiles, // Блокируем кнопку
                    colors = ButtonDefaults.buttonColors(
                        //containerColor = Color.Unspecified,
                        //contentColor = Color.Unspecified,
                        disabledContainerColor = Color.Gray,
                        disabledContentColor = Color.White,
                    )
                ) {
                    if (deletionState.isDeleting) {
                        Column {
                            Text(text = "Удаление...")
                            LinearProgressIndicator(
                                progress = if (deletionState.total > 0)
                                    deletionState.current.toFloat() / deletionState.total.toFloat()
                                else 0f
                            )
                            Text(
                                text = "${deletionState.current}/${deletionState.total}",
                            )
                        }
                    } else {
                        Text(text = "Удалить все файлы")
                    }
                }
            }
        }


    }


}

//@Preview
//@Composable
//fun AlbumInfoDownloadButtonPreview() {
//    val album = AlbumInfo(
//        id = 1,
//        repository = com.client.xvideos.l.repository.Repository(
//            db = androidx.room.Room.inMemoryDatabaseBuilder(
//                androidx.compose.ui.platform.LocalContext.current,
//                com.client.xvideos.l.db.AppLDatabase::class.java
//            ).build(),
//            snackBarEvent = SnackBarEvent(),
//            scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
//        ),
//        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
//    )
//    val vm = ScreenLAlbumSM(
//        idAlbum = 1L,
//        luscious = Luscious( kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main), com.client.xvideos.l.repository.Repository(db = androidx.room.Room.inMemoryDatabaseBuilder(androidx.compose.ui.platform.LocalContext.current, com.client.xvideos.l.db.AppLDatabase::class.java).build(), snackBarEvent = SnackBarEvent(), scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main))),
//        saved = com.client.xvideos.l.featured.saved.SavedL(db = androidx.room.Room.inMemoryDatabaseBuilder(androidx.compose.ui.platform.LocalContext.current, com.client.xvideos.l.db.AppLDatabase::class.java).build(), snackBarEvent = SnackBarEvent(), scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main), kDownloader = KDownloader.create(androidx.compose.ui.platform.LocalContext.current)),
//        scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main),
//        kDownloader = KDownloader.create(androidx.compose.ui.platform.LocalContext.current),
//        dowloaderL = com.client.xvideos.l.featured.downloader.DownloaderL(),
//        snackBarEvent = SnackBarEvent()
//    )
//    AlbumInfoDownloadButton(folderSize = 1000L, album = album, fileCountDownloaded = 10, fileCountError = 2, vm = vm, isDownloading = false, isDeletingFiles = false, deletionState = DownloaderAlbum.DeletionProgress(), isDeletingChange = {})
//}
