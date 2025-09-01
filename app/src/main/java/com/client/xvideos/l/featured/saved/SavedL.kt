package com.client.xvideos.l.featured.saved

import com.client.xvideos.common.di.ApplicationScope
import com.client.xvideos.common.kdownloader.KDownloader
import com.client.xvideos.l.db.AppLDatabase
import com.client.xvideos.redgifs.common.snackBar.SnackBarEvent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SavedL @Inject constructor(
    val db: AppLDatabase,
    snackBarEvent: SnackBarEvent,
    @ApplicationScope val scope: CoroutineScope,
    kDownloader: KDownloader,
) {

    val collection = SavedL_Collection(snackBarEvent)

    val albums = SavedL_Albums(snackBarEvent, db, scope)
    val likes = SavedL_Likes(snackBarEvent, kDownloader)

}

