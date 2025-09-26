package com.client.xvideos.l.featured.saved

import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.common.kdownloader.KDownloader
import com.client.xvideos.common.room.AppDatabase
import com.client.xvideos.common.snackBar.SnackBarEvent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedL @Inject constructor(
    val db: AppDatabase,
    snackBarEvent: SnackBarEvent,
    @ApplicationScope val scope: CoroutineScope,
    kDownloader: KDownloader,
) {

    val collection = SavedL_Collection(snackBarEvent)

    val albums = SavedL_Albums(snackBarEvent, db, scope)
    val likes = SavedL_Likes(snackBarEvent, kDownloader)
    val crypto =  SavedL_Crypto(snackBarEvent, kDownloader)
}

